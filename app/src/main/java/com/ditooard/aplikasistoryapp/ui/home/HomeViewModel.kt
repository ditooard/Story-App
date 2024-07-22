package com.ditooard.aplikasistoryapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ditooard.aplikasistoryapp.api.NetworkService
import com.ditooard.aplikasistoryapp.ui.register.DetailStory
import com.ditooard.aplikasistoryapp.ui.register.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _stories = MutableLiveData<List<DetailStory>>()
    val stories: LiveData<List<DetailStory>> get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    fun fetchStories(token: String) {
        _isLoading.value = true
        NetworkService.createApiService().fetchStories("Bearer $token")
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _stories.value = it.listStory
                            _message.value = it.message
                            _isError.value = false
                        } ?: run {
                            _message.value = "Response is empty"
                            _isError.value = true
                        }
                    } else {
                        _message.value = response.message()
                        _isError.value = true
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message
                    _isError.value = true
                }
            })
    }
}
