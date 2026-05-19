package com.oop.game.tank

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.InputHandler
import com.badlogic.gdx.math.MathUtils // 마우스 각도 계산, 포신 반동 계산을 위한 수학 클래스

class Tank2Twin(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
): SuperTank(x, y, worldWidth, worldHeight) {

    override var tankHealthPoint: Float = 100f
    override val tankMaxHealthPoint: Float = 100f
    override val tankDamage: Float = 8f
    override val tankBulletSize: Float = 5f
    override val tankReloadSpeed: Float = 20f
    private var tank2RecoilDataLeft = RecoilData(recoilAmount = 0.3f)
    private var tank2RecoilDataRight = RecoilData(recoilAmount = 0.3f)
    private var isLeft: Boolean = true
    private var isFiring: Boolean = false

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val gunLeft = Texture(Gdx.files.internal("tank_image/tank2_Twin/Twin_gun.png"))
    private val gunRight = Texture(Gdx.files.internal("tank_image/tank2_Twin/Twin_gun.png"))

    private val gunLeftWidth = gunLeft.width / tankProportion
    private val gunLeftHeight = gunLeft.height / tankProportion
    private val gunRightWidth = gunRight.width / tankProportion
    private val gunRightHeight = gunRight.height / tankProportion


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
        if (isFiring) {
            if (isLeft) {
                tank2RecoilDataLeft = recoil(
                    tank2RecoilDataLeft.recoilTime,
                    tank2RecoilDataLeft.recoilStrength,
                    tank2RecoilDataLeft.recoilAmount,
                    tankReloadSpeed
                )
                if (tank2RecoilDataLeft.recoilTime == 0f) isLeft = false
            } else {
                tank2RecoilDataRight = recoil(
                    tank2RecoilDataRight.recoilTime,
                    tank2RecoilDataRight.recoilStrength,
                    tank2RecoilDataRight.recoilAmount,
                    tankReloadSpeed
                )
                if (tank2RecoilDataRight.recoilTime == 0f) {
                    isLeft = true
                    isFiring = false
                }
            }
        }
        super.update(delta)
    }

    override fun draw(batch: SpriteBatch) {
        // 마우스와 탱크 각도 확인
        val angle = calAngle()

        /*
         * 포 반동 시스템
         *
         * 상세설명
         * 포신(gun)은 recoilStrength 만큼의 반동을 받음
         * 포신의 포구가 상단(y축)을 바라보고 있지 않을 경우
         * 포의 움직임은 body의 중앙이 아니라 위 아래를 기준으로 이루어짐
         * 이를 보완하기 위해서 삼각함수 사용
         *
         * 코사인함수: 반동 애니메이션을 위해 보정되야 할 x값을 계산
         * 사인함수: 반동 애니메이션을 위해 보정되어야 할 y값을 계산
         *
         * 내부 각도는 라디안으로 계산됨
         * 마우스가 바라보는 각도 * MathUtils.degreesToRadians 를 곱하면 라디안이 됨
         */
        val xRecoilLeft: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank2RecoilDataLeft.recoilStrength
        val yRecoilLeft: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank2RecoilDataLeft.recoilStrength

        val xRecoilRight: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank2RecoilDataRight.recoilStrength
        val yRecoilRight: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank2RecoilDataRight.recoilStrength

        batch.draw(gunLeft, // 텍스쳐
            x - gunLeftWidth / 2f - xRecoilLeft - 16f, // 위치
            y + 15f - yRecoilLeft, // 위치
            gunLeftWidth / 2f + 16f, -15f,
            gunLeftWidth,
            gunLeftHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gunLeft.width, gunLeft.height,
            false, false
        )

        batch.draw(gunRight, // 텍스쳐
            x - gunRightWidth / 2f - xRecoilRight + 16f, // 위치
            y + 15f - yRecoilRight, // 위치
            gunRightWidth / 2f - 16f, -15f,
            gunRightWidth,
            gunRightHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gunRight.width, gunRight.height,
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