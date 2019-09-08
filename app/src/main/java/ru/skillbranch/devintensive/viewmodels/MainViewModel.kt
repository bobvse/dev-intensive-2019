package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository



class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        val archived = chats
            .filter { it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.lastMessageDate }
        if (archived.count() < 1) {
            return@map chats
                .map { it.toChatItem() }
                .sortedBy { it.id.toInt() }
        } else {
            val archiveList = mutableListOf<ChatItem>()
            archiveList.add(0, archived.last())
            archiveList.addAll((chats.filter { !it.isArchived }.map { it.toChatItem() }))
            return@map archiveList
        }
    }




    fun getChatData() : LiveData<List<ChatItem>>{
            val result = MediatorLiveData<List<ChatItem>>()

            val filterF = {
                val queryStr = query.value!!
                val chats = chats.value!!

                result.value = if(queryStr.isEmpty()) chats
                else chats.filter { it.title.contains(queryStr, true) }
            }

            result.addSource(chats) {filterF.invoke()}
            result.addSource(query) {filterF.invoke()}
             return result
    }


    fun addToArchive(chatid: String) {
        val chat = chatRepository.find(chatid)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatid: String){
        val chat = chatRepository.find(chatid)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text

    }
}