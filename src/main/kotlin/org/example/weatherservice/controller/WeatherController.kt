package org.example.weatherservice.controller
import org.example.weatherservice.dto.WeatherResponse
import org.example.weatherservice.service.WeatherService
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/weather")
class WeatherController(
    private val weatherService: WeatherService
) {
    
    @GetMapping("/{city}")
    @PreAuthorize("hasRole('USER')")  // ← Requires authentication!
    fun getWeather(@PathVariable city: String): ResponseEntity<WeatherResponse> = runBlocking {
        try {
            val weather = weatherService.getWeatherByCity(city)
            ResponseEntity.ok(weather)
        } catch (e: Exception) {
            println("Error fetching weather for $city: ${e.message}")
            ResponseEntity.status(503).build()
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")  // ← Requires authentication!
    fun searchWeather(@RequestParam("q") cityName: String): ResponseEntity<WeatherResponse> = runBlocking {
        try {
            val weather = weatherService.getWeatherByCity(cityName)
            ResponseEntity.ok(weather)
        } catch (e: Exception) {
            println("Error fetching weather for $cityName: ${e.message}")
            ResponseEntity.status(503).build()
        }
    }
}