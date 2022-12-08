package com.generator.wildfyre.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.generator.wildfyre.R
import com.generator.wildfyre.defaultSettings
import com.generator.wildfyre.model.DefaultSettings
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            startActivity<MainActivity>()
            finish()
        },2000)

    }
}