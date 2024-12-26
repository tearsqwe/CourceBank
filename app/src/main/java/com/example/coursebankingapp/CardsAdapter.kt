package com.example.coursebankingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardsAdapter(private val cards: List<Card>) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_in_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.tvCardNumber.text = "Номер карты: ${card.cardNumber}"
        holder.tvUserName.text = "Владелец: ${card.userName}"
    }

    override fun getItemCount(): Int {
        return cards.size

       }
}