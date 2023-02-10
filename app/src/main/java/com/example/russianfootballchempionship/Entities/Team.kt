package com.example.russianfootballchempionship.Entities

import android.text.Editable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "Team")
data class Team(
    val Name: String)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    override fun toString(): String {
        return Name
    }
}

@Dao
interface TeamDao {

    @Query("SELECT * FROM Team")
    fun getAll() : List<Team>

    @Query("SELECT * FROM Team WHERE Name =:name")
    fun find(name: String) : Team?

    @Insert
    fun insert(team: Team)

}