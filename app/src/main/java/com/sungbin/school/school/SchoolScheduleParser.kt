@file:Suppress("NAME_SHADOWING")

package com.sungbin.school.school


import java.util.*
import java.util.regex.Pattern

internal object SchoolScheduleParser {

    private var schedulePattern: Pattern? = null
    @Throws(SchoolException::class)
    fun parse(rawData: String): List<SchoolSchedule> {
        var rawData = rawData

        if (schedulePattern == null) {
            schedulePattern = Pattern.compile("<strong></strong>")
        }

        if (rawData.isEmpty())
            throw SchoolException("불러온 데이터가 올바르지 않습니다.")

        val monthlySchedule = ArrayList<SchoolSchedule>()

        rawData = rawData.replace("\\s+".toRegex(), "")

        val chunk = rawData.split("textL\">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            for (i in 1 until chunk.size) {
                var trimmed = Utils.before(chunk[i], "</div>")
                val date = Utils.before(Utils.after(trimmed, ">"), "</em>")

                if (date.isEmpty()) continue

                val schedule = StringBuilder()
                while (trimmed.contains("<strong>")) {
                    val name = Utils.before(Utils.after(trimmed, "<strong>"), "</strong>")
                    schedule.append(name)
                    schedule.append("\n")
                    trimmed = Utils.after(trimmed, "</strong>")
                }
                monthlySchedule.add(SchoolSchedule(schedule.toString()))
            }
            return monthlySchedule

        } catch (e: Exception) {
            throw SchoolException("학사일정 정보 파싱에 실패했습니다. API를 최신 버전으로 업데이트 해 주세요.")
        }

    }

}