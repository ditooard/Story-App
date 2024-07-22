package com.ditooard.aplikasistoryapp.ui.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ditooard.aplikasistoryapp.R

class PasswordMatchValidator : AppCompatEditText, View.OnFocusChangeListener {

    var isPasswordValid = false

    init {
        initializePasswordField()
    }

    constructor(context: Context) : super(context) {
        initializePasswordField()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializePasswordField()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initializePasswordField()
    }

    private fun initializePasswordField() {
        transformationMethod = PasswordTransformationMethod.getInstance()
        background = ContextCompat.getDrawable(context, R.drawable.border)
        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validatePassword()
        }
    }

    private fun validatePassword() {
        val password = text.toString().trim()
        val confirmPassword = (parent as ViewGroup)
            .findViewById<PasswordValidator>(R.id.passwordEditText).text.toString().trim()

        isPasswordValid = password.length >= 8 && password == confirmPassword
        error = if (!isPasswordValid) {
            resources.getString(R.string.passwordNotMatch)
        } else {
            null
        }
    }
}
