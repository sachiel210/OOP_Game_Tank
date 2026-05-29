package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.bullet.Bullet3Triple

const val BULLET3_RELOAD_INTERVAL = 0.2f  // 삼발 초고속 연사, 5발/초

class Bullet3Triple(
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
        private const val barrelGap = 30f

        fun fire(x: Float, y: Float, aimX: Float, aimY: Float): List<SuperBullet> {
            val verticalVectorX = -aimY
            val verticalVectorY = aimX

            return listOf(
                Bullet3Triple(x - verticalVectorX * barrelGap, y - verticalVectorY * barrelGap, aimX, aimY),
                Bullet3Triple(x, y, aimX, aimY),
                Bullet3Triple(x + verticalVectorX * barrelGap, y + verticalVectorY * barrelGap, aimX, aimY)
            )
        }
    }
}