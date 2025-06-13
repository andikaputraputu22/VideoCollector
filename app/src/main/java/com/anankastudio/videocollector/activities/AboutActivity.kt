package com.anankastudio.videocollector.activities

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ActivityAboutBinding
import com.anankastudio.videocollector.utilities.LetterSpacingSpan
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    @Inject
    lateinit var utils: Utils

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupLogo()
        setupDataAbout()
        setupClickListener()
    }

    private fun setupStatusBar() {
        window?.apply {
            statusBarColor = ContextCompat.getColor(this@AboutActivity, R.color.bg_color)
            navigationBarColor = ContextCompat.getColor(this@AboutActivity, R.color.bg_color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                decorView.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                val windowInsetsController = WindowInsetsControllerCompat(this, decorView)
                windowInsetsController.isAppearanceLightStatusBars = false
                windowInsetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    private fun setupLogo() {
        val titleLogo = SpannableString(getString(R.string.app_name_uppercase))
        titleLogo.setSpan(
            LetterSpacingSpan(-0.1f),
            1,
            titleLogo.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.titleLogo.text = titleLogo
    }

    private fun setupDataAbout() {
        binding.versionName.text = utils.getVersionName(this@AboutActivity)
        binding.pexels.text = Html.fromHtml(
            getString(R.string.pexels),
            Html.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setupClickListener() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.pexels.setOnClickListener {
            utils.openExternalBrowser(
                "https://www.pexels.com/",
                this@AboutActivity
            )
        }
    }
}