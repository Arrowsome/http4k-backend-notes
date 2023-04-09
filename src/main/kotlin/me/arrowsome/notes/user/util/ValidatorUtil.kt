package me.arrowsome.notes.user.util

object ValidatorUtil {

    fun isEmailValid(email: String): Boolean {
        return Regex(EMAIL_PATTERN).matches(email)
    }

    private const val EMAIL_PATTERN = "\\S+@\\S\\.\\S"

    fun isPasswordValid(password: String): Boolean {
        return Regex(PASSWORD_PATTER).matches(password)
    }

    // at least one character, at least one digit, and at least 8 characters long
    private const val PASSWORD_PATTER = "^(?=.*[A-Za-z])(?=.*[1-9])[A-Za-z1-9@\$!%*#?&]{8,}\$"

}

