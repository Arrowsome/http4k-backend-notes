package me.arrowsome.notes.user.sealed

import me.arrowsome.notes.user.dto.TokenDto

sealed interface RegisterResult {
    data class Registered(val token: TokenDto) : RegisterResult
    data class InvalidProfile(val reason: String? = null) : RegisterResult
}
