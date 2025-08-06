package com.example.chatapp.utils

import com.example.chatapp.domain.data.FriendListItem
import com.example.chatapp.domain.data.People

object FriendListUtils {

    fun createListWithoutHeaders(people: List<People>): List<FriendListItem> {
        return people.map { FriendListItem.PersonItem(it) }
    }

    fun createListWithHeaders(people: List<People>, tabIndex: Int): List<FriendListItem> {
        val result = mutableListOf<FriendListItem>()
        
        when (tabIndex) {
            0, 1 -> {
                val sortedPeople = people.sortByGivenNameVietnamese()
                var currentLetter: Char? = null
                
                sortedPeople.forEach { person ->
                    val firstLetter = getLastNameFirstLetter(person)
                    
                    if (currentLetter != firstLetter) {
                        result.add(FriendListItem.Header(firstLetter.toString()))
                        currentLetter = firstLetter
                    }
                    
                    result.add(FriendListItem.PersonItem(person))
                }
            }
            2 -> {
                val filteredPeople = people.filter { 
                    !it.isFriend && (it.isRequestSent || it.isRequestReceived)
                }
                
                val sortedPeople = filteredPeople.sortedWith(compareBy<People> { person ->
                    if (person.isRequestReceived) 0 else 1
                }.thenBy { person ->
                    getGivenName(person).lowercase()
                })
                
                var currentIsReceived: Boolean? = null
                
                sortedPeople.forEach { person ->
                    if (currentIsReceived != person.isRequestReceived) {
                        val headerTitle = if (person.isRequestReceived) "LỜI MỜI KẾT BẠN" else "ĐÃ GỬI KẾT BẠN"
                        result.add(FriendListItem.Header(headerTitle))
                        currentIsReceived = person.isRequestReceived
                    }
                    
                    result.add(FriendListItem.PersonItem(person))
                }
            }
        }
        
        return result
    }

    private fun getLastNameFirstLetter(people: People): Char {
        val fullName = people.user.displayName
        return if (fullName != null && fullName.isNotBlank()) {
            val nameParts = fullName.trim().split("\\s+".toRegex())
            val lastName = nameParts.lastOrNull() ?: ""
            lastName.firstOrNull()?.uppercaseChar() ?: 'Z'
        } else {
            people.user.email?.firstOrNull()?.uppercaseChar() ?: 'Z'
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

    fun List<People>.sortByGivenNameVietnamese(): List<People> {
        return this.sortedBy { people ->
            val givenName = getGivenName(people)
            normalizeVietnamese(givenName).lowercase()
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