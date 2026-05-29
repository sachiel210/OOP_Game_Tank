package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

const val BULLET2_RELOAD_INTERVAL = 0.3f  // 쌍발 빠른 연사, 3.3발/초

class Bullet2Twin(
    x: Float,
    y: Float,
    aimX: Float,
    aimY: Float
) : SuperBullet(x, y, 25f, 25f, aimX, aimY, speed = 300f) {

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
            val verticalVectorY =  aimX

            return listOf(
                Bullet2Twin(x - verticalVectorX * barrelGap, y - verticalVectorY * barrelGap, aimX, aimY),
                Bullet2Twin(x + verticalVectorX * barrelGap, y + verticalVectorY * barrelGap, aimX, aimY)
            )
        }
    }
}