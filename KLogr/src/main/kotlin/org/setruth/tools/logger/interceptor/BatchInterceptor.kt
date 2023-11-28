package org.setruth.tools.logger.interceptor

import kotlinx.coroutines.*
import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.model.Log
import org.setruth.tools.logger.model.LogBatch
import org.setruth.tools.logger.Pipeline
import org.setruth.tools.logger.logDispatcher

class BatchInterceptor<LOG, LOGS>(
    private val size: Int,
    private val interval: Long,
    private val pipeline: Pipeline<LOG, LOGS>
) : Interceptor<Log<LOG>> {

    private val list = mutableListOf<Log<LOG>>()
    private var lastFlushTime = 0L
    private val scope = CoroutineScope(SupervisorJob())
    private var flushJob: Job? = null

    override fun log(tag: String, message: Log<LOG>, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) {
            list.add(message)
            flushJob?.cancel()

            if (canFlush()) {
                flush(chain, tag, priority)
            } else {
                flushJob = delayFlush(chain, tag, priority)
            }
        }
    }
    private fun canFlush() =
        lastFlushTime != 0L && System.currentTimeMillis() - lastFlushTime >= interval || list.size >= size

    private fun flush(chain: Chain, tag: String, priority: Int) {
        val logs = pipeline.pack(list.map { it.data })
        val logBatch = LogBatch(list.map { it.id }, logs)
        chain.proceed(tag, logBatch, priority)
        list.clear()
        lastFlushTime = System.currentTimeMillis()
    }

    private fun delayFlush(chain: Chain, tag: String, priority: Int) = scope.launch(logDispatcher) {
        val delayTime = if (lastFlushTime == 0L) interval else interval - (System.currentTimeMillis() - lastFlushTime)
        delay(delayTime)
        flush(chain, tag, priority)
    }
}
