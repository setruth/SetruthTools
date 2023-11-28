package org.setruth.tools.logger

/**
 * 打印方法调用栈
 */
fun getCallStack(blackList: List<String>): List<String> {
    return Thread.currentThread()
        .stackTrace.drop(3)
        .filter { it.className !in blackList }
        .map { "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
}


/**
 * 打印 [Iterable] 对象
 */
fun <T> Iterable<T>.log(map: (T) -> String) =
    fold(StringBuilder("[")) { acc: StringBuilder, t: T -> acc.append("\t${map(t)},") }.append("]").toString()


/**
 * 打印 [Map]
 * @param space 缩进空格
 */
fun <K, V> Map<K, V?>.log(space: Int = 0): String {
    val indent = StringBuilder()
    repeat(space) { indent.append(" ") }
    indent.toString()
    return StringBuilder("\n${indent}{").also { sb ->
        this.iterator().forEach { entry ->
            val value = entry.value.let { v ->
                (v as? Map<*, *>)?.log("${indent}${entry.key} = ".length) ?: v.toString()
            }
            sb.append("\n\t${indent}[${entry.key}] = $value,")
        }
        sb.append("\n${indent}}")
    }.toString()
}
