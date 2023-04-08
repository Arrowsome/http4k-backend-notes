package me.arrowsome.notes.user

import me.arrowsome.notes.user.model.LoginDto
import me.arrowsome.notes.user.model.RegisterDto
import me.arrowsome.notes.user.model.TokenDto
import org.http4k.core.Body
import org.http4k.format.Jackson.auto

object UserLenses {
    val tokenLens = Body.auto<TokenDto>().toLens()
    val loginLens = Body.auto<LoginDto>().toLens()
    val registerLens = Body.auto<RegisterDto>().toLens()
}