package com.oop.game.example.enempyList

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// DotEnemy 역할 : 플레이어와 접촉시 경험지를 주고 데미지는 주지 않음, 처음에 맵에 랜덤하게 스폰. 후 비중 : 40%

class DotEnemy(
    x: Float,
    y: Float
) : SuperEnemy(x, y, 40f, 40f) {

    // 이미지 로딩
    private val texture = Texture(Gdx.files.internal("enemy_image/enemy_dot.png"))

    override fun update(delta: Float) {

    }

    // 1. 채력 변수 - 종류마다 다르게 `
    override var enemyHp: Float = 1f

    // 2. 적이 충돌시 탱크에게 주는 데미지
    override var contactDamage: Float = 0f

    // 3. 속도
    override var enemySpeed: Float = 0f

    // 4. 플레이어 추쳑 시스템 -> DotEnemy는 필요 없음

    // 4. 추척 시스템
    override fun chasePlayer(playerX: Float, playerY: Float, delta: Float) {
        val dx = playerX - x   // 플레이어와 나의 x 차이
        val dy = playerY - y   // 플레이어와 나의 y 차이

        // x축 방향 이동
        if (dx > 0) x += enemySpeed * delta   // 플레이어가 오른쪽에 있으면 오른쪽으로
        if (dx < 0) x -= enemySpeed * delta   // 플레이어가 왼쪽에 있으면 왼쪽으로

        // y축 방향 이동
        if (dy > 0) y += enemySpeed * delta   // 플레이어가 위에 있으면 위로
        if (dy < 0) y -= enemySpeed * delta   // 플레이어가 아래에 있으면 아래로
    }

    // 5. 처치 시 점수
    override var getScore: Float = 10f

    // 6. 처치 시 경험치
    override var getExp: Float = 20f

    // 7.적 개체가 탱크에게 받는 데미지
    override fun takeDamage(amount: Float) {     // 적이 받는 데미지 처리
        enemyHp -= amount
    }

    // 8. 적이 죽어있는지 살아있는지 -> GameObject 에서 상속하는걸로
    override fun isAlive(): Boolean = enemyHp > 0

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }

}