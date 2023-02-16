package com.example.russianfootballchempionship

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.russianfootballchempionship.Entities.DbContext
import com.example.russianfootballchempionship.Entities.Game
import com.example.russianfootballchempionship.Entities.Team
import com.example.russianfootballchempionship.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private lateinit var dbContext: DbContext

    private lateinit var homeTeamSpinner: Spinner
    private lateinit var guestTeamSpinner: Spinner

    private lateinit var homeTeamGoalsField: EditText
    private lateinit var guestTeamGoalsField: EditText

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        /* Get database context */
        dbContext = DbContext.getContext(this)

        /* Find needed view */
        homeTeamSpinner = findViewById(R.id.homeTeamSpinner)
        guestTeamSpinner = findViewById(R.id.guestTeamSpinner)
        homeTeamGoalsField = findViewById(R.id.homeTeamGoalsField)
        guestTeamGoalsField = findViewById(R.id.guestTeamGoalsField)

        /* Fill the spinner */
        lifecycleScope.launch(Dispatchers.IO) {
            val teams = dbContext.teamDao().getAll()
            runOnUiThread {
                homeTeamSpinner.adapter = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, teams)
                guestTeamSpinner.adapter = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, teams)
            }
        }

        /* Loaded exist game info if exist */
        val bundle = intent.extras
        var game: Game? = null
        var isEdit = false
        if (bundle != null) {
            game = bundle.getSerializable("game") as Game
            isEdit = true
            if (game != null) fillGameData(game)
        }

        /* Exit button click */
        val exitBtn: Button = findViewById(R.id.cancelBtn)
        exitBtn.setOnClickListener {
            finish()
        }

        /* Save button click */
        val saveBtn: Button = findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener{

            val homeTeam = homeTeamSpinner.selectedItem.toString()
            val guestTeam = guestTeamSpinner.selectedItem.toString()

            if (homeTeam == guestTeam) {
                val toast = Toast.makeText(applicationContext,
                    "Home team equal guest team!", Toast.LENGTH_LONG)
                toast.show()
            } else {

                if (!isEdit) {
                    game = Game(homeTeam, guestTeam,
                    homeTeamGoalsField.text.toString().toInt(),
                    guestTeamGoalsField.text.toString().toInt())
                } else {
                    game!!.HomeTeam = homeTeam
                    game!!.GuestTeam = guestTeam
                    game!!.HomeGoals = homeTeamGoalsField.text.toString().toInt()
                    game!!.GuestGoals = guestTeamGoalsField.text.toString().toInt()
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    val find = dbContext.gameDao().getAll().find { x -> x == game }
                    if (find == null) {
                        if (isEdit) {
                            game!!.id?.let { it1 -> dbContext.gameDao().update(it1, game!!.HomeTeam, game!!.GuestTeam, game!!.HomeGoals, game!!.GuestGoals) }
                        } else {
                            dbContext.gameDao().insert(game!!)
                        }
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

    private fun fillGameData(game: Game) {

        lifecycleScope.launch(Dispatchers.IO) {
            val teams = dbContext.teamDao().getAll()
            runOnUiThread {
                val selectedTeamHome = teams.find { x -> x.Name == game.HomeTeam }
                val selectedTeamGuest = teams.find { x -> x.Name == game.GuestTeam }

                homeTeamSpinner.setSelection(teams.indexOf(selectedTeamHome))
                guestTeamSpinner.setSelection(teams.indexOf(selectedTeamGuest))

                homeTeamGoalsField.setText(game.HomeGoals.toString())
                guestTeamGoalsField.setText(game.GuestGoals.toString())
            }
        }
    }
}