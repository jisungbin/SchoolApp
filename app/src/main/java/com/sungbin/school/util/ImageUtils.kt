package com.sungbin.school.util

import android.content.Context
import android.os.AsyncTask
import android.widget.ImageView
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StorageUtils
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ImageUtils {
    fun download(context: Context, url: String) {
        StorageUtils.createFolder("서령고등학교")
        File("${StorageUtils.sdcard}/서령고등학교/.nomedia")
            .createNewFile()
        val path =
            getDownloadFilePath(
                context
            )
        ImageDownloadTask().execute(path, url)
    }

    fun set(path: String, view: ImageView, context: Context) {
        Glide.set(context, path, view)
        download(context, path)
    }

    fun getDownloadFilePath(context: Context): String{
        return "${StorageUtils.sdcard}/서령고등학교/${DataUtils.readData(context, "room", "3")}.plan"
    }

    private class ImageDownloadTask : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg params: String?): Void? {
            try {
                val imageFile = File(params[0]!!)
                if(imageFile.exists()) imageFile.delete()
                val imgUrl = URL(params[1])
                val conn = imgUrl.openConnection() as HttpURLConnection
                val len = conn.contentLength
                val tmpByte = ByteArray(len)
                val `is` = conn.inputStream
                val fos = FileOutputStream(imageFile)
                var read: Int

                while (true) {
                    read = `is`.read(tmpByte)
                    if (read <= 0) {
                        break
                    }
                    fos.write(tmpByte, 0, read)
                }

                `is`.close()
                fos.close()
                conn.disconnect()
            } catch (e: Exception) {
                StorageUtils.save("서령고등학교/${e.stackTrace[0]}.log", e.toString())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            return
        }

    }
}