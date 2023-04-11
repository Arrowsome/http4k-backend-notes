package notes.user.util

import org.mindrot.jbcrypt.BCrypt

object CryptoUtil {

    fun hashBcrypt(password: String): String {
        return BCrypt.hashpw(password, SALT)
    }

    private const val SALT = "\$2a\$10\$thisisafixedsaltforbcrypthashing"

}