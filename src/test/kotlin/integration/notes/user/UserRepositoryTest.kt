package integration.notes.user

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import notes.user.UserRepository
import notes.user.model.UserCredentialsException
import notes.user.model.UserEntity
import notes.user.model.UserNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class UserRepositoryTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userCollection: MongoCollection<UserEntity>
    private lateinit var database: MongoDatabase
    private lateinit var mongoClient: MongoClient

    @BeforeEach
    fun setup() {
        mongoClient = KMongo.createClient()
        database = mongoClient.getDatabase("test_notes")
        userCollection = database.getCollection<UserEntity>("profile")
        userRepository = UserRepository(userCollection)

        userCollection.insertMany(
            listOf(
                EXISTING_USER_ENTITY,
            )
        )
    }

    @AfterEach
    fun teardown() {
        userCollection.drop()
    }

    @Test
    fun `find a user with existing data`() {
        // given
        // when
        val user = userRepository.findUserByCredentials(EMAIL, PASSWORD)
        // then
        assertEquals(EXISTING_USER_ENTITY.email, user.email)
        assertNull(user.password)
        assertNotNull(user.id)
    }

    @Test
    fun `find a non existing user throws an error`() {
        // given
        // when
        val exception = assertThrows<UserCredentialsException> {
            userRepository.findUserByCredentials(EMAIL.substring(1), PASSWORD)
        }
        // then
        assertNotNull(exception)
    }

    @Test
    fun `user profile is created`() {
        // given
        // when
        userRepository.createUserWithProfile(NON_EXISTING_USER_ENTITY)
        val size = userCollection.countDocuments()
        // then
        assertEquals(2, size)
    }

    @Test
    fun `duplicate profile is not created`() {
        // given
        // when
        val exception = assertThrows<Exception> {
            userRepository.createUserWithProfile(EXISTING_USER_ENTITY)
        }
        // then
        assertNotNull(exception)
    }

    @Test
    fun `existing user if found`() {
        // given
        // when
        val result = userRepository.anyUserWithEmail(EXISTING_USER_ENTITY.email)
        // then
        assertTrue(result)
    }

    @Test
    fun `non existing user if not found`() {
        // given
        // when
        val result = userRepository.anyUserWithEmail(NON_EXISTING_USER_ENTITY.email)
        // then
        assertFalse(result)
    }


    companion object {
        private const val EMAIL = "john.doe@example.com"
        private const val PASSWORD = "!John@Doe9"

        private val EXISTING_USER_ENTITY = UserEntity(
            email = EMAIL,
            password = PASSWORD,
        )

        private val NON_EXISTING_USER_ENTITY = EXISTING_USER_ENTITY.copy(
            email = "jane.doe@example.com"
        )
    }

}