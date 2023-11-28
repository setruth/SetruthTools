package org.setruth.tools.logger.interceptor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.logDispatcher

private const val CHANNEL_CAPACITY = 50

class LinearInterceptor : Interceptor<Any> {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * A queue to cache log in memory
     */
    private val channel = Channel<Event>(CHANNEL_CAPACITY)

    init {
        scope.launch(logDispatcher) {
            channel.consumeEach { event ->
                try {
                    event.apply { chain.proceed(tag, message, priority) }
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun log(tag: String, message: Any, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) {
            scope.launch { channel.send(Event(tag, message, priority, chain)) }
        } else {
            chain.proceed(tag, message, priority)
        }
    }


    data class Event(val tag: String, val message: Any, val priority: Int, val chain: Chain)
}