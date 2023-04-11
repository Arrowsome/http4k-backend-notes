package notes.user

import notes.user.model.*
import notes.user.util.CryptoUtil
import notes.user.util.JwtUtil
import notes.user.util.ValidatorUtil

class UserService(
    private val jwtUtil: JwtUtil,
    private val cryptoUtil: CryptoUtil,
    private val validatorUtil: ValidatorUtil,
    private val userRepository: UserRepository,
) {

    fun loginUser(login: LoginDto): LoginResult {
        return try {
            var (email, password) = login
            password = cryptoUtil.hashBcrypt(password)

            if (!userRepository.anyUserWithEmail(email))
                throw UserNotFoundException()

            val user = userRepository.findUserByCredentials(email, password)

            val token = jwtUtil.generateToken(user.id.toString())

            LoginResult.LoggedIn(TokenDto(token))
        } catch (exc: UserNotFoundException) {
            LoginResult.NotFound
        } catch (exc: UserCredentialsException) {
            LoginResult.InvalidCredentials
        }
    }

    fun registerUser(register: RegisterDto): RegisterResult {
        return try {
            var (email, password) = register
            if (!validatorUtil.isEmailValid(email))
                throw ValidationException()
            if (!validatorUtil.isPasswordValid(password))
                throw ValidationException()
            password = cryptoUtil.hashBcrypt(password)

            if (userRepository.anyUserWithEmail(email))
                throw UserExistsException()

            userRepository.createUserWithProfile(
                UserEntity(
                    email = email,
                    password = password,
                )
            )

            RegisterResult.Registered
        } catch (exc: UserExistsException) {
            RegisterResult.DuplicateProfile
        } catch (exc: ValidationException) {
            RegisterResult.InvalidProfile
        }
    }

}

class ValidationException : Exception()