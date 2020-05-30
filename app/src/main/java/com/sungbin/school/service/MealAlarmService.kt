package com.sungbin.school.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.sungbin.school.R
import com.sungbin.school.utils.NotificationUtils
import com.sungbin.sungbintool.StorageUtils

abstract class MealAlarmService : Service() {
    private var mealLoadYear = ""
    private var mealLoadMonth = ""
    private var mealLoadDay = ""

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mealLoadYear = intent.getStringExtra("mealLoadYear")!!
        mealLoadMonth = intent.getStringExtra("mealLoadMonth")!!
        mealLoadDay = intent.getStringExtra("mealLoadDay")!!

        showMealNotification()

        return START_NOT_STICKY
    }

    private fun showMealNotification(){
        NotificationUtils.setGroupName("School Information")
        NotificationUtils.createChannel(this, "Today Meal Alarm", "Today meal notification alarm service.")
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val content = StorageUtils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "파일 오류!")
            content!!.replaceFirst("\n\n", "")

            var showContent = content.split("<$mealLoadYear-$mealLoadMonth-$mealLoadDay>")[1]

            if(showContent.contains("중식")){
                if(getCharNumber(showContent, "<") > 1){
                    showContent = showContent.split("<")[0]
                }
            }
            else showContent = showContent.split(".")[0]

            showContent = showContent.split("\n\n[석식]")[0]
                .split("[중식]")[1]
            showContent = "[중식]$showContent"

            NotificationUtils.showInboxStyleNotification(this, 1, "오늘의 급식", "밑으로 내리면 오늘의 급식을 확인할 수 있습니다.", showContent.split("\n").toTypedArray(), R.drawable.school)
        }
        else {
            NotificationUtils.showNormalNotification(this, 1, "오늘의 급식", "저장된 급식 정보가 없어서 오늘의 급식을 표시할 수 없습니다.", R.drawable.school)
        }
    }

    private fun getCharNumber(str: String, equ: String): Int {
        var count = 0
        for (element in str) {
            if (element.toString() == equ)
                count++
        }
        return count
    }
}