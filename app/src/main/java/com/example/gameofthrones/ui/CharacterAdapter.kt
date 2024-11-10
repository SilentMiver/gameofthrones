

package com.example.gameofthrones.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gameofthrones.R
import com.example.gameofthrones.data.Character

class CharacterAdapter : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {
    private var characters: List<Character> = emptyList()

    class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.characterName)
        val cultureView: TextView = view.findViewById(R.id.characterCulture)
        val bornView: TextView = view.findViewById(R.id.characterBorn)
        val titlesView: TextView = view.findViewById(R.id.characterTitles)
        val aliasesView: TextView = view.findViewById(R.id.characterAliases)
        val playedByView: TextView = view.findViewById(R.id.characterPlayedBy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_item, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position]
        holder.apply {
            nameView.text = "Name: ${character.name.ifEmpty { "Unknown" }}"
            cultureView.text = "Culture: ${character.culture.ifEmpty { "Unknown" }}"
            bornView.text = "Born: ${character.born.ifEmpty { "Unknown" }}"
            titlesView.text = "Titles: ${character.titles.joinToString(", ").ifEmpty { "None" }}"
            aliasesView.text = "Aliases: ${character.aliases.joinToString(", ").ifEmpty { "None" }}"
            playedByView.text = "Played by: ${character.playedBy.joinToString(", ").ifEmpty { "Unknown" }}"
        }
    }

    override fun getItemCount() = characters.size

    fun updateCharacters(newCharacters: List<Character>) {
        characters = newCharacters
        notifyDataSetChanged()
    }
}
