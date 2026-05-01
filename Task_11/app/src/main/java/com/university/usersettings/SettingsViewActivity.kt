package com.university.usersettings

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton

class SettingsViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Saved Settings"

        val tvLastSaved : TextView = findViewById(R.id.tvLastSaved)
        val tvNoSettings : TextView = findViewById(R.id.tvNoSettings)
        val tvTheme : TextView = findViewById(R.id.tvTheme)
        val tvNotif : TextView = findViewById(R.id.tvNotif)
        val tvLanguage : TextView = findViewById(R.id.tvLanguage)
        val tvFontSize : TextView = findViewById(R.id.tvFontSize)
        val tvStudentName : TextView = findViewById(R.id.tvStudentName)
        val btnEdit : MaterialButton = findViewById(R.id.btnEdit)


    }
}