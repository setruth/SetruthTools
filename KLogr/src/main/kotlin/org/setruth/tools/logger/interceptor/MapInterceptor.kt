package org.setruth.tools.logger.interceptor

import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.log

class MapInterceptor<K, V> : Interceptor<Map<K, V>> {
    override fun log(tag: String, message: Map<K, V>, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) {
            chain.proceed(tag, message.log(4), priority, args)
        } else {
            chain.proceed(tag, message, priority, args)
        }
    }
}
