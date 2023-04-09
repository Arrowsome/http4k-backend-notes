package me.arrowsome.notes.user.util

import org.mindrot.jbcrypt.BCrypt

object CryptoUtil {

    fun hashBcrypt(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

}