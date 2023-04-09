package me.arrowsome.notes.user

import me.arrowsome.notes.user.util.JwtUtil
import me.arrowsome.notes.user.util.CryptoUtil
import me.arrowsome.notes.user.util.ValidationException
import me.arrowsome.notes.user.util.ValidatorUtil
import me.arrowsome.notes.user.model.*

class UserService(
    private val jwtUtil: JwtUtil,
    private val cryptoUtil: CryptoUtil,
    private val validatorUtil: ValidatorUtil,
    private val userRepository: UserRepository,
) {

    fun loginUser(login: LoginDto): LoginResult {
        return try {
            var (email, password) = login
            password = cryptoUtil.hash(password)

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
            validatorUtil.checkEmail(email)
            validatorUtil.checkPassword(password)
            password = cryptoUtil.hash(password)

            userRepository.createUserWithProfile(UserEntity(
                email = email,
                password = password,
            ))

            RegisterResult.Registered
        } catch (exc: UserExistsException) {
            RegisterResult.DuplicateProfile
        } catch (exc: ValidationException) {
            RegisterResult.InvalidProfile
        }
    }

}