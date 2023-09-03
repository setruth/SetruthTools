package org.setruth.tools.klogr


//日志类型
internal enum class LogType(val logTypeName: String, val color: String) {
    ERR("ERR", "\u001B[31m"),
    DEBUG("DBUG", "\u001B[35m"),
    SUCCESS("SCSS", "\u001B[32m"),
    WARN("WARN", "\u001B[33m"),
    INFO("INFO", "\u001B[34m")
}