package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils

class Bullet1Normal(
    x: Float,
    y: Float,
    dirX: Float,
    dirY: Float
) : SuperBullet(x, y, 25f, 25f, dirX, dirY, speed = 400f) {

    private val texture = Texture(Gdx.files.internal("bullet_image/bullet_nomal.png"))
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}