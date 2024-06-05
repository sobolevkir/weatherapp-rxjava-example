package com.sobolevkir.weatherapp.domain

import android.util.Log
import com.sobolevkir.weatherapp.data.ForecaApi
import com.sobolevkir.weatherapp.data.dto.ForecaAuthRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ForecaRepository {

    private companion object {
        const val BASE_URL = "https://fnw-us.foreca.com"

        const val USER = "sobolevkir"
        const val PASSWORD = "2Ys6dFM6rQgr"
        const val HARDCODED_LOCATION = "Barcelona"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        // Добавляет CallAdapterFactory для RxJava
        .addCallAdapterFactory(
            RxJava2CallAdapterFactory.create()
        )
        .build()

    private val forecaService = retrofit.create(ForecaApi::class.java)

    private var token = ""

    fun getCurrentWeather() {
        forecaService.authenticate(ForecaAuthRequest(USER, PASSWORD))
            .flatMap { tokenResponse ->
                // Конвертируем полученный accessToken в новый запрос
                token = tokenResponse.token

                // Переключаемся на следующий сетевой запрос
                val bearerToken = "Bearer ${tokenResponse.token}"
                forecaService.getLocations(bearerToken, HARDCODED_LOCATION)
                    // Добавляем конвертацию результата в Pair,
                    // чтобы пробросить и результат, и access token
                    // дальше по цепочке
                    .map { Pair(it.locations, bearerToken) }
            }
            .flatMap { pairLocationsAndToken ->
                // Получаем данные из Pair
                val (locations, bearerToken) = pairLocationsAndToken
                // Опускаем обработку кейса с отсутствием локаций
                val firstLocation = locations.first()

                // Делаем запрос на текущую погоду
                forecaService.getForecast(bearerToken, firstLocation.id)
            }
            .retry { count, throwable ->
                count < 3 && throwable is HttpException && throwable.code() == 401
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { forecastResponse ->
                    // В итоговый subscribe теперь приходит прогноз
                    Log.d("RxJava", "Current forecast: ${forecastResponse.current}")
                },
                { error -> Log.e("RxJava", "Got error with auth or locations, or forecast", error) }
            )
    }



    /*fun authenticate() {
        forecaService.authenticate(ForecaAuthRequest(USER, PASSWORD))
            .enqueue(object : Callback<ForecaAuthResponse> {
                override fun onResponse(call: Call<ForecaAuthResponse>,
                                        response: Response<ForecaAuthResponse>
                ) {
                    if (response.code() == 200) {
                        token = response.body()?.token.toString()
                        search(token, HARDCODED_LOCATION)
                    } else {
                        Log.e("RxJavaForeca", "Something went wrong with auth: ${response.code().toString()}")
                    }
                }

                override fun onFailure(call: Call<ForecaAuthResponse>, t: Throwable) {
                    Log.e("RxJavaForeca", "onFailure auth request", t)
                }
            })
    }

    private fun search(accessToken: String, searchQuery: String) {
        val bearerToken = "Bearer $accessToken"
        forecaService.getLocations(bearerToken, searchQuery)
            .enqueue(object : Callback<LocationsResponse> {
                override fun onResponse(call: Call<LocationsResponse>,
                                        response: Response<LocationsResponse>) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.locations?.isNotEmpty() == true) {
                                val locations = response.body()?.locations!!

                                Log.d("RxJavaForeca", "Found locations!")
                                locations.forEach {
                                    Log.d("RxJavaForeca", it.toString())
                                }


                            } else {
                                Log.d("RxJavaForeca", "Nothing found")
                            }

                        }
                        401 -> authenticate()
                        else -> {
                            Log.e("RxJavaForeca", "Something went wrong with search: ${response.code().toString()}")
                        }
                    }

                }

                override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
                    Log.e("RxJavaForeca", "onFailure search request", t)
                }

            })
    }*/

}