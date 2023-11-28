package org.setruth.tools.logger.interceptor

import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Logger
import org.setruth.tools.logger.Interceptor
import java.io.PrintWriter
import java.io.StringWriter

class LogcatInterceptor : Interceptor<Any> {
    override fun log(tag: String, message: Any, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message) && Logger.curPriority <= priority && Logger.curPriority != Logger.NONE) {
            println("$priority, $tag, ${getFormatLog(message, *args)}")
        }
        chain.proceed(tag, message, priority, args)
    }

    /**
     * 格式化日志 或者 返回调用栈信息
     */
    private fun getFormatLog(message: Any, vararg args: Any) =
        if (message is Throwable) {
            getStackTraceString(message)
        } else {
            if (args.isNotEmpty()) {
                message.toString().format(args)
            } else {
                message.toString()
            }
        }

    private fun getStackTraceString(t: Throwable): String {
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    private fun String.format(args: Array<out Any>) = if (args.isEmpty()) this else String.format(this, *args)
}
