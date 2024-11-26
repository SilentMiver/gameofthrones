package com.example.gameofthrones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gameofthrones.databinding.FragmentHomeBinding
import com.example.gameofthrones.network.model.Character
import com.example.gameofthrones.network.CharactersAdapter
import com.example.gameofthrones.network.repository.CharacterRepository
import com.example.gameofthrones.utils.FileManager
import com.example.gameofthrones.R

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: CharactersAdapter
    private var currentPage = 1
    private var isLoading = false
    private val characters = mutableListOf<Character>()
    private lateinit var repository: CharacterRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = CharacterRepository()
        adapter = CharactersAdapter()
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = adapter

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                if (!isLoading && layoutManager != null &&
                    layoutManager.findLastCompletelyVisibleItemPosition() == characters.size - 1) {
                    currentPage++
                    fetchCharacters()
                }
            }
        })
        fetchCharacters()

        binding?.buttonSettings?.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    private fun fetchCharacters() {
        isLoading = true
        repository.fetchCharacters(currentPage, 50, object : CharacterRepository.FetchCharactersCallback {
            override fun onSuccess(newCharacters: List<Character>) {
                val filteredCharacters = newCharacters
                    .filter { it.name?.isNotBlank() == true }

                characters.addAll(filteredCharacters)
                adapter.setCharacters(characters)
                isLoading = false

                val characterNames = characters.map { it.name }
                FileManager.saveCharactersToFile(context!!, characterNames)
            }

            override fun onError(errorMessage: String) {
                isLoading = false
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}