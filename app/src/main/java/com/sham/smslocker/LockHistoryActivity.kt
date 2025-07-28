package com.sham.smslocker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sham.smslocker.data.AppPreferences
import com.sham.smslocker.databinding.ActivityLockHistoryBinding
import com.sham.smslocker.databinding.LayoutLockHistoryCardBinding

class LockHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockHistoryBinding
    private lateinit var preferences: AppPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = AppPreferences(this)
        binding = ActivityLockHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButton.setOnClickListener {
            finish() // Go back to the main page
        }

        setupLockHistory()

    }

    private fun setupLockHistory(){
        val history = preferences.lockLogs
        binding.lockHistory.removeAllViews()
        history.forEach { lockLog ->
            val lockHistoryCard = LayoutLockHistoryCardBinding.inflate(layoutInflater)
            lockHistoryCard.phoneNumber.text = lockLog.phoneNumber
            lockHistoryCard.timeLockedTxt.text = lockLog.timeLocked
            lockHistoryCard.usedPwdTxt.text = lockLog.password
            binding.lockHistory.addView(lockHistoryCard.root)
        }
    }


}
