package com.ditooard.aplikasistoryapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ditooard.aplikasistoryapp.R
import com.ditooard.aplikasistoryapp.databinding.ActivityMainBinding
import com.ditooard.aplikasistoryapp.ui.home.HomeActivity
import com.ditooard.aplikasistoryapp.ui.register.DataAccountLogin
import com.ditooard.aplikasistoryapp.ui.register.RegistrasiActivity
import com.ditooard.aplikasistoryapp.viewmodel.CustomViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var passwordVisible: Boolean = false

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()

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

        val preferences = UserPreferences.getInstance(dataStore)
        val userLoginViewModel = ViewModelProvider(this, CustomViewModelFactory(preferences))[UserViewModel::class.java]

        userLoginViewModel.getSessionLogin().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        mainViewModel.messageLogin.observe(this) { message ->
            handleLoginResponse(mainViewModel.isErrorLogin, message, userLoginViewModel)
        }

        mainViewModel.isLoadingLogin.observe(this) {
            displayLoading(it)
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

    private fun handleLoginResponse(isError: Boolean, message: String, userLoginViewModel: UserViewModel) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (!isError) {
            val user = mainViewModel.userLogin.value
            user?.loginResult?.let {
                userLoginViewModel.saveSessionLogin(true)
                userLoginViewModel.saveToken(it.token)
                userLoginViewModel.saveNama(it.name)
            }
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            binding.emailEditText.clearFocus()
            binding.passwordEditText.clearFocus()

            if (isInputValid()) {
                val loginData = DataAccountLogin(
                    binding.emailEditText.text.toString().trim(),
                    binding.passwordEditText.text.toString().trim()
                )
                mainViewModel.loginUser(loginData)
            } else {
                showValidationErrors()
            }
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegistrasiActivity::class.java))
        }
    }

    private fun isInputValid(): Boolean {
        return binding.emailEditText.isEmailValid && binding.passwordEditText.isPasswordValid
    }

    private fun showValidationErrors() {
        if (!binding.emailEditText.isEmailValid) binding.emailEditText.error = getString(R.string.noEmail)
        if (!binding.passwordEditText.isPasswordValid) binding.passwordEditText.error = getString(R.string.noPass)
        Toast.makeText(this, R.string.loginInvalid, Toast.LENGTH_SHORT).show()
    }

    private fun displayLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
