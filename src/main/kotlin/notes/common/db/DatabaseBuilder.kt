package notes.common.db


abstract class DatabaseBuilder<T> protected constructor() {

    @Volatile
    private var instance: T? = null

    fun getInstance(): T {
        return instance ?: synchronized(this) {
            instance ?: createInstance().also { instance = it }
        }
    }

    fun resetInstance() {
        instance = null
    }

    protected abstract fun createInstance(): T

    abstract val dbName: String
}

