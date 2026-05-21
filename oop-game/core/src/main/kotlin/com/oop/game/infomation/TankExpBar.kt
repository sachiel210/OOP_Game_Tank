package com.oop.game.infomation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

class TankExpBar( // GameObject의 자식 클래스
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
) : GameObject(x, y, 10f, 10f) {

    var currentLevel: Int = 1
    val maxLevel: Float = 45f
    var expPoint: Float = 0f // 현재 보유중인 경험치량
    val maxExp: Float = 1000f // 최대 경험치: 레벨업에 따라 최대 경험치 요구량이 늘어남

    var currentMaxExp: Float = maxExp
    var currentExp: Float = expPoint / currentMaxExp

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val backGround = Texture(Gdx.files.internal("tank_image/tankExpBar/expbar_background.png"))
    private val expBarLeftSide = Texture(Gdx.files.internal("tank_image/tankExpBar/expbar2.png"))
    private val expBarMiddleSide = Texture(Gdx.files.internal("tank_image/tankExpBar/expbar3.png"))
    private val expBarRightSide = Texture(Gdx.files.internal("tank_image/tankExpBar/expbar1.png"))

    protected val expBarProportion: Float = 2f // 기본 EXP바 비율을 정해주는 변수
    private val backGroundWidth = backGround.width / expBarProportion // EXP바 기본 너비
    private val backGroundHeight = backGround.height / expBarProportion // EXP바 기본 높이

    private val expBarLeftSideWidth = expBarLeftSide.width / expBarProportion // EXP바 좌측 반원 기본 너비
    private val expBarLeftSideHeight = expBarLeftSide.height / expBarProportion // EXP바 좌측 반원 기본 높이

    private val expBarRightSideWidth = expBarLeftSide.width / expBarProportion // EXP바 우측 반원 기본 너비
    private val expBarRightSideHeight = expBarLeftSide.height / expBarProportion // EXP바 우측 반원 기본 높이

    private val expBarMiddleSideWidth = expBarMiddleSide.width / expBarProportion // EXP바 중앙 사각형 기본 너비
    private val expBarMiddleSideHeight = expBarMiddleSide.height / expBarProportion // EXP바 중앙 사각형 기본 높이


    override fun update(delta: Float) {
        // 월드 경계 안쪽으로 가두기.
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)

        currentExp = expPoint / currentMaxExp
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
            y - backGroundHeight / 2f, // 탱크 중앙 하단 위치
            backGroundWidth,
            backGroundHeight
        )

        batch.draw(
            expBarLeftSide,
            x - backGroundWidth / 2f + 3f, // 체력바 좌측 위치
            y - expBarLeftSideHeight / 2f, // 체력바 배경과 같은 위치
            expBarLeftSideWidth,
            expBarLeftSideHeight
        )

        batch.draw(
            expBarMiddleSide,
            x - backGroundWidth / 2f + expBarLeftSideWidth + 3f, // 체력바 좌측 위치
            y - expBarMiddleSideHeight / 2f, // 체력바 배경과 같은 위치
            expBarMiddleSideWidth * 19.75f * currentExp,
            expBarMiddleSideHeight
        )

        batch.draw(
            expBarRightSide,
            x - backGroundWidth / 2f + expBarLeftSideWidth + 3f + expBarMiddleSideWidth * 19.75f * currentExp, // 체력바 좌측 위치
            y - expBarRightSideHeight / 2f, // 체력바 배경과 같은 위치
            expBarRightSideWidth,
            expBarRightSideHeight
        )
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        backGround.dispose()
        expBarLeftSide.dispose()
        expBarMiddleSide.dispose()
        expBarRightSide.dispose()
    }
}