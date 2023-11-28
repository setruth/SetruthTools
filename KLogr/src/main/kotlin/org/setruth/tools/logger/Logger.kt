package org.setruth.tools.logger

import org.setruth.tools.logger.interceptor.ListInterceptor
import org.setruth.tools.logger.interceptor.MapInterceptor
import java.util.regex.Pattern

object Logger {

    /**
     * Priority constant for the println method; use Log.v.
     */
    const val VERBOSE = 2

    /**
     * Priority constant for the println method; use Log.d.
     */
    const val DEBUG = 3

    /**
     * Priority constant for the println method; use Log.i.
     */
    const val INFO = 4

    /**
     * Priority constant for the println method; use Log.w.
     */
    const val WARN = 5

    /**
     * Priority constant for the println method; use Log.e.
     */
    const val ERROR = 6

    /**
     * Priority constant for the println method.
     */
    const val ASSERT = 7

    /**
     * Priority constant for no log
     */
    const val NONE = 8

    private val interceptors = mutableListOf<Interceptor<in Nothing>>()
    private val chain = Chain(interceptors)

    /**
     * A transient [Interceptor] only for next logging
     */
    private var onetimeInterceptor: ThreadLocal<Interceptor<*>>? = null

    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    /**
     * A transient tag only for next logging
     */
    private var onetimeTag = ThreadLocal<String>()
    private var tag: String?
        get() = onetimeTag.get()?.also { onetimeTag.remove() }
        set(value) {
            onetimeTag.set(value)
        }

    var curPriority = VERBOSE

    /**
     * 调用堆栈黑名单
     */
    private val blackList = listOf(
        Logger::class.java.name,
        Chain::class.java.name
    )

    fun log(message: Any, priority: Int = VERBOSE, vararg args: Any) {
        chain.proceed(createTag(), message, priority, *args)
        removeOneTimeInterceptor()
    }

    fun <T> list(message: Iterable<T>, priority: Int = VERBOSE, map: ((T) -> String)? = null) {
        interceptor(ListInterceptor(map))
        chain.proceed(createTag(), message, priority)
        removeOneTimeInterceptor()
    }

    fun <K, V> map(message: Map<K, V>, priority: Int = VERBOSE) {
        interceptor(MapInterceptor<K, V>())
        chain.proceed(createTag(), message, priority)
        removeOneTimeInterceptor()
    }

    fun tag(tag: String): Logger = apply {
        this.tag = tag
    }

    /**
     * 添加 [Interceptor] 自定义日志流程
     */
    fun <T> addInterceptor(interceptor: Interceptor<T>) {
        addInterceptor(interceptors.size, interceptor)
    }

    /**
     * 在 [index] 添加 [Interceptor] 自定义日志流程
     */
    fun <T> addInterceptor(index: Int, interceptor: Interceptor<T>) {
        interceptors.add(index, interceptor)
    }

    /**
     * 添加一次性 [Interceptor]
     */
    fun interceptor(interceptor: Interceptor<*>): Logger = apply {
        interceptors.add(0, interceptor)
        if (onetimeInterceptor == null) onetimeInterceptor = ThreadLocal()
        onetimeInterceptor?.set(interceptor)
    }

    fun removeInterceptor(interceptor: Interceptor<*>?) {
        interceptors.remove(interceptor)
    }

    /**
     * 为日志创建一次性标签，可以通过 [Logger.tag] 设置或使用自动生成的类名
     */
    private fun createTag(): String {
        return tag ?: Throwable().stackTrace
            .first { it.className !in blackList }
            .let(::createStackElementTag)
    }

    /**
     * 移除一次性拦截器
     */
    private fun removeOneTimeInterceptor() {
        onetimeInterceptor?.takeIf { it.get() != null }
            ?.also { removeInterceptor(it.get()) }
    }

    /**
     * 从 [Throwable] 生成调用栈
     */
    private fun createStackElementTag(element: StackTraceElement): String {
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag
    }
}
