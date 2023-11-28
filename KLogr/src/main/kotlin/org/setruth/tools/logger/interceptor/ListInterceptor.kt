package org.setruth.tools.logger.interceptor

import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.log

class ListInterceptor<T>(private val map: ((T) -> String)?) : Interceptor<Iterable<T>> {
    override fun log(tag: String, message: Iterable<T>, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) {
            val messageList = message.log { map?.invoke(it) ?: it.toString() }
            chain.proceed(tag, messageList, priority, args)
        } else {
            chain.proceed(tag, message, priority, args)
        }
    }
}
