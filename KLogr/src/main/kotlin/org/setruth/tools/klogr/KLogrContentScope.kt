package org.setruth.tools.klogr

import java.util.*


/**
 * 日志内容添加空间
 * @property contentList MutableList<LogContentListItem>
 */
class KLogrContentScope {

     val contentList: MutableList<LogContentListItem> = ArrayList()

    infix fun String.and(content: Any?) {
        contentList.add(LogContentListItem(this, content.toString()))
    }
}