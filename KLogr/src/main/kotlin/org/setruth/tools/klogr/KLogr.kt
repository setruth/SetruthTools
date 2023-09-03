package org.setruth.tools.klogr

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat


private val simpleDateTimeFormat = SimpleDateFormat("yyy/MM/dd(HH:mm:ss:SSS)")
private val simpleDateFormat = SimpleDateFormat("yyy-MM-dd")

/**
 * 核心内容，调用KLogger进行日志的打印执行
 */
object KLogr {
    //信息日志 蓝色
    @JvmStatic
    fun info(logTag: String = "info", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.INFO.createLog(logTag, contentList)
        }
    }

    //警告日志 黄色
    @JvmStatic
    fun warn(logTag: String = "warn", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.WARN.createLog(logTag, contentList)
        }
    }

    //错误日志 红色
    @JvmStatic
    fun err(logTag: String = "err", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.ERR.createLog(logTag, contentList)
        }
    }

    //测试日志 紫色
    @JvmStatic
    fun debug(logTag: String = "debug", content: KLogrContentScope.() -> Unit = {}) {
        KLogrContentScope().run {
            content()
            KLogrLogType.DEBUG.createLog(logTag, contentList)
        }
    }

    //通过日志 绿色
    @JvmStatic
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

}

/**
 * 打印日志
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
    if (!kLogrConfigInfo.showDebug && this == KLogrLogType.DEBUG) {
        writeLogFile(logStrInfo)
        return
    }
    println("$color$logStrInfo \u001b[0m")
    writeLogFile(logStrInfo)
}

/**
 * 写入日志到文件中
 * @param content String
 */
private fun writeLogFile(content: String) {
    if (kLogrConfigInfo.storagePath == null) {
        return
    }
    val logFileName = simpleDateFormat.format(System.currentTimeMillis())

    val fileNameList = getFileNameList(logFileName)
    val logFile = if (fileNameList!!.isEmpty()) {
        val file = File("${kLogrConfigInfo.storagePath}$logFileName(0).txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        file
    } else {
        val lastFile = fileNameList.last()
        if (lastFile.length() >= kLogrConfigInfo.logFileSize) {
            File("${kLogrConfigInfo.storagePath}$logFileName(${fileNameList.size}).txt").also { it.createNewFile() }
        } else {
            lastFile
        }
    }

    val fileWriter = FileWriter(logFile, true)
    val bufferedWriter = BufferedWriter(fileWriter)
    bufferedWriter.write(content)
    bufferedWriter.newLine()
    bufferedWriter.close()

}

/**
 * 转换时间戳根据日志类型
 * @receiver Long
 * @param KLogrLogType KLogrLogType
 * @return String
 */
private infix fun Long.formatTimestampByLogType(KLogrLogType: KLogrLogType) =
    "${KLogrLogType.logTypeName}---${simpleDateTimeFormat.format(this)}"

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
private fun getFileNameList(startName: String) =
    File(kLogrConfigInfo.storagePath!!).listFiles { file ->

        file.name.startsWith(startName)
    }
