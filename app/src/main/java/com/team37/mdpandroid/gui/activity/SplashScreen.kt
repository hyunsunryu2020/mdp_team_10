package com.team37.mdpandroid.gui.activity
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.team37.mdpandroid.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.team37.mdpandroid.gui.util.ConfigUtil

class SplashScreen : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        currentPage = ConfigUtil.SPLASH_PAGE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAGS_CHANGED
        )
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}