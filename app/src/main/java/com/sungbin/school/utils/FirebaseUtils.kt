package com.sungbin.school.utils


import android.content.Context
import android.util.Log
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object FirebaseUtils {
    private const val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
    private const val SERVER_KEY =
        "AAAAU0RK-8s:APA91bG594oFMP-_RGJlJtnrwo10SAWGZEXgCCHPZ3DCsbVrun0Zj2pAVxcd97wLJywsw-VwjdvYj341JGrxMcf1DV7W0iBkVo4blbcyZFVWjQkTD9YTaiLkM4JAXrwwSNp973sBghBT"

    fun subscribe(topic: String){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    fun unSubscribe(topic: String){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    fun sendNotiToFcm(title: String, message: String, to: String) {
        Thread {
            try {
                val root = JSONObject()
                val notification = JSONObject()
                notification.put("body", message)
                notification.put("title", title)
                root.put("data", notification)
                root.put("to", "/topics/$to")

                val url = URL(FCM_MESSAGE_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.doInput = true
                conn.addRequestProperty("Authorization", "key=$SERVER_KEY")
                conn.setRequestProperty("Accept", "application/json")
                conn.setRequestProperty("Content-type", "application/json")
                val os = conn.outputStream
                os.write(root.toString().toByteArray(charset("utf-8")))
                os.flush()
                conn.responseCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}