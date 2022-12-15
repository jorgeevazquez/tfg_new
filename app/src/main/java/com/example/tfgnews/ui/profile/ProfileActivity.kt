package com.example.tfgnews.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.tfgnews.MainActivity
import com.example.tfgnews.databinding.ActivityProfileBinding
import com.example.tfgnews.ui.auth.AuthActivity
import com.example.tfgnews.ui.base.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {


    lateinit var binding: ActivityProfileBinding
    var uriProfile : Uri? = null
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
        val modelProfile: ProfileViewModel by viewModels()

        modelProfile.setImageProfileFromInit(binding)


        binding.ivProfile.setOnClickListener{
            selectImageProfile()
        }
        binding.btPutImageProfile.setOnClickListener {
            modelProfile.setImageProfile(uriProfile)
            showMain()
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

    fun deleteAccount (){
        val user = FirebaseAuth.getInstance().currentUser
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
        val AuthIntent = Intent(this, AuthActivity::class.java)
        startActivity(AuthIntent)
    }

    private fun showMain(){
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)

    }

    private fun selectImageProfile(){
       getcontent.launch("image/*")
    }

}