package org.example.weatherservice.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class WeatherApiResponse(
    @JsonProperty("name") val cityName: String,
    @JsonProperty("main") val main: Main,
    @JsonProperty("weather") val weather: List<Weather>,
    @JsonProperty("wind") val wind: Wind,
    @JsonProperty("dt") val timestamp: Long
) {
    data class Main(
        @JsonProperty("temp") val temperature: Double,
        @JsonProperty("feels_like") val feelsLike: Double,
        @JsonProperty("humidity") val humidity: Int,
        @JsonProperty("pressure") val pressure: Int
    )

    data class Weather(
        @JsonProperty("id") val id: Int,
        @JsonProperty("main") val main: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("icon") val icon: String
    )

    data class Wind(
        @JsonProperty("speed") val speed: Double,
        @JsonProperty("deg") val degree: Int? = null
    )
}

data class WeatherResponse(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String,
    val pressure: Int,
    val cached: Boolean = false,
    val lastUpdated: String
)