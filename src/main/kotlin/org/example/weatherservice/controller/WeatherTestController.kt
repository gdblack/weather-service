package org.example.weatherservice.controller

import org.example.weatherservice.domain.model.WeatherCache
import org.example.weatherservice.domain.repository.WeatherCacheRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test/weather")
class WeatherTestController(
    private val weatherCacheRepository: WeatherCacheRepository
) {
    @GetMapping
    fun getAllWeatherCache(): List<WeatherCache> {
        return weatherCacheRepository.findAll()
    }

    @PostMapping
    fun createWeatherCache(@RequestBody request: CreateWeatherRequest): ResponseEntity<WeatherCache> {
        val weather = WeatherCache(
            cityName = request.cityName,
            temperature = request.temperature,
            feelsLike = request.feelsLike,
            description = request.description,
            humidity = request.humidity,
            windSpeed = request.windSpeed,
            icon = request.icon
        )

        val saved = weatherCacheRepository.save(weather)
        return ResponseEntity.ok(saved)
    }

    @GetMapping("/{cityName}")
    fun getWeatherByCity(@PathVariable cityName: String): ResponseEntity<WeatherCache> {
        val weather = weatherCacheRepository.findByCityNameIgnoreCase(cityName)
        return weather?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/count")
    fun countWeather(): Map<String, Long> {
        return mapOf("count" to weatherCacheRepository.count())
    }

}

data class CreateWeatherRequest(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String
)