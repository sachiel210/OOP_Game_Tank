package com.oop.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import com.oop.game.example.enempyList.*
import com.oop.game.example.Bullet1Normal
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

    private val player = Tank3Triple(
        x = worldWidth / 2,
        y = worldHeight / 2,
        worldWidth = worldWidth,
        worldHeight = worldHeight
    )

    private val healthBar = TankHealthBar(
        worldWidth / 2,
        worldHeight / 2,
        worldWidth,
        worldHeight,
        player.tankSpeed,
        player.tankHealthPoint,
        player.tankMaxHealthPoint
    )

    private val expBar = TankExpBar(
        offsetX + screenWidth / 2,
        offsetY + screenHeight / 2,
        worldWidth,
        worldHeight,
        player.tankSpeed
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
        expBar.x = offsetX + screenWidth / 2
        expBar.y = offsetY + screenHeight / 2 - 320f

        // 1) 게임 객체 갱신
        updateAllObjects(delta)

        if (Gdx.input.isKeyJustPressed(InputHandler.E)){ // 자해
            player.tankHealthPoint = player.tankHealthPoint - 20f
            if (player.tankHealthPoint < 0f) {
                player.tankHealthPoint = 0f
            }
        }
        healthBar.tankHealthPoint = player.tankHealthPoint

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

        // 6) 발사 로직
        if (Gdx.input.isButtonJustPressed(InputHandler.LeftMousClick)) {
            val bulletX = player.x
            val bulletY = player.y

            val mouseX = Gdx.input.x.toFloat() + offsetX
            val mouseY = (screenHeight - Gdx.input.y.toFloat()) + offsetY

            val dx = mouseX - bulletX
            val dy = mouseY - bulletY
            val len = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            val dirX = dx / len
            val dirY = dy / len

            add(Bullet1Normal(bulletX, bulletY, dirX, dirY))
        }
    }

    // updateInPlay() 밖으로 꺼낸 함수
    private fun spawnRandomEnemy() {
        val spawnX = (Math.random() * worldWidth).toFloat()
        val spawnY = (Math.random() * worldHeight).toFloat()

        val rand = (Math.random() * 100).toInt()
        val enemy = when {
            rand < spawnWeights[0]                                         -> DotEnemy(spawnX, spawnY)
            rand < spawnWeights[0] + spawnWeights[1]                       -> TriangleEnemy(spawnX, spawnY)
            rand < spawnWeights[0] + spawnWeights[1] + spawnWeights[2]     -> SquardEnemy(spawnX, spawnY)
            else                                                           -> PentagonEnemy(spawnX, spawnY)
        }
        add(enemy)
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
        drawTextInWorld(
            text = "WORLD CENTER",
            worldX = worldWidth / 2 - 70f,
            worldY = worldHeight / 2,
            color = Color.CYAN,
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
    }
}
