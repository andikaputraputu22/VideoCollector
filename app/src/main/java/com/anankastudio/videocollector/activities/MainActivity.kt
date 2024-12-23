package com.anankastudio.videocollector.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ActivityMainBinding
import com.anankastudio.videocollector.fragments.FragmentExplore
import com.anankastudio.videocollector.fragments.FragmentFavorite
import com.anankastudio.videocollector.fragments.FragmentHome

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeFragment = FragmentHome()
    private val exploreFragment = FragmentExplore()
    private val favoriteFragment = FragmentFavorite()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(homeFragment)

        setupStatusBar()
        setupBottomNavigation()
    }

    private fun setupStatusBar() {
        window?.apply {
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.bg_color)
            navigationBarColor = ContextCompat.getColor(this@MainActivity, R.color.bg_color)
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

    private fun setupBottomNavigation() {
        binding.bottomNavigation.onItemSelected = { position ->
            when(position) {
                0 -> replaceFragment(homeFragment)
                1 -> replaceFragment(exploreFragment)
                2 -> replaceFragment(favoriteFragment)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}