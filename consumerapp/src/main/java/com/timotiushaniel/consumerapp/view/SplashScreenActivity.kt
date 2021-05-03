package com.timotiushaniel.consumerapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.timotiushaniel.consumerapp.R

class SplashScreenActivity : AppCompatActivity() {
    private val splashTime: Long = 1100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, FavoriteActivity::class.java))
            finish()
        }, splashTime)
    }
}