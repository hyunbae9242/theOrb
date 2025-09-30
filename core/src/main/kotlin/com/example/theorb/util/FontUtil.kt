package com.example.theorb.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

object FontUtil {
    private fun hangulChars(): String {
        val sb = StringBuilder()
        for (c in '\uAC00'..'\uD7A3') sb.append(c) // 한글 완성형 전부
        return sb.toString()
    }

    fun load(size: Int, path: String = "fonts/Galmuri11.ttf"): BitmapFont {
        val gen = FreeTypeFontGenerator(Gdx.files.internal(path))
        val p = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            this.size = size
            borderWidth = 1f
            borderStraight = true
            // 픽셀 느낌 유지 (블러 없이 또렷)
            magFilter = Texture.TextureFilter.Nearest
            minFilter = Texture.TextureFilter.Nearest
            characters = FreeTypeFontGenerator.DEFAULT_CHARS +
                "0123456789.,:;!?@#%^&*()[]{}+-=_/\\\"'<> " +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                hangulChars()
        }
        val font = gen.generateFont(p)
        gen.dispose()
        return font
    }

    fun loadBold(size: Int, path: String = "fonts/Galmuri11-Bold.ttf"): BitmapFont {
        val gen = FreeTypeFontGenerator(Gdx.files.internal(path))
        val p = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            this.size = size
            borderWidth = 1f
            borderStraight = true
            // 픽셀 느낌 유지 (블러 없이 또렷)
            magFilter = Texture.TextureFilter.Nearest
            minFilter = Texture.TextureFilter.Nearest
            characters = FreeTypeFontGenerator.DEFAULT_CHARS +
                "0123456789.,:;!?@#%^&*()[]{}+-=_/\\\"'<> " +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                hangulChars()
        }
        val font = gen.generateFont(p)
        gen.dispose()
        return font
    }
}
