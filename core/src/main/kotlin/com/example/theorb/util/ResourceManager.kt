package com.example.theorb.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object ResourceManager {

    // 이미지 경로 상수들
    object Images {
        // UI 버튼들
        const val PAUSE_BUTTON = "images/buttons/Pause.png"
        const val HOME_BUTTON = "images/buttons/Home.png"
        const val PLAY_BUTTON = "images/buttons/Play.png"
        const val RESTART_BUTTON = "images/buttons/Repeat-Right.png"
        const val ARROW_LEFT = "images/buttons/SolidArrow-Left.png"
        const val ARROW_RIGHT = "images/buttons/SolidArrow-Right.png"

        // 버튼 배경들 - 직사각형 (112x56)
        const val BUTTON_BG_CANCEL = "images/buttons/Rectangle_Button_1.png"
        const val BUTTON_BG_CONFIRM = "images/buttons/Rectangle_Button_2.png"
        const val BUTTON_BG_HIGHLIGHT = "images/buttons/Rectangle_Button_Highlight.png"

        // 버튼 배경들 - 정사각형
        const val SQUARE_BUTTON_1 = "images/buttons/Square_Button_1.png"
        const val SQUARE_BUTTON_2 = "images/buttons/Square_Button_2.png"
        const val SQUARE_BUTTON_3 = "images/buttons/Square_Button_3.png"
        const val SQUARE_BUTTON_HIGHLIGHT = "images/buttons/Square_Button_Highlight.png"

        // 게임 오브젝트들
        const val BASE_ORB = "images/orbs/Base_orb.png"
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
    fun getArrowLeftDrawable(): TextureRegionDrawable = getDrawable(Images.ARROW_LEFT)
    fun getArrowRightDrawable(): TextureRegionDrawable = getDrawable(Images.ARROW_RIGHT)

    // 게임 오브젝트들을 위한 편의 메소드
    fun getBaseOrbDrawable(): TextureRegionDrawable = getDrawable(Images.BASE_ORB)

    // 버튼 배경들을 위한 편의 메소드 - 직사각형
    fun getButtonCancelBg(): TextureRegionDrawable = getDrawable(Images.BUTTON_BG_CANCEL)
    fun getButtonConfirmBg(): TextureRegionDrawable = getDrawable(Images.BUTTON_BG_CONFIRM)
    fun getButtonHighlightBg(): TextureRegionDrawable = getDrawable(Images.BUTTON_BG_HIGHLIGHT)

    // 버튼 배경들을 위한 편의 메소드 - 정사각형
    fun getSquareButton1(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_1)
    fun getSquareButton2(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_2)
    fun getSquareButton3(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_3)
    fun getSquareButtonHighlight(): TextureRegionDrawable = getDrawable(Images.SQUARE_BUTTON_HIGHLIGHT)

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
