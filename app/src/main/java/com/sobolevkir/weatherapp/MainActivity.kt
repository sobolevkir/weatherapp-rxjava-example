package com.sobolevkir.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sobolevkir.weatherapp.domain.ForecaRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Создали репозиторий и вызвали нужный метод
        val forecaRepository = ForecaRepository()
        forecaRepository.getCurrentWeather()
    }
}