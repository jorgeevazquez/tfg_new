package com.example.tfgnews

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.tfgnews.databinding.ActivityMainBinding
import com.google.android.gms.common.api.Response
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser?.email.toString()
    val uuid = UUID.randomUUID()
    val imageName = "${uuid}.jpg"
    var text1 = NewsDataClass(String(), String())
    val date = Timestamp.now()


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
        //subida firestore

        text1.notice = binding.etCard.text.toString()
        val text = text1.notice
        if (text.isEmpty()) {
            Toast.makeText(context, "Empty Text", Toast.LENGTH_SHORT).show()
        } else downloadImage(list, binding)
    }


    fun saveFireStorage(
        uricode: Uri?,
    ) {
        val storageReference = FirebaseStorage.getInstance().getReference("$userId/$imageName")
        if (uricode != null) {
            storageReference.putFile(uricode)
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
            //subida a firebase Database
            //val datos = hashMapOf("Data" to list) // subida de info
            db.collection(userId)
                .document()
                .set(mapOf("notice" to text1.notice,
                            "image" to text1.image,
                            "date" to date
                            ))

        }

    }

    fun getAllImagesMain(list:MutableList<NewsDataClass>) {
        val allData = db.collection(userId).orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
        allData.get().addOnSuccessListener { document ->
           document.documents.forEach{
              val image =  it.data?.get("image").toString()
              val notice =  it.data?.get("notice").toString()
               /*text1.notice = notice
               text1.image = image*/
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

