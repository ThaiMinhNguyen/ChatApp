package com.example.chatapp.domain.data

data class People(
    var user: User,
    var isFriend: Boolean = false,
    var isRequestSent: Boolean = false,
    var isRequestReceived: Boolean = false
) {
    constructor() : this(User(), false, false, false)
}


enum class PeopleAction {
    TOGGLE_REQUEST_RECEIVED_ACCEPT,
    TOGGLE_REQUEST_SENT,
    TOGGLE_REQUEST_RECEIVED_DECLINE
}

sealed class FriendListItem {
    data class Header(val title: String) : FriendListItem()
    data class PersonItem(val people: People) : FriendListItem()
}