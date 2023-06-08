package com.example.activityandnavigationlesson.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.activityandnavigationlesson.Contact
import com.example.activityandnavigationlesson.ContactsAdapter
import com.example.activityandnavigationlesson.R
import com.example.activityandnavigationlesson.databinding.ContactsBinding

class Contacts : Fragment() {

    private lateinit var binding: ContactsBinding
    private lateinit var contactsAdapter: ContactsAdapter

//--------------------------------------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContactsBinding.inflate(inflater, container, false)
        return binding.root
    }
//--------------------------------------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Установка адаптера для RecyclerView (списка контактов)
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactsAdapter = ContactsAdapter()
        binding.contactsRecyclerView.adapter = contactsAdapter
        contactsAdapter.notifyDataSetChanged()

//--------------------------------------------------------------------------------------------------
        binding.addContactButton.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction().replace(R.id.frame, AddContactFragment.newInstance2())
                .addToBackStack(null).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Проверка на наличие разрешения доступа к контактам
        if (checkPermission()) {
            // Если разрешение есть, выполни-ся фун-я которая загрузит список контактов
            loadContacts()
        } else {
            // Если разрешения нет - запросить его
            requestPermissions()
        }
        contactsAdapter.notifyDataSetChanged()
    }

//--------------------------------------------------------------------------------------------------
    private fun checkPermission(): Boolean {
        // Проверка наличия разрешения доступа к контактам
        val permission = Manifest.permission.READ_CONTACTS
        //Метод checkSelfPermission() возвращает значение типа Int, которое указывает наличие разрешения.
        // PackageManager.PERMISSION_GRANTED означает, что разрешение уже предоставлено, а PackageManager.
        // PERMISSION_DENIED означает, что разрешение не предоставлено.
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        //Результат проверки сохраняется в переменной result.
        return result == PackageManager.PERMISSION_GRANTED
        //Функция возвращает true, если result равно PackageManager.PERMISSION_GRANTED
    }
//--------------------------------------------------------------------------------------------------
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Обработка результатов запроса разрешений
        if (permissions[Manifest.permission.READ_CONTACTS] == true) {
            loadContacts() // Если разрешение есть, выполняется функция, которая загрузит список контактов
        } else {
            requestPermissions() // Если пользователь не предоставил разрешение
        }
    }
//--------------------------------------------------------------------------------------------------
private fun requestPermissions() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
    }
//--------------------------------------------------------------------------------------------------
    private fun loadContacts() {
        // Загрузите список контактов с использованием Contacts Provider
        val contacts = fetchContacts().toSet()
        contactsAdapter.contacts = contacts.toList()
        contactsAdapter.notifyDataSetChanged()
    }
//--------------------------------------------------------------------------------------------------
private fun fetchContacts(): List<Contact> {
    val contacts = mutableListOf<Contact>()
    val phoneSet = HashSet<String>()

    val contentResolver = requireContext().contentResolver
    val projection = arrayOf(
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    val sortOrder = ContactsContract.Contacts.DISPLAY_NAME

    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    cursor?.use {
        val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val nameIndex = it.getColumnIndex(sortOrder) // получаем индекс столбца имени контакта

        while (it.moveToNext()) {
            val phoneNumber = it.getString(phoneNumberIndex)
            val formattedPhoneNumber = formatPhoneNumber(phoneNumber)

            // Проверяем существование столбца имени
            if (nameIndex >= 0) {
                val name = it.getString(nameIndex)
                if (!phoneSet.contains(formattedPhoneNumber)) {
                    contacts.add(Contact(name, formattedPhoneNumber))
                    phoneSet.add(formattedPhoneNumber)
                }
            }
        }
    }
    return contacts
}
//--------------------------------------------------------------------------------------------------
    private fun formatPhoneNumber(phoneNumber: String): String {
        // Реализуйте логику форматирования номера телефона по вашим требованиям
        // Например, можно удалить пробелы и символы дефиса, чтобы унифицировать формат
        return phoneNumber.replace(" ", "").replace("-", "")
    }
//--------------------------------------------------------------------------------------------------
    companion object {
        fun newInstance() = Contacts()
    }
}
