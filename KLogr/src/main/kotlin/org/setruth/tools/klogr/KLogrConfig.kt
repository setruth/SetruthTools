package org.setruth.tools.klogr


/**
 * 打印启动日志信息
 */
internal fun showStartLogo() {
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
}


/**
 * KLogr的构建类，通过[createKLogrBuilder]函数调用来设置构建者中的Logr配置
 *
 * *****************默认配置*******************
 *
 * logSavePath: String = "" 默认存储在当前目录
 * showDebugLog: Boolean = true 默认显示debug日志
 * showStartInfo: Boolean = true 默认显示启动信息
 * logFileSize: Int = [DEFAULT_LOG_FILE_SIZE] 默认log文件大小1m超出会创建对应副本
 * activeLogSave:Boolean=false 默认不激活存储
 *
 * ******************************************
 */
class KLogrBuilder internal constructor() {
     var logSavePath: String = ""
     var showDebugLog: Boolean = true
     var showStartInfo: Boolean = true
     var logFileSize: Int = DEFAULT_LOG_FILE_SIZE
     var activeLogSave:Boolean=false

    /**
     * 通过调用build函数进行KLogr对象的创建
     * @return KLogr
     */
    fun build(): KLogr {
        val kLogr = KLogr(
            logSavePath,
            activeLogSave,
            showDebugLog,
            logFileSize
        )
        if (showStartInfo) {
            showStartLogo()
            kLogr.info("KLogger日志工具启动") {
                "是否显示debug日志" and showDebugLog
                "是否开启日志存储" and activeLogSave
                "日志存储文件夹" and logSavePath
                "是否显示启动信息" and showStartInfo
                "文件日志大小" and logFileSize
            }

        }
        return kLogr
    }
    companion object{
        //默认的日志文件大小
       private const val DEFAULT_LOG_FILE_SIZE=1024
    }
}

/**
 * 通过此函数进行[KLogrBuilder]的构建，从而构建出KLogr工具
 * @param kLogrConfigScope [@kotlin.ExtensionFunctionType] Function1<KLogrBuilder, Unit>
 * @return KLogrBuilder
 */
fun createKLogrBuilder(kLogrConfigScope: KLogrBuilder.() -> Unit) = KLogrBuilder().also(kLogrConfigScope)