package custom.project.utils

import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer

inline fun <T> T.then(block: (T) -> T): T = block(this)

inline fun <T> useDisposable(block: (Disposable) -> T): T {
    val disposable = Disposer.newDisposable()
    return try {
        block(disposable)
    } finally {
        disposable.dispose()
    }
}