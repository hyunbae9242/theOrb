package com.example.theorb.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object ResourceManager {

    // 이미지 경로 상수들
    object Images {
        // UI 버튼들
        const val PAUSE_BUTTON = "images/buttons/blue/Pause.png"
        const val HOME_BUTTON = "images/buttons/blue/Home.png"
        const val PLAY_BUTTON = "images/buttons/blue/Play.png"
        const val RESTART_BUTTON = "images/buttons/blue/Repeat-Right.png"

        // 버튼 배경들 - 직사각형
        const val BUTTON_BG_CANCEL = "images/buttons/blue/Rectangle_Button_nag.png"
        const val BUTTON_BG_CONFIRM = "images/buttons/blue/Rectangle_Button_pos.png"
        const val BUTTON_BG_HIGHLIGHT = "images/buttons/blue/Rectangle_Button_evt.png"

        // 네비게이션 버튼들 - 직사각형 B (165x42)
        const val NAV_BUTTON_BG_CANCEL = "images/buttons/blue/Rectangle_Button_B_nag.png"
        const val NAV_BUTTON_BG_CONFIRM = "images/buttons/blue/Rectangle_Button_B_pos.png"
        const val NAV_BUTTON_BG_HIGHLIGHT = "images/buttons/blue/Rectangle_Button_B_evt.png"

        // 버튼 배경들 - 정사각형
        const val SQUARE_BUTTON_1 = "images/buttons/blue/Square_Button_pos.png"
        const val SQUARE_BUTTON_2 = "images/buttons/blue/Square_Button_nag.png"
        const val SQUARE_BUTTON_HIGHLIGHT = "images/buttons/blue/Square_Button_evt.png"
        const val SQUARE_MENU_BUTTON_EVENT = "images/buttons/blue/Square_Menu_Button_evt.png"

        // 게임 오브젝트들
        const val BASE_ORB = "images/orbs/Base_orb.png"
        const val ORB_01 = "images/orbs/Orb_01.png"

        // 모달 패널들
        const val SQUARE_PANEL_320 = "images/panels/Square_panel_320.png"
        const val RECTANGLE_PANEL_430_278 = "images/panels/Rectangle_panel_430_278.png"
        const val RECTANGLE_PANEL_430_590 = "images/panels/Rectangle_panel_430_590.png"
        const val LIST_ROW_PANEL_376_88 = "images/panels/List_row_panel_376_88.png"
        const val VICTORY_PANEL = "images/panels/Victory_Panel_340_366.png"

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

    /**
     * 설정 버튼 Drawable (자주 사용되므로 편의 메소드)
     */
    fun getPauseButtonDrawable(): TextureRegionDrawable {
        return getDrawable(Images.PAUSE_BUTTON)
    }

    // 추가 버튼들을 위한 편의 메소드
    fun getHomeButtonDrawable(): TextureRegionDrawable = getDrawable(Images.HOME_BUTTON)
    fun getPlayButtonDrawable(): TextureRegionDrawable = getDrawable(Images.PLAY_BUTTON)
    fun getRestartButtonDrawable(): TextureRegionDrawable = getDrawable(Images.RESTART_BUTTON)

    // 게임 오브젝트들을 위한 편의 메소드
    fun getBaseOrbDrawable(): TextureRegionDrawable = getDrawable(Images.BASE_ORB)
    fun getOrb01Drawable(): TextureRegionDrawable = getDrawable(Images.ORB_01)

    // 모달 패널들을 위한 편의 메소드
    fun getSquarePanel320(): TextureRegionDrawable = getDrawable(Images.SQUARE_PANEL_320)
    fun getRectanglePanel430278(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_430_278)
    fun getRectanglePanel430590(): TextureRegionDrawable = getDrawable(Images.RECTANGLE_PANEL_430_590)
    fun getListRowPanel37688(): TextureRegionDrawable = getDrawable(Images.LIST_ROW_PANEL_376_88)
    fun getVictoryPanel(): TextureRegionDrawable = getDrawable(Images.VICTORY_PANEL)

    // 버튼 배경들을 위한 편의 메소드 - 직사각형
    fun getButtonCancelBg(): TextureRegionDrawable = getDrawable(Images.BUTTON_BG_CANCEL)
    fun getButtonConfirmBg(): TextureRegionDrawable = getDrawable(Images.BUTTON_BG_CONFIRM)
    fun getButtonHighlightBg(): TextureRegionDrawable = getDrawable(Images.BUTTON_BG_HIGHLIGHT)

    // 네비게이션 버튼들을 위한 편의 메소드 - 직사각형 B
    fun getNavButtonCancelBg(): TextureRegionDrawable = getDrawable(Images.NAV_BUTTON_BG_CANCEL)
    fun getNavButtonConfirmBg(): TextureRegionDrawable = getDrawable(Images.NAV_BUTTON_BG_CONFIRM)
    fun getNavButtonHighlightBg(): TextureRegionDrawable = getDrawable(Images.NAV_BUTTON_BG_HIGHLIGHT)

    // 버튼 배경들을 위한 편의 메소드 - 정사각형
    fun getSquareButton1(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_1)
    fun getSquareButton2(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_2)
    fun getSquareButtonHighlight(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_HIGHLIGHT)
    fun getSquareMenuButtonEvent(): TextureRegionDrawable = getDrawable(Images.SQUARE_MENU_BUTTON_EVENT)

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

    /**
     * 배경 이름에 따라 해당 배경의 레이어들을 반환
     */
    fun getBackgroundLayers(backgroundName: String): List<TextureRegionDrawable> {
        return when (backgroundName) {
            "clouds01" -> getBackgroundClouds01Layers()
            "clouds02" -> getBackgroundClouds02Layers()
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
