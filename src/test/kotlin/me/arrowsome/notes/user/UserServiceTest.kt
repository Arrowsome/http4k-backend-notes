package me.arrowsome.notes.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.arrowsome.notes.user.util.JwtUtil
import me.arrowsome.notes.user.util.CryptoUtil
import me.arrowsome.notes.user.util.ValidationException
import me.arrowsome.notes.user.util.ValidatorUtil
import me.arrowsome.notes.user.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserServiceTest {
    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository
    private lateinit var jwtUtil: JwtUtil
    private lateinit var cryptoUtil: CryptoUtil
    private lateinit var validatorUtil: ValidatorUtil

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        jwtUtil = mockk()
        cryptoUtil = mockk(relaxed = true)
        validatorUtil = mockk(relaxed = true)
        userService = UserService(jwtUtil, cryptoUtil, validatorUtil, userRepository,)
    }

    @Test
    fun `login user with provided credentials`() {
        // given
        every { userRepository.findByCredentials(any(), any()) } returns USER_ENTITY
        every { jwtUtil.generateToken(any()) } returns TOKEN
        // when
        val result = userService.loginUser(LOGIN_DTO)
        //
        verify { userRepository.findByCredentials(any(), any()) }
        verify { jwtUtil.generateToken(any()) }
        verify { cryptoUtil.hash(any()) }
        assertTrue(result is LoginResult.LoggedIn)
        assertEquals(TOKEN_DTO, (result as LoginResult.LoggedIn).token)
    }

    @Test
    fun `login fails with non-existing identity`() {
        // given
        every { userRepository.findByCredentials(any(), any()) } throws UserNotFoundException()
        // when
        val result = userService.loginUser(LOGIN_DTO)
        // then
        verify { cryptoUtil.hash(any()) }
        verify { userRepository.findByCredentials(any(), any()) }
        assertEquals(LoginResult.NotFound, result)
    }

    @Test
    fun `login fails with invalid credentials`() {
        // given
        every { userRepository.findByCredentials(any(), any()) } throws UserCredentialsException()
        // when
        val result = userService.loginUser(LOGIN_DTO)
        // then
        verify { cryptoUtil.hash(any()) }
        verify { userRepository.findByCredentials(any(), any()) }
        assertEquals(LoginResult.InvalidCredentials, result)
    }

    @Test
    fun `user is registered with valid data`() {
        // given
        every { userRepository.createWithProfile(any(), any()) } returns USER_ENTITY
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        verify { cryptoUtil.hash(any()) }
        verify { userRepository.createWithProfile(any(), any()) }
        assertEquals(RegisterResult.Registered, result)
    }

    @Test
    fun `duplicate user is not registered`() {
        // given
        every { userRepository.createWithProfile(any(), any()) } throws UserExistsException()
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        verify { userRepository.createWithProfile(any(), any()) }
        assertEquals(RegisterResult.DuplicateProfile, result)
    }

    @Test
    fun `users with invalid email is not registered`() {
        // given
        every { validatorUtil.checkEmail(any()) } throws ValidationException()
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        assertEquals(RegisterResult.InvalidProfile, result)
    }

    @Test
    fun `users with invalid password is not registered`() {
        // given
        every { validatorUtil.checkPassword(any()) } throws ValidationException()
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        assertEquals(RegisterResult.InvalidProfile, result)
    }

    companion object {
        private const val EMAIL = "john.doe@example.com"
        private const val TOKEN = "some.jwt.token"

        private val USER_ENTITY = UserEntity(
            id = "some#random#string",
            email = EMAIL,
        )

        private val LOGIN_DTO = LoginDto(
            email = EMAIL,
            password = "!John@doe1",
        )

        private val TOKEN_DTO = TokenDto(
            token =  TOKEN,
        )

        private val REGISTER_DTO = RegisterDto(
            email = EMAIL,
            password = "!John@doe1",
        )
    }

}