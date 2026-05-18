package com.oop.game.tank

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils // 마우스 각도 계산
import com.oop.game.InputHandler

class Tank4Quad(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
): SuperTank(x, y, worldWidth, worldHeight) {

    override val tankHealthPoint: Float = 100f
    override val tankDamage: Float = 10f
    override val tankBulletSize: Float = 10f
    override val tankReloadSpeed: Float = 10f
    private var tank4RecoilData = RecoilData(recoilAmount = 0.3f)

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val gun1Top = Texture(Gdx.files.internal("tank_image/tank4_Quad/Quad_gun.png"))
    private val gun2Left = Texture(Gdx.files.internal("tank_image/tank4_Quad/Quad_gun.png"))
    private val gun3Right = Texture(Gdx.files.internal("tank_image/tank4_Quad/Quad_gun.png"))
    private val gun4Down = Texture(Gdx.files.internal("tank_image/tank4_Quad/Quad_gun.png"))

    private val gunWidth = gun1Top.width / tankProportion
    private val gunHeight = gun1Top.height / tankProportion

    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 30x30 이고 w=30, h=30 이면 1:1 그대로 그려진다.
     */

    override fun update(delta: Float) {
        tank4RecoilData = recoil(tank4RecoilData.recoilTime,
            tank4RecoilData.recoilStrength,
            tank4RecoilData.recoilAmount,
            tankReloadSpeed,
            triggerFire = Gdx.input.isButtonJustPressed(InputHandler.LeftMousClick))

        super.update(delta)
    }

    override fun draw(batch: SpriteBatch) {
        // 마우스와 탱크 각도 확인
        val angle = calAngle()

        // 포 반동 시스템
        val xRecoil: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank4RecoilData.recoilStrength
        val yRecoil: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank4RecoilData.recoilStrength

        batch.draw(gun1Top, // 텍스쳐
            x - gunWidth / 2f - xRecoil, // 위치
            y + 25f - yRecoil, // 위치
            gunWidth / 2f, -25f,
            gunWidth,
            gunHeight,
            1f, 1f,
            angle - 90,
            0, 0,
            gun1Top.width, gun1Top.height,
            false, false
        )

        batch.draw(gun2Left, // 텍스쳐
            x - gunWidth / 2f + yRecoil, // 위치
            y + 25f - xRecoil, // 위치
            gunWidth / 2f, -25f,
            gunWidth,
            gunHeight,
            1f, 1f,
            angle,
            0, 0,
            gun2Left.width, gun2Left.height,
            false, false
        )

        batch.draw(gun3Right, // 텍스쳐
            x - gunWidth / 2f - yRecoil, // 위치
            y + 25f + xRecoil, // 위치
            gunWidth / 2f, -25f,
            gunWidth,
            gunHeight,
            1f, 1f,
            angle - 180,
            0, 0,
            gun3Right.width, gun3Right.height,
            false, false
        )

        batch.draw(gun4Down, // 텍스쳐
            x - gunWidth / 2f + xRecoil, // 위치
            y + 25f + yRecoil, // 위치
            gunWidth / 2f, -25f,
            gunWidth,
            gunHeight,
            1f, 1f,
            angle - 270,
            0, 0,
            gun4Down.width, gun4Down.height,
            false, false
        )

        super.draw(batch)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        gun1Top.dispose()
        super.dispose()
    }

}