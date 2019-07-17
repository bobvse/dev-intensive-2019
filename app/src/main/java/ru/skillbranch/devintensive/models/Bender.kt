package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val validationResult = question.validate(answer)
        if (validationResult != null) return "$validationResult\n${question.question}" to status.color

        return if(question.ansvers.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        } else {
            status = status.nextStatus()
            if (status == Status.NORMAL) {
                question = Question.NAME
                "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            } else {
                "Это неправильный ответ\n${question.question}" to status.color
            }
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val ansvers: List<String>) {
        NAME("Как меня зовут?", listOf("Бендер", "Bender")) {
            override fun nextQuestion(): Question = PROFESSION
            override fun validate(answer: String): String? {
                return if (answer.firstOrNull()?.isUpperCase()?.not() == true) "Имя должно начинаться с заглавной буквы" else null
            }
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun nextQuestion(): Question = MATERIAL
            override fun validate(answer: String): String? {
                return if (answer.firstOrNull()?.isLowerCase()?.not() == true) "Профессия должна начинаться со строчной буквы" else null
            }
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun nextQuestion(): Question = BDAY
            override fun validate(answer: String): String? {
                return if (answer.contains("""\d+""".toRegex())) "Материал не должен содержать цифр" else null
            }
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
            override fun validate(answer: String): String? {
                return if (answer.contains("""\D+""".toRegex())) "Год моего рождения должен содержать только цифры" else null
            }
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(answer: String): String? {
                return if (answer.length != 7 || answer.contains("""\D+""".toRegex())) "Серийный номер содержит только цифры, и их 7" else null
            }
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(answer: String): String? = null
        };

        abstract fun nextQuestion(): Question
        abstract fun validate(answer: String): String?
    }
}