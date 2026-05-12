package com.oop.game.tank

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import com.oop.game.InputHandler

import com.badlogic.gdx.math.MathUtils // 마우스 각도 계산
/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  탱크 부모 클래스. body, gun 이미지, 화살표 키로 조종.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  GameObject 를 상속하는 '가장 단순한' 예제다.
 *  자기 프로젝트의 Player 를 만들 때 이 파일을 통째로 복사해서
 *  texture 의 파일명을 자기 이미지로 바꾸거나,
 *  update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 *  핵심 포인트:
 *   ▸ Texture 는 객체가 살아있는 동안 한 번만 만들고 재사용 (생성 비용이 큼).
 *   ▸ 객체가 사라질 때 dispose() 로 GPU 자원 해제 — 기본 GameObject.dispose()를 override.
 *   ▸ batch.draw(texture, x, y, w, h) 한 줄로 이미지를 그린다.
 *
 * @param worldWidth/Height: 월드 크기를 받아 경계 밖으로 못 나가게 제한하는 용도.
 */

/**
 * 탱크 키우기 게임 탱크 주요 속성
 * 1. 체력: 탱크의 고유 체력
 * 2. 데미지: 탱크가 가지고 있는 고유 데미지
 * 3. 탄환 크기: 탱크가 쏘는 탄환의 고유 크기
 * 4. 연사속도: 탱크의 고유 연사속도
 *
 * 탱크 레벨업 시 위 스탯에 가중치 추가
 */

abstract class SuperTank( // GameObject의 자식 클래스
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
) : GameObject(x, y, 10f, 10f) {

    abstract val tankHealthPoint: Float
    abstract val tankDamage: Float
    abstract val tankBulletSize: Float
    abstract val tankReloadSpeed: Float
    protected val speed = 200f

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val body = Texture(Gdx.files.internal("tank_image/body.png"))

    protected val tankProportion: Float = 4f // 기본 탱크 크기를 정해주는 변수
    private val bodyWidth = body.width / tankProportion
    private val bodyHeight = body.height / tankProportion

    abstract fun recoil() // 포 반동 애니메이션 함수

    override fun update(delta: Float) {
        if (InputHandler.isKeyPressed(InputHandler.LEFT))  x -= speed * delta
        if (InputHandler.isKeyPressed(InputHandler.RIGHT)) x += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.UP))    y += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.DOWN))  y -= speed * delta

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
            body,
            x - bodyWidth / 2f, // 탱크 중앙 위치 확인
            y - bodyHeight / 2f, // 탱크 중앙 위치 확인
            bodyWidth,
            bodyHeight
        )
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        body.dispose()
    }
}
