package notes.user.model

sealed interface LoginResult {
    data class LoggedIn(val token: TokenDto) : LoginResult
    object InvalidCredentials : LoginResult
    object NotFound : LoginResult
}