package com.oop.game.bullet

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

abstract class SuperBullet(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    protected val aimX: Float,
    protected val aimY: Float,
    protected val speed: Float
) : GameObject(x, y, width, height) {

    override fun isAlive(): Boolean =
        y > -height && y < 1500f && x > -width && x < 1500f

    override fun update(delta: Float) {
        x += aimX * speed * delta
        y += aimY * speed * delta
    }
}