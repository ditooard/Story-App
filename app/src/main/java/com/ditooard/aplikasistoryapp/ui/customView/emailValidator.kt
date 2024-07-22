package com.ditooard.aplikasistoryapp.ui.customView

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ditooard.aplikasistoryapp.R

class EmailValid : AppCompatEditText, View.OnFocusChangeListener {

    private lateinit var existingEmail: String
    private var isEmailTaken = false
    var isEmailValid = false

    constructor(context: Context) : super(context) {
        initializeEmailField()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeEmailField()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initializeEmailField()
    }

    private fun initializeEmailField() {
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        background = ContextCompat.getDrawable(context, R.drawable.border)
        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmailValidity()
                if (isEmailTaken) {
                    showEmailTakenError()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            checkEmailValidity()
            if (isEmailTaken) {
                showEmailTakenError()
            }
        }
    }

    private fun checkEmailValidity() {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!isEmailValid) {
            resources.getString(R.string.wrongMailFormat)
        } else {
            null
        }
    }

    private fun showEmailTakenError() {
        error = if (isEmailTaken && text.toString().trim() == existingEmail) {
            resources.getString(R.string.takenMail)
        } else {
            null
        }
    }

    fun setEmailError(message: String, email: String) {
        existingEmail = email
        isEmailTaken = true
        error = if (text.toString().trim() == existingEmail) {
            message
        } else {
            null
        }
    }
}
