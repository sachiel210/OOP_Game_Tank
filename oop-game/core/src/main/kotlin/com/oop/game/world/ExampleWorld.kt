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


class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    private enum class GameState {
        IN_PLAY,
        GAME_OVER
    }

    private val player = Tank1Nomal(
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
    private var reloadTimer = 0f

    private val spawnWeights = listOf(
        40,  // DotEnemy      40%
        30,  // TriangleEnemy 30%
        20,  // SquareEnemy   20%
        10   // PentagonEnemy 10%
    )

    private var state = GameState.IN_PLAY

    private val tileTexture = Texture(Gdx.files.internal("tile.png"))
    private val tileSize = 64f

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
            GameState.GAME_OVER -> updateGameOver()
        }
    }

    private fun updateInPlay(delta: Float) {
        // 카메라 이동 (WASD)
        val cameraSpeed = 200f * delta
        if (InputHandler.isKeyPressed(InputHandler.W)) offsetY += cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.S)) offsetY -= cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.A)) offsetX -= cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.D)) offsetX += cameraSpeed

        offsetX = offsetX.coerceIn(0f, worldWidth - screenWidth)
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

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
        // 정답: 경험치 바가 작렬하게 터져버린다. 계산에 2프레임 이상 필요함
        // for 문 이용하면 해결 가능할지도. 그건 그냥 후반에 AI로 짤 때 시켜보자
        if (Gdx.input.isKeyJustPressed(InputHandler.R)){
            if (expBar.expPoint + 900f > expBar.currentMaxExp) { // 레벨업을 할 만큼 충분한 경험치가 모였는지 검사
                expBar.expPoint -= expBar.currentMaxExp // 현재 레벨에서 획득한 경험치는 전부 제거함 (시각효과 위해)
                expBar.expPoint += 900f // 획득한 경험치만큼 현재 경험치에 추가함
                expBar.currentMaxExp = expBar.currentMaxExp * 1.2f // 새로운 레벨은 레벨업 위해 필요 경험치가 1.2배 증가함
                expBar.currentLevel += 1 // 레벨 1 증가
            } else {
                expBar.expPoint += 900f // 레벨업에 충분한 경험치가 모이지 않았을 때는 그냥 더함
            }
        }

        // 2) 충돌 체크 — 모든 적과 플레이어 충돌 확인
        for (obj in getObjects()) {
            if (obj is SuperEnemy && player.collidesWith(obj)) {
                state = GameState.GAME_OVER
                break
            }
        }

        // 3) 죽은 객체 정리
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
        reloadTimer += delta
        if (Gdx.input.isButtonPressed(InputHandler.LeftMousClick) && reloadTimer >= BULLET1_RELOAD_INTERVAL) {
            reloadTimer = 0f

            val bulletX = player.x
            val bulletY = player.y

            val mouseX = Gdx.input.x.toFloat() + offsetX
            val mouseY = (screenHeight - Gdx.input.y.toFloat()) + offsetY

            val toMouseVectorX = mouseX - bulletX
            val toMouseVectorY = mouseY - bulletY
            val toMouseDist = Math.sqrt((toMouseVectorX * toMouseVectorX + toMouseVectorY * toMouseVectorY).toDouble()).toFloat()
            val aimVectorX = toMouseVectorX / toMouseDist
            val aimVectorY = toMouseVectorY / toMouseDist

            for (bullet in Bullet4Quad.fire(bulletX, bulletY, aimVectorX, aimVectorY)) {
                add(bullet)
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
            y = screenHeight / 2 + 40f,
            color = Color.WHITE,
            scale = 2f
        )
        drawTextOnScreen(
            text = "Level: ${expBar.currentLevel}",
            x = screenWidth / 2 - 50f,
            y = screenHeight / 2,
            color = Color.WHITE,
            scale = 1.5f
        )
        drawTextOnScreen(
            text = "EXP: ${expBar.expPoint.toInt()}",
            x = screenWidth / 2 - 50f,
            y = screenHeight / 2 - 35f,
            color = Color.WHITE,
            scale = 1.5f
        )
        drawTextOnScreen(
            text = "TIME: ${spawnTimer.toInt()}",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 75f,
            color = Color.WHITE,
            scale = 1.5f
        )
        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 100f,
            y = screenHeight / 2 - 100f,
            color = Color.WHITE,
            scale = 1f
        )
    }

    override fun dispose() {
        super.dispose()
        tileTexture.dispose()
    }
}
