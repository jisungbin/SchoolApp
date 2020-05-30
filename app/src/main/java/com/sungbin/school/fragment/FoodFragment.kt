package com.sungbin.school.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.sungbin.school.R
import com.sungbin.school.activity.FlameLayout
import com.sungbin.school.school.School
import com.sungbin.school.school.SchoolMenu
import com.sungbin.school.listener.OnSwipeListener
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.fragment_meal.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor


class FoodFragment : Fragment() {

    private var mealLoadDay:Int? = null
    private var mealLoadYear:Int? = null
    private var mealLoadMonth:Int? = null

    private lateinit var nextDay: ImageButton
    private lateinit var preDay: ImageButton
    private lateinit var info: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_meal, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        StorageUtils.createFolder("서령고등학교")

        info = (context as FlameLayout).info!!
        preDay = (context as FlameLayout).preDay!!
        nextDay = (context as FlameLayout).nextDay!!

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            info.text = "$year-${(month + 1)}-$day"
            mealLoadMonth = month + 1
            mealLoadDay = day
            mealLoadYear = year
            MealTask().execute()
        }

        preDay.visibility = View.VISIBLE
        nextDay.visibility = View.VISIBLE

        mealLoadMonth = getTime("MM").toInt()
        mealLoadDay = getTime("dd").toInt()
        mealLoadYear = getTime("yyyy").toInt()

        info.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

        MealTask().execute()

        info.setOnClickListener {
            val year = info.text.split("-")[0].toInt()
            val month = info.text.split("-")[1].toInt() - 1
            val day = info.text.split("-")[2].toInt()

            val dialog = DatePickerDialog(context!!, dateSetListener, year, month, day)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
        }

        nextDay.setOnClickListener { //날짜 앞으로 가기
            var year = info.text.split("-")[0].toInt()
            var month = info.text.split("-")[1].toInt()
            var day = info.text.split("-")[2].toInt()
            val lastDay = (getAllDay(year, month + 1, day) - getAllDay(year, month, day)).toInt()

            if (day == lastDay){ //막날에 다음날 눌렀을때
                if (month == 12){
                    year += 1
                    month = 1
                    day = 1
                }
                else {
                    month += 1
                    day = 1
                }
            }
            else {
                day += 1
            }

            mealLoadYear = year
            mealLoadMonth = month
            mealLoadDay = day

            info.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

            MealTask().execute()
        }

        preDay.setOnClickListener { //날짜 뒤로가기
            var year = info.text.split("-")[0].toInt()
            var month = info.text.split("-")[1].toInt()
            var day = info.text.split("-")[2].toInt()

            if (day == 1){ //1일에 뒤로가기 -> 전달 막날로
                if (month == 1){
                    month = 12
                    year -= 1
                }
                else {
                    month -= 1
                }

                day = 1 //임시값

                val lastDay = (getAllDay(year, month + 1, day) - getAllDay(year, month, day)).toInt()

                mealLoadYear = year
                mealLoadMonth = month
                mealLoadDay = lastDay

                info.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

                MealTask().execute()
            }
            else { //1일이 아닌 그 다음 날 부터 뒤로가기
                day -= 1

                mealLoadYear = year
                mealLoadMonth = month
                mealLoadDay = day

                info.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

                MealTask().execute()
            }
        }

        meal.setOnTouchListener(object : OnSwipeListener(context!!) {
            override fun onSwipeLeftToRight() {
                preDay.performClick()
            }

            override fun onSwipeRightToLeft() {
                nextDay.performClick()
            }
        })

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 45)
        calendar.set(Calendar.SECOND, 0)

        val alarmIntent = Intent("MealAlarmServiceListener")
            .putExtra("mealLoadYear", mealLoadYear!!)
            .putExtra("mealLoadMonth", mealLoadMonth!!)
            .putExtra("mealLoadDay", mealLoadDay!!)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
        else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun getTime(type: String): String{
        val sdf = SimpleDateFormat(type, Locale.KOREA)
        return sdf.format(Date(System.currentTimeMillis()))
    }

    @SuppressLint("StaticFieldLeak")
    private inner class MealTask : AsyncTask<Void?, Void?, Void?>() {
        var textMenu: String? = null
        var menu: List<SchoolMenu>? = null
        var dialog: SweetAlertDialog? = null
        var content = ""
        var isText = false

        override fun onPreExecute() {
            dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            dialog!!.progressHelper.barColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
            dialog!!.titleText = getString(R.string.loading_meal)
            dialog!!.setCancelable(false)
            if(StorageUtils.read("서령고등학교/$mealLoadYear-$mealLoadMonth.data", "null") == "null") {
                 dialog!!.show()
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            if (StorageUtils.read("서령고등학교/$mealLoadYear-$mealLoadMonth.data", "null") != "null") {
                textMenu = StorageUtils.read("서령고등학교/$mealLoadYear-$mealLoadMonth.data", "")
                isText = true
            }
            else {
                menu = School(School.Type.HIGH, School.Region.CHUNGNAM, "N100000176")
                    .getMonthlyMenu(mealLoadYear!!, mealLoadMonth!!)
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            if(!isText) {
                for (i in menu!!.indices) {
                    var str = "<${mealLoadYear.toString()}-${mealLoadMonth.toString()}-${(i + 1)}>${menu!![i]}"
                    if(!str.contains("중식"))
                        str = "<${mealLoadYear.toString()}-${mealLoadMonth.toString()}-${(i + 1)}>\n\n해당 일에는 급식이 없거나,\n학교에서 나이스에 급식을 업로드 하지 않았습니다."
                    content += "\n\n" + str.replaceFirst("\n", "")
                }

                StorageUtils.save("서령고등학교/$mealLoadYear-$mealLoadMonth.data", content)
                ToastUtils.show(context!!, getString(R.string.saved_meal_data), ToastUtils.SHORT, ToastUtils.SUCCESS)
            }
            else content = textMenu!!

            content.replaceFirst("\n\n", "")
            var showContent = content.split("<${mealLoadYear.toString()}-${mealLoadMonth.toString()}-${mealLoadDay.toString()}>")[1]

            if(showContent.contains("중식")){
                if(getCharNumber(showContent, "<") > 1){
                    showContent = showContent.split("<")[0]
                }
            }
            else showContent = showContent.split(".")[0]

            meal.text = showContent

            if(dialog!!.isShowing) dialog!!.cancel()
        }

    };

    private fun getAllDay(y:Int, m:Int, d:Int): Double{
        var month = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (y % 4 == 0) {
            month = arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }
        var amoon = 0
        for(i in 1 until m){
            amoon += month[i-1]
        }
        return (floor(365.24253716252537 * y) + amoon + d) - 366
    }


    fun getCharNumber(str: String, equ:String): Int {
        var count = 0
        for (element in str) {
            if (element.toString() == equ)
                count++
        }
        return count
    }

}