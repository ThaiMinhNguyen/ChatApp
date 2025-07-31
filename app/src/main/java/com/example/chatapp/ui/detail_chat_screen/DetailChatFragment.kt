package com.example.chatapp.ui.detail_chat_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.DetailChatScreenBinding
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.MessageType
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class DetailChatFragment : Fragment() {
    private var _binding: DetailChatScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: MessageListAdapter
    private lateinit var conversationId: String

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
        
        setUpRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        messageAdapter = MessageListAdapter("currentUserId")  //Dummy id
        binding.rvChatMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        val dummyData = List(20) { index ->
            val senderId = if (index % 3 == 0) "currentUserId" else "otherUserId"
            val senderName = if (senderId == "currentUserId") "Current User" else "Other User"
            val senderAvatar = if (senderId == "currentUserId") {
                "https://example.com/currentUserAvatar.png"
            } else {
                "https://example.com/otherUserAvatar.png"
            }

            Message(
                id = "msg_$index",
                conversationId = "conv_1",
                senderId = senderId,
                senderName = senderName,
                senderAvatar = senderAvatar,
                content = "Message $index",
                messageType = MessageType.entries.toTypedArray().random(),
                timestamp = System.currentTimeMillis() - (index * 1000L * 60),
                isRead = Random.nextBoolean()
            )
        }

        messageAdapter.submitList(dummyData)
    }


}