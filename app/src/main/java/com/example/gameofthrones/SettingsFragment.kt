package com.example.gameofthrones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.gameofthrones.databinding.FragmentSettingsBinding
import com.example.gameofthrones.utils.DataStoreManager
import com.example.gameofthrones.utils.FileManager
import com.example.gameofthrones.utils.PreferencesManager
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        dataStoreManager = DataStoreManager(requireContext())

        lifecycleScope.launch {
            binding.editTextEmail.setText(preferencesManager.getEmail())
            binding.switchNotifications.isChecked = preferencesManager.getNotificationsEnabled()
            binding.switchTheme.isChecked = dataStoreManager.getDarkTheme()
            binding.seekBarFontSize.progress = dataStoreManager.getFontSize()
        }

        updateFileStatus()

        binding.buttonSave.setOnClickListener { saveSettings() }
        binding.buttonBack.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        binding.buttonDeleteFile.setOnClickListener {
            deleteFileIfExists()
        }

        binding.buttonRestoreFile.setOnClickListener {
            restoreFileIfExists()
        }
    }

    private fun updateFileStatus() {
        val fileExists = FileManager.isFileExists(requireContext(), "characters.txt")
        binding.textFileStatus.text = if (fileExists) {
            "Файл 'characters.txt' уже присутствует в системе."
        } else {
            "Файл 'characters.txt' еще не создан."
        }
        binding.buttonDeleteFile.isEnabled = fileExists
    }

    private fun deleteFileIfExists() {
        val fileName = "characters.txt"


        val backupSuccessful = FileManager.backupFile(requireContext(), fileName)
        if (!backupSuccessful) {
            Toast.makeText(requireContext(), "Не удалось создать резервную копию файла", Toast.LENGTH_SHORT).show()
            return
        }


        val deleted = FileManager.deleteFile(requireContext(), fileName)
        if (deleted) {
            Toast.makeText(requireContext(), "Файл удален", Toast.LENGTH_SHORT).show()
            updateFileStatus()
        } else {
            Toast.makeText(requireContext(), "Не удалось удалить файл", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreFileIfExists() {
        val restored = FileManager.restoreFile(requireContext(), "characters.txt")
        if (restored) {
            Toast.makeText(requireContext(), "Файл успешно восстановлен", Toast.LENGTH_SHORT).show()
            updateFileStatus()
        } else {
            Toast.makeText(requireContext(), "Не удалось восстановить файл", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSettings() {
        preferencesManager.setEmail(binding.editTextEmail.text.toString())
        preferencesManager.setNotificationsEnabled(binding.switchNotifications.isChecked)
        dataStoreManager.setDarkTheme(binding.switchTheme.isChecked)
        dataStoreManager.setFontSize(binding.seekBarFontSize.progress)
        Toast.makeText(requireContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show()
    }
}



