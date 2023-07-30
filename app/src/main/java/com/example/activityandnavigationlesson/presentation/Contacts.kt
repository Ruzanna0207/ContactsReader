package com.example.activityandnavigationlesson.presentation
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
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
import com.example.activityandnavigationlesson.data_classes.Contact
import com.example.activityandnavigationlesson.adapters.ContactsAdapter
import com.example.activityandnavigationlesson.R
import com.example.activityandnavigationlesson.databinding.ContactsBinding

class Contacts : Fragment() {

    private lateinit var binding: ContactsBinding
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var favoriteContacts: List<Contact>
        private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Установка адаптера для RecyclerView (списка контактов)
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactsAdapter = ContactsAdapter()
        binding.contactsRecyclerView.adapter = contactsAdapter
        contactsAdapter.notifyDataSetChanged()

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
    }

    private fun checkPermission(): Boolean {
        // Проверка наличия разрешения доступа к контактам
        val permission = Manifest.permission.READ_CONTACTS
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

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

    private fun requestPermissions() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
    }

    private fun fetchContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val phoneSet = HashSet<String>()

        val contentResolver = requireContext().contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.STARRED
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
            val nameIndex = it.getColumnIndex(sortOrder)
            val isStarredIndex = it.getColumnIndex(ContactsContract.Contacts.STARRED)

            while (it.moveToNext()) {
                val phoneNumber = it.getString(phoneNumberIndex)
                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                val isFavorite = sharedPreferences.getBoolean(formattedPhoneNumber, false)

                val name = it.getString(nameIndex)
                val contact = Contact(name, formattedPhoneNumber, isFavorite)
                if (!phoneSet.contains(formattedPhoneNumber)) {
                    contacts.add(contact)
                    phoneSet.add(formattedPhoneNumber)
                }
            }
        }
        return contacts
    }

    private fun loadContacts() {
        //получ-е списка контактов с использ-ем Contacts Provider
        val allContacts = fetchContacts()
        favoriteContacts = allContacts.filter { it.isFavorite }
        contactsAdapter.contacts = allContacts
        contactsAdapter.notifyDataSetChanged()
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(" ", "").replace("-", "")
    }

    companion object {
        fun newInstance() = Contacts()
    }
}
