package com.nextgentrainer.kotlin.data.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import com.nextgentrainer.R
import com.nextgentrainer.kotlin.data.source.CloudStorageSource
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class GifRepository(private val storageSource: CloudStorageSource) {
    private val client = OkHttpClient()

    val imagesMap = HashMap<String, String>()

    fun sendPostRequest(repId: String, movementId: String) {
        val mediaType = "application/json; charset=utf-8"
        val userId = Firebase.auth.currentUser!!.uid
        val passwd = storageSource.context.getString(R.string.GIF_API_PASSWORD)

        val jsonobject = JsonObject()
        jsonobject.addProperty("userId", userId)
        jsonobject.addProperty("repetitionId", repId)
        jsonobject.addProperty("movementId", movementId)
        jsonobject.addProperty("password", passwd)

        val body = RequestBody.create(MediaType.parse(mediaType), jsonobject.toString())

        val request = Request.Builder()
            .url("https://nextgentrainer-api-bwwgnth3ja-lm.a.run.app/build_gif")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                Log.d("GIF Repository", response.body().string())
            }
        })
    }

    fun downloadImage(repId: String, movementId: String): String {
        if (repId.isEmpty()) {
            return ""
        }
        val localFile = File.createTempFile(repId, ".webp")
        storageSource.getImage("$movementId.webp", localFile).addOnSuccessListener {
            imagesMap[repId] = localFile.absolutePath
        }.addOnFailureListener {
            // Toast.makeText(storageSource.context, "Failed to download GIFs", Toast.LENGTH_LONG).show()
            Log.d("Gifs", "failed to download gif for $repId")
        }
        return localFile.absolutePath
    }
}
