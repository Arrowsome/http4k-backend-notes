package unit.notes.user

import com.mongodb.client.MongoCollection
import com.mongodb.client.result.InsertOneResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import notes.user.UserRepository
import notes.user.model.UserCredentialsException
import notes.user.model.UserEntity
import notes.user.model.UserNotFoundException
import org.bson.BsonString
import org.bson.conversions.Bson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.findOne
import org.litote.kmongo.newId

class UserRepositoryTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userCollection: MongoCollection<UserEntity>

    @BeforeEach
    fun setup() {
        userCollection = mockk()
        userRepository = UserRepository(userCollection)
    }

    @Test
    fun `find and return user by credential from datasource`() {
        // given
        every { userCollection.find(any<Bson>()).projection(any<Bson>()).limit(1).firstOrNull() } returns USER_ENTITY
        // when
        val entity = userRepository.findUserByCredentials(EMAIL, PASSWORD)
        // then
        verify { userCollection.find(any<Bson>()).projection(any<Bson>()).limit(1).firstOrNull() }
        assertEquals(USER_ENTITY, entity)
    }

    @Test
    fun `not finding a user results in exception`() {
        // given
        every { userCollection.find(any<Bson>()).projection(any<Bson>()).limit(1).firstOrNull() } returns null
        // when
        val exception = assertThrows<UserCredentialsException> {
            userRepository.findUserByCredentials(EMAIL, PASSWORD)
        }
        // then
        verify { userCollection.find(any<Bson>()).projection(any<Bson>()).limit(1).firstOrNull() }
        assertNotNull(exception)
    }

    @Test
    fun `create a user with valid profile`() {
        // given
        every { userCollection.insertOne(any()) } returns INSERT_ONE_ACKNOWLEDGED
        // when
        userRepository.createUserWithProfile(USER_ENTITY)
        // then
        verify { userCollection.insertOne(any()) }
    }

    @Test
    fun `unexpected database failing insert leads to exception`() {
        // given
        every { userCollection.insertOne(any()) } returns INSERT_ONE_UNACKNOWLEDGED
        // when
        val exception = assertThrows<Exception> {
            userRepository.createUserWithProfile(USER_ENTITY)
        }
        // then
        verify { userCollection.insertOne(any()) }
        assertNotNull(exception)
    }

    @Test
    fun `existing user if found`() {
        // given
        every { userCollection.findOne(any<Bson>()) } returns USER_ENTITY
        // when
        val result = userRepository.anyUserWithEmail(EMAIL)
        // then
        verify { userCollection.findOne(any<Bson>()) }
        assertTrue(result)
    }

    @Test
    fun `non existing user if not found`() {
        // given
        every { userCollection.findOne(any<Bson>()) } returns null
        // when
        val result = userRepository.anyUserWithEmail(EMAIL)
        // then
        verify { userCollection.findOne(any<Bson>()) }
        assertFalse(result)
    }

    @Test
    fun `get salt for user by email address`() {

    }


    companion object {
        private const val EMAIL = "john@doe@example.com"
        private const val PASSWORD = "!John@Doe9"

        private val USER_ENTITY = UserEntity(
            id = newId(),
            email = EMAIL,
            password = null,
        )

        private val INSERT_ONE_ACKNOWLEDGED = InsertOneResult.acknowledged(
            BsonString(newId<UserEntity>().toString()),
        )

        private val INSERT_ONE_UNACKNOWLEDGED = InsertOneResult.unacknowledged()
    }

}