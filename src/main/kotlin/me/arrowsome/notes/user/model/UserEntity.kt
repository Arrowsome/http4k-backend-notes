package me.arrowsome.notes.user.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class UserEntity(
    @BsonId
    val id: Id<UserEntity>? = null,
    val email: String,
    val password: String? = null,
)