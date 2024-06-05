package com.sobolevkir.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа для запроса на аутентификацию
 */
class ForecaAuthResponse(
    @SerializedName("access_token")
    val token: String
)