package com.ditooard.aplikasistoryapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ditooard.aplikasistoryapp.R
import com.ditooard.aplikasistoryapp.databinding.ActivityRegistrasiBinding
import com.ditooard.aplikasistoryapp.ui.home.HomeActivity
import com.ditooard.aplikasistoryapp.ui.main.MainViewModel
import com.ditooard.aplikasistoryapp.ui.main.UserViewModel
import com.ditooard.aplikasistoryapp.ui.main.UserPreferences
import com.ditooard.aplikasistoryapp.ui.main.dataStore
import com.ditooard.aplikasistoryapp.viewmodel.CustomViewModelFactory

class RegistrasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrasiBinding

    private var passwordVisible: Boolean = false

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = resources.getString(R.string.createAccount)
            setDisplayHomeAsUpEnabled(true)
        }
        initializeListeners()

        binding.passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Index of the drawable end
                if (event.rawX >= (binding.passwordEditText.right - binding.passwordEditText.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.confirmPasswordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Index of the drawable end
                if (event.rawX >= (binding.confirmPasswordEditText.right - binding.confirmPasswordEditText.compoundDrawables[drawableEnd].bounds.width())) {
                    toggleConfirmPasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        val preferences = UserPreferences.getInstance(dataStore)
        val userLoginViewModel = ViewModelProvider(this, CustomViewModelFactory(preferences))[UserViewModel::class.java]

        userLoginViewModel.getSessionLogin().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                startActivity(Intent(this@RegistrasiActivity, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }

        mainViewModel.messageRegist.observe(this) { message ->
            handleRegistrationResponse(mainViewModel.isErrorRegist, message)
        }

        mainViewModel.isLoadingRegist.observe(this) {
            showLoading(it)
        }

        mainViewModel.messageLogin.observe(this) { message ->
            handleLoginResponse(mainViewModel.isErrorLogin, message, userLoginViewModel)
        }

        mainViewModel.isLoadingLogin.observe(this) {
            showLoading(it)
        }
    }

    private fun togglePasswordVisibility() {
        if (passwordVisible) {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visible_off, 0)
        } else {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visible_on, 0)
        }
        passwordVisible = !passwordVisible
        binding.passwordEditText.text?.let {
            binding.passwordEditText.setSelection(it.length)
        }
    }

    private fun toggleConfirmPasswordVisibility() {
        if (passwordVisible) {
            binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.confirmPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visible_off, 0)
        } else {
            binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.confirmPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visible_on, 0)
        }
        passwordVisible = !passwordVisible
        binding.confirmPasswordEditText.text?.let {
            binding.confirmPasswordEditText.setSelection(it.length)
        }
    }

    private fun handleLoginResponse(isError: Boolean, message: String, userLoginViewModel: UserViewModel) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (!isError) {
            mainViewModel.userLogin.value?.loginResult?.let {
                userLoginViewModel.saveSessionLogin(true)
                userLoginViewModel.saveToken(it.token)
                userLoginViewModel.saveNama(it.name)
            }
        }
    }

    private fun handleRegistrationResponse(isError: Boolean, message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (!isError) {
            val loginData = DataAccountLogin(binding.emailEditText.text.toString().trim(), binding.passwordEditText.text.toString().trim())
            mainViewModel.loginUser(loginData)
        } else {
            if (message == "1") {
                binding.emailEditText.setEmailError(resources.getString(R.string.takenMail), binding.emailEditText.text.toString())
            }
        }
    }

    private fun initializeListeners() {
        binding.registerButton.setOnClickListener {
            binding.apply {
                nameEditText.clearFocus()
                emailEditText.clearFocus()
                passwordEditText.clearFocus()
                confirmPasswordEditText.clearFocus()
            }

            if (isInputValid()) {
                val registrationData = DataAccountRegist(
                    name = binding.nameEditText.text.toString().trim(),
                    email = binding.emailEditText.text.toString().trim(),
                    password = binding.passwordEditText.text.toString().trim()
                )
                mainViewModel.registerUser(registrationData)
            } else {
                showValidationErrors()
            }
        }
    }

    private fun isInputValid(): Boolean {
        return binding.nameEditText.isNameValid && binding.emailEditText.isEmailValid && binding.passwordEditText.isPasswordValid && binding.confirmPasswordEditText.isPasswordValid
    }

    private fun showValidationErrors() {
        if (!binding.nameEditText.isNameValid) binding.nameEditText.error = resources.getString(R.string.noName)
        if (!binding.emailEditText.isEmailValid) binding.emailEditText.error = resources.getString(R.string.noEmail)
        if (!binding.passwordEditText.isPasswordValid) binding.passwordEditText.error = resources.getString(R.string.noPass)
        if (!binding.confirmPasswordEditText.isPasswordValid) binding.confirmPasswordEditText.error = resources.getString(R.string.noConfirmPass)

        Toast.makeText(this, R.string.loginInvalid, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
