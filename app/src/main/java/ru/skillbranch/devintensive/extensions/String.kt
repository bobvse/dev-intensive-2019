package ru.skillbranch.devintensive.extensions

fun String.truncate(count: Int = 16) : String {
    val processedString = this.trim()
    return if (processedString.length <= count) processedString
            else processedString.substring(0, count).trim() + "..."
}

fun String.stripHtml() : String {
    return this.replace("<[^<]*?>|&#\\d+;".toRegex(), "").replace("\\s+".toRegex(), " ")
}