package org.setruth.tools.logger.interceptor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.setruth.tools.logger.Chain
import org.setruth.tools.logger.Interceptor
import org.setruth.tools.logger.model.LogBatch
import org.setruth.tools.logger.Pipeline

class UploadInterceptor<LOG, LOGS>(private val pipeline: Pipeline<LOG, LOGS>) : Interceptor<LogBatch<LOGS>> {
    private val scope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

    override fun log(tag: String, message: LogBatch<LOGS>, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message)) scope.launch {
        }
    }
}
