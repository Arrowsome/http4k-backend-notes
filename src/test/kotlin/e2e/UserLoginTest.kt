package e2e

import e2e._util.*
import notes.LOGIN_URL
import notes.REGISTER_URL
import notes.backend
import notes.user.UserLenses
import notes.user.util.JwtUtil
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.*

class UserLoginTest {
    private lateinit var server: Http4kServer
    private lateinit var database: AppTestDatabase

    @BeforeEach
    fun setup() {
        database = AppTestDatabase.getInstance()
        database.drop()
        server = backend(testDi).asServer(Jetty(port = 0)).start()
    }

    @AfterEach
    fun teardown() {
        database.userCollection.drop()
        server.stop()
    }

    @Test
    fun `existing user logins`() {
        // given
        val registerUrl = "${BASE_URL}:${server.port()}$REGISTER_URL"
        val registerRequest = Request(Method.POST, registerUrl).body(REGISTER_JSON)
        val loginUrl = "${BASE_URL}:${server.port()}$LOGIN_URL"
        val loginRequest = Request(Method.POST, loginUrl).body(LOGIN_JSON)
        // when
        val registerResponse = OkHttp().invoke(registerRequest)
        val loginResponse = OkHttp().invoke(loginRequest)
        val entities = database.userCollection.find().toList()
        val tokenDto = UserLenses.tokenLens.extract(loginResponse)
        val token = JwtUtil.verifyToken(tokenDto.token)
        // then
        Assertions.assertEquals(Status.CREATED, registerResponse.status)
        Assertions.assertEquals(Status.OK, loginResponse.status)
        Assertions.assertNotNull(tokenDto)
        Assertions.assertNotNull(token)
        Assertions.assertEquals(entities.first().id.toString(), token.getClaim("user_id").asString())
    }

    @Test
    fun `existing user with invalid credentials can not login`() {
        // given
        val registerUrl = "${BASE_URL}:${server.port()}$REGISTER_URL"
        val registerRequest = Request(Method.POST, registerUrl).body(REGISTER_JSON)
        val loginUrl = "${BASE_URL}:${server.port()}$LOGIN_URL"
        val loginRequest = Request(Method.POST, loginUrl).body(LOGIN_JSON_WITH_DIFF_PASSWORD)
        // when
        val registerResponse = OkHttp().invoke(registerRequest)
        val loginResponse = OkHttp().invoke(loginRequest)
        // then
        Assertions.assertEquals(Status.CREATED, registerResponse.status)
        Assertions.assertEquals(Status.UNAUTHORIZED, loginResponse.status)
    }

    @Test
    fun `non-existing user can not logins`() {
        // given
        val loginUrl = "${BASE_URL}:${server.port()}$LOGIN_URL"
        val loginRequest = Request(Method.POST, loginUrl).body(LOGIN_JSON)
        // when
        val loginResponse = OkHttp().invoke(loginRequest)
        // then
        Assertions.assertEquals(Status.NOT_FOUND, loginResponse.status)
    }

}