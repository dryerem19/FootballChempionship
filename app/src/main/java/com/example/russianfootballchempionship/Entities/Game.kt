package com.example.russianfootballchempionship.Entities

import androidx.room.*

@Entity(tableName = "Game")
data class Game(
    var HomeTeam: String,
    var GuestTeam: String,
    var HomeGoals: Int,
    var GuestGoals: Int
) : java.io.Serializable
{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null


}

@Dao
interface GameDao {

    @Query("SELECT * FROM Game")
    fun getAll() : List<Game>

    @Query("UPDATE Game SET HomeTeam = :homeT, GuestTeam = :guestT, HomeGoals = :homeG, GuestGoals = :guestG WHERE id LIKE :id")
    fun update(id: Int, homeT: String, guestT: String, homeG: Int, guestG: Int) : Int

    @Insert
    fun insert(game: Game)

}