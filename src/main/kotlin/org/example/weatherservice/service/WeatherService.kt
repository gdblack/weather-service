package org.example.weatherservice.service

import org.example.weatherservice.domain.model.WeatherCache
import org.example.weatherservice.domain.repository.WeatherCacheRepository
import org.example.weatherservice.dto.WeatherApiResponse
import org.example.weatherservice.dto.WeatherResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class WeatherService(
    private val webClient: WebClient,
    private val weatherCacheRepository: WeatherCacheRepository,
    @Value("\${weather.api.key}") private val apiKey: String
) {

    private val cacheExpirationMinutes = 5L

    suspend fun getWeatherByCity(cityName: String): WeatherResponse {
        val cachedWeather = weatherCacheRepository.findByCityNameIgnoreCaseAndLastUpdatedAfter(
            cityName = cityName,
            lastUpdated = LocalDateTime.now().minusDays(cacheExpirationMinutes)
        )

        if (cachedWeather != null) {
            println("Cache HIT for $cityName")
            return cachedWeather.toResponse(cached = true)
        }

        println("Cache MISS for $cityName - fetching from API")

        // Step 2: Fetch from external API
        val apiResponse = fetchFromExternalApi(cityName)

        // Step 3: Save to database cache
        val weatherCache = WeatherCache(
            cityName = apiResponse.cityName,
            temperature = apiResponse.main.temperature,
            feelsLike = apiResponse.main.feelsLike,
            description = apiResponse.weather.firstOrNull()?.description ?: "Unknown",
            humidity = apiResponse.main.humidity,
            windSpeed = apiResponse.wind.speed,
            icon = apiResponse.weather.firstOrNull()?.icon ?: ""
        )

        weatherCacheRepository.save(weatherCache)
        println("Saed to cache: $cityName")

        return weatherCache.toResponse(cached = false)
    }

    private suspend fun fetchFromExternalApi(cityName: String): WeatherApiResponse {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/weather")
                    .queryParam("q", cityName)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
            }
            .retrieve()
            .awaitBody()
    }

    private fun WeatherCache.toResponse(cached: Boolean) = WeatherResponse(
        cityName = this.cityName,
        temperature = this.temperature,
        feelsLike = this.feelsLike,
        description = this.description.replaceFirstChar { it.uppercase() },
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        icon = this.icon,
        pressure = 0,  // Not storing pressure in cache yet
        cached = cached,
        lastUpdated = this.lastUpdated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}