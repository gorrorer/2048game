package gameField

import databaseAccessor.GameSave


class GameField {

    private var desk = createMatrix(4, 4, 0)
    private var stateStack = ArrayDeque<Pair<Matrix<Int>, Int>>()
    private var currentPoints = 0
    private val tilePool = arrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 4)

    init {
        generateTile()
        generateTile()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Points: $currentPoints\n")
        for (x in 0 until desk.height) {
            for (y in 0 until desk.width) {
                sb.append(desk[x, y], "    ")
            }
            sb.append("\n\n")
        }
        return "$sb"
    }

    fun generateTile() {
        val freeTiles = desk.getFreeTiles()
        if (freeTiles.isNotEmpty()) {
            desk[desk.getFreeTiles().random()] = tilePool.random()
        }
    }

    fun checkForFail(): Boolean {
        return (desk == this.clone().swipeDown().desk) && (desk == this.clone().swipeUp().desk) &&
                (desk == this.clone().swipeLeft().desk) && (desk == this.clone().swipeRight().desk)
    }

    fun createSave(name: String): GameSave {
        return GameSave(name, desk.toString(), currentPoints)
    }

    fun loadSave(save: GameSave) {
        val dataList = save.desk.split(",").map { it.toInt() }
        var count = 0
        for (y in 0 until desk.height) {
            for (x in 0 until desk.width) {
                desk[y, x] = dataList[x+count]
            }
            count += 4
        }
        currentPoints = save.score
    }

    fun loadState() {
        if (stateStack.isNotEmpty()) {
            val newState = stateStack.removeFirst()
            desk = newState.first.clone()
            currentPoints = newState.second
        } else {
            println("No more backups")
        }
    }

    fun swipeRight(): GameField {
        saveState()
        moveRight()
        for (y in 0 until desk.height) {
            for (x in desk.width-2 downTo  0) {
                if (desk[y, x+1] == desk[y, x]) {
                    desk[y, x+1] *= 2
                    currentPoints += desk[y, x+1]
                    desk[y, x] = 0
                }
            }
        }
        moveRight()
        return this
    }

    fun swipeLeft(): GameField {
        saveState()
        moveLeft()
        for (y in 0 until desk.height) {
            for (x in 1 until desk.width) {
                if (desk[y, x-1] == desk[y, x]) {
                    desk[y, x-1] *= 2
                    currentPoints += desk[y, x-1]
                    desk[y, x] = 0
                }
            }
        }
        moveLeft()
        return this
    }

    fun swipeUp(): GameField {
        saveState()
        moveUp()
        for (x in 0 until desk.width) {
            for (y in 1 until desk.height) {
                if (desk[y-1, x] == desk[y, x]) {
                    desk[y-1, x] *= 2
                    currentPoints += desk[y-1, x]
                    desk[y, x] = 0
                }
            }
        }
        moveUp()
        return this
    }

    fun swipeDown(): GameField {
        saveState()
        moveDown()
        for (x in 0 until desk.width) {
            for (y in desk.height - 2 downTo 0) {
                if (desk[y+1, x] == desk[y, x]) {
                    desk[y+1, x] *= 2
                    currentPoints += desk[y+1, x]
                    desk[y, x] = 0
                }
            }
        }
        moveDown()
        return this
    }

    private fun saveState() {
        if (stateStack.size == 5) {
            stateStack.removeLast()
            stateStack.addFirst(Pair(desk.clone(), currentPoints))
        } else {
            stateStack.addFirst(Pair(desk.clone(), currentPoints))
        }
    }

    private fun clone(): GameField {
        val newInstance = GameField()
        newInstance.desk = desk.clone()
        return newInstance
    }

    private fun Matrix<Int>.clone(): Matrix<Int> {
        val newInstance = createMatrix(this.height, this.width, 0)
        for (y in 0 until this.height){
            for (x in 0 until this.width){
                newInstance[y, x] = this[y, x]
            }
        }
        return newInstance
    }

    private fun moveRight() {
        for (y in 0 until desk.height) {
            for (x in desk.width-2 downTo  0) {
                var count = 0
                for (currentX in x + 1 until desk.width) {
                    if (desk[y, currentX] == 0) {
                        count++
                    }
                }
                val tmp = desk[y, x]
                desk[y, x] = 0
                desk[y, x+count] = tmp
            }
        }
    }

    private fun moveLeft() {
        for (y in 0 until desk.height) {
            for (x in 1 until desk.width) {
                var count = 0
                for (currentX in x - 1 downTo 0) {
                    if (desk[y, currentX] == 0) {
                        count++
                    }
                }
                val tmp = desk[y, x]
                desk[y, x] = 0
                desk[y, x-count] = tmp
            }
        }
    }

    private fun moveUp() {
        for (x in 0 until desk.width) {
            for (y in 1 until desk.height) {
                var count = 0
                for (currentY in y - 1 downTo 0) {
                    if (desk[currentY, x] == 0) {
                        count++
                    }
                }
                val tmp = desk[y, x]
                desk[y, x] = 0
                desk[y-count, x] = tmp
            }
        }
    }

    private fun moveDown() {
        for (x in 0 until desk.width) {
            for (y in desk.height - 2 downTo 0) {
                var count = 0
                for (currentY in y + 1 until desk.height) {
                    if (desk[currentY, x] == 0) {
                        count++
                    }
                }
                val tmp = desk[y, x]
                desk[y, x] = 0
                desk[y+count, x] = tmp
            }
        }
    }

}