package com.sungbin.school.school

class SchoolMenu {
    var breakfast: String
    var lunch: String
    var dinner: String = "급식이 없습니다"

    init {
        lunch = dinner
        breakfast = lunch
    }

    override fun toString(): String {
        return "\n\n$breakfast\n\n$lunch\n\n$dinner"
    }
}