package com.sungbin.school.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sungbin.school.service.MealAlarmService


class AlarmServiceListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mealLoadYear = intent.getStringExtra("mealLoadYear")
        val mealLoadMonth = intent.getStringExtra("mealLoadMonth")
        val mealLoadDay = intent.getStringExtra("mealLoadDay")
        val i = Intent(context, MealAlarmService::class.java)
            .putExtra("mealLoadYear", mealLoadYear!!)
            .putExtra("mealLoadMonth", mealLoadMonth!!)
            .putExtra("mealLoadDay", mealLoadDay!!)
        context.startService(i)
    }
}