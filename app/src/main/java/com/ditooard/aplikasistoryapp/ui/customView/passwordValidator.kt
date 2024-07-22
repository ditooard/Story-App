package com.ditooard.aplikasistoryapp.ui.customView

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ditooard.aplikasistoryapp.R

class PasswordValidator : AppCompatEditText, View.OnTouchListener {

    var isPasswordValid: Boolean = false

    init {
        setupPasswordField()
    }

    constructor(context: Context) : super(context) {
        setupPasswordField()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupPasswordField()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        setupPasswordField()
    }

    private fun setupPasswordField() {
        transformationMethod = PasswordTransformationMethod.getInstance()
        background = ContextCompat.getDrawable(context, R.drawable.border)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswordValidity()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) {
            checkPasswordValidity()
        }
    }

    private fun checkPasswordValidity() {
        isPasswordValid = (text?.length ?: 0) >= 8
        error = if (!isPasswordValid) {
            resources.getString(R.string.lessPass)
        } else {
            null
        }
    }
}
