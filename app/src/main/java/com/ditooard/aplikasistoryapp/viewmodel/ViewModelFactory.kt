package com.ditooard.aplikasistoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ditooard.aplikasistoryapp.ui.main.UserViewModel
import com.ditooard.aplikasistoryapp.ui.main.UserPreferences

class CustomViewModelFactory(private val preferences: UserPreferences) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(preferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
