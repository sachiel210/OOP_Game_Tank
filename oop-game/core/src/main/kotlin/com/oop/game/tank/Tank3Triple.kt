package com.oop.game.tank

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.InputHandler
import com.badlogic.gdx.math.MathUtils // 마우스 각도 계산

class Tank3Triple(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
): SuperTank(x, y, worldWidth, worldHeight) {

    override val tankHealthPoint: Float = 200f
    override val tankDamage: Float = 8f
    override val tankBulletSize: Float = 5f
    override val tankReloadSpeed: Float = 20f
    private var tank3RecoilDataLeft = RecoilData(recoilAmount = 0.3f)
    private var tank3RecoilDataRight = RecoilData(recoilAmount = 0.3f)
    private var tank3RecoilDataMiddle = RecoilData(recoilAmount = 0.2f)
    private var fireOrder: Float = 0f
    private var isFiring: Boolean = false

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val gunLeft = Texture(Gdx.files.internal("tank_image/tank3_Triple/Triple_gun_side.png"))
    private val gunRight = Texture(Gdx.files.internal("tank_image/tank3_Triple/Triple_gun_side.png"))
    private val gunMiddle = Texture(Gdx.files.internal("tank_image/tank3_Triple/Triple_gun_middle.png"))

    private val gunLeftWidth = gunLeft.width / tankProportion
    private val gunLeftHeight = gunLeft.height / tankProportion
    private val gunRightWidth = gunRight.width / tankProportion
    private val gunRightHeight = gunRight.height / tankProportion
    private val gunMiddleWidth = gunMiddle.width / tankProportion
    private val gunMiddleHeight = gunMiddle.height / tankProportion


    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 30x30 이고 w=30, h=30 이면 1:1 그대로 그려진다.
     */

    override fun update(delta: Float) {
        if ((Gdx.input.isButtonJustPressed(InputHandler.LeftMousClick))){
            isFiring = true
        }
        if (isFiring){
            if (fireOrder == 0f) {
                tank3RecoilDataLeft = recoil(tank3RecoilDataLeft.recoilTime,
                    tank3RecoilDataLeft.recoilStrength,
                    tank3RecoilDataLeft.recoilAmount,
                    tankReloadSpeed)
                if (tank3RecoilDataLeft.recoilTime == 0f) fireOrder = 1f
            } else if (fireOrder == 1f){
                tank3RecoilDataRight = recoil(tank3RecoilDataRight.recoilTime,
                    tank3RecoilDataRight.recoilStrength,
                    tank3RecoilDataRight.recoilAmount,
                    tankReloadSpeed)
                if (tank3RecoilDataRight.recoilTime == 0f) fireOrder = 2f
            } else {
                tank3RecoilDataMiddle = recoil(tank3RecoilDataMiddle.recoilTime,
                    tank3RecoilDataMiddle.recoilStrength,
                    tank3RecoilDataMiddle.recoilAmount,
                    tankReloadSpeed)
                if (tank3RecoilDataMiddle.recoilTime == 0f) {
                    isFiring = false
                    fireOrder = 0f
                }
            }
        }
        super.update(delta)
    }

    override fun draw(batch: SpriteBatch) {
        // 마우스와 탱크 각도 확인
        val angle = calAngle()

        val xRecoilLeft: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank3RecoilDataLeft.recoilStrength
        val yRecoilLeft: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank3RecoilDataLeft.recoilStrength

        val xRecoilRight: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank3RecoilDataRight.recoilStrength
        val yRecoilRight: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank3RecoilDataRight.recoilStrength

        val xRecoilMiddle: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank3RecoilDataMiddle.recoilStrength
        val yRecoilMiddle: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank3RecoilDataMiddle.recoilStrength

        batch.draw(gunLeft, // 텍스쳐
            x - gunLeftWidth / 2f - xRecoilLeft - 20f, // 위치
            y + 10f - yRecoilLeft, // 위치
            gunLeftWidth / 2f + 20f, -10f,
            gunLeftWidth,
            gunLeftHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gunLeft.width, gunLeft.height,
            false, false
        )

        batch.draw(gunRight, // 텍스쳐
            x - gunRightWidth / 2f - xRecoilRight + 20f, // 위치
            y + 10f - yRecoilRight, // 위치
            gunRightWidth / 2f - 20f, -10f,
            gunRightWidth,
            gunRightHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gunRight.width, gunRight.height,
            false, false
        )

        batch.draw(gunMiddle, // 텍스쳐
            x - gunMiddleWidth / 2f - xRecoilMiddle, // 위치
            y + 20f - yRecoilMiddle, // 위치
            gunMiddleWidth / 2f, -20f,
            gunMiddleWidth,
            gunMiddleHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gunMiddle.width, gunMiddle.height,
            false, false
        )

        super.draw(batch)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        gunLeft.dispose()
        gunRight.dispose()
        super.dispose()
    }

}