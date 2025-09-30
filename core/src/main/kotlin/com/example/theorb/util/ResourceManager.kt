package com.example.theorb.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object ResourceManager {

    // 이미지 경로 상수들
    object Images {
        // Retro Ui 배경없는 버튼
        const val RETRO_GEAR = "images/buttons/retro/Gear.png"

        // Retro UI 버튼들
        const val RETRO_PAUSE_DEFAULT = "images/buttons/retro/Pause_d.png"
        const val RETRO_PAUSE_EVENT = "images/buttons/retro/Pause_e.png"
        const val RETRO_HOME_DEFAULT = "images/buttons/retro/Home_d.png"
        const val RETRO_HOME_EVENT = "images/buttons/retro/Home_e.png"
        const val RETRO_PLAY_DEFAULT = "images/buttons/retro/Play_d.png"
        const val RETRO_PLAY_EVENT = "images/buttons/retro/Play_e.png"

        // Retro Square 버튼들
        const val RETRO_SQUARE_POS_DEFAULT = "images/buttons/retro/Square_pos_d.png"
        const val RETRO_SQUARE_POS_EVENT = "images/buttons/retro/Square_pos_e.png"
        const val RETRO_SQUARE_NAG_DEFAULT = "images/buttons/retro/Square_nag_d.png"
        const val RETRO_SQUARE_NAG_EVENT = "images/buttons/retro/Square_nag_e.png"

        // Retro Rectangle 버튼들
        const val RETRO_RECTANGLE_POS_DEFAULT = "images/buttons/retro/Rectangle_pos_d.png"
        const val RETRO_RECTANGLE_POS_EVENT = "images/buttons/retro/Rectangle_pos_e.png"
        const val RETRO_RECTANGLE_NAG_DEFAULT = "images/buttons/retro/Rectangle_nag_d.png"
        const val RETRO_RECTANGLE_NAG_EVENT = "images/buttons/retro/Rectangle_nag_e.png"

        // 모달 패널들
        const val RECTANGLE_PANEL_180_340 = "images/panels/Rectangle_panel_180_340.png"
        const val RECTANGLE_PANEL_340_448 = "images/panels/Rectangle_panel_340_448.png"
        const val RECTANGLE_PANEL_340_180 = "images/panels/Rectangle_panel_340_180.png"
        const val RECTANGLE_PANEL_340_120 = "images/panels/Rectangle_panel_340_120.png"
        const val RECTANGLE_PANEL_252_84 = "images/panels/Rectangle_panel_252_84.png"
        const val SQUARE_PANEL_360 = "images/panels/Square_panel_360.png"
        // 스킬 아이콘 이미지들
        const val SKILL_ICON_PANEL_48_48 = "images/panels/Skill_icon_panel_48.png"

        // 배경 이미지들 - clouds01
        const val BACKGROUND_CLOUDS01_1 = "images/background/clouds01/1.png"
        const val BACKGROUND_CLOUDS01_2 = "images/background/clouds01/2.png"
        const val BACKGROUND_CLOUDS01_3 = "images/background/clouds01/3.png"
        const val BACKGROUND_CLOUDS01_4 = "images/background/clouds01/4.png"
        const val BACKGROUND_CLOUDS01_5 = "images/background/clouds01/5.png"

        // 배경 이미지들 - clouds02
        const val BACKGROUND_CLOUDS02_1 = "images/background/clouds02/1.png"
        const val BACKGROUND_CLOUDS02_2 = "images/background/clouds02/2.png"
        const val BACKGROUND_CLOUDS02_3 = "images/background/clouds02/3.png"
        const val BACKGROUND_CLOUDS02_4 = "images/background/clouds02/4.png"

        // 배경 이미지들 - city01, city02, city03
        const val BACKGROUND_CITY01_1 = "images/background/city01/1.png"
        const val BACKGROUND_CITY02_1 = "images/background/city02/1.png"
        const val BACKGROUND_CITY03_1 = "images/background/city03/1.png"
    }

    // 텍스처 캐시
    private val textureCache = mutableMapOf<String, Texture>()

    /**
     * 텍스처를 로드하고 캐시에 저장
     */
    fun getTexture(path: String): Texture {
        return textureCache.getOrPut(path) {
            Texture(Gdx.files.internal(path))
        }
    }

    /**
     * TextureRegionDrawable을 반환
     */
    fun getDrawable(path: String): TextureRegionDrawable {
        return TextureRegionDrawable(getTexture(path))
    }


    // Retro 버튼들을 위한 편의 메소드
    fun getRetroGear(): TextureRegionDrawable = getDrawable(Images.RETRO_GEAR)
    fun getRetroPauseDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_PAUSE_DEFAULT)
    fun getRetroPauseEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_PAUSE_EVENT)
    fun getRetroHomeDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_HOME_DEFAULT)
    fun getRetroHomeEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_HOME_EVENT)
    fun getRetroPlayDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_PLAY_DEFAULT)
    fun getRetroPlayEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_PLAY_EVENT)

    // Retro Square 버튼들을 위한 편의 메소드
    fun getRetroSquarePosDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_SQUARE_POS_DEFAULT)
    fun getRetroSquarePosEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_SQUARE_POS_EVENT)
    fun getRetroSquareNagDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_SQUARE_NAG_DEFAULT)
    fun getRetroSquareNagEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_SQUARE_NAG_EVENT)

    // Retro Rectangle 버튼들을 위한 편의 메소드
    fun getRetroRectanglePosDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_RECTANGLE_POS_DEFAULT)
    fun getRetroRectanglePosEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_RECTANGLE_POS_EVENT)
    fun getRetroRectangleNagDefault(): TextureRegionDrawable = getDrawable(Images.RETRO_RECTANGLE_NAG_DEFAULT)
    fun getRetroRectangleNagEvent(): TextureRegionDrawable = getDrawable(Images.RETRO_RECTANGLE_NAG_EVENT)


    // 모달 패널들을 위한 편의 메소드
    fun getSquarePanel360(): TextureRegionDrawable = getDrawable(Images.SQUARE_PANEL_360)
    fun getRectanglePanel180340(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_180_340)
    fun getRectanglePanel340448(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_340_448)
    fun getRectanglePanel340180(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_340_180)
    fun getRectanglePanel340120(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_340_120)
    fun getRectanglePanel25284(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_252_84)
    // 스킬 아이콘 편의 메소드
    fun getSkillIconPanel4848(): TextureRegionDrawable = getDrawable(Images.SKILL_ICON_PANEL_48_48)
    // 배경 이미지들을 위한 편의 메소드 - clouds01
    fun getBackgroundClouds01Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_1)
    fun getBackgroundClouds01Layer2(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_2)
    fun getBackgroundClouds01Layer3(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_3)
    fun getBackgroundClouds01Layer4(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_4)
    fun getBackgroundClouds01Layer5(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS01_5)

    // 배경 이미지들을 위한 편의 메소드 - clouds02
    fun getBackgroundClouds02Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_1)
    fun getBackgroundClouds02Layer2(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_2)
    fun getBackgroundClouds02Layer3(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_3)
    fun getBackgroundClouds02Layer4(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CLOUDS02_4)

    // 배경 이미지들을 위한 편의 메소드 - city 배경들
    fun getBackgroundCity01Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CITY01_1)
    fun getBackgroundCity02Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CITY02_1)
    fun getBackgroundCity03Layer1(): TextureRegionDrawable = getDrawable(Images.BACKGROUND_CITY03_1)

    /**
     * 배경 레이어들을 순서대로 반환
     */
    fun getBackgroundClouds01Layers(): List<TextureRegionDrawable> {
        return listOf(
            getBackgroundClouds01Layer1(),
            getBackgroundClouds01Layer2(),
            getBackgroundClouds01Layer3(),
            getBackgroundClouds01Layer4(),
            getBackgroundClouds01Layer5()
        )
    }

    fun getBackgroundClouds02Layers(): List<TextureRegionDrawable> {
        return listOf(
            getBackgroundClouds02Layer1(),
            getBackgroundClouds02Layer2(),
            getBackgroundClouds02Layer3(),
            getBackgroundClouds02Layer4()
        )
    }

    fun getBackgroundCity01Layers(): List<TextureRegionDrawable> {
        return listOf(getBackgroundCity01Layer1())
    }

    fun getBackgroundCity02Layers(): List<TextureRegionDrawable> {
        return listOf(getBackgroundCity02Layer1())
    }

    fun getBackgroundCity03Layers(): List<TextureRegionDrawable> {
        return listOf(getBackgroundCity03Layer1())
    }

    /**
     * 배경 이름에 따라 해당 배경의 레이어들을 반환
     */
    fun getBackgroundLayers(backgroundName: String): List<TextureRegionDrawable> {
        return when (backgroundName) {
            "clouds01" -> getBackgroundClouds01Layers()
            "clouds02" -> getBackgroundClouds02Layers()
            "city01" -> getBackgroundCity01Layers()
            "city02" -> getBackgroundCity02Layers()
            "city03" -> getBackgroundCity03Layers()
            else -> getBackgroundClouds01Layers() // 기본값
        }
    }

    /**
     * 모든 캐시된 텍스처 해제 (게임 종료 시 호출)
     */
    fun dispose() {
        textureCache.values.forEach { it.dispose() }
        textureCache.clear()
        Gdx.app.log("ResourceManager", "All textures disposed")
    }

    /**
     * 특정 텍스처 해제
     */
    fun disposeTexture(path: String) {
        textureCache[path]?.let { texture ->
            texture.dispose()
            textureCache.remove(path)
            Gdx.app.log("ResourceManager", "Texture disposed: $path")
        }
    }

    /**
     * 캐시 상태 확인 (디버깅용)
     */
    fun getCacheInfo(): String {
        return "Cached textures: ${textureCache.size} - ${textureCache.keys.joinToString(", ")}"
    }
}
