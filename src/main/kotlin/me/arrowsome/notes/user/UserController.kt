package me.arrowsome.notes.user

import me.arrowsome.notes.user.model.LoginResult
import me.arrowsome.notes.user.model.RegisterResult
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with

class UserController(
    private val userLenses: UserLenses,
    private val userService: UserService,
) {

    fun loginUser(request: Request): Response {
        val loginDto = userLenses.loginLens.extract(request)

        val result = userService.loginUser(loginDto)

        return when (result) {
            is LoginResult.LoggedIn -> Response(OK)
                .with(userLenses.tokenLens of result.token)
            LoginResult.InvalidCredentials -> Response(UNAUTHORIZED)
            LoginResult.NotFound -> TODO()
        }
    }

    fun registerUser(request: Request): Response {
        val registerDto = userLenses.registerLens.extract(request)

        val registerResult = userService.registerUser(registerDto)

        return when (registerResult) {
            is RegisterResult.Registered -> Response(CREATED)
            is RegisterResult.InvalidProfile -> Response(BAD_REQUEST)
            RegisterResult.DuplicateProfile -> TODO()
        }
    }

}
