package notes.common.db

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import notes.user.model.UserEntity
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

open class AppDatabase protected constructor(private val mongoClient: MongoClient) {
    protected val mongodb = mongoClient.getDatabase(dbName)

    companion object : DatabaseBuilder<AppDatabase>() {

        override fun createInstance(): AppDatabase {
            val client = KMongo.createClient()
            return AppDatabase(client)
        }

        private const val USER_COLL = "profile"

        override val dbName: String
            get() = "test_note"
    }

    fun close() {
        mongoClient.close()
    }

    val userCollection: MongoCollection<UserEntity>
        get() = mongodb.getCollection<UserEntity>(USER_COLL)

}