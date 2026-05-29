package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

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

    private val barrelGap = 30f

    fun fire(): ArrayList<SuperBullet> {
        val verticalVectorX = -aimX
        val verticalVectorY =  aimY

        val leftBullet = Bullet3Triple(
            x - verticalVectorX * barrelGap,
            y - verticalVectorY * barrelGap,
            aimX, aimY
        )

        val middleBullet = Bullet3Triple(
            x,
            y,
            aimX, aimY
        )

        val rightBullet = Bullet3Triple(
            x + verticalVectorX * barrelGap,
            y + verticalVectorY * barrelGap,
            aimX, aimY
        )

        val bullets = ArrayList<SuperBullet>()
        bullets.add(leftBullet)
        bullets.add(middleBullet)
        bullets.add(rightBullet)
        return bullets
    }
}