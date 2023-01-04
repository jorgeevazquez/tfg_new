package com.example.tfgnews.ui.profile

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.tfgnews.databinding.ActivityProfileBinding
import com.example.tfgnews.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {


    lateinit var binding: ActivityProfileBinding
    private var uriProfile : Uri? = null
    private val getcontent =
        registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
            uriProfile  = uri
            Glide.with(binding.ivProfile)
                .load(uriProfile)
                .circleCrop()
                .into(binding.ivProfile)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val modelProfile: ProfileViewModel by viewModels()
        modelProfile.setImageProfileFromInit(binding)


        binding.ivProfile.setOnClickListener{
            selectImageProfile()
        }
        binding.btPutImageProfile.setOnClickListener {
            modelProfile.setImageProfile(uriProfile, binding)
            finish()
            Toast.makeText(this, "Profile Image update success", Toast.LENGTH_LONG).show()
        }

        binding.tvProfile.text = FirebaseAuth.getInstance().currentUser?.email
        binding.btDeleteAccount.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete?")
            builder.setMessage("Are you sure?")
            builder.setPositiveButton("Yes"){_,_ ->
                deleteAccount()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    private fun deleteAccount (){
        val user = FirebaseAuth.getInstance().currentUser
        val mAuth = FirebaseAuth.getInstance()
        val mAuthId = mAuth.uid.toString()
        val db = FirebaseFirestore.getInstance()
        val allData = db.collection(mAuthId)
        val userId = mAuth.currentUser?.email.toString()

        //Delete Firebase Database
        allData.get().addOnSuccessListener { documents ->
            documents.documents.forEach { document ->
                val idDocument = document.id
                db.collection(mAuthId).document(idDocument).delete().addOnSuccessListener {
                    Log.i("DeleteDocument", "OK")
                }
                    .addOnFailureListener { exceptionDocument ->
                        Log.i("DeleteDocument", exceptionDocument.message.toString() )
                    }

            }
        }
            .addOnFailureListener { exception ->
                Log.i("DeleteGetDocument", exception.message.toString())
            }

        //Delete Firebase Storage
        val referenceStorage = FirebaseStorage.getInstance().reference
        referenceStorage.child(userId).listAll().addOnSuccessListener {
            it.items.forEach {
                Log.i("DeleteImage", it.toString())
                it.delete().addOnSuccessListener {
                    Log.i("DeleteImage", it.toString())
                }
                    .addOnFailureListener {
                        Log.i("DeleteImage", it.toString())
                    }
            }
            Log.i("DeleteImage", "OK")

        }
            .addOnFailureListener {
                Log.i("DeleteImage", it.message.toString())
            }
        // Delete Users autentication
        user?.delete()
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "User Delete success", Toast.LENGTH_LONG).show()
                    showAuth()
                }
            }

            ?.addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
            }
    }

    private fun showAuth(){
        val authIntent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }

    private fun selectImageProfile(){
       getcontent.launch("image/*")
    }
}