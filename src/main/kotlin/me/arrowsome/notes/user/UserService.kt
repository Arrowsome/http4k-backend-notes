package me.arrowsome.notes.user

import me.arrowsome.notes.user.dto.LoginDto
import me.arrowsome.notes.user.dto.RegisterDto
import me.arrowsome.notes.user.sealed.LoginResult
import me.arrowsome.notes.user.sealed.RegisterResult

class UserService {

    fun loginUser(login: LoginDto): LoginResult = TODO()

    fun registerUser(register: RegisterDto): RegisterResult = TODO()

}