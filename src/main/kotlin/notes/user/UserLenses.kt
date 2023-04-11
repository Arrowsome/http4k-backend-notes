package notes.user

import notes.user.model.LoginDto
import notes.user.model.RegisterDto
import notes.user.model.TokenDto
import org.http4k.core.Body
import org.http4k.format.Jackson.auto

object UserLenses {
    val tokenLens = Body.auto<TokenDto>().toLens()
    val loginLens = Body.auto<LoginDto>().toLens()
    val registerLens = Body.auto<RegisterDto>().toLens()
}