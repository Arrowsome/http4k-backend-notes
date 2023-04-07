package me.arrowsome.notes.user.sealed

import me.arrowsome.notes.user.dto.TokenDto

sealed interface LoginResult {
    data class LoggedIn(val token: TokenDto) : LoginResult
    object InvalidCredentials : LoginResult
}