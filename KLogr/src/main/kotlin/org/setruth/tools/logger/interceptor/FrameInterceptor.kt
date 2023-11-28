package org.setruth.tools.logger.interceptor

import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor

private const val HEADER =
    "┌──────────────────────────────────────────────────────────────────────────────────────────────────────"
private const val FOOTER =
    "└──────────────────────────────────────────────────────────────────────────────────────────────────────"
private const val LEFT_BORDER = '│'

class FrameInterceptor : Interceptor<Any> {
    override fun log(tag: String, message: Any, priority: Int, chain: Chain, vararg args: Any) {
        val msg = HEADER + "\n" + LEFT_BORDER + message + "\n" + FOOTER
        chain.proceed(tag, msg, priority, args)
    }
}
