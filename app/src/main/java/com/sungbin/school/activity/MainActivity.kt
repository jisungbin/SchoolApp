package com.sungbin.school.activity

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.sungbin.permissionrequester.library.PermissionRequester
import com.sungbin.permissionrequester.library.dto.Permission
import com.sungbin.permissionrequester.library.dto.PermissionType
import com.sungbin.school.R
import com.sungbin.school.dto.Type
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var type = Type.STUDENT
    private var room = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       /* if (DataUtils.readData(applicationContext, "type", "null") != "null") {
            finish()
            startActivity(Intent(this, FlameLayout::class.java))
            return
        }*/

        setContentView(R.layout.activity_main)
        tv_title.text = getString(R.string.input_information)

        PermissionRequester
            .with(this)
            .setAppData(
                R.drawable.school,
                getString(R.string.app_name),
                getString(R.string.please_agress_use_permission)
            )
            .addRequiredPermission(
                Permission(
                    PermissionType.STORAGE,
                    getString(R.string.access_storage),
                    getString(R.string.need_for_save_meal)
                )
            )
            .create(1000)

        val rooms = ArrayList<String>()
        for(i in 1..9) rooms.add("${i}반")
        ms_rooms.setItems(rooms)

        ms_rooms.setOnItemSelectedListener { _, _, _, item ->
            room = item.toString().split("반")[0].toInt()
        }

        ts_type.setOnToggleSwitchChangeListener { position, _ ->
            when (position) {
                0 -> {
                    type = Type.STUDENT
                    YoYo.with(Techniques.FadeOut)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                outlinedTextField.visibility = View.INVISIBLE
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationStart(p0: Animator?) {

                            }
                        })
                        .duration(500)
                        .playOn(outlinedTextField)
                }
                else -> { //선생님
                    type = Type.TEACHER
                    outlinedTextField.visibility = View.VISIBLE
                    YoYo.with(Techniques.FadeIn)
                        .duration(500)
                        .playOn(outlinedTextField)
                }
            }
        }

        btn_done.setOnClickListener {
            finish()
            if(type == Type.TEACHER) {
                if(et_pw.text.toString() != getString(R.string.string_password)) {
                    ToastUtils.show(
                        applicationContext,
                        "선생님 비밀번호가 일치하지 않습니다.\n학생으로 가입합니다.",
                        ToastUtils.SHORT,
                        ToastUtils.WARNING
                    )
                    type = Type.STUDENT
                }
            }
            DataUtils.saveData(applicationContext, "type", type.toString())
            DataUtils.saveData(applicationContext, "room", room.toString())
            startActivity(Intent(this, FlameLayout::class.java))
        }

    }
}
