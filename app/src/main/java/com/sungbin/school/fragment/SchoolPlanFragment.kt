package com.sungbin.school.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.AsyncTask
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
import com.sungbin.school.listener.OnSwipeListener
import com.sungbin.school.school.SchoolSchedule
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.fragment_meal.*
import java.text.SimpleDateFormat
import java.util.*


class SchoolPlanFragment : Fragment() {

    private var planLoadYear:Int? = null
    private var planLoadMonth:Int? = null

    private lateinit var nextDay: ImageButton
    private lateinit var preDay: ImageButton
    private lateinit var info: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_meal, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        StorageUtils.createFolder("서령고등학교/plan")

        info = (context as FlameLayout).info!!
        preDay = (context as FlameLayout).preDay!!
        nextDay = (context as FlameLayout).nextDay!!

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            info.text = "$year-${(month + 1)}-$day"
            planLoadMonth = month + 1
            planLoadYear = year
            PlanTask().execute()
        }

        preDay.visibility = View.VISIBLE
        nextDay.visibility = View.VISIBLE

        planLoadMonth = getTime("MM").toInt()
        planLoadYear = getTime("yyyy").toInt()

        info.text ="$planLoadYear-$planLoadMonth"

       PlanTask().execute()

        info.setOnClickListener {
            val year = info.text.split("-")[0].toInt()
            val month = info.text.split("-")[1].toInt() - 1

            val dialog = DatePickerDialog(context!!, dateSetListener, year, month, getTime("dd").toInt())
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
        }

        nextDay.setOnClickListener { //날짜 앞으로 가기
            planLoadMonth = planLoadMonth!! + 1
            if(planLoadMonth!! > 12) {
                planLoadYear = planLoadYear!! + 1
                planLoadMonth = 1
            }

            info.text ="$planLoadYear-$planLoadMonth"

            PlanTask().execute()
        }

        preDay.setOnClickListener { //날짜 뒤로가기
            planLoadMonth = planLoadMonth!! - 1
            if(planLoadMonth!! < 1) {
                planLoadYear = planLoadYear!! - 1
                planLoadMonth = 12
            }

            info.text ="$planLoadYear-$planLoadMonth"

            PlanTask().execute()
        }

        meal.setOnTouchListener(object : OnSwipeListener(context!!) {
            override fun onSwipeLeftToRight() {
                preDay.performClick()
            }

            override fun onSwipeRightToLeft() {
                nextDay.performClick()
            }
        })
    }

    private fun getTime(type: String): String{
        val sdf = SimpleDateFormat(type, Locale.KOREA)
        return sdf.format(Date(System.currentTimeMillis()))
    }

    @SuppressLint("StaticFieldLeak")
    private inner class PlanTask : AsyncTask<Void?, Void?, Void?>() {
        var textMenu: String? = null
        var menu: List<SchoolSchedule>? = null
        var dialog: SweetAlertDialog? = null
        var content = ""
        var isText = false

        override fun onPreExecute() {
            dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            dialog!!.progressHelper.barColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
            dialog!!.titleText = getString(R.string.loading_plan)
            dialog!!.setCancelable(false)
            if(StorageUtils.read("서령고등학교/plan/$planLoadYear-$planLoadMonth.data", "null") == "null") {
                dialog!!.show()
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            if (StorageUtils.read("서령고등학교/plan/$planLoadYear-$planLoadMonth.data", "null") != "null") {
                textMenu = StorageUtils.read("서령고등학교/plan/$planLoadYear-$planLoadMonth.data", "")
                isText = true
            }
            else {
                menu = School(School.Type.HIGH, School.Region.CHUNGNAM, "N100000176")
                    .getMonthlySchedule(planLoadYear!!, planLoadMonth!!)!!
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            if(!isText) {
                for((index, element) in menu!!.withIndex()){
                    if(element.schedule.isNotBlank()){
                        content = "$content\n${index+1}일 : ${element.schedule}"
                    }
                }
                StorageUtils.save("서령고등학교/plan/$planLoadYear-$planLoadMonth.data", content)
                ToastUtils.show(context!!, getString(R.string.saved_plan_data), ToastUtils.SHORT, ToastUtils.SUCCESS)
            }
            else content = textMenu!!

            meal.text = content

            if(dialog!!.isShowing) dialog!!.cancel()
        }

    }

}