package com.example.gameofthrones

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gameofthrones.data.Character
import com.example.gameofthrones.ui.CharacterAdapter
import com.example.gameofthrones.viewmodel.GoTViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private val viewModel: GoTViewModel by viewModels()
    private lateinit var adapter: CharacterAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var bottomNavigation: BottomNavigationView

    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.charactersRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()
        checkPermissions()

        viewModel.loadCharacters(6)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_characters -> {
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermissions() {
        if (!hasStoragePermission()) {
            requestStoragePermission()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CharacterAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun saveCharactersToFile(characters: List<Character>) {
        if (!hasStoragePermission()) {
            requestStoragePermission()
            return
        }

        lifecycleScope.launch {
            try {
                val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                if (!documentsDir.exists()) {
                    documentsDir.mkdirs()
                }

                val file = File(documentsDir, "characters_6.txt")

                withContext(Dispatchers.IO) {
                    file.bufferedWriter().use { writer ->
                        characters.forEach { character ->
                            writer.write("Name: ${character.name}\n")
                            writer.write("Culture: ${character.culture}\n")
                            writer.write("Born: ${character.born}\n")
                            writer.write("Titles: ${character.titles.joinToString(", ")}\n")
                            writer.write("Aliases: ${character.aliases.joinToString(", ")}\n")
                            writer.write("Played by: ${character.playedBy.joinToString(", ")}\n")
                            writer.write("\n")
                        }
                    }
                }
                Toast.makeText(this@MainActivity, "Characters saved to file", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.characters.collectLatest { characters ->
                adapter.updateCharacters(characters)
                saveCharactersToFile(characters)
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
                    Toast.makeText(this@MainActivity, "Error: $it", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}