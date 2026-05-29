package com.oop.game.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

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

    fun fire(): ArrayList<SuperBullet> {
        val verticalVectorX = -aimY
        val verticalVectorY =  aimX

        val frontBullet = Bullet4Quad(x, y,  aimX,           aimY)           // 앞 (마우스 방향)
        val leftBullet  = Bullet4Quad(x, y,  verticalVectorX,  verticalVectorY) // 좌 (90도)
        val rightBullet = Bullet4Quad(x, y, -verticalVectorX, -verticalVectorY) // 우 (270도)
        val backBullet  = Bullet4Quad(x, y, -aimX,           -aimY)          // 뒤 (180도)

        val bullets = ArrayList<SuperBullet>()
        bullets.add(frontBullet)
        bullets.add(leftBullet)
        bullets.add(rightBullet)
        bullets.add(backBullet)
        return bullets
    }
}