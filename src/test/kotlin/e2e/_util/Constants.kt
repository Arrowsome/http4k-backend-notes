package e2e._util

const val BASE_URL = "http://0.0.0.0"

const val REGISTER_JSON = """
            {
                "email": "john.doe@example.com",
                "password": "!John@Doe1"
            }
        """

const val REGISTER_JSON_WITH_INVALID_PASSWORD = """
            {
                "email": "john.doe@example.com",
                "password": "!Jo@Do1"
            }
        """

const val LOGIN_JSON = """
            {
                "email": "john.doe@example.com",
                "password": "!John@Doe1"
            }
        """

const val LOGIN_JSON_WITH_DIFF_PASSWORD = """
            {
                "email": "john.doe@example.com",
                "password": "!John+Doe1"
            }
        """