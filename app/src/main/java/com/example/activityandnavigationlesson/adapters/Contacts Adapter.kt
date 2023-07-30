package com.example.activityandnavigationlesson.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.activityandnavigationlesson.data_classes.Contact
import com.example.activityandnavigationlesson.databinding.ForRecyclerviewBinding


class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {
    var contacts = listOf<Contact>()

    class ContactViewHolder(private val binding: ForRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.userName.text = contact.name
            binding.userNumber.text = contact.number

            when(contact.isFavorite) {
                true -> binding.favorite.visibility = View.VISIBLE
                else -> binding.favorite.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ForRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}