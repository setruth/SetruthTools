package org.setruth.tools.logger

class Chain(
    private val interceptors: List<Interceptor<in Nothing>>,
    private val index: Int = 0
) {
    fun proceed(tag: String, message: Any, priority: Int, vararg args: Any) {
        val next = Chain(interceptors, index + 1)// new Chain every time to avoid multi-thread problem(index is val)
        try {
            @Suppress("UNCHECKED_CAST")
            (interceptors.getOrNull(index) as? Interceptor<Any>)?.log(tag, message, priority, next, *args)
        } catch (e: Exception) {
            println("Chain.proceed[tag, message, priority, args]: e=${e}")
        }
    }
}
