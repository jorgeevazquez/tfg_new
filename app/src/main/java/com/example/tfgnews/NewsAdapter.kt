package com.example.tfgnews

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.tfgnews.databinding.NoticeCardBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

// La clase NewsAdapter extiende de RecyclerView.adapter
class NewsAdapter(private var news: MutableList<NewsDataClass>, private val context: Context)
    :RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mBinding: NoticeCardBinding = NoticeCardBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Creamos una vista, que define la interfaz con los items
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notice_card,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //este metodo asocia la vista con los datos, debe contener un holder y la position
        val textNew = news[position]
        holder.mBinding.tvCard.text = textNew.notice
       Glide.with(holder.mBinding.imgCard).load(textNew.image).into(holder.mBinding.imgCard)



        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.email.toString()

        fun deleteNews() {
            val documentId = db.collection(userId)
            documentId.get().addOnSuccessListener {
                val id = it.documents.get(position)
                val id2 = id.id
                db.collection(userId).document(id2)
                    .delete()
                news.removeAt(position)
                updateAdapter(news)
                notifyItemRemoved(position)
            }
        }
        holder.mBinding.btDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete?")
            builder.setMessage("Are you sure?")
            builder.setPositiveButton("Yes"){_,_ ->
                deleteNews()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

    }
    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    override fun getItemCount(): Int {
        return news.size
    }
    fun updateAdapter(listNueva: MutableList<NewsDataClass>) {
        news = listNueva
        notifyDataSetChanged()
    }
    }

