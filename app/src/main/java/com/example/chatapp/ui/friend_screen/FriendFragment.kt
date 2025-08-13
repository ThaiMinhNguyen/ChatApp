package com.example.chatapp.ui.friend_screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.FriendScreenBinding
import com.example.chatapp.domain.data.FriendListItem
import com.example.chatapp.domain.data.FriendshipStatus
import com.example.chatapp.domain.data.People
import com.example.chatapp.utils.FriendListUtils
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.UserViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {
    private var _binding : FriendScreenBinding? = null
    private val binding get() = _binding!!

    private val userViewModel : UserViewModel by activityViewModels()
    private val authenticationViewModel : AuthenticationViewModel by activityViewModels()

    private lateinit var friendAdapter: FriendItemAdapter
    private var currentTab = 0

    private var peopleList: MutableList<People> = emptyList<People>().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FriendScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setUpRecyclerView()
        setUpListener()
        setUpTab()
        setUpSearchListener()
        setUpSwipeActions()
    }



    private fun setUpListener() {
        binding.tvCancel.setOnClickListener {
            switchToNormalMode()
            binding.etSearch.text?.clear()
        }

    }

    private fun setUpObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    userViewModel.people.collect{ people ->
                        peopleList = people.toMutableList()
                        val friendRequestCount = people.count { !it.isFriend && it.isRequestReceived }
                        updateBadgeCount(friendRequestCount)
                        handleTabSelection()
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun setUpTab(){
        val friends = requireContext().getString(R.string.friend).uppercase()
        val all = requireContext().getString(R.string.all).uppercase()
        binding.tlFriend.addTab(binding.tlFriend.newTab().setText(friends))
        binding.tlFriend.addTab(binding.tlFriend.newTab().setText(all))

        val customTab = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_tab_with_badge, null)

        val tab = binding.tlFriend.newTab()
        tab.customView = customTab
        binding.tlFriend.addTab(tab)

        binding.tlFriend.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                updateTabTextColors()
                handleTabSelection()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        handleTabSelection()
    }

    private fun handleTabSelection() {
        when (currentTab) {
            0 -> {
                val friendsList = peopleList.filter { it.isFriend }
                val listWithHeaders = FriendListUtils.createListWithHeaders(friendsList, currentTab)
                friendAdapter.submitList(listWithHeaders)
            }
            1 -> {
                val listWithHeaders = FriendListUtils.createListWithHeaders(peopleList, currentTab)
                friendAdapter.submitList(listWithHeaders)
            }
            2 -> {
                val listWithHeaders = FriendListUtils.createListWithHeaders(peopleList, currentTab)
                friendAdapter.submitList(listWithHeaders)
            }
        }
    }

    private fun updateTabTextColors() {
        for (i in 0 until binding.tlFriend.tabCount) {
            val tab = binding.tlFriend.getTabAt(i)
            val customView = tab?.customView

            if (customView != null) {
                val tabText = customView.findViewById<TextView>(R.id.tvTabText)
                if (i == currentTab) {
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                } else {
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
                }
            }
        }
    }

    private fun updateBadgeCount(count: Int) {
        val tab = binding.tlFriend.getTabAt(2)
        val customView = tab?.customView

        if (customView != null) {
            val badge = customView.findViewById<TextView>(R.id.tvBadge)
            if (count > 0) {
                badge.text = count.toString()
                badge.visibility = View.VISIBLE
            } else {
                badge.visibility = View.GONE
            }
        }
    }


    private fun setUpRecyclerView() {
        val currentUser = authenticationViewModel.user.value
        Log.d("MyLog - FriendFragment", "People List: $peopleList")
        friendAdapter = FriendItemAdapter(
            onItemClick = { people ->
                Toast.makeText(requireContext(), "Clicked: ${people.user.displayName}", Toast.LENGTH_SHORT).show()
            },
            onAddFriendClick = { people ->
                Toast.makeText(requireContext(), "Add Friend: ${people.user.displayName}", Toast.LENGTH_SHORT).show()
                val status : FriendshipStatus = if (people.isRequestSent) {
                    FriendshipStatus.NONE
                } else {
                    FriendshipStatus.PENDING
                }
                userViewModel.toggleFriendRequest(currentUser!!, people.user, status)

            },
            onAcceptFriendClick = { people ->
                Toast.makeText(requireContext(), "Accept Friend: ${people.user.displayName}", Toast.LENGTH_SHORT).show()
                val status = FriendshipStatus.ACCEPTED
                userViewModel.toggleFriendRequest(people.user, currentUser!!, status) //đảo chỗ 2 user để không bị ghi đè lên requestBy
            }
        )

        binding.rvPeopleList.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun setUpSearchListener(){
        binding.etSearch.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                switchToSearchMode()
            } else {
                hideKeyboard()
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filteredList = peopleList.filter { it.isFriend && it.user.displayName?.contains(query, ignoreCase = true) == true }
                    if (filteredList.isEmpty()) {
                        onNoResultsFound()
                    } else {
                        binding.llNoSearchResult.visibility = View.GONE
                        friendAdapter.submitList(FriendListUtils.createListWithoutHeaders(filteredList))
                    }
                } else {
                    binding.llNoSearchResult.visibility = View.GONE
                    val filteredList = peopleList.filter { it.isFriend  }
                    friendAdapter.submitList(FriendListUtils.createListWithoutHeaders(filteredList))
                }
            }
        })
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    private fun switchToSearchMode() {
        submitAllFriends()
        binding.tvCancel.visibility = View.VISIBLE
        binding.tlFriend.visibility = View.GONE
        binding.tvSearchTitle.visibility = View.VISIBLE
    }

    private fun submitAllFriends() {
        val friendsList = peopleList.filter { it.isFriend }
        binding.llNoSearchResult.visibility = View.GONE
        friendAdapter.submitList(FriendListUtils.createListWithoutHeaders(friendsList))
    }

    private fun switchToNormalMode() {
        binding.tvCancel.visibility = View.GONE
        binding.tlFriend.visibility = View.VISIBLE
        binding.tvSearchTitle.visibility = View.GONE
        binding.llNoSearchResult.visibility = View.GONE
        binding.etSearch.clearFocus()
        handleTabSelection()
    }

    private fun onNoResultsFound(){
        friendAdapter.submitList(null)
        binding.llNoSearchResult.visibility = View.VISIBLE
    }

    private fun setUpSwipeActions(){

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val possition = viewHolder.adapterPosition
                if (possition == RecyclerView.NO_POSITION) return 0

                val item = friendAdapter.currentList.getOrNull(possition)
                val canSwipe = item is FriendListItem.PersonItem && item.people.isRequestReceived

                return if (canSwipe) makeMovementFlags(0, ItemTouchHelper.LEFT) else 0
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val chosenItem = friendAdapter.currentList[position] as FriendListItem.PersonItem
                val chosenUser = chosenItem.people.user
                val currentUser = authenticationViewModel.user.value
                userViewModel.toggleFriendRequest(chosenUser, currentUser!!, FriendshipStatus.DECLINED) //đảo chỗ 2 user để không bị ghi đè lên requestBy

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (dX < 0) {
                    val bg = "#F44336".toColorInt().toDrawable()
                    bg.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    bg.draw(c)

                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.WHITE
                        textSize = 40f
                        typeface = Typeface.DEFAULT_BOLD
                    }
                    val text = requireContext().getString(R.string.decline)
                    val textWidth = paint.measureText(text)
                    val textX = (itemView.right - 40f) - textWidth
                    val textY = itemView.top + itemView.height / 2f + 20f
                    c.drawText(text, textX, textY, paint)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvPeopleList)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}