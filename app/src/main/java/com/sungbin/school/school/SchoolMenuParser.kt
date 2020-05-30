@file:Suppress("NAME_SHADOWING")

package com.sungbin.school.school

import java.util.*

internal object SchoolMenuParser {

    @Throws(SchoolException::class)
    fun parse(rawData: String): List<SchoolMenu> {
        var rawData = rawData

        if (rawData.isEmpty())
            throw SchoolException("불러온 데이터가 올바르지 않습니다.")

        val monthlyMenu = ArrayList<SchoolMenu>()

        rawData = rawData.replace("\\s+".toRegex(), "")

        val buffer = StringBuilder()

        var inDiv = false

        try {
            var i = 0
            while (i < rawData.length) {
                if (rawData[i] == 'v') {
                    if (inDiv) {
                        buffer.delete(buffer.length - 4, buffer.length)
                        if (buffer.isNotEmpty())
                            monthlyMenu.add(parseDay(buffer.toString()))
                        buffer.setLength(0)
                    } else {
                        i++
                    }
                    inDiv = !inDiv
                } else if (inDiv) {
                    buffer.append(rawData[i])
                }
                i++
            }

            return monthlyMenu

        } catch (e: Exception) {
            throw SchoolException("급식 정보 파싱에 실패했습니다. API를 최신 버전으로 업데이트 해 주세요.")
        }

    }

    private fun parseDay(rawData: String): SchoolMenu {
        var rawData = rawData

        val menu = SchoolMenu()
        rawData = rawData.replace("(석식)", "")
        rawData = rawData.replace("(선)", "")

        val chunk = rawData.split("<br/>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var parsingMode = 0
        val menuStrings = arrayOfNulls<StringBuilder>(3)
        for (i in 0..2)
            menuStrings[i] = StringBuilder()

        for (i in 1 until chunk.size) {

            if (chunk[i].trim { it <= ' ' }.isEmpty())
                continue

            when (chunk[i]) {
                "[조식]" -> {
                    parsingMode = 0
                }
                "[중식]" -> {
                    parsingMode = 1
                }
                "[석식]" -> {
                    parsingMode = 2
                }
            }

            if (menuStrings[parsingMode]!!.isNotEmpty())
                menuStrings[parsingMode]!!.append("\n")
            menuStrings[parsingMode]!!.append(chunk[i])
        }

        menu.breakfast = menuStrings[0].toString()
        menu.lunch = menuStrings[1].toString()
        menu.dinner = menuStrings[2].toString()

        return menu
    }
}