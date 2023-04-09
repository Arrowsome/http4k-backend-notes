package me.arrowsome.notes.user

import com.mongodb.client.MongoCollection
import me.arrowsome.notes.user.model.UserEntity
import me.arrowsome.notes.user.model.UserNotFoundException
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.include
import org.litote.kmongo.projection

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
            .firstOrNull() ?: throw UserNotFoundException()
    }

    fun createUserWithProfile(user: UserEntity) {
        userCollection.insertOne(user).let { result ->
            if (!result.wasAcknowledged())
                throw Exception()
        }
    }

}