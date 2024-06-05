package com.sobolevkir.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Модель для запроса на аутентификацию
 */
class ForecaAuthRequest(
    @SerializedName("user")
    val user: String,
    @SerializedName("password")
    val password: String
)