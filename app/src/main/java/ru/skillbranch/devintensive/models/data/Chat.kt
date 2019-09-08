package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.extensions.truncate
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat (
    val id : String,
    val title: String,
    val members : List<User> = listOf(),
    var messages : MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
){

    //@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount(): Int = messages.filter { !it.isReaded }.size

    //@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date? = messages.lastOrNull()?.date

    //@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String?, String?> = when(val lastMessage = messages.lastOrNull()){
        null -> null to null
        is TextMessage -> lastMessage.text?.truncate(128) to lastMessage.from?.firstName
        is ImageMessage -> "${lastMessage.from?.firstName ?: "Кто-то"} - отправил фото" to lastMessage.from?.firstName
        else -> "" to ""
    }

    private fun isSingle() : Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        val user = members.first()
        val lastMessage = lastMessageShort()
        return if (isSingle()) {
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessage.first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline,
                author = lastMessage.second
            )
        } else {
            ChatItem(
                id,
                null,
                Utils.toInitials(user.firstName ?: user.lastName, null) ?: "??",
                title,
                lastMessage.first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessage.second
            )
        }
    }
}

enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}