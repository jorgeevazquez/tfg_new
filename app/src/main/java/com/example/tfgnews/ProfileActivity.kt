package com.example.tfgnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tfgnews.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {


    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


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
        val homeIntent = Intent(this, AuthActivity::class.java)
        startActivity(homeIntent)
    }
}