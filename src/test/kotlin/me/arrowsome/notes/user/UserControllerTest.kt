package me.arrowsome.notes.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import me.arrowsome.notes.user.dto.LoginDto
import me.arrowsome.notes.user.dto.RegisterDto
import me.arrowsome.notes.user.dto.TokenDto
import me.arrowsome.notes.user.sealed.LoginResult
import me.arrowsome.notes.user.sealed.RegisterResult
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserControllerTest {
    private lateinit var userController: UserController
    private lateinit var userService: UserService
    private lateinit var userLenses: UserLenses

    @BeforeEach
    fun setup() {
        userService = mockk()
        userLenses = spyk()
        userController = UserController(
            userLenses,
            userService,
        )
    }

    @Test
    fun `user with valid credentials is logged in`() {
        // given
        every { userService.loginUser(LOGIN) } returns LoginResult.LoggedIn(TOKEN)
        val request = Request(POST, "/api/users/login")
            .with(userLenses.loginLens of LOGIN)
        // when
        val response = userController.loginUser(request)
        // then
        verify { userService.loginUser(LOGIN) }
        assertEquals(response.status, OK)
        assertEquals(TOKEN, userLenses.tokenLens.extract(response))
    }

    @Test
    fun `user with invalid credentials is notified`() {
        // given
        every { userService.loginUser(LOGIN) } returns LoginResult.InvalidCredentials
        val request = Request(POST, "/api/users/login")
            .with(userLenses.loginLens of LOGIN)
        // when
        val response = userController.loginUser(request)
        // then
        verify { userService.loginUser(LOGIN) }
        assertEquals(response.status, UNAUTHORIZED)
    }

    @Test
    fun `user is registered with success`() {
        // given
        every { userService.registerUser(REGISTER) } returns RegisterResult.Registered(TOKEN)
        val request = Request(POST, "/api/users")
            .with(userLenses.registerLens of REGISTER)
        // when
        val response = userController.registerUser(request)
        // then
        verify { userService.registerUser(REGISTER) }
        assertEquals(response.status, CREATED)
        assertEquals(TOKEN, userLenses.tokenLens.extract(response))
    }

    @Test
    fun `user is not registered using invalid data`() {
        // given
        every { userService.registerUser(REGISTER) } returns RegisterResult.InvalidProfile()
        val request = Request(POST, "/api/users")
            .with(userLenses.registerLens of REGISTER)
        // when
        val response = userController.registerUser(request)
        // then
        verify { userService.registerUser(REGISTER) }
        assertEquals(response.status, BAD_REQUEST)
    }

    companion object {

        val LOGIN = LoginDto(
            email = "john.doe@example.com",
            password = "+John@Doe!"
        )

        val REGISTER = RegisterDto(
            email = "john.doe@example.com",
            password = "+John@Doe!"
        )

        val TOKEN = TokenDto(
            token = "valid.jwt.token"
        )

    }

}