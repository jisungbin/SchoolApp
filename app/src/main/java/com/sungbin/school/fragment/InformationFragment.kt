package com.sungbin.school.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sungbin.school.R
import kotlinx.android.synthetic.main.fragment_information.*


/**
 * Created by SungBin on 2020-05-31.
 */

class InformationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_github.setOnClickListener {
                
        }

        btn_opensource.setOnClickListener {

        }
    }
}