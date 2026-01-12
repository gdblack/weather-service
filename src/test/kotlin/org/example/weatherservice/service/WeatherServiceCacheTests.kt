package org.example.weatherservice.service

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.example.weatherservice.domain.model.WeatherCache
import org.example.weatherservice.domain.repository.WeatherCacheRepository
import org.example.weatherservice.dto.WeatherApiResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherServiceCacheTests {

    private lateinit var weatherService: WeatherService
    private lateinit var weatherCacheRepository: WeatherCacheRepository
    private lateinit var webClient: WebClient

    @BeforeEach
    fun setup() {
        weatherCacheRepository = mockk()
        webClient = mockk()
        weatherService = WeatherService(webClient, weatherCacheRepository, "test-api-key")
    }

    @Test
    fun `should return cached weather when valid cache exists`() = runBlocking {
        // Arrange
        val cityName = "London"
        val cachedWeather = WeatherCache(
            id = 1,
            cityName = cityName,
            temperature = 15.0,
            feelsLike = 14.0,
            description = "Partly cloudy",
            humidity = 65,
            windSpeed = 10.0,
            icon = "02d",
            pressure = 1013,
            lastUpdated = LocalDateTime.now()
        )

        coEvery {
            weatherCacheRepository.findByCityNameIgnoreCaseAndLastUpdatedAfter(
                cityName = cityName,
                lastUpdated = any()
            )
        } returns cachedWeather

        // Act
        val result = weatherService.getWeatherByCity(cityName)

        // Assert
        assertEquals(cityName, result.cityName)
        assertEquals(15.0, result.temperature)
        assertEquals(1013, result.pressure)
        assertTrue(result.cached)
    }

    @Test
    fun `should fetch from API when cache is expired`() = runBlocking {
        // Arrange
        val cityName = "Paris"
        val apiResponse = mockApiResponse(cityName)

        coEvery {
            weatherCacheRepository.findByCityNameIgnoreCaseAndLastUpdatedAfter(
                cityName = cityName,
                lastUpdated = any()
            )
        } returns null // No valid cache

        coEvery {
            webClient.get()
        } returns mockk {
            coEvery { uri(any<String>()) } returns mockk {
                coEvery { retrieve() } returns mockk {
                    coEvery { awaitBody<WeatherApiResponse>() } returns apiResponse
                }
            }
        }

        coEvery {
            weatherCacheRepository.save(any())
        } returns WeatherCache(
            id = 1,
            cityName = cityName,
            temperature = apiResponse.main.temperature,
            feelsLike = apiResponse.main.feelsLike,
            description = apiResponse.weather.first().description,
            humidity = apiResponse.main.humidity,
            windSpeed = apiResponse.wind.speed,
            icon = apiResponse.weather.first().icon,
            pressure = apiResponse.main.pressure
        )

        // Act
        val result = weatherService.getWeatherByCity(cityName)

        // Assert
        assertEquals(cityName, result.cityName)
        assertEquals(20.0, result.temperature)
        assertEquals(1012, result.pressure)
        assertTrue(!result.cached)
    }

    @Test
    fun `should persist pressure field to cache`() = runBlocking {
        // Arrange
        val cityName = "Berlin"
        val pressure = 1015
        val apiResponse = mockApiResponse(cityName, pressure = pressure)

        coEvery {
            weatherCacheRepository.findByCityNameIgnoreCaseAndLastUpdatedAfter(
                cityName = cityName,
                lastUpdated = any()
            )
        } returns null

        var savedCache: WeatherCache? = null
        coEvery {
            weatherCacheRepository.save(any())
        } answers {
            savedCache = firstArg()
            savedCache!!
        }

        coEvery {
            webClient.get()
        } returns mockk {
            coEvery { uri(any<String>()) } returns mockk {
                coEvery { retrieve() } returns mockk {
                    coEvery { awaitBody<WeatherApiResponse>() } returns apiResponse
                }
            }
        }

        // Act
        weatherService.getWeatherByCity(cityName)

        // Assert
        verify { weatherCacheRepository.save(any()) }
        assertEquals(pressure, savedCache?.pressure)
    }

    @Test
    fun `should have correct cache TTL of 5 minutes`() = runBlocking {
        // Arrange
        val cityName = "Tokyo"
        val now = LocalDateTime.now()

        coEvery {
            weatherCacheRepository.findByCityNameIgnoreCaseAndLastUpdatedAfter(
                cityName = cityName,
                lastUpdated = any()
            )
        } returns null

        coEvery {
            weatherCacheRepository.save(any())
        } returns mockk()

        coEvery {
            webClient.get()
        } returns mockk {
            coEvery { uri(any<String>()) } returns mockk {
                coEvery { retrieve() } returns mockk {
                    coEvery { awaitBody<WeatherApiResponse>() } returns mockApiResponse(cityName)
                }
            }
        }

        // Act
        weatherService.getWeatherByCity(cityName)

        // Assert - Verify that the repository was called with lastUpdated 5 minutes ago
        verify {
            weatherCacheRepository.findByCityNameIgnoreCaseAndLastUpdatedAfter(
                cityName = cityName,
                lastUpdated = match { timestamp ->
                    val timeDiff = now.minusMinutes(5)
                    timestamp.isBefore(timeDiff.plusSeconds(10)) && timestamp.isAfter(timeDiff.minusSeconds(10))
                }
            )
        }
    }

    private fun mockApiResponse(
        cityName: String,
        temperature: Double = 20.0,
        feelsLike: Double = 19.0,
        humidity: Int = 60,
        windSpeed: Double = 15.0,
        pressure: Int = 1012
    ): WeatherApiResponse {
        return WeatherApiResponse(
            cityName = cityName,
            main = WeatherApiResponse.Main(
                temperature = temperature,
                feelsLike = feelsLike,
                humidity = humidity,
                pressure = pressure
            ),
            weather = listOf(
                WeatherApiResponse.Weather(
                    id = 500,
                    main = "Rain",
                    description = "light rain",
                    icon = "10d"
                )
            ),
            wind = WeatherApiResponse.Wind(speed = windSpeed),
            timestamp = System.currentTimeMillis() / 1000
        )
    }
}
