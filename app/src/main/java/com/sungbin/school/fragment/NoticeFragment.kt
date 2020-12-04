package com.sungbin.school.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sungbin.recyclerviewadaptermaker.library.AdapterHelper
import com.sungbin.school.R
import com.sungbin.school.util.NoticeUtils
import kotlinx.android.synthetic.main.fragment_notice.*

/**
 * Created by SungBin on 2020-05-26.
 */

class NoticeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val list = NoticeUtils.getList()

        if (list.isEmpty()) {
            rv_notices.visibility = View.GONE
            lav_empty.visibility = View.VISIBLE
            tv_empty.visibility = View.VISIBLE
        } else {
            rv_notices.visibility = View.VISIBLE
            lav_empty.visibility = View.GONE
            tv_empty.visibility = View.GONE
            AdapterHelper
                .with(rv_notices)
                .bindLayout(R.layout.view_notice_list)
                .addViewBindListener { item, view, position ->
                    val value = item[position].toString().split("::")
                    val layout = view as LinearLayout
                    layout.findViewById<TextView>(R.id.tv_title).text = value[0]
                    layout.findViewById<TextView>(R.id.tv_time).text = value[1]
                    layout.findViewById<TextView>(R.id.tv_content).text = value[2]

                    if(item[position] == item[item.size-1]) {
                        layout.findViewById<View>(R.id.v_line).visibility = View.GONE
                    }
                }
                .create(list)
            rv_notices.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }
}