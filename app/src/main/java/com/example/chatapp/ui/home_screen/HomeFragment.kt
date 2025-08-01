package com.example.chatapp.ui.home_screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.HomeScreenBinding
import com.example.chatapp.domain.data.ChatOverview
import com.example.chatapp.domain.data.ChatSearchResult


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
        setUpListener()
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

        submitFakeChatList()

    }

    private fun submitFakeChatList() {
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

    private fun setUpListener(){
        setUpSearchListener()
        setUpCancelSearchListener()
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
                submitFakeSearchResults()
            } else {
                //Hide keyboard when focus is lost
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            }
        }
    }

    private fun submitFakeSearchResults() {
        val searchResults = List(5) { index ->
            ChatSearchResult(
                conversationId = "conv$index",
                contactId = "contact$index",
                contactName = "Contact $index",
                contactAvatar = "https://example.com/avatar$index.png",
                messageMatch = index
            )
        }
        chatListAdapter.submitList(searchResults)
    }

    private fun switchToSearchMode() {
        binding.tvCancel.visibility = View.VISIBLE

    }


    private fun switchToNormalMode() {
        binding.tvCancel.visibility = View.GONE
        binding.llNoSearchResult.visibility = View.GONE
        binding.etSearch.clearFocus()
        submitFakeChatList()
    }

    private fun onNoResultsFound(){
        binding.llNoSearchResult.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}