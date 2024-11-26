package com.example.gameofthrones.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileManager {
    private const val TAG = "FileManager"
    private const val BACKUP_FILE_NAME = "backup_characters.txt"

    fun isFileExists(context: Context, fileName: String): Boolean {
        val uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(fileName)

        return try {
            context.contentResolver.query(uri, null, selection, selectionArgs, null)
                ?.use { it.count > 0 } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking file: ", e)
            false
        }
    }

    fun saveCharactersToFile(context: Context, characters: List<String?>) {
        val charactersText = characters.joinToString("\n")
        val fileName = "characters_${System.currentTimeMillis()}.txt"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(charactersText.toByteArray())
                    Log.d(TAG, "File saved in Documents with name: $fileName")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error saving file in Documents: ", e)
            }
        }
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        val uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(fileName)

        return try {
            val deletedRows = context.contentResolver.delete(uri, selection, selectionArgs)
            deletedRows > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting file: ", e)
            false
        }
    }

    fun backupFile(context: Context, originalFileName: String): Boolean {
        val backupFile = File(context.filesDir, BACKUP_FILE_NAME)
        val uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(originalFileName)

        return try {
            context.contentResolver.query(uri, null, selection, selectionArgs, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val fileUri = Uri.withAppendedPath(
                            uri,
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                        )
                        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                            FileOutputStream(backupFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                                Log.d(TAG, "File backed up to internal storage.")
                                true
                            }
                        } ?: false
                    } else false
                } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error backing up file: ", e)
            false
        }
    }

    fun restoreFile(context: Context, originalFileName: String): Boolean {
        val backupFile = File(context.filesDir, BACKUP_FILE_NAME)

        if (!backupFile.exists()) {
            Log.d(TAG, "No backup file found for restoration.")
            return false
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, originalFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        return uri?.let {
            try {
                FileInputStream(backupFile).use { inputStream ->
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                        Log.d(TAG, "File restored to external storage.")
                        true
                    }
                } ?: false
            } catch (e: IOException) {
                Log.e(TAG, "Error restoring file: ", e)
                false
            }
        } ?: false
    }
}