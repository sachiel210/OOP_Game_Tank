package com.oop.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import com.oop.game.example.enempyList.*
import com.oop.game.bullet.*
import com.oop.game.infomation.*
import com.oop.game.tank.*
import kotlin.math.floor
import com.badlogic.gdx.math.MathUtils
import kotlin.math.exp


class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    private enum class GameState {
        IN_PLAY,
        TANK_LEVEL_UP,
        GAME_OVER
    }

    private var player: SuperTank = Tank1Nomal(
        x = worldWidth / 2,
        y = worldHeight / 2,
        worldWidth = worldWidth,
        worldHeight = worldHeight
    )

    // 체력바 인스턴스
    // 탱크를 따라다니기 때문에 탱크 속도를 인수로 넣음
    private val healthBar = TankHealthBar(
        worldWidth / 2,
        worldHeight / 2,
        worldWidth,
        worldHeight,
        player.tankSpeed,
        player.tankHealthPoint,
        player.tankMaxHealthPoint
    )

    // 경험치바 인스턴스
    // 카메라를 따라다님
    private val expBar = TankExpBar(
        offsetX + screenWidth / 2,
        offsetY + screenHeight / 2,
        worldWidth,
        worldHeight
    )

    private val spawnInterval = 3f
    private var spawnTimer = 0f

    private val spawnWeights = listOf(
        40,  // DotEnemy      40%
        30,  // TriangleEnemy 30%
        20,  // SquareEnemy   20%
        10   // PentagonEnemy 10%
    )

    private var state = GameState.IN_PLAY

    private val tileTexture = Texture(Gdx.files.internal("tile.png"))
    private val tileSize = 64f

    // 탱크 선택 버튼 이미지
    private val tankSelectTextures = listOf(
        Texture(Gdx.files.internal("tank_image/tank2_Twin/Twin_selection.png")),           // 0 → Twin
        Texture(Gdx.files.internal("tank_image/tank3_Triple/Triple_selection.png")),       // 1 → Triple
        Texture(Gdx.files.internal("tank_image/tank4_Quad/Quad_selection.png")),           // 2 → Quad
        Texture(Gdx.files.internal("tank_image/tank5_Sniper/Sniper_selection.png")),       // 3 → Sniper
        Texture(Gdx.files.internal("tank_image/tank6_Ranger/Ranger_selection.png")),       // 4 → Ranger
        Texture(Gdx.files.internal("tank_image/tank7_Destroyer/Destroyer_selection.png"))  // 5 → Destroyer
    )
    private var currentOptions: List<Int> = emptyList() // 다음 레벨에서 선택 가능한 탱크를 확인

    init {

        repeat(5) {
            val spawnX = (Math.random() * worldWidth).toFloat()
            val spawnY = (Math.random() * worldHeight).toFloat()
            add(DotEnemy(spawnX, spawnY))
        }
        add(player)
        add(healthBar)
        add(expBar)
    }

    override fun update(delta: Float) {
        when (state) {
            GameState.IN_PLAY -> updateInPlay(delta)
            GameState.TANK_LEVEL_UP -> updateTankLevelUp(delta)
            GameState.GAME_OVER -> updateGameOver()
        }
    }

    private fun updateInPlay(delta: Float) {
        // 카메라 이동 탱크추적
        offsetX = (player.x - screenWidth / 2f).coerceIn(0f, worldWidth - screenWidth)
        offsetY = (player.y - screenHeight / 2f).coerceIn(0f, worldHeight - screenHeight)
        // 카메라 위치가 coerceIn 범위 내부로 제한이 됨
        // 감사합니다 구글

        // 경험치 바 위치 갱신
        expBar.x = offsetX + screenWidth / 2 // 화면 중앙에 위치
        expBar.y = offsetY + screenHeight / 2 - 320f // 320만큼 빼서 화면 하단에 배치되도록 유도

        // 1) 게임 객체 갱신
        updateAllObjects(delta)

        // 테스트를 위한 체력 자해 시스템
        if (Gdx.input.isKeyJustPressed(InputHandler.E)){ // E를 누르는 것이 트리거로 발동됨
            player.tankHealthPoint = player.tankHealthPoint - 20f // 20만큼 체력을 제거함
            if (player.tankHealthPoint < 0f) { // 체력이 0보다 작으면
                player.tankHealthPoint = 0f // 0 이하로 가지 않도록 예외처리
            }
        }
        healthBar.tankHealthPoint = player.tankHealthPoint // 체력바와 탱크의 체력을 연동시켜 줌

        // 레벨링 시스템
        // 문제: 레벨 2개 이상을 한 프레임에 올려야 하는 상황이 되면?
        // 정답: 경험치 바가 작렬하게 터져버린다. 펑~. 계산에 2프레임 이상 필요함
        // for 문 이용하면 해결 가능할지도. 그건 그냥 후반에 AI로 짤 때 시켜보자
        // 업데이트: while문으로 살렸다 만세!
        if (Gdx.input.isKeyJustPressed(InputHandler.R)){
            if (expBar.expPoint + 6000f > expBar.currentMaxExp) { // 레벨업을 할 만큼 충분한 경험치가 모였는지 검사
                expBar.expPoint += 6000f  // 일단 경험치 더하고

                while (expBar.expPoint >= expBar.currentMaxExp) {  // 레벨업 조건 충족하는 동안 반복
                    expBar.expPoint -= expBar.currentMaxExp // 초과한 경험치는 남김
                    expBar.currentMaxExp = expBar.currentMaxExp * 1.1f
                    expBar.currentLevel += 1
                    checkLevelUpgrade()
                }
            } else {
                expBar.expPoint += 6000f // 레벨업에 충분한 경험치가 모이지 않았을 때는 그냥 더함
            }
        }

        // ── 탄환 vs 적 충돌 판정 ──
        for (bullet in getObjects()) {
            if (bullet is SuperBullet) {
                for (target in getObjects()) {
                    if (target is SuperEnemy && target !is DotEnemy && bullet.collidesWith(target)) {
                        target.takeDamage(bullet.damage) // HP 감소만
                        bullet.kill()                    // 탄환 제거
                        break                            // 탄환 하나당 적 하나만 처리
                    }
                }
            }
        }


        // 2) 충돌 체크 — 모든 적과 플레이어 충돌 확인
        for (obj in getObjects()) {
            if (obj is SuperEnemy && player.collidesWith(obj)) {
                if (obj is DotEnemy) {
                    obj.takeDamage(obj.enemyHp)
                    expBar.expPoint += obj.getExp
                } else {
                    player.takeDamage(obj.contactDamage)
                    obj.takeDamage(obj.enemyHp)
                    expBar.expPoint += obj.getExp
                }
            }
        }

        if (player.tankHealthPoint <= 0) {
            state = GameState.GAME_OVER
        }

        updateAllObjects(delta)
        removeDead()

        // 4) 타이머로 랜덤 스폰
        spawnTimer += delta
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f
            spawnRandomEnemy()
        }

        // 5) 플레이어 좌표 전달 (적 추적)
        for (obj in getObjects()) {
            if (obj is SuperEnemy) {
                obj.chasePlayer(player.x, player.y, delta)
            }
        }

        // 적들이 추적한 뒤 겹치면 밀어내기
        resolveEnemyCollisions()

        // 6) 발사 로직
        if (Gdx.input.isButtonJustPressed(InputHandler.LeftMousClick)) {
            val bulletX = player.x
            val bulletY = player.y

            val mouseX = Gdx.input.x.toFloat() + offsetX
            val mouseY = (screenHeight - Gdx.input.y.toFloat()) + offsetY

            val toMouseVectorX = mouseX - bulletX
            val toMouseVectorY = mouseY - bulletY
            val toMouseDist = Math.sqrt((toMouseVectorX * toMouseVectorX + toMouseVectorY * toMouseVectorY).toDouble()).toFloat()
            val aimVectorX = toMouseVectorX / toMouseDist
            val aimVectorY = toMouseVectorY / toMouseDist

            val temp = Bullet5Sniper(bulletX - 8f, bulletY - 4f, aimVectorX, aimVectorY)
            val bullets = temp.fire()
            for (i in 0 until bullets.size) {
                add(bullets[i])
            }
        }
    }

    // updateInPlay() 밖으로 꺼낸 함수
    private fun spawnRandomEnemy() {
        val spawnX = (Math.random() * worldWidth).toFloat()
        val spawnY = (Math.random() * worldHeight).toFloat()

        val rand = (Math.random() * 100).toInt()
        val enemy = when {
            rand < spawnWeights[0]                                          -> DotEnemy(spawnX, spawnY)
            rand < spawnWeights[0] + spawnWeights[1]                       -> TriangleEnemy(spawnX, spawnY)
            rand < spawnWeights[0] + spawnWeights[1] + spawnWeights[2]     -> SquardEnemy(spawnX, spawnY)
            else                                                            -> PentagonEnemy(spawnX, spawnY)
        }
        add(enemy)
    }

    // ── 적들끼리 겹치지 않게 밀어내기 ──
    private fun resolveEnemyCollisions() {
        // 살아있는 적들만 모음
        val enemies = mutableListOf<SuperEnemy>()
        for (obj in getObjects()) {
            // DotEnemy는 충돌 판정에서 제외
            if (obj is SuperEnemy && obj !is DotEnemy) {
                enemies.add(obj)
            }
        }

        // 모든 적의 쌍을 검사 (i+1부터 시작 → 같은 쌍 두 번 검사 안 함)
        for (i in enemies.indices) {
            for (j in i + 1 until enemies.size) {
                val a = enemies[i]
                val b = enemies[j]

                if (a.collidesWith(b)) {
                    pushApart(a, b)
                }
            }
        }
    }

    // 두 적을 서로 반대 방향으로 밀어냄
    private fun pushApart(a: SuperEnemy, b: SuperEnemy) {
        val pushAmount = 2f   // 한 프레임에 밀어내는 거리

        val dx = b.x - a.x
        val dy = b.y - a.y

        // x축 방향 분리
        if (dx > 0) {
            b.x += pushAmount
            a.x -= pushAmount
        } else {
            b.x -= pushAmount
            a.x += pushAmount
        }

        // y축 방향 분리
        if (dy > 0) {
            b.y += pushAmount
            a.y -= pushAmount
        } else {
            b.y -= pushAmount
            a.y += pushAmount
        }
    }

    // 탱크 레벨업시 관련 함수들
    private fun checkLevelUpgrade() {
        val options: List<Int>? = when{
            // 15레벨 달성시 Twin Sniper 이지선다
            // 15레벨을 넘겨도 탱크 업그레이드에 문제가 없게 설계됨
            (expBar.currentLevel >= 15 && player is Tank1Nomal) -> listOf(0, 3)
            expBar.currentLevel >= 30 -> when (player) {
                is Tank2Twin   -> listOf(1, 2)  // Twin 골랐으면 Triple, Quad 이지선다
                is Tank5Sniper -> listOf(4, 5)  // Sniper 골랐으면 Ranger, Destroyer 이지선다
                else -> null             // 예외처리
            }
            else -> null // 특정 레벨이 아니면 그냥 레벨업
        }
        if (options != null) {
            currentOptions = options  // 멤버 변수에 할당
            state = GameState.TANK_LEVEL_UP
        }
    }

    // 선택한 탱크 반환
    private fun createTank(index: Int): SuperTank {
        val x = player.x
        val y = player.y
        return when (index) {
            0 -> Tank2Twin(x, y, worldWidth, worldHeight)
            1 -> Tank3Triple(x, y, worldWidth, worldHeight)
            2 -> Tank4Quad(x, y, worldWidth, worldHeight)
            3 -> Tank5Sniper(x, y, worldWidth, worldHeight)
            4 -> Tank6Ranger(x, y, worldWidth, worldHeight)
            5 -> Tank7Destroyer(x, y, worldWidth, worldHeight)
            else -> Tank1Nomal(x, y, worldWidth, worldHeight)
        }
    }

    private fun updateTankLevelUp(delta: Float) {
        updateInPlay(delta)
        if (!Gdx.input.isButtonJustPressed(InputHandler.LeftMousClick)) return // 클릭 안 했으면 그냥 넘어감

        val mouseX = Gdx.input.x.toFloat()
        val mouseY = screenHeight - Gdx.input.y.toFloat() // Y축 반전

        val tex0 = tankSelectTextures[currentOptions[0]]
        val totalW = currentOptions.size * (tex0.width / 5f + 20f) - 20f // 버튼들 총 너비 계산
        val startX = screenWidth / 2 - totalW / 2 // 화면 중앙 기준으로 정렬

        for (i in currentOptions.indices) {
            val index = currentOptions[i]
            val tex = tankSelectTextures[index]
            val bw = tex.width / 5f  // 원본의 1/5 크기
            val bh = tex.height / 5f // 원본의 1/5 크기
            val bx = startX + i * (bw + 20f)
            val by = screenHeight / 2 - bh / 2f // 화면 세로 중앙

            if (mouseX in bx..(bx + bw) && mouseY in by..(by + bh)) { // 버튼 영역 안에 클릭했는가?
                val newTank = createTank(index)
                remove(player)  // 기존 탱크 삭제
                player = newTank
                add(player)     // 새 탱크 입장
                healthBar.tankSpeed = player.tankSpeed
                healthBar.tankHealthPoint = player.tankHealthPoint
                healthBar.tankMaxHealthPoint = player.tankMaxHealthPoint
                state = GameState.IN_PLAY // 통상적인 게임 플레이 상태로 전환
            }
        }
    }

    private fun drawTankSelectOverlay() {
        drawTextOnScreen(
            text = "choose tank!",
            x = screenWidth / 2 - 100f,
            y = screenHeight / 2 + 120f,
            color = Color.YELLOW,
            scale = 2f
        )

        val tex0 = tankSelectTextures[currentOptions[0]]
        val totalW = currentOptions.size * (tex0.width / 5f + 20f) - 20f
        val startX = screenWidth / 2 - totalW / 2

        for (i in currentOptions.indices) {
            val index = currentOptions[i]
            val tex = tankSelectTextures[index]
            val bw = tex.width / 5f
            val bh = tex.height / 5f
            val bx = startX + i * (bw + 20f)
            val by = screenHeight / 2 - bh / 2f

            batch.begin()
            batch.draw(tex, bx, by, bw, bh) // 탱크 선택 버튼 그리기
            batch.end()
        }
    }
// 탱크 레벨업시 관련 함수들 여기까지

    private fun updateGameOver() {
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    override fun drawBackground(batch: SpriteBatch) {
        val startCol = floor(offsetX / tileSize).toInt() - 1
        val startRow = floor(offsetY / tileSize).toInt() - 1
        val cols = (screenWidth / tileSize).toInt() + 3
        val rows = (screenHeight / tileSize).toInt() + 3

        for (row in startRow until startRow + rows) {
            for (col in startCol until startCol + cols) {
                val drawX = col * tileSize - offsetX
                val drawY = row * tileSize - offsetY
                batch.draw(tileTexture, drawX, drawY, tileSize, tileSize)
            }
        }

        batch.color = Color.WHITE
    }

    override fun render(delta: Float) {
        super.render(delta)
        drawHud()
        when (state) {
            GameState.IN_PLAY -> {}
            GameState.TANK_LEVEL_UP -> drawTankSelectOverlay()
            GameState.GAME_OVER -> drawGameOverOverlay()
        }
    }

    private fun drawHud() {
        drawTextOnScreen(
            text = "HP: 3",
            x = 10f,
            y = screenHeight - 10f,
            color = Color.YELLOW,
            scale = 1.2f
        )
        drawTextInWorld(
            text = "WORLD CENTER",
            worldX = worldWidth / 2 - 70f,
            worldY = worldHeight / 2,
            color = Color.CYAN,
            scale = 1.5f
        )

        drawTextOnScreen( // 레벨 표시
            text = "LV. ${expBar.currentLevel}",
            x = screenWidth / 2 - 20f,   // 원하는 위치로 조정
            y = 50f,
            color = Color.WHITE,
            scale = 1.5f
        )
    }

    private fun drawGameOverOverlay() {
        drawTextOnScreen(
            text = "Game Over!",
            x = screenWidth / 2 - 80f,
            y = screenHeight / 2,
            color = Color.WHITE,
            scale = 2f
        )
        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 40f,
            color = Color.WHITE,
            scale = 1f
        )
    }

    override fun dispose() {
        super.dispose()
        tileTexture.dispose()
        for (tex in tankSelectTextures) {
            tex.dispose()
        }
    }
}
