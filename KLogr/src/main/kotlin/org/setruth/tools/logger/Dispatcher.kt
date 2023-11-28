package org.setruth.tools.logger

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val logDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()