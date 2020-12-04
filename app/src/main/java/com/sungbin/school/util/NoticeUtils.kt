package com.sungbin.school.util

import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StorageUtils.sdcard
import java.io.File

object NoticeUtils {
    const val PATH = "서령고등학교/notice"

    fun getList(): ArrayList<Any>{
        val array = ArrayList<Any>()
        val list = File("$sdcard/$PATH").listFiles()
        if(list != null) {
            for (element in list) {
                val title = element.name
                val data = StorageUtils.read("$PATH/$title", "")!!.split("::")
                val value = "$title::${data[0]}::${data[1]}"
                array.add(value)
            }
        }
        return array
    }
}