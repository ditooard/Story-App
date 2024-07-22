package com.ditooard.aplikasistoryapp.ui.customView

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ditooard.aplikasistoryapp.R

class NameValid : AppCompatEditText, View.OnFocusChangeListener {

    var isNameValid = false

    constructor(context: Context) : super(context) {
        initNameEditText()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initNameEditText()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initNameEditText()
    }

    private fun initNameEditText() {
        inputType = InputType.TYPE_CLASS_TEXT
        background = ContextCompat.getDrawable(context, R.drawable.border)
        onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            checkNameValidity()
        }
    }

    private fun checkNameValidity() {
        isNameValid = text.toString().trim().isNotEmpty()
        error = if (!isNameValid) {
            resources.getString(R.string.noName)
        } else {
            null
        }
    }
}
