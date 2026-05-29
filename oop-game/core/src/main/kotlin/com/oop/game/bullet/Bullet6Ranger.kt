package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

const val BULLET6_RELOAD_INTERVAL = 0.6f  // 단발 강력한 데미지, 1.7발/초

class Bullet6Ranger(
    x: Float,
    y: Float,
    aimX: Float,
    aimY: Float
) : SuperBullet(x, y, 20f, 20f, aimX, aimY, speed = 500f, damage = 60f) {

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
                Bullet6Ranger(x, y, aimX, aimY)
            )
        }
    }
}
