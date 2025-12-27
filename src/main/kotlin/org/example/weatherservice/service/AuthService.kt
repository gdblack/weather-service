package org.example.weatherservice.service

import org.example.weatherservice.domain.model.User
import org.example.weatherservice.domain.repository.UserRepository
import org.example.weatherservice.dto.AuthResponse
import org.example.weatherservice.dto.LoginRequest
import org.example.weatherservice.dto.RegisterRequest
import org.example.weatherservice.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {
    fun register(request: RegisterRequest): AuthResponse {
        // Check if username exists
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        // Create user with hashed password
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password).toString(), // Hash password
            email = request.email,
            roles = setOf("ROLE_USER")
        )

        val savedUser = userRepository.save(user)
        val token = jwtUtil.generateToken(savedUser)

        return AuthResponse(
            token = token,
            username = savedUser.username,
            email = savedUser.email,
            roles = savedUser.roles
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        // Authenticate user
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password)
        )

        // Load user
        val user = userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid credentials")

        // Generate token
        val token = jwtUtil.generateToken(user)

        return AuthResponse(
            token = token,
            username = user.username,
            email = user.email,
            roles = user.roles
        )
    }
}