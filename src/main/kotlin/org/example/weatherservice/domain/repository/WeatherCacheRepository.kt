package org.example.weatherservice.domain.repository

import org.example.weatherservice.domain.model.WeatherCache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WeatherCacheRepository: JpaRepository<WeatherCache, Long> {
    fun findByCityNameIgnoreCase(cityName: String): WeatherCache?
    fun findByCityNameIgnoreCaseAndLastUpdatedAfter(cityName: String, lastUpdated: LocalDateTime): WeatherCache?
}