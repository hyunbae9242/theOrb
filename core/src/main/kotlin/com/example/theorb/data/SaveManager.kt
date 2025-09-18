package com.example.theorb.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json

object SaveManager {
    private val json = Json()
    private val saveFile = Gdx.files.local("save.json")

    fun save(data: SaveData) {
        saveFile.writeString(json.prettyPrint(data), false)
    }

    fun load(): SaveData {
        return if (saveFile.exists()) {
            try {
                val loaded = json.fromJson(SaveData::class.java, saveFile)
                Gdx.app.log("SaveManager", "게임 데이터 로드됨: $loaded")
                loaded
            } catch (e: Exception) {
                Gdx.app.error("SaveManager", "저장 파일 로드 실패, 새 데이터 생성", e)
                SaveData()
            }
        } else {
            Gdx.app.log("SaveManager", "저장 파일 없음 → 새 데이터 생성")
            SaveData()
        }
    }
}
