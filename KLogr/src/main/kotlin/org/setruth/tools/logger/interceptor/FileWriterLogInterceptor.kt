package org.setruth.tools.logger.interceptor

import kotlinx.coroutines.*
import okio.BufferedSink
import okio.appendingSink
import okio.buffer
import okio.gzip
import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.logDispatcher
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileWriterLogInterceptor(
    private val dir: String,
    private val interval: Long = 300L
) : Interceptor<Any> {
    private var bufferedSink: BufferedSink? = null
    private var logFile = File(getFileName())
    private var lastFlushTime = 0L
    private var flushJob: Job? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun canFlush() =
        lastFlushTime != 0L && System.currentTimeMillis() - lastFlushTime >= interval

    override fun log(tag: String, message: Any, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) {

            flushJob?.cancel()

            if (canFlush()) {
                delayFlush(chain, message, tag, priority)
            } else {
                flushJob = delayFlush(chain, message, tag, priority)
            }
        }
    }

    // 以今天日期为文件名
    private fun getToday(): String = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
    private fun getFileName() = "$dir${File.separator}${getToday()}.log"

    // 不关流，复用 bufferedSink 对象
    private fun checkSink(): BufferedSink {
        if (bufferedSink == null) {
            bufferedSink = logFile.appendingSink().gzip().buffer()
        }
        return bufferedSink!!
    }

    private fun delayFlush(chain: Chain, message: Any, tag: String, priority: Int) = scope.launch(logDispatcher) {
        val delayTime = if (lastFlushTime == 0L) interval else interval - (System.currentTimeMillis() - lastFlushTime)
        delay(delayTime)
        flush(chain, message, tag, priority)
    }

    private fun flush(chain: Chain, message: Any, tag: String, priority: Int) {
        val sink = checkSink()
        sink.writeUtf8("[$tag] $message")
        sink.writeUtf8("\n")
        chain.proceed(tag,message,priority)
        lastFlushTime = System.currentTimeMillis()
    }
}
