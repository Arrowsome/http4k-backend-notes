package me.arrowsome.notes.user

import me.arrowsome.notes.user.dto.LoginDto
import me.arrowsome.notes.user.dto.RegisterDto
import me.arrowsome.notes.user.dto.TokenDto
import org.http4k.core.Body
import org.http4k.format.Jackson.auto

object UserLenses {
    val tokenLens = Body.auto<TokenDto>().toLens()
    val loginLens = Body.auto<LoginDto>().toLens()
    val registerLens = Body.auto<RegisterDto>().toLens()
}