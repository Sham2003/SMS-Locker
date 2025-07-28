package com.sham.smslocker


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.setMargins
import com.sham.smslocker.data.AppPreferences
import com.sham.smslocker.databinding.ActivityPasswordManagerBinding
import com.sham.smslocker.databinding.DialogAddPasswordBinding
import com.sham.smslocker.databinding.LayoutPasswordCardBinding
import com.sham.smslocker.data.PasswordItem

class PasswordCardController(
    val item: PasswordItem,
    private val binding: LayoutPasswordCardBinding,
    private val onDelete: (Int) -> Unit,
    private val onSave: (Int, String) -> Unit
) {
    val view: View get() = binding.root
    var password = item.password
    private var isRevealed = false
    private var isEditing = false

    init {
        setupLayout()
        bindActions()
    }

    private fun setupLayout() {
        binding.root.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(5)
        }

        updateViewMode()
        updateEditMode()
        binding.passwordInput.setText(password)
    }

    private fun bindActions() {
        binding.eyeButton.setOnClickListener {
            isRevealed = !isRevealed
            updateViewMode()
        }

        binding.editButton.setOnClickListener {
            isEditing = true
            updateEditMode()
        }

        binding.saveButton.setOnClickListener {
            val newPassword = binding.passwordInput.text.toString()
            if (newPassword.isNotBlank()) {
                password = newPassword
                binding.passwordText.text = if (isRevealed) password else "*".repeat(password.length)
                onSave(item.id, newPassword)
            }
            isEditing = false
            updateEditMode()
        }

        binding.deleteButton.setOnClickListener {
            onDelete(item.id)
        }
    }

    private fun updateViewMode() {
        binding.passwordText.text = if (isRevealed) password else "*".repeat(password.length)
        binding.eyeButton.setImageResource(
            if (isRevealed) R.drawable.ic_menu_hide else R.drawable.ic_menu_show
        )
    }

    private fun updateEditMode() {
        binding.passwordInput.setText(password)
        binding.passwordInput.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.eyeButton.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.saveButton.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.editButton.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.passwordText.visibility = if (isEditing) View.GONE else View.VISIBLE
    }
}



class PasswordManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordManagerBinding
    private lateinit var preferences: AppPreferences
    private lateinit var adderDialog: Dialog
    private lateinit var dialogBinding : DialogAddPasswordBinding
    private var cardControllers = mutableListOf<PasswordCardController>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = AppPreferences(this)
        binding = ActivityPasswordManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPasswordAdder()
        // Add Password
        binding.addPasswordButton.setOnClickListener {
            Log.d(MY_lOG_TAG,"Add Password button clicked")
            showAddPasswordDialog()
        }

        // Back Button
        binding.backButton.setOnClickListener {
            finish() // Navigate back to MainActivity
        }

        initPasswordList()
    }

    private fun initPasswordList(){
        preferences.passwords.forEach {
            cardControllers.add(getPasswordCard(it))
        }
        cardControllers.forEach {
            binding.passwordList.addView(it.view)
        }

    }

    private fun getPasswordCard(item : PasswordItem): PasswordCardController {
        val passwordCardBinding = LayoutPasswordCardBinding.inflate(layoutInflater)

        return PasswordCardController(item, passwordCardBinding,
                onDelete = {
                    preferences.passwords = preferences.passwords.filter { it.id != item.id }
                    cardControllers.removeIf { it.item.id == item.id }
                    binding.passwordList.removeView(passwordCardBinding.root)
                },
                onSave = { id, password ->
                    preferences.passwords = preferences.passwords.map {
                        if (it.id == id) it.copy(password = password) else it
                    }
                }
            )
    }

    private fun initPasswordAdder(){
        adderDialog = Dialog(this)
        dialogBinding = DialogAddPasswordBinding.inflate(adderDialog.layoutInflater)
        adderDialog.setContentView(dialogBinding.root)

        val newPasswordInput = dialogBinding.newPasswordInputDialog
        dialogBinding.addDialogButton.setOnClickListener {
            val newPassword = newPasswordInput.text.toString()
            if (newPassword.isNotBlank()) {
                newPasswordInput.text.clear()
                val newPasswordItem = PasswordItem(newPassword)
                preferences.passwords = preferences.passwords + newPasswordItem
                val cardController = getPasswordCard(newPasswordItem)
                cardControllers.add(cardController)
                binding.passwordList.addView(cardController.view)
                Toast.makeText(this, "Added Password", Toast.LENGTH_SHORT).show()
                adderDialog.hide()
            } else {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.cancelDialogButton.setOnClickListener { adderDialog.hide() }

    }

    private fun showAddPasswordDialog() {
        dialogBinding.newPasswordInputDialog.setText("")
        adderDialog.show()
    }
}


