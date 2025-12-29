package org.example.weatherservice.security

import org.example.weatherservice.domain.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        return userRepository.findByUsername(username!!)
            ?: throw UsernameNotFoundException("Username $username not found")
    }
}