package org.setruth.tools.logger

import org.setruth.tools.logger.interceptor.*

fun Logger.simpleInit(size: Int, duration: Long, pipeline: Pipeline<*, *>, dir: String) {
    Logger.apply {
        addInterceptor(LogcatInterceptor())
        addInterceptor(LinearInterceptor())
        addInterceptor(LogInterceptor())
        addInterceptor(FileWriterLogInterceptor(dir, duration))
        addInterceptor(BatchInterceptor(size, duration, pipeline))
        addInterceptor(UploadInterceptor(pipeline))
    }
}

fun Logger.defaultInit() {
    Logger.apply {
        addInterceptor(LogcatInterceptor())
    }
}