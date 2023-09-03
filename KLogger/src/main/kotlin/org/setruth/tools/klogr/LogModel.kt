package org.setruth.tools.klogr

/**
 * TODO
 * @author setruth
 * @date 2023/9/1
 * @time 10:29
 */
internal data class LogInfo(
    val logTag:String,
    val logThreadInfo: LogThreadInfo,
    val content:List<LogContentListItem>
)
internal data class LogThreadInfo(
    val threadName:String,
    val stackTraceElement: StackTraceElement,
)
data class LogContentListItem(
    val tag:String,
    val content:String
){
    override fun toString(): String="$tag : $content"
}