package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

const val BULLET4_RELOAD_INTERVAL = 0.5f  // 4방향, 한 번에 4발이라 중간 텀

class Bullet4Quad(
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
                Bullet4Quad(x, y,  aimX, aimY),
                Bullet4Quad(x, y, verticalVectorX,  verticalVectorY),
                Bullet4Quad(x, y, -verticalVectorX, -verticalVectorY),
                Bullet4Quad(x, y, -aimX, -aimY)
            )
        }
    }
}