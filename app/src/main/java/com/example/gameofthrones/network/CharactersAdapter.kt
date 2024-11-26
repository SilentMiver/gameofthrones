package com.example.gameofthrones.network

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gameofthrones.databinding.ItemCharacterBinding
import com.example.gameofthrones.network.model.Character

class CharactersAdapter : RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {
    private var characters: List<Character> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount(): Int = characters.size

    fun setCharacters(newCharacters: List<Character>) {
        characters = newCharacters
        notifyDataSetChanged()
    }

    class CharacterViewHolder(
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            binding.characterName.text = character.name ?: ""

            val text = buildString {
                append(character.culture?.takeIf { it.isNotBlank() } ?: "Hello! ")
                append(" ")
                append(character.born?.takeIf { it.isNotBlank() } ?: " ")
                append(" ")
                append(character.titles?.takeIf { it.isNotEmpty() }?.toString() ?: " ")
                append(" ")
                append(character.aliases?.takeIf { it.isNotEmpty() }?.toString() ?: " ")
                append(" ")
                append(character.playedBy?.takeIf { it.isNotEmpty() }?.toString() ?: " ")
            }

            binding.messageText.text = text
        }
    }
}