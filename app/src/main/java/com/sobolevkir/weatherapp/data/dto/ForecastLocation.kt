package com.sobolevkir.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа на запрос поиска локаций —
 * модель одной локации.
 */
data class ForecastLocation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String
)