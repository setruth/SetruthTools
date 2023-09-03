package org.setruth.tools.klogr

import java.io.File

//KLogger全局配置文件
internal val kLogrConfigInfo = KLogrConfigInfo()

/**
 * 初始化日志库配置，需要在[KLogrConfigSettingScope]的作用域中进行函数调用设置KLogger的配置
 * 可以不执行initKLoggerConfig，默认会有基本配置
 ***********************************
 * showStartLogger: Boolean = true,
 * showDebug: Boolean = true,
 * storagePath: String? = null,
 * logFileSize:Long=1024*1024
 ***********************************
 * @param configSettingScope [@kotlin.ExtensionFunctionType] Function1<KLogrConfigSettingScope, Unit>
 * @return KLogrConfigSettingScope
 */
fun initKLogrConfig(configSettingScope: KLogrConfigSettingScope.() -> Unit = {}) =
    KLogrConfigSettingScope().apply {
        configSettingScope()
        if (kLogrConfigInfo.showStartLogger) {
            printStartLog()
        }
        kLogrConfigInfo.storagePath?.also { saveFolderPath ->
            val directory = File(saveFolderPath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }
    }

/**
 * 打印启动日志信息
 */
internal fun printStartLog() {
    println(
        """
        [34m
         Author:Setruth Email:1607908758@qq.com
         github：https://github.com/setruth/SetruthTools
           _   __ _                        
          | | / /| |                        
          | |/ / | |      ___    __ _  _ __ 
          |    \ | |     / _ \  / _` || '__|
          | |\  \| |____| (_) || (_| || |   
          \_| \_/\_____/ \___/  \__, ||_|   
                                 __/ |      
                                |___/       
         
    """.trimIndent()
    )
    KLogr.info("KLogger日志工具启动") {
        "是否显示debug日志" and kLogrConfigInfo.showDebug
        "日志存储文件夹" and kLogrConfigInfo.storagePath
        "是否显示日志启动初始Logo及配置内容" and kLogrConfigInfo.showStartLogger
    }
}

/**
 * KLogger日志库的基本配置内容
 * @property showStartLogger Boolean
 * @property showDebug Boolean
 * @property storagePath String?
 * @constructor
 */
internal data class KLogrConfigInfo(
    var showStartLogger: Boolean = true,
    var showDebug: Boolean = true,
    var storagePath: String? = null,
    var logFileSize:Long=1024*1024
)

/**
 * KLogger的配置空间，请调用[initKLogrConfig]进行配置
 */
class KLogrConfigSettingScope {
    /**
     * 日志文件存储的文件夹路径,填入自动开启日志存储
     * @param saveFolderPath String
     */
    fun storage(saveFolderPath: String) {
        kLogrConfigInfo.storagePath = saveFolderPath
    }

    /**
     * 是否显示日志启动提示
     * @param enabled Boolean
     */
    fun startLogger(enabled: Boolean = true) {
        kLogrConfigInfo.showStartLogger = enabled
    }

    /**
     * 是否启动Debug日志的显示
     * 当启动日志存储的时候，虽然不打印debug内容，但是会存储到日志文件中
     * @param enabled Boolean
     */
    fun showDebugLog(enabled: Boolean = true) {
        kLogrConfigInfo.showDebug = enabled
    }

    /**
     * 设置日志存储文件的大小，默认是以当天的日期作为文件名，文件名后面接序号
     * 当日志文件的代销超出设定的大小的时候自动创建最大序号的下一个新的当天文件
     *
     * 例如logDate(0).txt ，例如logDate(1).txt
     * @param size Long
     */
    fun logFileSize(size: Long = 1024*1024) {
        kLogrConfigInfo.logFileSize = size
    }
}