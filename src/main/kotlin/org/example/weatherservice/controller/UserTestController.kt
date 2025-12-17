package org.example.weatherservice.controller

import org.example.weatherservice.domain.model.User
import org.example.weatherservice.domain.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test/users")
class UserTestController(
    private val userRepository: UserRepository
) {
    @GetMapping
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest) : ResponseEntity<User>
    {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        var user = User(
            username = request.username,
            email = request.email,
            password = request.password // TODO: Hash this in real app!
        )

        val saved = userRepository.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @GetMapping("/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<User> {
        val user = userRepository.findByUsername(username)  // Returns User?

        // Kotlin-style null handling
        return user?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = userRepository.findByEmail(email)
        return user?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/count")
    fun countUsers(): Map<String, Long> {
        return mapOf("count" to userRepository.count())
    }

    @DeleteMapping("/{username}")
    fun deleteUser(@PathVariable username: String): ResponseEntity<Void> {
        val user = userRepository.findByUsername(username)
        return if (user != null) {
            userRepository.delete(user)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class CreateUserRequest(val username: String, val email: String, val password: String)