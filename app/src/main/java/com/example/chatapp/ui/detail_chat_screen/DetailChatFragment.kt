package com.example.chatapp.ui.detail_chat_screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.DetailChatScreenBinding
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.MessageType
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.UserUtils
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class DetailChatFragment : Fragment() {
    private var _binding: DetailChatScreenBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by activityViewModels()
    private val authViewModel: AuthenticationViewModel by activityViewModels()

    private lateinit var messageAdapter: MessageListAdapter
    private lateinit var conversationId: String

    private var user: User? = null

    private var messageList = emptyList<Message>()

    private val imgPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
        if (it != null) {
            Log.d("MyLog - PhotoPicker", "Selected URI: $it")
        } else {
            Log.d("MyLog - PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailChatScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val args: DetailChatFragmentArgs by navArgs()
        conversationId = args.chatConversationId
        setUpView()
        setUpRealtimeListener()
        setUpObserver()
        setUpRecyclerView()
        setUpListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.stopRoomMessageListener()
        _binding = null
    }

    private fun setUpView() {
        user = chatViewModel.currentChatUser.value
        val currentUser = authViewModel.user.value
        chatViewModel.markMessageAsRead(conversationId, currentUser!!.uid)
        updateUI()
    }

    private fun setUpListener(){
        binding.apply {

            btnAttachment.setOnClickListener {
                imgPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSendMessage.setOnClickListener {
                val content = etMessageInput.text.toString()
                if(content.isNotBlank()) {
                    val currentUser = authViewModel.user.value
                    val otherUserId = UserUtils.getOtherId(conversationId, currentUser!!.uid)
                    chatViewModel.sendMessage(currentUser, otherUserId!!, content)
                    hideKeyboard()
                }
            }
        }
    }

    private fun setUpRealtimeListener(){
        chatViewModel.listenToRoomMessage(conversationId)
    }

    private fun setUpObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    chatViewModel.currentChatUser.collect{
                        user = it
                        updateUI()
                    }
                }
                launch {
                    chatViewModel.currentMessageList.collect{
                        messageList = it
                        messageAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun updateUI(){
        binding.apply {
            if (user == null){
                tvContactName.text = ""
                civContactAvatar.setImageResource(R.drawable.ic_user_register)
                etMessageInput.isEnabled = false
                ivSendMessage.isEnabled = false
                return
            }

            tvContactName.text = user!!.displayName ?: user!!.email ?: ""
            etMessageInput.isEnabled = true
            ivSendMessage.isEnabled = true

            Glide.with(civContactAvatar)
                .load(user!!.photoUrl)
                .placeholder(R.drawable.ic_user_register)
                .error(R.drawable.ic_user_register)
                .into(civContactAvatar)
        }
    }

    private fun hideKeyboard() {
        binding.etMessageInput.text = null
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.etMessageInput.windowToken, 0)
    }

    private fun setUpRecyclerView() {
        val currentUser = authViewModel.user.value
        messageAdapter = MessageListAdapter(currentUser!!.uid)
        binding.rvChatMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        messageAdapter.submitList(messageList)
    }


}