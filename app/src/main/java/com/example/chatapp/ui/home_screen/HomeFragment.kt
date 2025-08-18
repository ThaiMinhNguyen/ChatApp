package com.example.chatapp.ui.home_screen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.HomeScreenBinding
import com.example.chatapp.domain.data.ChatListItem
import com.example.chatapp.domain.data.Room
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.UserUtils
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.ChatViewModel
import com.example.chatapp.view_model.UserViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private var _binding : HomeScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatListAdapter: ChatListAdapter

    private val authenticationViewModel : AuthenticationViewModel by activityViewModels()
    private val chatViewModel : ChatViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()

    private var isSearchMode = false

    private var roomById: Map<String, Room> = emptyMap()
    private var userById: Map<String, User> = emptyMap()

    private var roomItemList: List<ChatListItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpListener()
        setUpRealTimeListener()
        setUpObserver()
    }

    private fun setUpView() {
        setUpRecyclerView()
    }

    private fun setUpRealTimeListener(){
        val user = authenticationViewModel.user.value
        chatViewModel.listenToRoomFlow(user!!)
        chatViewModel.listenUnreadByRoom(user.uid)
        chatViewModel.listenTopRooms(user.uid)
    }

    private fun setUpObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    combine(
                        chatViewModel.rooms,
                        userViewModel.people,
                        authenticationViewModel.user,
                        chatViewModel.unreadByRoom
                    ) { rooms, people, currentUser, unread ->
                        val currentUid = currentUser?.uid
                        userById = people.associate { it.user.uid to it.user }
                        roomById = rooms.associateBy { it.participants.sorted().joinToString("_") }
                        val localUserById = userById
                        rooms.map { room ->
                            val otherUid = room.participants.firstOrNull { it != currentUid } ?: currentUid
                            val otherUser = otherUid?.let { uid -> localUserById[uid] }
                            var lastMessageDisplay = room.lastMessage?:""
                            val selfSender = requireContext().getString(R.string.you)
                            if(room.lastMessageSenderId == currentUid){
                                lastMessageDisplay = "$selfSender: " + room.lastMessage
                            }
                            ChatListItem.RoomItem(
                                room.copy(
                                    roomName = room.roomName ?: otherUser?.displayName,
                                    roomAvatar = room.roomAvatar ?: otherUser?.photoUrl,
                                    lastMessage = lastMessageDisplay
                                ),
                                unread = unread[room.participants.sorted().joinToString("_")] ?: 0
                            )
                        }
                    }.collect { roomItems ->
                        roomItemList = roomItems.toList()
                        if(!isSearchMode) {
                            chatListAdapter.submitList(roomItems)
                        }
                    }
                }
                launch {
                    chatViewModel.rooms.collect { rooms ->
                        roomById = rooms.associateBy { it.participants.sorted().joinToString("_") }
                    }
                }
                launch {
                    userViewModel.people.collect { people ->
                        userById = people.associate { it.user.uid to it.user }
                    }
                }
                launch {
                    chatViewModel.searchResults.collect{ searchResults ->
                        val completeSearchResult = searchResults.map {
                            it.copy(
                                contactName = userById[it.contactId]?.displayName ?: "Unknown",
                                contactAvatar = userById[it.contactId]?.photoUrl ?: ""
                            )
                        }
                        if(completeSearchResult.isEmpty()){
                            onNoResultsFound()
                        }
                        chatListAdapter.submitList(completeSearchResult)
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        chatListAdapter = ChatListAdapter { chatId ->
            navigateToDetailChat(chatId)
        }
        binding.rvChatList.apply {
            adapter = chatListAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }

    }

    private fun navigateToDetailChat(roomId: String) {
        val room = roomById[roomId]
        chatViewModel.setCurrentRoom(room)
        if(room?.roomName == null){
            val currentUser = authenticationViewModel.user.value
            val otherUserId = UserUtils.getOtherId(roomId, currentUser!!.uid)
            val otherUser = if (otherUserId != null) userById[otherUserId] else null
            chatViewModel.setCurrentChatUser(otherUser)
        }
        val action = HomeFragmentDirections.actionHomeFragmentToDetailChatFragment(roomId)
        findNavController().navigate(action)
    }

    private fun setUpListener(){
        setUpSearchListener()
        setUpCancelSearchListener()
        binding.btnCreateNewChat.setOnClickListener {
            findNavController().navigate(R.id.newChatFragment)
        }
    }

    private fun setUpCancelSearchListener() {
        binding.tvCancel.setOnClickListener{
            binding.etSearch.text?.clear()
            switchToNormalMode()
        }

    }

    private fun setUpSearchListener(){
        binding.etSearch.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                switchToSearchMode()
            } else {
                //Hide keyboard when focus is lost
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            }
        }

        binding.etSearch.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val query = p0.toString()
                    if(query.isBlank()){
                        chatListAdapter.submitList(emptyList())
                    } else {
                        val user = authenticationViewModel.user.value
                        chatViewModel.searchMessages(user!!.uid, query)
                    }
                }

            }
        )
    }


    private fun switchToSearchMode() {
        isSearchMode = true
        chatListAdapter.submitList(emptyList())
        binding.tvCancel.visibility = View.VISIBLE
    }


    private fun switchToNormalMode() {
        isSearchMode = false
        binding.tvCancel.visibility = View.GONE
        binding.llNoSearchResult.visibility = View.GONE
        binding.etSearch.clearFocus()
        chatListAdapter.submitList(roomItemList)
    }


    private fun onNoResultsFound(){
        if(isSearchMode) {
            binding.llNoSearchResult.visibility = View.VISIBLE
        } else {
            binding.llNoSearchResult.visibility = View.GONE

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.stopRoomFlowListener()
        _binding = null
    }
}