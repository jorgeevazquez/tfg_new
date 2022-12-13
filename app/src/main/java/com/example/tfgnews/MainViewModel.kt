package com.example.tfgnews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgnews.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class MainViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser?.email.toString()
    val uuid = UUID.randomUUID()
    val imageName = "${uuid}.jpg"
    var text1 = NewsDataClass(String(), String())



    //private val list = mutableListOf<NewsDataClass>()
    private var _listaConsultaMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> =
        MutableLiveData()
    val listaConsultaMutableLivedata: LiveData<MutableList<NewsDataClass>> get() = _listaConsultaMutableLivedata
    private var _listaNewsMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> =
        MutableLiveData()
    val listaNewsMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> get() = _listaNewsMutableLivedata

    fun isTextEmptyDowloadImage(
        binding: ActivityMainBinding, list: MutableList<NewsDataClass>, context: Context
    ) {

        text1.notice = binding.etCard.text.toString()
        val text = text1.notice
        //val textImage = text1.image
        if (text.isEmpty()) {
            Toast.makeText(context, "Empty Text", Toast.LENGTH_SHORT).show()
        } else {
            downloadImage(list, binding)
            Toast.makeText(context, "TheBestMoment Upload", Toast.LENGTH_SHORT).show()
            binding.btSelectImageFromGalery.setBackgroundResource(R.color.background_button)
            binding.btUploadImage.setBackgroundResource(R.color.background_button)
        }
    }


    fun saveFireStorage(
        uricode: Uri?,
        binding: ActivityMainBinding,
    ) {
        val storageReference = FirebaseStorage.getInstance().getReference("$userId/$imageName")
        if (uricode != null) {

            storageReference.putFile(uricode)
                .addOnProgressListener {
                    val progress = (100* it.bytesTransferred/it.totalByteCount).toDouble()
                    binding.PbImage.progress = progress.toInt()
                    binding.tvProgressBar.text = "Cargando imagen... $progress%"
                }
                .addOnCompleteListener {
                    binding.tvProgressBar.text = "Imagen subida!"
                    binding.btUploadImage.setBackgroundResource(R.drawable.ic_check)
                }
        }
    }

    fun downloadImage(list: MutableList<NewsDataClass>, binding: ActivityMainBinding) {
        //descargar URL
        val storageReferencetoDownload = FirebaseStorage.getInstance().reference
        val path = storageReferencetoDownload.child("$userId")

        //obtenerUrl
        path.child(imageName).downloadUrl.addOnSuccessListener { uri ->
            val text1 = NewsDataClass(String(), String())
            Log.i("firebase", "$uri")
            text1.image = uri.toString()
            text1.notice = binding.etCard.text.toString()
            list.add(text1)
            _listaNewsMutableLivedata.postValue(list)
            val date = Timestamp.now()
            db.collection(userId)
                .document()
                .set(mapOf("notice" to text1.notice,
                            "image" to text1.image,
                            "date" to date
                            ))
            binding.tvProgressBar.text = "No hay imagen cargada"
        }

    }

    fun getAllImagesMain(list:MutableList<NewsDataClass>) {
        val allData = db.collection(userId).orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
        allData.get().addOnSuccessListener { document ->
           document.documents.forEach{
              val image =  it.data?.get("image").toString()
              val notice =  it.data?.get("notice").toString()
               val classData = NewsDataClass(notice,image)
               list.add(classData)
               _listaNewsMutableLivedata.postValue(list)

           }

            }
            .addOnFailureListener { exception ->
                Log.d("firebaseGet", "get failed with ", exception)

            }
    }

}

