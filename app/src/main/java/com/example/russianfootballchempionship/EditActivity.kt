package com.example.russianfootballchempionship

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.russianfootballchempionship.Entities.DbContext
import com.example.russianfootballchempionship.Entities.Game
import com.example.russianfootballchempionship.Entities.Team
import com.example.russianfootballchempionship.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private lateinit var dbContext: DbContext
    private lateinit var teams: List<Team>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        dbContext = DbContext.getContext(this)

        val homeTeamSpinner: Spinner = findViewById(R.id.homeTeamSpinner)
        val guestTeamSpinner: Spinner = findViewById(R.id.guestTeamSpinner)

        val homeTeamGoalsField: EditText = findViewById(R.id.homeTeamGoalsField)
        val guestTeamGoalsField: EditText = findViewById(R.id.guestTeamGoalsField)

        lifecycleScope.launch(Dispatchers.IO) {
            teams = dbContext.teamDao().getAll()
            runOnUiThread {
                homeTeamSpinner.adapter = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, teams)
                guestTeamSpinner.adapter = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, teams)
            }
        }

        val exitBtn: Button = findViewById(R.id.cancelBtn)
        exitBtn.setOnClickListener {
            finish()
        }

        val saveBtn: Button = findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener{
            val homeTeam = homeTeamSpinner.selectedItem.toString()
            val guestTeam = guestTeamSpinner.selectedItem.toString()

            if (homeTeam == guestTeam) {
                val toast = Toast.makeText(applicationContext,
                    "Home team equal guest team!", Toast.LENGTH_LONG)
                toast.show()
            } else {
                val game = Game(homeTeam, guestTeam,
                    homeTeamGoalsField.text.toString().toInt(),
                    guestTeamGoalsField.text.toString().toInt())

                lifecycleScope.launch(Dispatchers.IO) {
                    val find = dbContext.gameDao().getAll().find { x -> x == game }
                    if (find == null) {
                        dbContext.gameDao().insert(game)
                        runOnUiThread {
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            val toast = Toast.makeText(applicationContext,
                                "Game already exist!", Toast.LENGTH_LONG)
                            toast.show()
                        }
                    }
                }
            }
        }
    }
}