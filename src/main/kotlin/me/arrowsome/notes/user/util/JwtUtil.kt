package me.arrowsome.notes.user.util

import com.auth0.jwt.interfaces.DecodedJWT

object JwtUtil {
    fun generateToken(userId: String): String = TODO()

    fun verifyToken(token: String): DecodedJWT = TODO()
}