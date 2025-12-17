package org.example.weatherservice.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "weather_cache")
class WeatherCache(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val cityName: String,

    @Column(nullable = false)
    val temperature: Double,

    val feelsLike: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String,

    val lastUpdated: LocalDateTime = LocalDateTime.now()
) {
}