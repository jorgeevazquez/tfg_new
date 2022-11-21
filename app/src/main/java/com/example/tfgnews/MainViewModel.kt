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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI
import java.util.*

class MainViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser?.email.toString()
    val uuid = UUID.randomUUID()
    val imageName = "${uuid}.jpg"



    //private val list = mutableListOf<NewsDataClass>()
    private var _listaConsultaMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> =
        MutableLiveData()
    val listaConsultaMutableLivedata: LiveData<MutableList<NewsDataClass>> get() = _listaConsultaMutableLivedata
    private var _listaNewsMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> =
        MutableLiveData()
    val listaNewsMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> get() = _listaNewsMutableLivedata


    fun isTextEmpty(
        binding: ActivityMainBinding,
        list: MutableList<NewsDataClass>,
        context: Context
    ) {
        //subida firestore
        val text1 = NewsDataClass(String(), String())
        text1.notice = binding.etCard.text.toString()
        val text = text1.notice
        if (text.isEmpty()) {
            Toast.makeText(context, "Empty Text", Toast.LENGTH_SHORT).show()
        } else list.add(text1)
        _listaNewsMutableLivedata.postValue(list)
    }


    fun saveFireStorage(list: MutableList<NewsDataClass>, uricode: Uri?, binding: ActivityMainBinding) {
        val storageReference = FirebaseStorage.getInstance().getReference("$userId/$imageName")
        if (uricode != null) {
            storageReference.putFile(uricode)
        }

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
            val datos = hashMapOf("Data" to list) // subida de info
            db.collection("Datos")
                .document(userId)
                .set(datos)

        }

            }

    fun saveFireBase(list: MutableList<NewsDataClass>) {
    val datos = hashMapOf("Data" to list) // subida de info
    db.collection("Datos")
        .document(userId)
        .set(datos)
        }
    fun downloadUrl(){
        val storageReferencetoDownload = FirebaseStorage.getInstance().reference
        val path = storageReferencetoDownload.child("jorgevazquez")
        //obtenerUrl
        path.child(imageName).downloadUrl.addOnSuccessListener { uri ->
            Log.i("firebase", "$uri")

        }
    }

        }


  /*  fun uploadImage(uricode: Uri?) {
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val storageReference = FirebaseStorage.getInstance().getReference("$userId/$imageName")
        if (uricode != null) {
            storageReference.putFile(uricode)
        }
    }
*/





    /*fun getAllData() {
        val listaprov = mutableListOf<NewsDataClass>()
        db.collection("Datos")
            .document(userId)
            .get()
            .addOnSuccessListener {
                var datos = it.get("Datos")
                _listaNewsMutableLivedata.value = (datos as MutableList<NewsDataClass>)
               // _listaNewsMutableLivedata.postValue(it.get("Datos") as MutableList<NewsDataClass>)
            }

    }
}*/

  /*  private val productRef: CollectionReference = db.collection("Datos")

    fun getResponseFromFirestoreUsingLiveData(): MutableLiveData<MutableList<NewsDataClass>> {
        val mutableLiveData = MutableLiveData<MutableList<NewsDataClass>>()
        productRef.get().addOnCompleteListener { task ->
            var response = listOf<NewsDataClass>()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response = result.documents.mapNotNull() { snapShot ->
                        snapShot.toObject(NewsDataClass::class.java)
                    }
                }
            } else {
                task.exception
            }
            mutableLiveData.value = response as MutableList<NewsDataClass>
        }
        return mutableLiveData
    }
}
*/

