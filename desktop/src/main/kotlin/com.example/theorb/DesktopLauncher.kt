package com.example.theorb

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            setTitle("The Orb")
            setWindowedMode(480, 800)
            useVsync(true)
            setForegroundFPS(60)
        }
        Lwjgl3Application(TheOrb(), config)
    }
}
