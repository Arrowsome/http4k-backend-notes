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
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class UserRegistrationTest {
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
        server.stop()
    }

    @Test
    fun `register user`() {
        // given
        val url = "$BASE_URL:${server.port()}$REGISTER_URL"
        val request = Request(Method.POST, url).body(REGISTER_JSON)
        // when
        val response = OkHttp().invoke(request)
        val entities = database.userCollection.find().toList()
        // then
        assertEquals(CREATED, response.status)
        assertEquals(1, entities.count())
        val user = entities.first()
        assertNotNull(user.id)
        assertEquals(24, user.id.toString().length)
        assertEquals(user.email, "john.doe@example.com")
    }

    @Test
    fun `register duplicate user fails`() {
        // given
        val url = "$BASE_URL:${server.port()}$REGISTER_URL"
        val request = Request(Method.POST, url).body(REGISTER_JSON)
        // when
        val firstRequest = OkHttp().invoke(request)
        val dupRequest = OkHttp().invoke(request)
        val entities = database.userCollection.find().toList()
        // then
        assertEquals(CREATED, firstRequest.status)
        assertEquals(CONFLICT, dupRequest.status)
        assertEquals(1, entities.count())
        val user = entities.first()
        assertNotNull(user.id)
        assertEquals(24, user.id.toString().length)
        assertEquals(user.email, "john.doe@example.com")
    }

    @Test
    fun `invalid password leads to registration failure`() {
        // given
        val url = "$BASE_URL:${server.port()}$REGISTER_URL"
        val request = Request(Method.POST, url).body(REGISTER_JSON_WITH_INVALID_PASSWORD)
        // when
        val response = OkHttp().invoke(request)
        val entities = database.userCollection.find().toList()
        // then
        assertEquals(BAD_REQUEST, response.status)
        assertEquals(0, entities.count())
    }

}