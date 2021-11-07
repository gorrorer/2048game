package databaseAccessor

import java.sql.*

class DatabaseAccessor {

    private var connection: Connection
    private var statement: Statement
    private lateinit var resultSet: ResultSet


    init {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:db/gamesaves.db")
        statement = connection.createStatement()
        statement.execute("CREATE TABLE if not exists 'gamesaves' ( 'id' integer primary key autoincrement," +
                " 'save_name' text not null, 'desk' text not null, 'score' integer not null);")
    }


    fun writeSave(save: GameSave) {
        statement.execute("INSERT INTO 'gamesaves' ('save_name', 'desk', 'score') " +
                "VALUES ('${save.name}', '${save.desk}', ${save.score});")
    }

    fun readSave(id: Int):GameSave {
        var name = ""
        var desk = ""
        var score = 0
        resultSet = statement.executeQuery("SELECT * FROM gamesaves WHERE id = $id")
        while (resultSet.next()) {
            name = resultSet.getString("save_name")
            desk = resultSet.getString("desk")
            score = resultSet.getInt("score")
        }
        return GameSave(name, desk, score)
    }

    /*
    fun deleteSave(id: Int) {
        statement.execute("DELETE FROM 'gamesaves' WHERE id = $id" )
    }
    */

    fun readSaves() {
        resultSet = statement.executeQuery("SELECT * FROM gamesaves")
        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val name = resultSet.getString("save_name")
            val score = resultSet.getInt("score")
            print("ID = $id \t")
            print("save_name = $name\t")
            println("score = $score")
        }
        resultSet.close()
    }


    fun closeConnection() {
        connection.close()
        statement.close()
    }
}