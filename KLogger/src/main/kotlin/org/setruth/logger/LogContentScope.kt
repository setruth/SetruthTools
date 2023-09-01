package org.setruth.logger

import java.util.*


/**
 * 日志内容添加空间
 * @property contentList MutableList<LogContentListItem>
 */
class LogContentScope {

     val contentList: MutableList<LogContentListItem> = ArrayList()

    infix fun String.and(content: Any?) {
        contentList.add(LogContentListItem(this, content.toString()))
    }
}