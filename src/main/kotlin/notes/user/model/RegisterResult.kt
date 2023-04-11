package notes.user.model

sealed interface RegisterResult {
    object Registered : RegisterResult
    object InvalidProfile : RegisterResult
    object DuplicateProfile : RegisterResult
}
