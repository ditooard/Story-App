package com.ditooard.aplikasistoryapp.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ditooard.aplikasistoryapp.databinding.ActivitySplashScreenBinding
import com.ditooard.aplikasistoryapp.ui.home.HomeActivity
import com.ditooard.aplikasistoryapp.ui.main.MainActivity
import com.ditooard.aplikasistoryapp.ui.main.UserViewModel
import com.ditooard.aplikasistoryapp.ui.main.UserPreferences
import com.ditooard.aplikasistoryapp.ui.main.dataStore
import com.ditooard.aplikasistoryapp.viewmodel.CustomViewModelFactory

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = UserPreferences.getInstance(dataStore)
        val loginViewModel = ViewModelProvider(this, CustomViewModelFactory(preferences))[UserViewModel::class.java]

        loginViewModel.getSessionLogin().observe(this) { isLoggedIn ->
            startSplashScreenAnimation(isLoggedIn)
        }
    }

    private fun startSplashScreenAnimation(isLoggedIn: Boolean) {
        val fadeInAnimation = ObjectAnimator.ofFloat(binding.bottomSplashScreen, View.ALPHA, 1f).apply {
            duration = 2000
        }

        AnimatorSet().apply {
            playTogether(fadeInAnimation)
            start()
        }

        val nextActivity = if (isLoggedIn) HomeActivity::class.java else MainActivity::class.java

        binding.splashImage.animate()
            .setDuration(3000)
            .alpha(0f)
            .withEndAction {
                startActivity(Intent(this@SplashScreenActivity, nextActivity))
                finish()
            }
    }
}
