package com.sungbin.school.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.License
import com.marcoscg.licenser.LicenserDialog
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
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/sungbin5304/SchoolApp"
                    )
                )
            )
        }

        btn_opensource.setOnClickListener {
            LicenserDialog(activity)
                .setTitle("Opensource License")
                .setCustomNoticeTitle("License for Libraries:")
                .setLibrary(
                    Library("Android Support Libraries",
                    "https://developer.android.com/topic/libraries/support-library/index.html",
                    License.APACHE2)
                )
                .setLibrary(
                    Library(
                        "Android-Toggle-Switch",
                        "https://github.com/BelkaLab/Android-Toggle-Switch",
                        License.MIT
                    )
                )
                .setLibrary(
                    Library("Licenser",
                    "https://github.com/marcoscgdev/Licenser",
                    License.MIT
                    )
                )
                .setLibrary(
                    Library(
                        "Logger",
                        "https://github.com/orhanobut/logger",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Lottie",
                        "https://github.com/airbnb/lottie-android",
                        License.APACHE2
                    )
                )

                .setLibrary(
                    Library(
                        "material components android",
                        "https://github.com/material-components/material-components-android",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "MaterialSpinner",
                        "https://github.com/jaredrummler/MaterialSpinner",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Android View Animations",
                        "https://github.com/daimajia/AndroidViewAnimations",
                        License.MIT
                    )
                )
                .setLibrary(
                    Library(
                        "Firebase",
                        "https://github.com/firebase/quickstart-android",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Glide",
                        "https://github.com/bumptech/glide",
                        License.BSD3
                    )
                )
                .setLibrary(
                    Library(
                        "SweetAlertDialog",
                        "https://github.com/F0RIS/sweet-alert-dialog",
                        License.MIT
                    )
                )
                .setLibrary(
                    Library(
                        "AndroidUtils",
                        "https://github.com/sungbin5304/AndroidUtils",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "AdapterHelper",
                        "https://github.com/sungbin5304/AdapterHelper",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "CrashReporter",
                        "https://github.com/MindorksOpenSource/CrashReporter",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "logger",
                        "https://github.com/orhanobut/logger",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "TedBottomPicker",
                        "https://github.com/ParkSangGwon/TedBottomPicker",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "TedImagePicker",
                        "https://github.com/ParkSangGwon/TedImagePicker",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "PermissionRequester",
                        "https://github.com/sungbin5304/PermissionRequester",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "SmoothBottomBar",
                        "https://github.com/ibrahimsn98/SmoothBottomBar",
                        License.MIT
                    )
                )
                .show()
        }
    }
}