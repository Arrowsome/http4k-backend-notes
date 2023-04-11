package notes.user

import com.mongodb.client.MongoCollection
import notes.user.model.UserCredentialsException
import notes.user.model.UserEntity
import notes.user.model.UserNotFoundException
import org.litote.kmongo.*

class UserRepository(
    private val userCollection: MongoCollection<UserEntity>,
) {

    fun findUserByCredentials(email: String, password: String): UserEntity {
        return userCollection.find(
            and(
                UserEntity::email eq email,
                UserEntity::password eq password,
            ),
        )
            .projection(include(UserEntity::email))
            .limit(1)
            .firstOrNull() ?: throw UserCredentialsException()
    }

    fun createUserWithProfile(user: UserEntity) {
        userCollection.insertOne(user).let { result ->
            if (!result.wasAcknowledged())
                throw Exception()
        }
    }

    fun anyUserWithEmail(email: String): Boolean {
        return userCollection
            .findOne(UserEntity::email eq email) != null
    }

}