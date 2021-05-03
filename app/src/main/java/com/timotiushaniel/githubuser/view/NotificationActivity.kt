package com.timotiushaniel.githubuser.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timotiushaniel.githubuser.R
import com.timotiushaniel.githubuser.alarm.AlarmReceiver
import com.timotiushaniel.githubuser.databinding.ActivityNotificationBinding
import com.timotiushaniel.githubuser.helper.Constant

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBarTitle()

        alarmReceiver = AlarmReceiver()
        mSharedPreferences = getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE)

        setSwitchReminder()
        binding.swReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmReceiver.setDailyReminder(
                    this,
                    AlarmReceiver.ALARM_TYPE,
                    getString(R.string.reminder_summary)
                )
            } else {
                alarmReceiver.cancelAlarm(this)
            }
            saveChanges(isChecked)
        }
    }

    private fun setSwitchReminder() {
        binding.swReminder.isChecked = mSharedPreferences.getBoolean(Constant.DAILY, false)
    }

    private fun saveChanges(value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(Constant.DAILY, value)
        editor.apply()
    }

    private fun setActionBarTitle() {
        if (supportActionBar != null) {
            supportActionBar?.title = "Reminder Settings"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}