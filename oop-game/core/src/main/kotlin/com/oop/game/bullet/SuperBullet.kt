package com.oop.game.example

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

abstract class SuperBullet(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    protected val dirX: Float,
    protected val dirY: Float,
    protected val speed: Float
) : GameObject(x, y, width, height) {

    override fun isAlive(): Boolean =
        y > -height && y < 1500f && x > -width && x < 1500f

    override fun update(delta: Float) {
        x += dirX * speed * delta
        y += dirY * speed * delta
    }
}