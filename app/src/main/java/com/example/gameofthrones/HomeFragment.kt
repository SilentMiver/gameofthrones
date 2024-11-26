package com.example.gameofthrones

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gameofthrones.databinding.FragmentHomeBinding
import com.example.gameofthrones.db.AppDatabase
import com.example.gameofthrones.db.CharacterRepository
import com.example.gameofthrones.db.model.CharacterEntity
import com.example.gameofthrones.network.CharactersAdapter
import com.example.gameofthrones.network.model.Character
import com.example.gameofthrones.network.repository.CharacterNetworkRepository
import com.example.gameofthrones.utils.FileManager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: CharactersAdapter
    private var currentPage = 1
    private var isLoading = false
    private val characters = mutableListOf<Character>()
    private lateinit var repository: CharacterNetworkRepository
    private lateinit var characterRepository: CharacterRepository
    private var refreshCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        characterRepository = CharacterRepository(db.characterDao())

        repository = CharacterNetworkRepository()
        adapter = CharactersAdapter()
        binding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerView?.adapter = adapter

        lifecycleScope.launchWhenStarted {
            characterRepository.getAllCharacters().collect { characterEntities ->
                if (characterEntities.isNotEmpty()) {
                    characters.clear()
                    characters.addAll(characterEntities.map {
                        Character(
                            it.name,
                            it.culture,
                            it.born,
                            it.titles,
                            it.aliases,
                            it.playedBy
                        )
                    })
                    adapter.setCharacters(characters)
                }
            }
        }

        checkDataInDb()

        binding?.buttonSettings?.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == characters.size - 1) {
                    currentPage++
                    fetchCharacters()
                }
            }
        })

        binding?.buttonRefresh?.setOnClickListener {
            if (!isLoading) {
                refreshCount++
                isLoading = true
                fetchCharacters()
            }
        }
    }

    private fun checkDataInDb() {
        lifecycleScope.launch {
            val savedCharacters = characterRepository.getAllCharacters()
            if (savedCharacters.toList().isEmpty()) {
                fetchCharacters()
            }
        }
    }

    private fun fetchCharacters() {
        isLoading = true
        lifecycleScope.launch {
            characterRepository.deleteAllCharacters() // Очистить базу данных
            repository.fetchCharacters(
                currentPage,
                50,
                object : CharacterNetworkRepository.FetchCharactersCallback {
                    override fun onSuccess(newCharacters: List<Character>) {
                        val filteredCharacters =
                            newCharacters.filter { it.name != null && it.name!!.isNotBlank() }
                        filteredCharacters.map { it.name += refreshCount }  // Обновить имена с добавлением refreshCount
                        characters.clear()  // Очистить старый список
                        characters.addAll(filteredCharacters)  // Добавить новые данные
                        adapter.setCharacters(characters)  // Обновить адаптер
                        isLoading = false

                        val characterNames = characters.mapNotNull { it.name }
                        FileManager.saveCharactersToFile(requireContext(), characterNames)

                        saveCharactersToDatabase(filteredCharacters)  // Сохранить новые данные в базе данных
                    }

                    override fun onError(errorMessage: String) {
                        isLoading = false
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun saveCharactersToDatabase(apiCharacters: List<Character>) {
        lifecycleScope.launch {
            val characterEntities = apiCharacters.map {
                CharacterEntity(
                    id = null,
                    name = it.name ?: "Unknown ",
                    culture = it.culture ?: "Unknown ",
                    born = it.born ?: "Unknown ",
                    titles = it.titles ?: listOf("Unknown "),
                    aliases = it.aliases ?: listOf("Unknown "),
                    playedBy = it.playedBy ?: listOf("Unknown ")
                )
            }

            withContext(Dispatchers.IO) {
                characterRepository.insertAll(characterEntities)

                val savedCharacters = characterRepository.getAllCharacters()

                savedCharacters.collect { characters ->
                    characters.forEach {
                        Log.d("SQLite", "Saved character: ${it.name}")
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
