package org.setruth.tools.logger

interface Interceptor<T> {

    fun log(tag: String, message: T, priority: Int, chain: Chain, vararg args: Any)

    val isLoggable: (T) -> Boolean get() = { true }
}
