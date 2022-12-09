package com.example.tfgnews

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tfgnews.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadImage: AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    private var pickedImage: Uri? = null
    private var pickedBitmap : Bitmap? = null
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            pickedImage = data?.data

            if (pickedImage != null){
                if (Build.VERSION.SDK_INT >= 28)
                {
                    val source = ImageDecoder.createSource(this.contentResolver,pickedImage!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    val imageSelect = findViewById<ImageView>(R.id.imgCard)
                    imageSelect.setImageBitmap(pickedBitmap)
                }
                else
                {
                    pickedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedImage)
                    val imageSelect = findViewById<ImageView>(R.id.imgCard)
                    imageSelect.setImageBitmap(pickedBitmap)

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val imageSelect = mBinding.btSelectImageFromGalery
        val butShare = mBinding.btUploadImage
        //val userCommentText = findViewById<TextView>(R.id.)

        val storage = FirebaseStorage.getInstance()
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseFirestore.getInstance()

        imageSelect.setOnClickListener{
            pickImage()
        }

        butShare.setOnClickListener{
            //Storage Process
            //UUID -> Universal Unique ID
            val uuid = UUID.randomUUID()
            val imageName = "${uuid}.jpg"

            val reference = storage.reference
            val imageReference = reference.child("images").child(imageName)

            if  (pickedImage != null){
                imageReference.putFile(pickedImage!!).addOnSuccessListener {
                    val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                    uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        val currentUserEmail = auth.currentUser!!.email.toString()
                        val date = Timestamp.now()

                        //Database Process
                        val postHashMap = hashMapOf<String, Any>()
                        postHashMap["Image URL"] = downloadUrl
                        postHashMap["User Email"] = currentUserEmail
                        postHashMap["Post Date"] = date

                        database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(this,"Uploaded!",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun pickImage(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //No Permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else {
            openGallery()
        }
    }
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }

}