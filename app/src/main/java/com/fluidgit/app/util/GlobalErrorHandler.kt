package com.fluidgit.app.util

import android.util.Log

object GlobalErrorHandler {
    fun initialize() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("FluidGit_CRASH", "Uncaught exception on thread ${thread.name}", throwable)
            // Ideally we'd store this in DataStore or file for next launch to show an error pill.
            // For now, intercept the crash.
            // Let the default handler run if necessary:
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
