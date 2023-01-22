package co.nextgentrainer.kotlin.data.source

import android.content.Context
import com.google.android.gms.common.annotation.KeepName
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ktx.storage
import java.io.File

@KeepName
class CloudStorageSource(val context: Context) {
    val gsref = "gs://nextgentrainer-c380e.appspot.com"

    val gsReferenceObject = Firebase.storage.getReferenceFromUrl(gsref)

    fun getImage(gsString: String, file: File): FileDownloadTask {
        return gsReferenceObject.child(gsString).getFile(file)
    }
}
