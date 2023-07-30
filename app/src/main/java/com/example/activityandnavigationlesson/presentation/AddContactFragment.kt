package com.example.activityandnavigationlesson.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.activityandnavigationlesson.R
import com.example.activityandnavigationlesson.databinding.AddContactBinding

class AddContactFragment : Fragment() {

    private lateinit var editTextName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var buttonAddContact: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var favoriteCheck: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddContactBinding.inflate(layoutInflater)

        editTextName = binding.userName
        editTextPhoneNumber = binding.userNumber
        buttonAddContact = binding.buttonEnter
        favoriteCheck = binding.favorite
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        buttonAddContact.setOnClickListener {
            addContact()
        }

        return binding.root
    }

    // фун-я для добавл-я контакта
    private fun addContact() {
        val name = editTextName.text.toString().trim()
        val phoneNumber = editTextPhoneNumber.text.toString().trim()
        val isFavorite = favoriteCheck.isChecked

        if (name.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Введите имя и номер телефона", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_CONTACTS),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
        }
        startActivity(contactIntent)
        sharedPreferences.edit().putBoolean(phoneNumber, isFavorite).apply()

        requireActivity().supportFragmentManager
            .beginTransaction().replace(R.id.frame, Contacts.newInstance()).commit()
    }

    //проверка разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Обработка результатов запроса разрешений
        if (permissions[Manifest.permission.READ_CONTACTS] == true) {
            addContact() // Если разрешение есть, выполняется функция, которая загрузит список контактов
        } else {
            requestPermissions() // Если пользователь не предоставил разрешение
            Toast.makeText(
                requireContext(),
                "Не удалось получить разрешение на добавление контакта",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        fun newInstance2() = AddContactFragment()
    }
}
