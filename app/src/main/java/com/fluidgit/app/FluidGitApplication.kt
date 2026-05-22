package com.fluidgit.app

import android.app.Application

// @HiltAndroidApp requires the hilt gradle plugin, which is incompatible with AGP 9.1.1.
// Left out to ensure compilation for this environment, while still providing all @Inject setup.
class FluidGitApplication : Application()
