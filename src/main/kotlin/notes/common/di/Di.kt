package notes.common.di

import notes.common.db.AppDatabase
import notes.common.db.DatabaseBuilder
import notes.user.UserController
import notes.user.UserLenses
import notes.user.UserRepository
import notes.user.UserService
import notes.user.util.CryptoUtil
import notes.user.util.JwtUtil
import notes.user.util.ValidatorUtil
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val di = DI {
    bindSingleton { UserLenses }
    bindSingleton { JwtUtil }
    bindSingleton { CryptoUtil }
    bindSingleton { ValidatorUtil }
    bindSingleton { AppDatabase.getInstance().userCollection }

    bindSingleton {
        UserController(
            userLenses = instance(),
            userService = instance(),
        )
    }

    bindSingleton {
        UserService(
            jwtUtil = instance(),
            cryptoUtil = instance(),
            validatorUtil = instance(),
            userRepository = instance(),
        )
    }

    bindSingleton {
        UserRepository(
            userCollection = instance()
        )
    }


}