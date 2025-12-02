package com.example.theorb.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.theorb.TheOrb

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            // 배터리 절약을 위한 설정
            useAccelerometer = false
            useCompass = false
            useGyroscope = false

            // 안정성을 위한 설정
            numSamples = 0 // MSAA 비활성화
        }
        initialize(TheOrb(), config)
    }
}
