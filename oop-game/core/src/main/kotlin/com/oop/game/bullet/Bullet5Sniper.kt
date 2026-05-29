package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

const val BULLET5_RELOAD_INTERVAL = 0.8f  // 스나이퍼 단발 고데미지, 1.25발/초

class Bullet5Sniper(
    x: Float,
    y: Float,
    aimX: Float,
    aimY: Float
) : SuperBullet(x, y, 80f, 80f, aimX, aimY, speed = 700f) {

    private val texture = Texture(Gdx.files.internal("bullet_image/bullet_nomal.png"))

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }

    companion object {
        fun fire(x: Float, y: Float, aimX: Float, aimY: Float): List<SuperBullet> {

            return listOf(
                Bullet5Sniper(x, y, aimX, aimY)
            )
        }
    }
}
