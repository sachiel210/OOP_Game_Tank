package com.oop.game.tank

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils // 마우스 각도 계산
import com.oop.game.InputHandler

class Tank5Sniper(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
): SuperTank(x, y, worldWidth, worldHeight) {

    override var tankHealthPoint: Float = 100f
    override val tankMaxHealthPoint: Float = 100f
    override val tankDamage: Float = 10f
    override val tankBulletSize: Float = 10f
    override val tankReloadSpeed: Float = 10f
    private var tank5RecoilData = RecoilData(recoilAmount = 0.5f)

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val gun = Texture(Gdx.files.internal("tank_image/tank5_Sniper/Sniper_gun.png"))
    private val gunPed = Texture(Gdx.files.internal("tank_image/tank5_Sniper/Sniper_gun_ped.png"))

    private val gunWidth = gun.width / tankProportion
    private val gunHeight = gun.height / tankProportion
    private val gunPedWidth = gunPed.width / tankProportion
    private val gunPedHeight = gunPed.height / tankProportion

    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 30x30 이고 w=30, h=30 이면 1:1 그대로 그려진다.
     */

    override fun update(delta: Float) {
        tank5RecoilData = recoil(tank5RecoilData.recoilTime,
            tank5RecoilData.recoilStrength,
            tank5RecoilData.recoilAmount,
            tankReloadSpeed,
            triggerFire = Gdx.input.isButtonJustPressed(InputHandler.LeftMousClick))

        super.update(delta)
    }

    override fun draw(batch: SpriteBatch) {
        // 마우스와 탱크 각도 확인
        val angle = calAngle()

        val xRecoil: Float = MathUtils.cos(angle * MathUtils.degreesToRadians) * tank5RecoilData.recoilStrength
        val yRecoil: Float = MathUtils.sin(angle * MathUtils.degreesToRadians) * tank5RecoilData.recoilStrength

        batch.draw(gun, // 텍스쳐
            x - gunWidth / 2f - xRecoil, // 위치
            y + 20f - yRecoil, // 위치
            gunWidth / 2f, -20f,
            gunWidth,
            gunHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gun.width, gun.height,
            false, false
        )

        batch.draw(gunPed, // 텍스쳐
            x - gunPedWidth / 2f, // 위치
            y + 25f, // 위치
            gunPedWidth / 2f, -25f,
            gunPedWidth,
            gunPedHeight,
            1f, 1f,
            angle-90,
            0, 0,
            gunPed.width, gunPed.height,
            false, false
        )

        super.draw(batch)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        gun.dispose()
        super.dispose()
    }

}