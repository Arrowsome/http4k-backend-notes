package unit.notes.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import notes.user.UserRepository
import notes.user.UserService
import notes.user.ValidationException
import notes.user.util.JwtUtil
import notes.user.util.CryptoUtil
import notes.user.util.ValidatorUtil
import notes.user.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.newId

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
        every { userRepository.findUserByCredentials(any(), any()) } returns USER_ENTITY
        every { jwtUtil.generateToken(any()) } returns TOKEN
        every { userRepository.anyUserWithEmail(any()) } returns true
        // when
        val result = userService.loginUser(LOGIN_DTO)
        // then
        verify { userRepository.anyUserWithEmail(any()) }
        verify { userRepository.findUserByCredentials(any(), any()) }
        verify { jwtUtil.generateToken(any()) }
        verify { cryptoUtil.hashBcrypt(any()) }
        assertTrue(result is LoginResult.LoggedIn)
        assertEquals(TOKEN_DTO, (result as LoginResult.LoggedIn).token)
    }

    @Test
    fun `login fails with non-existing identity`() {
        // given
        every { userRepository.anyUserWithEmail(any()) } returns false
        every { userRepository.findUserByCredentials(any(), any()) } throws UserNotFoundException()
        // when
        val result = userService.loginUser(LOGIN_DTO)
        // then
        verify { userRepository.anyUserWithEmail(any()) }
        verify { cryptoUtil.hashBcrypt(any()) }
        assertEquals(LoginResult.NotFound, result)
    }

    @Test
    fun `login fails with invalid credentials`() {
        // given
        every { userRepository.anyUserWithEmail(any()) } returns true
        every { userRepository.findUserByCredentials(any(), any()) } throws UserCredentialsException()
        // when
        val result = userService.loginUser(LOGIN_DTO)
        // then
        verify { userRepository.anyUserWithEmail(any()) }
        verify { cryptoUtil.hashBcrypt(any()) }
        verify { userRepository.findUserByCredentials(any(), any()) }
        assertEquals(LoginResult.InvalidCredentials, result)
    }

    @Test
    fun `user is registered with valid data`() {
        // given
        every { validatorUtil.isEmailValid(any()) } returns true
        every { validatorUtil.isPasswordValid(any()) } returns true
        every { userRepository.anyUserWithEmail(any()) } returns false
        every { userRepository.createUserWithProfile(any()) } returns Unit
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        verify { validatorUtil.isEmailValid(any()) }
        verify { validatorUtil.isPasswordValid(any()) }
        verify { userRepository.anyUserWithEmail(any()) }
        verify { cryptoUtil.hashBcrypt(any()) }
        verify { userRepository.createUserWithProfile(any()) }
        assertEquals(RegisterResult.Registered, result)
    }

    @Test
    fun `duplicate user is not registered`() {
        // given
        every { validatorUtil.isEmailValid(any()) } returns true
        every { validatorUtil.isPasswordValid(any()) } returns true
        every { userRepository.anyUserWithEmail(any()) } returns true
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        verify { validatorUtil.isEmailValid(any()) }
        verify { validatorUtil.isPasswordValid(any()) }
        verify { userRepository.anyUserWithEmail(any()) }
        assertEquals(RegisterResult.DuplicateProfile, result)
    }

    @Test
    fun `users with invalid email is not registered`() {
        // given
        every { validatorUtil.isEmailValid(any()) } throws ValidationException()
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        verify { validatorUtil.isEmailValid(any()) }
        assertEquals(RegisterResult.InvalidProfile, result)
    }

    @Test
    fun `user with invalid password is not registered`() {
        // given
        every { validatorUtil.isPasswordValid(any()) } throws ValidationException()
        // when
        val result = userService.registerUser(REGISTER_DTO)
        // then
        assertEquals(RegisterResult.InvalidProfile, result)
    }

    companion object {
        private const val EMAIL = "john.doe@example.com"
        private const val TOKEN = "some.jwt.token"

        private val USER_ENTITY = UserEntity(
            id = newId(),
            email = EMAIL,
            password = null,
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