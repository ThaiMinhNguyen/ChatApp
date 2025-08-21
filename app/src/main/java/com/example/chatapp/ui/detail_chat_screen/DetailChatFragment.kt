package com.example.chatapp.ui.detail_chat_screen

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.OnReceiveContentListener
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.DetailChatScreenBinding
import com.example.chatapp.databinding.ImagePreviewDialogBinding
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.MessageStatus
import com.example.chatapp.domain.data.MessageType
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.UserUtils
import com.example.chatapp.utils.setImageChatUrl
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.ChatViewModel
import com.example.chatapp.view_model.StorageViewModel
import com.example.chatapp.view_model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class DetailChatFragment : Fragment() {
    private var _binding: DetailChatScreenBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val authViewModel: AuthenticationViewModel by activityViewModels()
    private val storageViewModel: StorageViewModel by activityViewModels()

    private lateinit var messageAdapter: MessageListAdapter
    private lateinit var conversationId: String

    private var user: User? = null

    private var messageList = emptyList<Message>()
    private var lastDisplayedLastMsgId: String? = null

    private val imgPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
        if (it != null) {
            Log.d("MyLog - PhotoPicker", "Selected URI: $it")
            showPreviewDialog(it)
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
        setUpRecyclerView()
        setUpRealtimeListener()
        setUpObserver()
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
        getChatUser()
        chatViewModel.markMessageAsRead(conversationId, currentUser!!.uid)
        updateUI()
    }

    private fun getChatUser(){
        val currentUser = authViewModel.user.value
        val otherUserId = UserUtils.getOtherId(conversationId, currentUser!!.uid)
        val otherUser = userViewModel.people.value.find { it.user.uid == otherUserId }?.user
        chatViewModel.setCurrentChatUser(otherUser)
    }

    private fun setUpListener(){
        binding.apply {

            //Send gif và sticker
            ViewCompat.setOnReceiveContentListener(
                etMessageInput,
                arrayOf("image/*", "video/*")
            ) { _, payload ->
                val clipData = payload.clip
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    Log.d("MyLog - DetailChat", "Selected URI from content: $uri")
                    showPreviewDialog(uri)
                }
                null
            }

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

    private fun showPreviewDialog(uri: Uri){
        val dialogBinding = ImagePreviewDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            ivPreview.setImageChatUrl(uri.toString())
            btnSend.isEnabled = true
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
        dialogBinding.apply {
            btnSend.setOnClickListener {
                val currentUser = authViewModel.user.value
                val message = Message(
                    roomId = conversationId,
                    content = uri.toString(),
                    senderId = currentUser?.uid ?: "",
                    senderName = currentUser?.displayName?: "Unknown",
                    senderAvatar = currentUser?.photoUrl.toString(),
                    messageType = MessageType.IMAGE,
                    messageStatus = MessageStatus.SENDING,
                    localId = UUID.randomUUID().toString(),
                )
                if(user != null) {
                    chatViewModel.sendImageMessage(user!!.uid, message, requireContext())
                }
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
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
                    userViewModel.people.collect{
                        getChatUser()
                        val avatarMap = it.associate { it.user.uid to it.user.photoUrl }
                        messageAdapter.setAvatarMap(avatarMap)
                    }
                }
                launch {
                    chatViewModel.messageListMap.collect{ map ->
                        val newList = map[conversationId] ?: emptyList()
                        val prevLastId = lastDisplayedLastMsgId
                        val newLastId = newList.lastOrNull()?.uid
                        val lastChanged = newLastId != prevLastId

                        messageList = newList
                        messageAdapter.submitList(newList)

                        val lm = binding.rvChatMessages.layoutManager as LinearLayoutManager
                        val isNearBottom = lm.findLastVisibleItemPosition() >= (messageAdapter.itemCount - 2)
                        val lastSenderIsMe = newList.lastOrNull()?.senderId == authViewModel.user.value?.uid
                        if (lastChanged && isNearBottom && lastSenderIsMe) {
                            scrollToBottom()
                        }

                        lastDisplayedLastMsgId = newLastId
                        val currentUser = authViewModel.user.value
                        chatViewModel.markMessageAsRead(conversationId, currentUser!!.uid)
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
                stackFromEnd = true
            }
        }
        val lm = binding.rvChatMessages.layoutManager as LinearLayoutManager
        binding.rvChatMessages.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstPos = lm.findFirstVisibleItemPosition()
                    if(firstPos <= 0){
                        chatViewModel.loadMoreForRoom(conversationId)
                    }
                }
            }
        )
        messageAdapter.submitList(chatViewModel.messageListMap.value[conversationId])
    }

    private fun scrollToBottom(){
        if(messageAdapter.itemCount > 0){
            binding.rvChatMessages.post {
                binding.rvChatMessages.smoothScrollBy(0, Int.MAX_VALUE)
            }
        }
    }


}