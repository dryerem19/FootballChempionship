package com.example.russianfootballchempionship.Entities

import androidx.room.*

@Entity(tableName = "Game")

data class Game(
                val HomeTeam: String,
                val GuestTeam: String,
                val HomeGoals: Int,
                val GuestGoals: Int
)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null


}

@Dao
interface GameDao {

    @Query("SELECT * FROM Game")
    fun getAll() : List<Game>

    @Insert
    fun insert(game: Game)

}