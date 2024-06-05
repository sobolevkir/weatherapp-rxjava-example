package com.sobolevkir.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа на запрос поиска локаций —
 * контейнер для списка локаций.
 */
class LocationsResponse(
    @SerializedName("locations")
    val locations: List<ForecastLocation>
)