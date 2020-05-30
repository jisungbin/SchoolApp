package com.sungbin.school.school

object Utils {
    fun before(string: String, delimiter: String): String {
        val index = string.indexOf(delimiter)
        return string.substring(0, index)
    }

    fun after(string: String, delimiter: String): String {
        val index = string.indexOf(delimiter)
        return string.substring(index + delimiter.length)
    }
}