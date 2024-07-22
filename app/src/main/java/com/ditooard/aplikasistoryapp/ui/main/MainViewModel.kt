package com.ditooard.aplikasistoryapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ditooard.aplikasistoryapp.api.NetworkService
import com.ditooard.aplikasistoryapp.ui.register.DataAccountLogin
import com.ditooard.aplikasistoryapp.ui.register.DataAccountRegist
import com.ditooard.aplikasistoryapp.ui.register.DetailResponse
import com.ditooard.aplikasistoryapp.ui.register.LoginResponse
import retrofit2.Call
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _isLoadingLogin = MutableLiveData<Boolean>()
    private val _isLoadingRegist = MutableLiveData<Boolean>()
    private val _messageLogin = MutableLiveData<String>()
    private val _userLogin = MutableLiveData<LoginResponse>()
    private val _messageRegist = MutableLiveData<String>()

    val isLoadingLogin: LiveData<Boolean> = _isLoadingLogin
    val messageLogin: LiveData<String> = _messageLogin
    val userLogin: LiveData<LoginResponse> = _userLogin
    val isLoadingRegist: LiveData<Boolean> = _isLoadingRegist
    val messageRegist: LiveData<String> = _messageRegist

    var isErrorLogin: Boolean = false
    var isErrorRegist: Boolean = false

    fun loginUser(loginDataAccount: DataAccountLogin) {
        _isLoadingLogin.value = true
        val api = NetworkService.createApiService().loginUser(loginDataAccount)
        sendRequest(api, onResponse = { response ->
            _isLoadingLogin.value = false
            val responseBody = response.body()

            if (response.isSuccessful) {
                isErrorLogin = false
                _userLogin.value = responseBody!!
                _messageLogin.value = "Holaaa ${_userLogin.value!!.loginResult.name}!"
            } else {
                handleError(response.code(), _messageLogin)
            }
        }, onFailure = { t ->
            handleError(t, _messageLogin)
        })
    }

    fun registerUser(registDataUser: DataAccountRegist) {
        _isLoadingRegist.value = true
        val api = NetworkService.createApiService().registerUser(registDataUser)
        sendRequest(api, onResponse = { response ->
            _isLoadingRegist.value = false
            if (response.isSuccessful) {
                isErrorRegist = false
                _messageRegist.value = "Akun anda berhasil dibuat"
            } else {
                handleError(response.code(), _messageRegist)
            }
        }, onFailure = { t ->
            handleError(t, _messageRegist)
        })
    }

    private fun <T> sendRequest(
        call: Call<T>,
        onResponse: (Response<T>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        call.enqueue(object : retrofit2.Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    private fun handleError(code: Int, messageLiveData: MutableLiveData<String>) {
        isErrorLogin = true
        when (code) {
            401 -> messageLiveData.value =
                "Email/Password yang anda masukan tidak tepat, silahkan coba lagi"
            408 -> messageLiveData.value =
                "Terjadi kesalahan teknis, silahkan coba lagi"
            else -> messageLiveData.value = "Pesan error: $code"
        }
    }

    private fun handleError(t: Throwable, messageLiveData: MutableLiveData<String>) {
        isErrorLogin = true
        _isLoadingLogin.value = false
        messageLiveData.value = "Informasi error: ${t.message}"
    }
}
