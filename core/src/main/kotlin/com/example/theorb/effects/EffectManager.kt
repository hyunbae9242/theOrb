package com.example.theorb.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array as GdxArray

object EffectManager {
    private val textureCache = HashMap<String, Texture>()
    private val animCache = HashMap<String, Animation<TextureRegion>>()

    fun load(type: EffectType): Animation<TextureRegion> {
        val key = "${type.path}#row=${type.row}"
        return animCache.getOrPut(key) {
            val tex = textureCache.getOrPut(type.path) {
                Texture(Gdx.files.internal(type.path)).apply {
                    setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
                }
            }
            val frameW = tex.width / type.cols
            val frameH = tex.height / type.rows
            val grid = TextureRegion.split(tex, frameW, frameH)

            val frames = GdxArray<TextureRegion>(type.cols)
            for (i in 0 until type.cols) frames.add(grid[type.row][i])

            Animation(1f / type.fps, frames, Animation.PlayMode.NORMAL)
        }
    }
}
