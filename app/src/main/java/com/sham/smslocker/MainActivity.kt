package com.sham.smslocker


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import com.sham.smslocker.data.AppPreferences
import com.sham.smslocker.utils.PermissionLocker
import com.sham.smslocker.databinding.ActivityMainBinding


const val MY_lOG_TAG = "SHAM_LOG"

class MainActivity : AppCompatActivity() {

    private lateinit var permissionLocker: PermissionLocker
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: AppPreferences
    init {
        Log.d(MY_lOG_TAG,"App Created")
    }


    private fun initAppUi(){

        binding.switchButton.isChecked = preferences.isReceiverAllowed
        updateServiceStatusText(preferences.isReceiverAllowed)

        binding.lockButton.setOnClickListener {
            permissionLocker.lockScreen()
        }

        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->
            preferences.isReceiverAllowed = isChecked
            updateServiceStatusText(isChecked)
        }

        binding.passwordManagerButton.setOnClickListener {
            //Toast.makeText(this,"Goto Password list", Toast.LENGTH_SHORT).show();
            val intent = Intent(this, PasswordManagerActivity::class.java)
            startActivity(intent)
        }
        binding.alarmButton.setOnClickListener {
            //Toast.makeText(this,"Goto Password list", Toast.LENGTH_SHORT).show();
//            val intent = Intent(this, AlarmScreenActivity::class.java)
//            startActivity(intent)
        }

        binding.historyButton.setOnClickListener {
            val intent = Intent(this, LockHistoryActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = AppPreferences(this)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        permissionLocker = PermissionLocker(this,{},{
            finish()
        })
        permissionLocker.start()

        initAppUi()
    }


    private fun updateServiceStatusText(isRunning: Boolean) {
        val statusTextView = binding.serviceStatus
        if (isRunning) {
            statusTextView.text = buildString { append("Service is ON") }
            statusTextView.setTextColor(getColor(R.color.green)) // Use a color resource for green
        } else {
            statusTextView.text = buildString { append("Service is OFF") }
            statusTextView.setTextColor(getColor(R.color.red)) // Use a color resource for red
        }
    }

}