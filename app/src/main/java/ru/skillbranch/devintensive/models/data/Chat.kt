package ru.skillbranch.devintensive.models.data


import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.repositories.ChatRepository
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id:String,
    val title:String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount():Int{
        var messagesUnread = messages.filter { !it.isReaded }
        return messagesUnread.count()
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date?{
        return messages.lastOrNull()?.date
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String?, String?> = when(val lastMessage = messages.lastOrNull()){
        is TextMessage -> lastMessage.text to "${lastMessage.from?.firstName}"
        is ImageMessage ->"${lastMessage.from?.firstName} - отправил фото" to "${lastMessage.from?.firstName}"
        else -> "Сообщений еще нет" to ""

    }



    private fun isSingle():Boolean = members.size == 1
    fun toChatItem(): ChatItem {
           return if(isSingle()&& !isArchived) {
               val user = members.first()
               ChatItem(
                   id,
                   user.avatar,
                   Utils.toInitials(user.firstName, user.lastName) ?: "??",
                   "${user.firstName ?: ""} ${user.lastName ?: ""}",
                   lastMessageShort().first,
                   unreadableMessageCount(),
                   lastMessageDate()?.shortFormat(),
                   user.isOnline
               )
           }else if (!isArchived){
               ChatItem(
                   id,
                   null,
                   "",
                   title,
                   lastMessageShort().first,
                   unreadableMessageCount(),
                   lastMessageDate()?.shortFormat(),
                   false,
                   ChatType.GROUP,
                   lastMessageShort().second
               )
           }else{
               val chatsLast =  ChatRepository.loadChats().value!!.filter { it.isArchived }.sortedBy { it.lastMessageDate() }
               ChatItem(
                   "-1",
                   null,
                   "",
                   "Архив чатов",
                   chatsLast.last().lastMessageShort().first,
                   chatsLast.sumBy { it.unreadableMessageCount()},
                   chatsLast.last().lastMessageDate()?.shortFormat(),
                   false,
                   ChatType.ARCHIVE,
                   "@${chatsLast.last().lastMessageShort().second}"
               )
           }
    }
    fun toChatItemArchive(): ChatItem {
            val user = members.first()
            return ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
    }
}

enum class ChatType{
    SINGLE,
    GROUP,
    ARCHIVE
}


