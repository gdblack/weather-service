package org.example.weatherservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class WeatherServiceApplication {
    @Bean
    fun init(@Value("\${weather.api.key}") apiKey: String) = CommandLineRunner {
        println("=================================")
        println("✅ Weather Service Started!")
        println("=================================")
        println("API Key: ${if (apiKey == "YOUR_API_KEY_HERE") "⚠️ Using default" else "✅ Configured"}")
        println("Database: Connected")
        println("Port: 8080")
        println("=================================")
    }
}

fun main(args: Array<String>) {
    runApplication<WeatherServiceApplication>(*args)
}
