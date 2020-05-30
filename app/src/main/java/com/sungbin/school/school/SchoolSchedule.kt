package com.sungbin.school.school

class SchoolSchedule {
    var schedule: String

    constructor() {
        schedule = ""
    }

    constructor(schedule: String) {
        this.schedule = schedule
    }

    override fun toString(): String {
        return schedule
    }
}