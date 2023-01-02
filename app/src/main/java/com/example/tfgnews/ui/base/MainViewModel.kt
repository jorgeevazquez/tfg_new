package com.example.tfgnews.ui.base


import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgnews.data.NewsDataClass
import com.example.tfgnews.R
import com.example.tfgnews.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val mAuthId = mAuth.uid.toString()
    private val userId = mAuth.currentUser?.email.toString()
    private lateinit var uuid: UUID
    private var text1 = NewsDataClass(String(), String())

    private var _listaNewsMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> =
        MutableLiveData()
    val listaNewsMutableLivedata: MutableLiveData<MutableList<NewsDataClass>> get() = _listaNewsMutableLivedata


    fun saveFireStorage(
        uricode: Uri?,
        binding: ActivityMainBinding,
        list: MutableList<NewsDataClass>,
        context: Context
    ) {
        uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val storageReference = FirebaseStorage.getInstance().getReference("$userId/$imageName")

        if (uricode != null) {
            storageReference.putFile(uricode)
                .addOnProgressListener {
                    val progress = (100* it.bytesTransferred/it.totalByteCount).toDouble()
                   /* binding.PbImage.progress = progress.toInt()
                    binding.PbImage.isIndeterminate = true*/
                    binding.tvProgressBar.text = "Cargando imagen... $progress%"
                    binding.btnAdd.isEnabled = false
                    binding.flProgressBarDashboard.visibility = View.VISIBLE

                }
                .addOnCompleteListener {
                    binding.tvProgressBar.text = "Imagen subida!"
                   /* binding.PbImage.isIndeterminate = false*/
                }
                .addOnSuccessListener {
                    isTextEmptyDowloadImage(binding,list,context,uricode)
                    binding.btnAdd.isEnabled = true
                    binding.flProgressBarDashboard.visibility = View.GONE
                    Log.i("firebaseUpload", "Imagen subida")

                }
                .addOnFailureListener {
                    Log.i("firebaseUpload", "get failed with ")
                }
        }else {
            Toast.makeText(context, "Empty Image", Toast.LENGTH_SHORT).show()
            Log.i("firebaseUpload", "uricode Null ")
        }

    }

    private fun downloadImage(list: MutableList<NewsDataClass>,
                              binding: ActivityMainBinding,
                              ) {
        //descargar URL
        val storageReferencetoDownload = FirebaseStorage.getInstance().reference
        val path = storageReferencetoDownload.child("$userId")
        //obtener Url y sube a Firebase Database
        var imageName = "${uuid}.jpg"
        path.child(imageName).downloadUrl.addOnSuccessListener { uri ->
            val text1 = NewsDataClass(String(), String())
            Log.i("firebase", "$uri")
            text1.image = uri.toString()
            text1.notice = binding.etCard.text.toString()
            list.add(text1)
            _listaNewsMutableLivedata.postValue(list)
            val date = Timestamp.now()
            db.collection(mAuthId)
                .document()
                .set(mapOf("notice" to text1.notice,
                            "image" to text1.image,
                            "date" to date
                            ))
            Log.i("firebaseUpload", "datos Subidos")
            binding.tvProgressBar.text = "Select Your Moment"

        }
    }


    fun isTextEmptyDowloadImage(
        binding: ActivityMainBinding,
        list: MutableList<NewsDataClass>,
        context: Context,
        uricode: Uri?

    ) {
        text1.notice = binding.etCard.text.toString()
        val text = text1.notice
        if (text.isNullOrEmpty()) {
            Toast.makeText(context, "Empty Text or Empty Image", Toast.LENGTH_SHORT).show()
        }
        else {
            downloadImage(list, binding)
            Toast.makeText(context, "TheBestMoment Upload", Toast.LENGTH_SHORT).show()
            binding.btSelectImageFromGalery.setBackgroundResource(R.color.background_button)
            //binding.btUploadImage.setBackgroundResource(R.color.background_button)
        }
    }

    fun getAllImagesMain(list:MutableList<NewsDataClass>) {
        viewModelScope.launch {
            val allData = db.collection(mAuthId)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
            allData.get().addOnSuccessListener { document ->
                document.documents.forEach {
                    val image = it.data?.get("image").toString()
                    val notice = it.data?.get("notice").toString()
                    val classData = NewsDataClass(notice, image)
                    list.add(classData)
                    _listaNewsMutableLivedata.postValue(list)

                }
            }
                .addOnFailureListener { exception ->
                    Log.d("firebaseGet", "get failed with ", exception)

                }
        }

    }

    fun setAuthUser(){
        db.collection("users")
            .document(mAuth.uid.toString())
            .set(mapOf("userId" to mAuth.uid.toString()))
    }

}

