package me.arrowsome.notes.user

import me.arrowsome.notes.user.model.UserEntity

class UserRepository {

    fun findByCredentials(email: String, password: String): UserEntity = TODO()

    fun createWithProfile(email: String, password: String) : UserEntity = TODO()

}