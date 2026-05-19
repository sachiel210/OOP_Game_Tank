package com.oop.game.tank

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
    private val tankSpeed: Float,
) : GameObject(x, y, 10f, 10f)
{
    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val backGround = Texture(Gdx.files.internal("tank_image/tankHealthBar/background.png"))

    protected val healthBarProportion: Float = 4f // 기본 체력바 비율을 정해주는 변수
    private val backGroundWidth = backGround.width / healthBarProportion // 체력바 기본 너비
    private val backGroundHeight = backGround.height / healthBarProportion // 체력바 기본 높이


    override fun update(delta: Float) {
        if (InputHandler.isKeyPressed(InputHandler.LEFT))  x -= tankSpeed * delta
        if (InputHandler.isKeyPressed(InputHandler.RIGHT)) x += tankSpeed * delta
        if (InputHandler.isKeyPressed(InputHandler.UP))    y += tankSpeed * delta
        if (InputHandler.isKeyPressed(InputHandler.DOWN))  y -= tankSpeed * delta

        // 월드 경계 안쪽으로 가두기.
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)
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
            y - backGroundHeight / 2f - 50f, // 탱크 중앙 위치
            backGroundWidth,
            backGroundHeight
        )
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        backGround.dispose()
    }
}