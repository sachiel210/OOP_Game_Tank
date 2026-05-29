package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

const val BULLET1_RELOAD_INTERVAL = 0.5f  // 기본 단발, 2발/초

class Bullet1Normal(
    x: Float,
    y: Float,
    aimX: Float,
    aimY: Float
) : SuperBullet(x, y, 25f, 25f, aimX, aimY, speed = 300f, damage = 10f) {

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
                Bullet1Normal(x, y, aimX, aimY)
            )
        }
    }
}