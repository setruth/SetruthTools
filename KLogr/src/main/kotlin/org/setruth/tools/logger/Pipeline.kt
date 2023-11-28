package org.setruth.tools.logger

interface Pipeline<LOG, LOGS> {

    /**
     * 将日志转为二进制数组
     */
    fun toByteArray(log: LOG): ByteArray

    /**
     * 打包日志
     */
    fun pack(logs: List<LOG>): LOGS

    /**
     * 上传 [logBatch] 到服务器
     */
    suspend fun upload(logBatch: LOGS): Boolean
}
