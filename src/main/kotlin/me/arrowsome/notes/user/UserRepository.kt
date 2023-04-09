package me.arrowsome.notes.user

import com.mongodb.client.MongoCollection
import me.arrowsome.notes.user.model.UserEntity
import me.arrowsome.notes.user.model.UserNotFoundException
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class UserRepository(
    private val userCollection: MongoCollection<UserEntity>,
) {

    fun findUserByCredentials(email: String, password: String): UserEntity {
        return userCollection.findOne(
            and(
                UserEntity::email eq email,
                UserEntity::password eq password,
            ),
        ) ?: throw UserNotFoundException()
    }

    fun createUserWithProfile(user: UserEntity) {
        userCollection.insertOne(user).let { result ->
            if (!result.wasAcknowledged())
                throw Exception()
        }
    }

}