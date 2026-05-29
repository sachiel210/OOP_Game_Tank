package com.oop.game.example.enempyList

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  적 예제 — enemy.png 이미지, 수평으로 자동 왕복 이동.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  GameObject 를 상속해 만든 '입력 없이 스스로 움직이는' 객체 예제.
 *
 *  핵심 포인트:
 *   ▸ update() 에 입력 처리가 없다 — AI(자율 행동)는 여기서 작성.
 *   ▸ direction 이라는 '상태 변수'를 둬서 좌/우 방향 전환을 구현.
 *
 *  응용 아이디어:
 *   ▸ 생성자에서 speed 를 받아 FastEnemy, SlowEnemy 로 다양화
 *   ▸ 체력(hp)과 takeDamage() 메서드 추가
 *   ▸ 이동 패턴을 사인파, 원운동 등으로 바꾸기
 *
 * @param minX 왕복 이동의 왼쪽 한계 (보통 0f)
 * @param maxX 왕복 이동의 오른쪽 한계 (보통 worldWidth)
 */
abstract class SuperEnemy(
    x: Float,
    y: Float,
    width : Float,
    height: Float
) : GameObject(x, y, width, height) {

    // 1. 채력 변수 - 종류마다 다르게 `
    abstract var enemyHp : Float
        protected set

    // 2. 적이 충돌시 탱크에게 주는 데미지
    abstract var contactDamage : Float
        protected set

    // 3. 속도
    abstract var enemySpeed : Float
        protected set
    // 4. 플레이어 추쳑 시스템

    open fun chasePlayer(playerX: Float, playerY: Float, delta: Float) {
        val dx = playerX - x   // 플레이어와 나의 x 차이
        val dy = playerY - y   // 플레이어와 나의 y 차이

        // x축 방향 이동
        if (dx > 0) x += enemySpeed * delta   // 플레이어가 오른쪽에 있으면 오른쪽으로
        if (dx < 0) x -= enemySpeed * delta   // 플레이어가 왼쪽에 있으면 왼쪽으로

        // y축 방향 이동
        if (dy > 0) y += enemySpeed * delta   // 플레이어가 위에 있으면 위로
        if (dy < 0) y -= enemySpeed * delta   // 플레이어가 아래에 있으면 아래로
    }


    // 5. 치치 시 점수
    abstract var getScore : Float
        protected set

    // 6. 처치 시 경험치
    abstract var getExp : Float
        protected set

    // 7. 적 개체가 탱크에게 받는 데미지
    open fun takeDamage(amount: Float) {     // 적이 받는 데미지 처리
        enemyHp -= amount
    }
    // 8. 적이 죽어있는지 살아있는지 -> GameObject 에서 상속하는걸로
    override fun isAlive(): Boolean = enemyHp > 0

    /**
     * 자신의 이미지를 그린다.
     *   원본은 40x40 이고 width/height 도 40 이라 1:1 로 그려진다.
     *   더 크게 보이게 하려면 width/height 를 늘리면 자동 확대된다.
     */
    abstract  override fun draw(batch: SpriteBatch)

    abstract override fun dispose()
}