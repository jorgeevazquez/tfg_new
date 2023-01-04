package com.example.tfgnews.ui.profile

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.tfgnews.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel: ViewModel() {
    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser?.email.toString()

    fun setImageProfile(uri:Uri?, binding: ActivityProfileBinding){
        val storageReference = FirebaseStorage.getInstance().getReference("$userId/ProfileImage.jpg")
        if (uri != null) {
            storageReference.putFile(uri)
                .addOnProgressListener {
                    binding.flProgressBarDashboardProfile.visibility = View.VISIBLE
                }
                .addOnCompleteListener {
                    binding.flProgressBarDashboardProfile.visibility = View.GONE
                }
                .addOnFailureListener {
                    Log.i("ProfileImage", it.toString())
                }
            }
        }

    fun setImageProfileFromInit(binding: ActivityProfileBinding) {
        val storageReferencetoDownload = FirebaseStorage.getInstance().reference
        val path = storageReferencetoDownload.child("$userId")
        path.child("ProfileImage.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(binding.ivProfile).load(it).circleCrop().into(binding.ivProfile)
            Log.i("ImageProfile", "ImageProfileOk")
        }
            .addOnFailureListener {
                Log.i("ImageProfile", "Empty")
            }
    }
}