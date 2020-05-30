package com.sungbin.school.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.sungbin.school.R
import com.sungbin.school.dto.Type
import com.sungbin.school.fragment.*
import com.sungbin.school.utils.FirebaseUtils
import com.sungbin.school.utils.ImageUtils
import com.sungbin.school.utils.NoticeUtils
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StorageUtils.getFileSize
import com.sungbin.sungbintool.ToastUtils
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_frame.*
import java.io.File
import java.net.URLDecoder
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow


/**
 * Created by SungBin on 2020-05-26.
 */

class FlameLayout : AppCompatActivity() {

    private val fragmentManager: FragmentManager = this.supportFragmentManager

    var nextDay: ImageButton? = null
    var preDay: ImageButton? = null
    var info: TextView? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        StorageUtils.createFolder(NoticeUtils.PATH)
        FirebaseUtils.subscribe("notice")

        info = findViewById(R.id.info)
        preDay = findViewById(R.id.pre_day)
        nextDay = findViewById(R.id.next_day)

        fragmentManager.beginTransaction().add(
            R.id.framelayout,
            FoodFragment()
        ).commit()

        fab.hide()

        bottombar.onItemSelected = {
            val fragmentTransaction = fragmentManager.beginTransaction()
            when (it) {
                0 -> { //급식
                    fragmentTransaction.replace(
                        R.id.framelayout,
                        FoodFragment()
                    ).commit()
                    fab.hide()
                    toolbar_layout.visibility = View.VISIBLE
                    toolbar_layout2.visibility = View.GONE
                    toolbar_title.text = getString(R.string.string_meal)
                }
                1 -> { //시간표
                    tv_title.text = getString(R.string.string_plan)
                    toolbar_layout.visibility = View.GONE
                    toolbar_layout2.visibility = View.VISIBLE
                    fragmentTransaction.replace(
                        R.id.framelayout,
                        PlanFragment()
                    ).commit()
                    fab.show()
                    fab.setOnClickListener {
                        ToastUtils.show(
                            applicationContext,
                            getString(R.string.choose_upload_plan),
                            ToastUtils.SHORT,
                            ToastUtils.INFO
                        )
                        TedBottomPicker.with(this)
                            .show { url ->
                                val link = URLDecoder.decode(url.toString().replaceFirst("file:///", ""), "UTF-8")
                                val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                                pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext,
                                    R.color.colorAccent)
                                pDialog.titleText = getString(R.string.upload_plan)
                                pDialog.setCancelable(false)
                                pDialog.show()
                                val file = Uri.fromFile(File(link))
                                val riversRef = FirebaseStorage.getInstance().reference
                                    .child("${DataUtils.readData(applicationContext, "room", "3")}.plan")
                                val uploadTask = riversRef.putFile(file)
                                uploadTask.addOnFailureListener { ex ->
                                    val exception = ex as StorageException
                                    pDialog.titleText = getString(R.string.upload_fail)
                                    pDialog.contentText = exception.toString()
                                    pDialog.showCancelButton(true)
                                    pDialog.setCancelButton(getString(R.string.string_close), null)
                                }.addOnProgressListener { taskSnapshot ->
                                    pDialog.contentText =
                                        "${getFileSize(taskSnapshot.bytesTransferred.toInt())} / ${getFileSize(
                                            taskSnapshot.totalByteCount.toInt()
                                        )}"
                                }.addOnSuccessListener {
                                    riversRef.downloadUrl.addOnSuccessListener { link ->
                                        ImageUtils.set(link.toString(), PlanFragment.imageView, applicationContext)
                                        pDialog.cancel()
                                        ToastUtils.show(
                                            applicationContext,
                                            getString(R.string.success_add_plan),
                                            ToastUtils.SHORT,
                                            ToastUtils.SUCCESS
                                        )
                                    }
                                    riversRef.downloadUrl.addOnFailureListener { ex ->
                                        val exception = ex as StorageException
                                        pDialog.titleText = getString(R.string.fail_get_download_link)
                                        pDialog.contentText = exception.toString()
                                        pDialog.showCancelButton(true)
                                        pDialog.setCancelButton(getString(R.string.string_close), null)
                                    }
                                }
                            }
                    }
                }
                2 -> { //학사일정
                    toolbar_title.text = getString(R.string.string_school_plan)
                    toolbar_layout.visibility = View.VISIBLE
                    toolbar_layout2.visibility = View.GONE
                    fragmentTransaction.replace(
                        R.id.framelayout,
                        SchoolPlanFragment()
                    ).commit()
                    fab.hide()
                }
                3 -> { //공지
                    tv_title.text = getString(R.string.string_notice)
                    toolbar_layout.visibility = View.GONE
                    toolbar_layout2.visibility = View.VISIBLE
                    fragmentTransaction.replace(
                        R.id.framelayout,
                        NoticeFragment()
                    ).commit()
                    if(DataUtils.readData(applicationContext, "type", Type.STUDENT.toString()).toInt()
                        == Type.TEACHER) {
                        fab.show()
                    }
                    else {
                        fab.hide()
                    }
                    fab.show()
                    fab.setOnClickListener {
                        val layout = LayoutInflater
                            .from(applicationContext)
                            .inflate(R.layout.view_add_notice, null, false)
                        val alert = BottomSheetDialog(this)
                        alert.setContentView(layout)
                        alert.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                        val title = layout.findViewById<TextInputEditText>(R.id.et_title)
                        val content = layout.findViewById<TextInputEditText>(R.id.et_content)
                        val push = layout.findViewById<Button>(R.id.btn_done)
                        push.setOnClickListener {
                            FirebaseUtils.sendNotiToFcm(
                                title.text.toString(),
                                content.text.toString(),
                                "notice"
                            )
                            ToastUtils.show(
                                applicationContext,
                                getString(R.string.done_push_notice),
                                ToastUtils.SHORT,
                                ToastUtils.SUCCESS
                            )
                            alert.cancel()
                        }
                        alert.show()
                    }
                }
                else -> { //설정
                    tv_title.text = getString(R.string.string_setting)
                    toolbar_layout.visibility = View.GONE
                    toolbar_layout2.visibility = View.VISIBLE
                    fragmentTransaction.replace(
                        R.id.framelayout,
                        InformationFragment()
                    ).commit()
                    fab.hide()
                }
            }
        }
    }

    private fun getFileSize(size: Int): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups =
            (log10(size.toDouble()) / log10(1000.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1000.0.pow(digitGroups.toDouble())
        ).toString() + " " + units[digitGroups]
    }

}