package com.ditooard.aplikasistoryapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ditooard.aplikasistoryapp.api.NetworkService
import com.ditooard.aplikasistoryapp.ui.register.DetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahStoryViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> get() = _loadingStatus

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun uploadImage(photo: MultipartBody.Part, description: RequestBody, token: String) {
        _loadingStatus.value = true
        val apiService = NetworkService.createApiService().postStory(
            photo, description,
            "Bearer $token"
        )
        apiService.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _loadingStatus.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.error) {
                            _message.value = it.message
                        } else {
                            _message.value = "Error: ${it.message}"
                        }
                    } ?: run {
                        _message.value = "Response body is null"
                    }
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _loadingStatus.value = false
                _message.value = t.message
            }
        })
    }
}
