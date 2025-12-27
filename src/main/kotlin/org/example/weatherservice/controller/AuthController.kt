package org.example.weatherservice.controller

import jakarta.validation.Valid
import org.example.weatherservice.dto.AuthResponse
import org.example.weatherservice.dto.LoginRequest
import org.example.weatherservice.dto.RegisterRequest
import org.example.weatherservice.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse>
    {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse>
    {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }
}