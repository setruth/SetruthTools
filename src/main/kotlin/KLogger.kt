import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.setruth.logger.KLogger
import org.setruth.logger.initKLoggerConfig

/**
 * TODO
 * @author setruth
 * @date 2023/9/1
 * @time 22:33
 */
fun main(): Unit = runBlocking {
    initKLoggerConfig{
        storage("C:\\Users\\Setruth\\Desktop\\logger\\")
        showDebugLog(false)
    }
    KLogger.info("开始执行")
    KLogger.debug ("debug测试")
    KLogger.warn { "线程暂停" and "${1.5}s" }
    delay(1500L)

    launch(Dispatchers.IO) {
        KLogger.success("IO协程") {
            "debug内容" and "内容1"
            "debug内容" and "内容2"
        }
    }
    KLogger.err { "结束" and "不正常结束" }
}