package com.sungbin.school.service

import com.google.firebase.messaging.RemoteMessage
import com.sungbin.school.R
import com.sungbin.school.utils.NoticeUtils
import com.sungbin.school.utils.NotificationUtils
import com.sungbin.sungbintool.StorageUtils
import java.text.SimpleDateFormat
import java.util.*

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        NotificationUtils.setGroupName(getString(R.string.app_name))
        NotificationUtils.createChannel(
            applicationContext,
            getString(R.string.app_name),
            getString(R.string.notice_notification)
        )
        val title = remoteMessage.data["title"]!!
        val content = remoteMessage.data["body"]!!
        NotificationUtils.showNormalNotification(
            applicationContext,
            1,
            title,
            content,
            R.drawable.school
        )
        val value = "${getTime()}::$content"
        StorageUtils.createFolder(NoticeUtils.PATH)
        StorageUtils.save("${NoticeUtils.PATH}/$title", value)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun getTime(): String {
        val df = SimpleDateFormat("M월 d일 a hh:mm", Locale.KOREA)
        val date = Date()
        return df.format(date)
    }
}

