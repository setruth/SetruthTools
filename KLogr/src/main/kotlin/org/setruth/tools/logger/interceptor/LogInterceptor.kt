package org.setruth.tools.logger.interceptor

import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.model.Log
import java.util.*

class LogInterceptor : Interceptor<Any> {
    override fun log(tag: String, message: Any, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) chain.proceed(tag, Log(UUID.randomUUID().toString(), message), priority, args)
    }
}
