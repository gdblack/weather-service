package org.example.weatherservice.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController(
    @Value("\${weather.api.key}")
    private val API_KEY : String,
    @Value("\${weather.api.base_url")
    private val BASE_URL : String,
    @Value("\${jwt.expiration}")
    private val JWT_EXPIRATION : Long)
{
    @GetMapping
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "apiKeyConfigured" to (API_KEY != "YOUR_API_KEY_HERE"),
            "apiBaseUrl" to BASE_URL,
            "jwtExpiration" to "${JWT_EXPIRATION / 1000 / 60} minutes"
        )
    }
}
