package com.example.chatapp.ui.friend_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.FriendScreenBinding
import com.example.chatapp.domain.data.People
import com.example.chatapp.domain.data.PeopleAction
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.FriendListUtils
import com.google.android.material.tabs.TabLayout
import kotlin.random.Random

class FriendFragment : Fragment() {
    private var _binding : FriendScreenBinding? = null
    private val binding get() = _binding!!


    private lateinit var friendAdapter: FriendItemAdapter
    private var currentTab = 0

    private lateinit var peopleList: MutableList<People>

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
        setUpRecyclerView()
        setUpTab()
    }

    private fun setUpTab(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("BẠN BÈ"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("TẤT CẢ"))

        val customTab = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_tab_with_badge, null)

        val tab = binding.tabLayout.newTab()
        tab.customView = customTab
        binding.tabLayout.addTab(tab)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
                Toast.makeText(requireContext(), "BẠN BÈ", Toast.LENGTH_SHORT).show()
                val friendsList = peopleList.filter { it.isFriend }
                val listWithHeaders = FriendListUtils.createListWithHeaders(friendsList, currentTab)
                friendAdapter.submitList(listWithHeaders)
            }
            1 -> {
                Toast.makeText(requireContext(), "TẤT CẢ", Toast.LENGTH_SHORT).show()
                val listWithHeaders = FriendListUtils.createListWithHeaders(peopleList, currentTab)
                friendAdapter.submitList(listWithHeaders)
            }
            2 -> {
                Toast.makeText(requireContext(), "YÊU CẦU", Toast.LENGTH_SHORT).show()
                updateBadgeCount(0)
                val listWithHeaders = FriendListUtils.createListWithHeaders(peopleList, currentTab)
                friendAdapter.submitList(listWithHeaders)
            }
        }
    }

    private fun updateTabTextColors() {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
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

    fun updateBadgeCount(count: Int) {
        val tab = binding.tabLayout.getTabAt(2)
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
        peopleList = generatePeopleList()
        Log.d("MyLog - FriendFragment", "People List: $peopleList")
        friendAdapter = FriendItemAdapter(
            onItemClick = { people ->
                Toast.makeText(requireContext(), "Clicked: ${people.user.displayName}", Toast.LENGTH_SHORT).show()
            },
            onAddFriendClick = { people ->
                Toast.makeText(requireContext(), "Add Friend: ${people.user.displayName}", Toast.LENGTH_SHORT).show()
                onDataChange(people, PeopleAction.TOGGLE_REQUEST_SENT)
            },
            onAcceptFriendClick = { people ->
                Toast.makeText(requireContext(), "Accept Friend: ${people.user.displayName}", Toast.LENGTH_SHORT).show()
                onDataChange(people, PeopleAction.TOGGLE_REQUEST_RECEIVED_ACCEPT)
            }
        )

        binding.rvPeopleList.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun onDataChange(person: People, action: PeopleAction) {
        val index = peopleList.indexOfFirst { it.user.uid == person.user.uid }

        if (index != -1) {
            val updatedPerson = when (action) {
                PeopleAction.TOGGLE_REQUEST_RECEIVED_ACCEPT -> {
                    peopleList[index].copy(isFriend = true, isRequestReceived = false)
                }
                PeopleAction.TOGGLE_REQUEST_SENT -> {
                    peopleList[index].copy(isRequestSent = !peopleList[index].isRequestSent)
                }
                PeopleAction.TOGGLE_REQUEST_RECEIVED_DECLINE -> {
                    peopleList[index].copy(isRequestReceived = false)
                }
            }

            peopleList[index] = updatedPerson

            handleTabSelection()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    //TODO: các func nên để trong viewModel
    fun generatePeopleList(): MutableList<People> {
        val users = listOf(
            User("1", "user1@example.com", "John Doe", "https://example.com/photo1.jpg", "1234567890", "1990-01-01", true),
            User("2", "user2@example.com", "Jane Smith", "https://example.com/photo2.jpg", "0987654321", "1985-05-15", true),
            User("3", "user3@example.com", "Robert Brown", "https://example.com/photo3.jpg", "1122334455", "2000-10-20", false),
            User("4", "user4@example.com", "Emily Davis", "https://example.com/photo4.jpg", "6677889900", "1992-03-05", true),
            User("5", "user5@example.com", "Michael Wilson", "https://example.com/photo5.jpg", "2233445566", "1988-07-11", false),
            User("6", "user6@example.com", "Olivia Johnson", "https://example.com/photo6.jpg", "5566778899", "1995-09-30", true),
            User("7", "user7@example.com", "William Lee", "https://example.com/photo7.jpg", "4433221100", "1993-12-25", true),
            User("8", "user8@example.com", "Sophia Martinez", "https://example.com/photo8.jpg", "3344556677", "1991-11-18", true),
            User("9", "user9@example.com", "James Taylor", "https://example.com/photo9.jpg", "2233557788", "1994-04-04", false),
            User("10", "user10@example.com", "Isabella Anderson", "https://example.com/photo10.jpg", "9988776655", "1996-06-22", true),
            User("11", "user11@example.com", "David Thomas", "https://example.com/photo11.jpg", "1010101010", "1987-08-15", false),
            User("12", "user12@example.com", "Charlotte White", "https://example.com/photo12.jpg", "1231231234", "2001-02-28", true),
            User("13", "user13@example.com", "Daniel Harris", "https://example.com/photo13.jpg", "1414141414", "1984-12-12", false),
            User("14", "user14@example.com", "Ava Martin", "https://example.com/photo14.jpg", "1515151515", "1999-05-17", true),
            User("15", "user15@example.com", "Lucas Clark", "https://example.com/photo15.jpg", "1616161616", "2002-08-19", true)
        )

        return users.map { user ->
            val isFriend = Random.nextBoolean()
            val isRequestSent = if(isFriend) false else Random.nextBoolean()
            val isRequestReceived = if(isRequestSent||isFriend) false else Random.nextBoolean()
            People(
                user,
                isFriend = isFriend,
                isRequestSent = isRequestSent,
                isRequestReceived = isRequestReceived
            )
        }.sortByGivenNameVietnamese().toMutableList()
    }


    fun List<People>.sortByGivenNameVietnamese(): List<People> {
        return this.sortedBy { people ->
            val givenName = getGivenName(people)
            normalizeVietnamese(givenName).lowercase()
        }
    }

    private fun getGivenName(people: People): String {
        val fullName = people.user.displayName

        return if (fullName != null && fullName.isNotBlank()) {
            val nameParts = fullName.trim().split("\\s+".toRegex())
            nameParts.lastOrNull() ?: ""
        } else {
            people.user.email ?: ""
        }
    }


    private fun normalizeVietnamese(text: String): String {
        return text
            .replace(Regex("[àáảãạăằắẳẵặâầấẩẫậ]"), "a")
            .replace(Regex("[èéẻẽẹêềếểễệ]"), "e")
            .replace(Regex("[ìíỉĩị]"), "i")
            .replace(Regex("[òóỏõọôồốổỗộơờớởỡợ]"), "o")
            .replace(Regex("[ùúủũụưừứửữự]"), "u")
            .replace(Regex("[ỳýỷỹỵ]"), "y")
            .replace("đ", "d")
            .replace(Regex("[ÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬ]"), "A")
            .replace(Regex("[ÈÉẺẼẸÊỀẾỂỄỆ]"), "E")
            .replace(Regex("[ÌÍỈĨỊ]"), "I")
            .replace(Regex("[ÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢ]"), "O")
            .replace(Regex("[ÙÚỦŨỤƯỪỨỬỮỰ]"), "U")
            .replace(Regex("[ỲÝỶỸỴ]"), "Y")
            .replace("Đ", "D")
    }

}