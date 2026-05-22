package com.fluidgit.app

import android.app.Application
import com.fluidgit.app.di.AppContainer

class FluidGitApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
