package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(val id: String,
           var firstName: String?,
           var lastName: String?,
           var avatar: String?,
           var rating: Int = 0,
           var respect: Int = 0,
           var lastVisit: Date? = Date(),
           var isOnline: Boolean = false) {

    constructor(id: String, firstName: String?, lastName: String?) : this(
            id = id,
            firstName = firstName,
            lastName = lastName,
            avatar = null
    )

    constructor(id: String) : this(id, "John", "Dou")

    init {
        println("It's Alive!!! \n${if (lastName == "Dou") "His name id $firstName $lastName" else "And his name is $firstName $lastName!!!"}")
    }

    fun printMe() =
            println("""
        firstName: $firstName
        lastName :$lastName
        avatar : $avatar
        rating :$rating
        respect : $respect
        lastVisit : $lastVisit
        isOnline : $isOnline
        """.trimIndent())

    companion object Factory {
        private var lastId: Int = -1

        fun makeUser(fullName: String?): User {
            lastId++

            var (firstName, lastName) = Utils.parseFullName(fullName)

            if (fullName == "" || fullName == " ") {
                firstName = null
                lastName = null
            }


            return User(id = "$lastId", firstName = firstName, lastName = lastName)
        }

    }

}