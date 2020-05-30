package com.sungbin.school.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.storage.FirebaseStorage
import com.sungbin.school.R
import com.sungbin.school.utils.Glide
import com.sungbin.school.utils.ImageUtils
import com.sungbin.sungbintool.DataUtils
import kotlinx.android.synthetic.main.fragment_plan.*
import java.io.File


/**
 * Created by SungBin on 2020-05-27.
 */

class PlanFragment : Fragment() {

    companion object {
        lateinit var imageView: ImageView
        lateinit var lottieView: LottieAnimationView
        lateinit var textView: TextView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imageView = iv_plan
        lottieView = lav_empty
        textView = tv_empty

        val path = ImageUtils.getDownloadFilePath(context!!)
        if (File(path).exists()) {
            Glide.set(context!!, BitmapFactory.decodeFile(path), imageView)
        }
        else {
            val riversRef = FirebaseStorage.getInstance().reference
                .child("${DataUtils.readData(context!!, "room", "3")}.plan")
            riversRef.downloadUrl.addOnSuccessListener { link ->
                ImageUtils.set(link.toString(), imageView, context!!)
            }
            riversRef.downloadUrl.addOnFailureListener {
                lottieView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                imageView.visibility = View.GONE
            }
        }
    }
}