package com.ditooard.aplikasistoryapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun getSessionLogin(): LiveData<Boolean> {
        return pref.getLoginSession().asLiveData()
    }

    fun getNama(): LiveData<String> {
        return pref.getName().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun saveSessionLogin(loginSession: Boolean) {
        viewModelScope.launch {
            pref.saveLoginSession(loginSession)
        }
    }

    fun saveNama(token: String) {
        viewModelScope.launch {
            pref.saveName(token)
        }
    }

    fun clearLoginData() {
        viewModelScope.launch {
            pref.clearDataLogin()
        }
    }

}