package com.example.activityandnavigationlesson

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.activityandnavigationlesson.databinding.ActivityCreateContactBinding
import com.example.activityandnavigationlesson.databinding.CustomDialogBinding
import com.example.activityandnavigationlesson.fragments.AddContactFragment
import com.example.activityandnavigationlesson.fragments.Contacts

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateContactBinding

//--------------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCreateContactBinding.inflate(layoutInflater)
    setContentView(binding.root)
//--------------------------------------------------------------------------------------------------
    supportFragmentManager
        .beginTransaction().replace(R.id.frame, Contacts.newInstance()).commit()
//--------------------------------------------------------------------------------------------------
    setSupportActionBar(binding.myToolbar) // toolbar
    binding.myToolbar.setNavigationOnClickListener {  // alert dialog
        AlertDialog.Builder(this)
            .setTitle("")
            .setPositiveButton("Список контактов") { _, _ ->
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame, Contacts.newInstance()).addToBackStack(null).commit()
            }
            .setNegativeButton("Добавить контакт") { _, _ ->
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame, AddContactFragment.newInstance2()).addToBackStack(null)
                    .commit()
            }
            .setCancelable(false)
            .show()
    }
}
//==================================================================================================
        override fun onCreateOptionsMenu(menu: Menu?): Boolean { //создание меню в toolbar
            menuInflater.inflate(R.menu.main_menu, menu)
            return true
        }
//--------------------------------------------------------------------------------------------------
        override fun onOptionsItemSelected(item: MenuItem): Boolean { // если кнопка в меню тулбара(справа) будет нажата выполнится условие для alert dialog
            val view = CustomDialogBinding.inflate(layoutInflater)
            val alertDialog =
                android.app.AlertDialog.Builder(this).setView(view.root).create()  //custom dialog

            view.yesBtn.setOnClickListener {
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame, Contacts.newInstance()).commit()
                alertDialog.dismiss()
            }

            view.noBtn.setOnClickListener {
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame, AddContactFragment.newInstance2())
                    .commit()
                alertDialog.dismiss()
            }

            when (item.itemId) {
                R.id.addContactMenu -> {
                    alertDialog.show()
                }
            }
            return false
        }
    }