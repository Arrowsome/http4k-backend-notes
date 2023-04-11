package e2e._util

import notes.common.di.di
import org.kodein.di.Copy
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val testDi = DI {
    extend(di = di, allowOverride = true, copy = Copy.allBut { tag("userCollection") })

    bindSingleton(overrides = true) {
        AppTestDatabase.getInstance().userCollection
    }
}