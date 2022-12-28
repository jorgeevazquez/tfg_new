package com.example.tfgnews.ui.base

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tfgnews.data.NewsDataClass
import com.example.tfgnews.R
import com.example.tfgnews.databinding.NoticeCardBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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
        Glide.with(holder.mBinding.imgCard).load(textNew.image)
            .into(holder.mBinding.imgCard)
        fun deleteNews() {
            val db = FirebaseFirestore.getInstance()
            val mAuth = FirebaseAuth.getInstance()
            val userId = mAuth.currentUser?.email.toString()
            val mAuthId = mAuth.uid.toString()
            val referenceUrl = textNew.image
            println(referenceUrl)
            val referenceStorage = FirebaseStorage.getInstance().getReferenceFromUrl(referenceUrl!!)
            val documentId = db.collection(mAuthId).orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
            documentId.get().addOnSuccessListener {
                val id = it.documents.get(position)
                val id2 = id.id
                db.collection(mAuthId).document(id2)
                    .delete()
                referenceStorage.delete().addOnSuccessListener {
                    Log.i("DeleteStorage", "OK")
                }
                    .addOnFailureListener{
                        Log.i("DeleteStorage", "$it")
                    }
                news.removeAt(position)
                updateAdapter(news)
                notifyItemRemoved(position)
                Toast.makeText(context, "TheBestMoment delete success", Toast.LENGTH_SHORT)
                    .show()
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

