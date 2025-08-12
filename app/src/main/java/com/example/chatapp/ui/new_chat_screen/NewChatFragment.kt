package com.example.chatapp.ui.new_chat_screen

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.NewChatScreenBinding
import com.example.chatapp.domain.data.User
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.ChatViewModel
import com.example.chatapp.view_model.UserViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewChatFragment : Fragment() {
    private var _binding: NewChatScreenBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var friendSelectionAdapter: FriendSelectionAdapter
    private lateinit var selectedFriendsAdapter: SelectedFriendsAdapter
    private var allUsers: List<User> = emptyList()
    private val selectedUsers = mutableListOf<User>()

    private val chatViewModel: ChatViewModel by activityViewModels()
    private val authViewModel: AuthenticationViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewChatScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpListeners()
        setUpObserver()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setUpRecyclerView() {

        friendSelectionAdapter = FriendSelectionAdapter { user, isSelected ->
            handleUserSelection(user, isSelected)
        }
        
        binding.rvFriendList.apply {
            adapter = friendSelectionAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }


        selectedFriendsAdapter = SelectedFriendsAdapter { user ->
            removeSelectedUser(user)
        }
        
        binding.rvSelectedFriends.apply {
            adapter = selectedFriendsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }
    
    private fun setUpListeners() {
        //NavigateUp của nav_graph còn popBackStack của FragmentContainer|| chỉ khác khi dùng deeplink từ app khác
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.tvCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }

        binding.btnCreateChat.setOnClickListener {
            if (selectedUsers.isNotEmpty()) {

                createChatWithSelectedUsers()
            }
        }
    }


    private fun setUpObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    userViewModel.people.collect{ people ->
                        val userList = people.filter { it.isFriend }.map { it.user }
                        friendSelectionAdapter.submitList(userList)
                    }
                }
                launch {
                    chatViewModel.navEvents.collect{
                        if(it is ChatViewModel.NavEvent.ToDetail){
                            val action = NewChatFragmentDirections.actionNewChatFragmentToDetailChatFragment(it.roomId)
                            val option =  NavOptions.Builder()
                                .setPopUpTo(R.id.newChatFragment, inclusive = true)
                                .build()
                            findNavController().navigate(action, option)
                        }
                    }
                }
            }
        }
    }

    private fun filterUsers(query: String) {
        val filteredUsers = if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { user ->
                (user.displayName?.contains(query, ignoreCase = true) == true) ||
                (user.email?.contains(query, ignoreCase = true) == true)
            }
        }
        friendSelectionAdapter.submitList(filteredUsers)
    }
    
    private fun handleUserSelection(user: User, isSelected: Boolean) {
        if (isSelected) {
            // Add user to selected list
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user)
            }
        } else {
            // Remove user from selected list
            selectedUsers.remove(user)
        }
        
        // Update selected friends RecyclerView
        selectedFriendsAdapter.submitList(selectedUsers.toList())
        
        // Update UI based on selection count
        updateSelectionUI()
    }

    private fun removeSelectedUser(user: User) {
        selectedUsers.remove(user)
        
        // Update the main adapter to uncheck this user
        friendSelectionAdapter.removeUser(user)

        // Update selected friends RecyclerView
        selectedFriendsAdapter.submitList(selectedUsers.toList())
        

        updateSelectionUI()
    }

    private fun createChatWithSelectedUsers() {
        //Currently create chat for the first item
        val currentUser = authViewModel.user.value
        val selectedFriend = selectedUsers[0]
        chatViewModel.createRoom(currentUser!!, selectedFriend)
    }
    
    private fun updateSelectionUI() {
        // TODO: Update UI to show selected count
        if (selectedUsers.isNotEmpty()){
            binding.llSelectedFriendsContainer.visibility = View.VISIBLE
            selectedFriendsAdapter.submitList(selectedUsers.toList())
        } else {
            binding.llSelectedFriendsContainer.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}