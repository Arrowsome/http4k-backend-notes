package me.arrowsome.notes.user.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.time.Instant
import java.time.temporal.ChronoUnit

object JwtUtil {

    fun generateToken(userId: String): String {
        return try {
            JWT.create()
                .withClaim(ID_CLAIM, userId)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .sign(algorithm)
        } catch (exc: Exception) {
            throw JwtGenerationException()
        }
    }

    fun verifyToken(token: String): DecodedJWT {
        return try {
            JWT.require(algorithm)
                .build()
                .verify(token)
        } catch (exc: Exception) {
            throw JwtVerificationException()
        }
    }

    private val algorithm = Algorithm.HMAC256("my_jwt_secret")
    private const val ID_CLAIM = "user_id"
    private const val ISSUER = "me.arrowsome.notes"
    private const val AUDIENCE = "user"
}

class JwtGenerationException : Exception()
class JwtVerificationException : Exception()