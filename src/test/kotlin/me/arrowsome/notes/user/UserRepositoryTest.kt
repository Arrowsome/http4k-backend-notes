package me.arrowsome.notes.user

import com.mongodb.client.MongoCollection
import com.mongodb.client.result.InsertOneResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.arrowsome.notes.user.UserRepository
import me.arrowsome.notes.user.model.UserEntity
import me.arrowsome.notes.user.model.UserNotFoundException
import org.bson.BsonString
import org.bson.conversions.Bson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
        every { userCollection.findOne(any<Bson>()) } returns USER_ENTITY
        // when
        val entity = userRepository.findUserByCredentials(EMAIL, PASSWORD)
        // then
        assertEquals(USER_ENTITY, entity)
    }

    @Test
    fun `not finding a user results in exception`() {
        // given
        every { userCollection.findOne(any<Bson>()) } returns null
        // when
        val exception = assertThrows<UserNotFoundException> {
            val entity = userRepository.findUserByCredentials(EMAIL, PASSWORD)
        }
        // then
        verify { userCollection.findOne(any<Bson>()) }
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