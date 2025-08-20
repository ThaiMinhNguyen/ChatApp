package com.example.chatapp.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.Storage
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: Storage
) {

    suspend fun uploadFile(
        bucketName: String,
        filePath: String,
        fileName: String
    ) : Result<String> = withContext(Dispatchers.IO){
        return@withContext try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Throwable("File does not exist at the specified path: $filePath"))
            }
            val inputStream = file.inputStream()
            val byte = inputStream.readBytes()
            storage.from(bucketName).upload(fileName, byte){
                upsert = true
            }

            val url = storage.from(bucketName).publicUrl(fileName)

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to upload file: ${e.message}"))
        }
    }

    suspend fun uploadFileFromUri(
        bucketName: String,
        uri: Uri,
        fileName: String,
        context: Context
    ) : Result<String> = withContext(Dispatchers.IO){
        return@withContext try {
            var bytes : ByteArray = byteArrayOf()
            context.contentResolver.openInputStream(uri)?.use {
                bytes = it.readBytes()
            }
            if (bytes.isEmpty()) {
                Log.e("MyLog - StorageRepo", "Failed to read file content from URI: $uri")
                return@withContext Result.failure(Exception("Failed to read file content"))
            }

            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val contentTypeConvert = ContentType.parse(mimeType)

            storage.from(bucketName).upload(fileName, bytes){
                upsert = true
                contentType = contentTypeConvert
            }
            val url = storage.from(bucketName).publicUrl(fileName)

            Result.success(url)
        } catch (e: Exception) {
            Log.e("MyLog - StorageRepo", "Error uploading file from URI: $uri", e)
            Result.failure(Exception("Failed to upload file: ${e.message}"))
        }
    }

    suspend fun deleteFile(
        bucketName: String,
        fileName: String
    ) : Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            storage.from(bucketName).delete(fileName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete file: ${e.message}"))
        }
    }



}