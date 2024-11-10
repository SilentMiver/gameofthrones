package com.example.gameofthrones

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gameofthrones.ui.CharacterAdapter
import com.example.gameofthrones.viewmodel.GoTViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: GoTViewModel by viewModels()
    private lateinit var adapter: CharacterAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.charactersRecyclerView)
        progressBar = findViewById(R.id.progressBar)

        setupRecyclerView()
        setupObservers()


        viewModel.loadCharacters(6)
    }

    private fun setupRecyclerView() {
        adapter = CharacterAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.characters.collectLatest { characters ->
                adapter.updateCharacters(characters)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    // Handle error - you might want to show a toast or snackbar here
                }
            }
        }
    }
}