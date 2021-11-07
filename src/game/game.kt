package game

import databaseAccessor.DatabaseAccessor
import databaseAccessor.GameSave
import gameField.GameField

class Game {

    private val keys = "Press b to undo move\nPress x to save your game\nPress l to load game"

    fun start() {
        val sb = StringBuilder()
        sb.append("1. Start new game\n")
        sb.append("2. Load game\n")
        println("$sb")
        when (readLine()) {
            "1" -> newGame()
            "2" -> loadGame()
        }
    }

    private fun newGame(){
        val gameField = GameField()
        _newGame(gameField)
    }

    private fun newGame(save: GameSave) {
        val gameField = GameField()
        gameField.loadSave(save)
        _newGame(gameField)
    }

    private fun _newGame(gameField: GameField) {
        println(gameField.toString())
        println(keys)
        var loseFlag = false
        while (!loseFlag) {
            when (readLine()) {
                "a" -> {
                    gameField.swipeLeft()
                    gameField.generateTile()
                }
                "s" -> {
                    gameField.swipeDown()
                    gameField.generateTile()
                }
                "d" -> {
                    gameField.swipeRight()
                    gameField.generateTile()
                }
                "w" -> {
                    gameField.swipeUp()
                    gameField.generateTile()
                }
                "b" -> {
                    gameField.loadState()
                }
                "x" -> {
                    print("Type name for your save: ")
                    val name = readLine().toString()
                    println()
                    val save = gameField.createSave(name)
                    val accessor = DatabaseAccessor()
                    accessor.writeSave(save)
                    accessor.closeConnection()
                }
                "l" -> {
                    loadGame()
                }
            }
            println(gameField.toString())
            println(keys)
            loseFlag = gameField.checkForFail()
        }
        if (loseFlag) println("you lose")
    }



    private fun loadGame() {
        val accessor = DatabaseAccessor()
        accessor.readSaves()
        println("\nChoose save by id")
        val id = readLine()!!.toInt()
        val save = accessor.readSave(id)
        accessor.closeConnection()
        newGame(save)
    }
}