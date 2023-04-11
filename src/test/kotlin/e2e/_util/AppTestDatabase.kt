package e2e._util

import com.mongodb.client.MongoClient
import notes.common.db.AppDatabase
import notes.common.db.DatabaseBuilder
import org.litote.kmongo.KMongo

class AppTestDatabase private constructor(client: MongoClient) : AppDatabase(client) {

    companion object : DatabaseBuilder<AppTestDatabase>() {

        override fun createInstance(): AppTestDatabase {
            val client = KMongo.createClient()
            return AppTestDatabase(client)
        }

        override val dbName: String
            get() = "test_note"
    }

    fun drop() {
        mongodb.drop()
    }

    fun cleanup() {
        drop()
        close()
        resetInstance()
    }

}
