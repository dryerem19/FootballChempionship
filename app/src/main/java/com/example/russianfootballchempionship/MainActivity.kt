package com.example.russianfootballchempionship

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.russianfootballchempionship.Entities.DbContext
import com.example.russianfootballchempionship.Entities.Game
import com.example.russianfootballchempionship.Entities.Team
import com.example.russianfootballchempionship.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnGameClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbContext: DbContext
    private lateinit var gameList: List<Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbContext = DbContext.getContext(this)

        binding.gameRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.gameRecyclerView.setHasFixedSize(true)

        updateGameList()
        binding.addGameBtn.setOnClickListener {
            val i = Intent(this, EditActivity::class.java)
            i.putExtra("is_edit", false)
            startActivity(i)
        }
    }

    override fun onGameItemClicked(position: Int) {
        val i = Intent(this, EditActivity::class.java)
        i.putExtra("is_edit", true)
        i.putExtra("team_home", gameList[position].HomeTeam)
        i.putExtra("guest_team", gameList[position].GuestTeam)
        i.putExtra("home_goals", gameList[position].HomeGoals)
        i.putExtra("guest_goals", gameList[position].GuestGoals)
        startActivity(i)
    }

    override fun onRestart() {
        super.onRestart()
        updateGameList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addTeam -> {
                val inputTeam = EditText(this)
                inputTeam.height = 30
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setView(inputTeam)

                dialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(R.string.add) { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            val name = inputTeam.text.toString().trim()
                            val find = dbContext.teamDao().getAll().find { x -> x.Name == name }
                            if (find == null) {
                                dbContext.teamDao().insert(Team(name))
                            } else {
                                runOnUiThread {
                                    val toast = Toast.makeText(applicationContext,
                                        "Team already exist!", Toast.LENGTH_LONG)
                                    toast.show()
                                }
                            }
                        }
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }

                val dialog = dialogBuilder.create()
                dialog.show()
            }
            R.id.deleteAll -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    dbContext.clearAllTables()
                    runOnUiThread {
                        updateGameList()
                    }
                }
            }
            R.id.exit -> {
                moveTaskToBack(true)
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private fun updateGameList() {
        val ref = this
        lifecycleScope.launch(Dispatchers.IO) {
            gameList = dbContext.gameDao().getAll()
            runOnUiThread {
                binding.gameRecyclerView.adapter = GameAdapter(gameList, ref)
            }
        }
    }
}