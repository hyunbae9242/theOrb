package com.example.theorb.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json

object SaveManager {
    private val json = Json()
    // 플랫폼에 따라 적절한 저장소 사용
    private val saveFile = try {
        // 안드로이드/iOS는 local 사용 (앱 내부 저장소)
        Gdx.files.local("save.json")
    } catch (e: Exception) {
        // 데스크톱/macOS는 external 사용
        Gdx.files.external(".theorb/save.json")
    }

    fun save(data: SaveData) {
        try {
            saveFile.writeString(json.prettyPrint(data), false)
        } catch (e: Exception) {
            Gdx.app.error("SaveManager", "저장 실패", e)
        }
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
