package org.setruth.tools.klogr

import java.io.*
import java.text.SimpleDateFormat


private val simpleDateTimeFormat = SimpleDateFormat("yyy/MM/dd(HH:mm:ss:SSS)")
private val simpleDateFormat = SimpleDateFormat("yyy-MM-dd")

/**
 * 核心内容，调用KLogger进行日志的打印执行
 */
class KLogr internal constructor(
    private val saveFolderPath: String,
    private val activeLogSave: Boolean,
    private val showDebugLog: Boolean,
    private val logMaxFileSize: Int,
) {
    //信息日志 蓝色
    fun info(logTag: String = "info", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.INFO.createLog(logTag, contentList)
        }
    }

    //警告日志 黄色
    fun warn(logTag: String = "warn", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.WARN.createLog(logTag, contentList)
        }
    }

    //错误日志 红色
    fun err(logTag: String = "err", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.ERR.createLog(logTag, contentList)
        }
    }

    //测试日志 紫色
    fun debug(logTag: String = "debug", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.DEBUG.createLog(logTag, contentList)
        }
    }

    //通过日志 绿色
    fun success(logTag: String = "success", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.SUCCESS.createLog(logTag, contentList)
        }
    }

    /**
     * 获取调用位置的线程信息
     * @return LogThreadInfo
     */
    private fun getThreadLogInfo() = Thread.currentThread().run {
        val nowStackIndex = stackTrace.indexOfLast {
            it.className == this@KLogr.javaClass.name
        }
        val stackTraceElement = stackTrace[nowStackIndex + 1]
        LogThreadInfo(name, stackTraceElement)
    }

    /**
     * 创建日志信息，并且进行打印
     * @receiver KLogrLogType
     * @param logTag String
     * @param logContentList List<LogContentListItem>
     */
    private fun KLogrLogType.createLog(logTag: String, logContentList: List<LogContentListItem>) {
        getThreadLogInfo().also { logThreadInfo ->
            this printLog LogInfo(
                logTag = logTag,
                logThreadInfo = logThreadInfo,
                content = logContentList
            )
        }
    }

    /**
     * 控制台打印Log，并且进行日志存储判断
     * @receiver KLogrLogType
     * @param logInfo LogInfo
     */

    private infix fun KLogrLogType.printLog(logInfo: LogInfo) {
        val logStrInfo = """ 
            => ${System.currentTimeMillis() formatTimestampByLogType this} 
            || logTag  : ${logInfo.logTag}---Thread: ${logInfo.logThreadInfo.threadName} 
            || location: ${logInfo.logThreadInfo.stackTraceElement} 
            || content : ${handleContent(logInfo.content)}
        """.trimIndent()
        if (!showDebugLog && this == KLogrLogType.DEBUG) {
            writeLogFile(activeLogSave,logStrInfo, saveFolderPath, logMaxFileSize)
            return
        }
        println("$color$logStrInfo \u001b[0m")
        writeLogFile(activeLogSave,logStrInfo, saveFolderPath, logMaxFileSize)
    }
}

/**
 * 写入日志到文件中
 * @param content String
 */
private fun writeLogFile(activeLogSave:Boolean,content: String, saveFolderPath: String, logFileMaxSize: Int) {
    if (!activeLogSave) {
        return
    }
    val folderPath = checkLogSavePath(saveFolderPath)
    val logFileName = simpleDateFormat.format(System.currentTimeMillis())
    val fileNameList = getFileList(logFileName, folderPath)
    val logFile = if (fileNameList!!.isEmpty()) {
        val file = File("${folderPath}\\${logFileName}(0).txt",)
        if (!file.exists()) {
            file.createNewFile()
        }
        file
    } else {
        val lastFile = fileNameList.last()
        if (lastFile.length() >= logFileMaxSize) {
            File("${folderPath}\\$logFileName(${fileNameList.size}).txt").also { it.createNewFile() }
        } else {
            lastFile
        }
    }
    logFile.appendText("$content \n")

}

/**
 * 判断文件夹存储路径是否存在
 * 如果为"" 则在当前文件夹创建一个名为KLogrLog的文件进行日志存储
 * 如果不为空但是文件夹不存在，则自动创建
 * 如果不为空且存在，则不做处理
 * @param path String
 * @return String 存储的文件夹路径
 */
private fun checkLogSavePath(path: String): String {
    val folderPath = if (path.isBlank()) {
        // 如果路径为空，则生成当前路径下的文件夹名为Logger
        val currentPath = System.getProperty("user.dir")
        val loggerFolder = File(currentPath, "KLogrLog")
        if (!loggerFolder.exists()) {
            loggerFolder.mkdir()
        }
        loggerFolder.absolutePath
    } else {
        // 如果路径不为空，则判断文件夹是否存在，不存在则创建
        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdir()
        }
        folder.absolutePath
    }

    return folderPath
}
/**
 * 转换时间戳根据日志类型
 * @receiver Long
 * @param logType KLogrLogType
 * @return String
 */
private infix fun Long.formatTimestampByLogType(logType: KLogrLogType) =
    "${logType.logTypeName}---${simpleDateTimeFormat.format(this)}"

/**
 * 处理打印的内容
 * @param contentList List<LogContentListItem>
 * @return String
 */
private fun handleContent(contentList: List<LogContentListItem>): String {
    if (contentList.isEmpty()) {
        return "empty"
    }
    var con = ""
    contentList.forEachIndexed { index, contentItem ->
        con += contentItem.toString()
        if (index != contentList.size - 1) {
            con = "$con | "
        }
    }
    return con
}

/**
 * 获取存储文件夹列表中的文件，根据文件日期进行匹配
 * @param startName String
 * @return Array<File>
 */

private fun getFileList(startName: String, saveFolderPath: String) =
    File(saveFolderPath).listFiles()
        ?.filter { it.name.startsWith(startName) }
        ?.sortedWith(compareBy({ extractNumber(it.name) }, { it.name }))

fun extractNumber(fileName: String): Int {
    val startIndex = fileName.indexOf('(') + 1
    val endIndex = fileName.indexOf(')')
    return fileName.substring(startIndex, endIndex).toInt()
}