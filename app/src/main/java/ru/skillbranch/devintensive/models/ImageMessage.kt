package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.extensions.humanizeDiff
import java.util.*

class ImageMessage(from: User?, chat: Chat, isIncoming: Boolean=false, date: Date,var image:String) : BaseMessage(from, chat, isIncoming, date) {
    override fun formatMessage(): String = "id:${from?.firstName} +" +
            " ${if (isIncoming) "получил" else "отправил"} изображение \"$image\" ${date.humanizeDiff()}"
}