package com.example.russianfootballchempionship

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.russianfootballchempionship.Entities.Game
import kotlinx.coroutines.CoroutineScope

interface OnGameClickListener {
    fun onGameItemClicked(position: Int)
}

class GameAdapter(private val gameList: List<Game>,
                  private val onGameClickListener: OnGameClickListener
) :
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_item,
            parent, false)
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val currentItem = gameList[position]
        holder.homeTeam.text = currentItem.HomeTeam
        holder.guestTeam.text = currentItem.GuestTeam
        holder.homeGoals.text = currentItem.HomeGoals.toString()
        holder.guestGoals.text = currentItem.GuestGoals.toString()

        holder.itemView.setOnClickListener {
            onGameClickListener.onGameItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val homeTeam : TextView = itemView.findViewById(R.id.teamHome)
        val guestTeam: TextView = itemView.findViewById(R.id.teamGuest)
        val homeGoals: TextView = itemView.findViewById(R.id.goalsHome)
        val guestGoals: TextView = itemView.findViewById(R.id.goalsGuest)

    }
}