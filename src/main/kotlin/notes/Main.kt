package notes

import notes.common.di.di
import notes.user.UserController
import org.http4k.core.Method.POST
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.kodein.di.DI
import org.kodein.di.instance

fun main() {
    backend(di)
        .asServer(Jetty(port = 9000))
        .start()
}

fun backend(di: DI): RoutingHttpHandler {
    val userController by di.instance<UserController>()

    return routes(
        // User
        REGISTER_URL bind POST to userController::registerUser,
        LOGIN_URL bind POST to userController::loginUser
    )
}

private const val BASE_URL = "/api"
private const val USERS_URL = "$BASE_URL/users"
const val REGISTER_URL = USERS_URL
const val LOGIN_URL = "$USERS_URL/login"