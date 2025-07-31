package com.example.chatapp.ui.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.HomeScreenBinding
import com.example.chatapp.domain.data.ChatOverview

class HomeFragment : Fragment() {
    private var _binding : HomeScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatListAdapter: ChatListAdapter

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
    }

    private fun setUpView() {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        chatListAdapter = ChatListAdapter({chatId->navigateToDetailChat(chatId)})
        binding.rvChatList.apply {
            adapter = chatListAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }


        //For testing, using dummy data
        val dummyData = List(20) { index ->
            ChatOverview(
                conversationId = "conv_$index",
                contactId = "contact_$index",
                contactName = "Contact $index",
                contactAvatar = "https://example.com/avatar_$index.png",
                lastMessage = "Last message from Contact $index",
                lastMessageTime = System.currentTimeMillis() - (index * 1000L * 60),
                unreadCount = index % 5,
                isOnline = index % 2 == 0,
                isTyping = index % 3 == 0
            )
        }

        chatListAdapter.submitList(dummyData.sortedBy { it.lastMessageTime }.reversed())

    }

    private fun navigateToDetailChat(conversationId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailChatFragment(conversationId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}