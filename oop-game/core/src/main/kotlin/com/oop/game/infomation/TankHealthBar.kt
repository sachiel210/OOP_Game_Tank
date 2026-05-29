package com.oop.game.infomation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import com.oop.game.InputHandler

class TankHealthBar( // GameObject의 자식 클래스
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
    var tankSpeed: Float,
    var tankHealthPoint: Float,
    var tankMaxHealthPoint: Float
) : GameObject(x, y, 10f, 10f) {

    private var tankCurrentHealth = tankHealthPoint / tankMaxHealthPoint

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val backGround = Texture(Gdx.files.internal("tank_image/tankHealthBar/healthbar_background.png"))
    private val healthBarLeftSide = Texture(Gdx.files.internal("tank_image/tankHealthBar/healthbar1.png"))
    private val healthBarMiddleSide = Texture(Gdx.files.internal("tank_image/tankHealthBar/healthbar2.png"))
    private val healthBarRightSide = Texture(Gdx.files.internal("tank_image/tankHealthBar/healthbar3.png"))

    protected val healthBarProportion: Float = 4f // 기본 체력바 비율을 정해주는 변수
    private val backGroundWidth = backGround.width / healthBarProportion // 체력바 기본 너비
    private val backGroundHeight = backGround.height / healthBarProportion // 체력바 기본 높이

    private val healthBarLeftSideWidth = healthBarLeftSide.width / healthBarProportion // 체력바 좌측 반원 기본 너비
    private val healthBarLeftSideHeight = healthBarLeftSide.height / healthBarProportion // 체력바 좌측 반원 기본 높이

    private val healthBarRightSideWidth = healthBarLeftSide.width / healthBarProportion // 체력바 우측 반원 기본 너비
    private val healthBarRightSideHeight = healthBarLeftSide.height / healthBarProportion // 체력바 우측 반원 기본 높이

    private val healthBarMiddleSideWidth = healthBarMiddleSide.width / healthBarProportion // 체력바 중앙 사각형 기본 너비
    private val healthBarMiddleSideHeight = healthBarMiddleSide.height / healthBarProportion // 체력바 중앙 사각형 기본 높이


    override fun update(delta: Float) {
        if (InputHandler.isKeyPressed(InputHandler.A))  x -= tankSpeed * delta
        if (InputHandler.isKeyPressed(InputHandler.D)) x += tankSpeed * delta
        if (InputHandler.isKeyPressed(InputHandler.W))    y += tankSpeed * delta
        if (InputHandler.isKeyPressed(InputHandler.S))  y -= tankSpeed * delta

        // 월드 경계 안쪽으로 가두기.
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)
        tankCurrentHealth = tankHealthPoint / tankMaxHealthPoint
    }

    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 30x30 이고 w=30, h=30 이면 1:1 그대로 그려진다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(
            backGround,
            x - backGroundWidth / 2f, // 탱크 중앙 위치
            y - backGroundHeight / 2f - 50f, // 탱크 중앙 하단 위치
            backGroundWidth,
            backGroundHeight
        )

        batch.draw(
            healthBarLeftSide,
            x - backGroundWidth / 2f + 2f, // 체력바 좌측 위치
            y - healthBarLeftSideHeight / 2f - 50f, // 체력바 배경과 같은 위치
            healthBarLeftSideWidth,
            healthBarLeftSideHeight
        )

        batch.draw(
            healthBarMiddleSide,
            x - backGroundWidth / 2f + healthBarLeftSideWidth + 2f, // 체력바 좌측 위치
            y - healthBarMiddleSideHeight / 2f  - 50f, // 체력바 배경과 같은 위치
            healthBarMiddleSideWidth * 9.2f * tankCurrentHealth,
            healthBarMiddleSideHeight
        )

        batch.draw(
            healthBarRightSide,
            x - backGroundWidth / 2f + healthBarLeftSideWidth + 2f + healthBarMiddleSideWidth * 9.2f * tankCurrentHealth, // 체력바 좌측 위치
            y - healthBarRightSideHeight / 2f - 50f, // 체력바 배경과 같은 위치
            healthBarRightSideWidth,
            healthBarRightSideHeight
        )
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        backGround.dispose()
        healthBarLeftSide.dispose()
        healthBarMiddleSide.dispose()
        healthBarRightSide.dispose()
    }
}