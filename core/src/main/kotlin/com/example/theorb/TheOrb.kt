package com.example.theorb

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.example.theorb.data.SaveData
import com.example.theorb.data.SaveManager
import com.example.theorb.screens.HomeScreen

class TheOrb : Game() {
    lateinit var saveData: SaveData
    lateinit var batch: SpriteBatch

    override fun create() {
        saveData = SaveManager.load()
        batch = SpriteBatch()
        setScreen(HomeScreen(this)) // 시작 화면을 HomeScreen으로
    }

    override fun dispose() {
        SaveManager.save(saveData)
        batch.dispose()
        super.dispose()
    }
}
