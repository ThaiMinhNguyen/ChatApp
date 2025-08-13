package com.example.chatapp.ui.home_screen

import android.content.Context
import android.os.Bundle
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
import com.example.chatapp.view_model.*
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

            }
        }
    }

    private fun setUpRecyclerView() {
        chatListAdapter = ChatListAdapter(
            {
                chatId->navigateToDetailChat(chatId)
            }
        )
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
    }

    private fun setUpCancelSearchListener() {
        binding.tvCancel.setOnClickListener{
            binding.etSearch.text?.clear()
            switchToNormalMode()
        }

        binding.btnCreateNewChat.setOnClickListener {
            findNavController().navigate(R.id.newChatFragment)
        }
    }

    private fun setUpSearchListener(){
        binding.etSearch.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                switchToSearchMode()
                submitFakeSearchResults()
            } else {
                //Hide keyboard when focus is lost
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            }
        }
    }

    private fun submitFakeSearchResults() {
        val results = List(5) { index ->
            ChatListItem.SearchResultItem(
                roomId = "conv$index",
                contactId = "contact$index",
                contactName = "Contact $index",
                contactAvatar = "https://example.com/avatar$index.png",
                messageMatch = index
            )
        }
        chatListAdapter.submitList(results)
    }

    private fun switchToSearchMode() {
        isSearchMode = true
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
        binding.llNoSearchResult.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.stopRoomFlowListener()
        _binding = null
    }
}