package com.ditooard.aplikasistoryapp.ui.register

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class DataAccountLogin(
    var email: String,
    var password: String
)

data class DataAccountRegist(
    var name: String,
    var email: String,
    var password: String
)

data class LoginResponse(
    var error: Boolean,
    var message: String,
    var loginResult: ResultLogin
)

data class DetailResponse(
    var error: Boolean,
    var message: String
)

data class StoryResponse(
    var error: String,
    var message: String,
    var listStory: List<DetailStory>
)


data class ResultLogin(
    var userId: String,
    var name: String,
    var token: String
)


@Parcelize
data class DetailStory(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable
