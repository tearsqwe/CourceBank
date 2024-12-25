package com.example.coursebankingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val userLogin: EditText= findViewById(R.id.ETLogin)
        val userPhone: EditText= findViewById(R.id.ETPhone)
        val userPass: EditText=findViewById(R.id.ETPassword)
        val userPassRepeat: EditText=findViewById(R.id.ETPassRepeat)
        val tvAuth:TextView=findViewById(R.id.TVAuthoise)
        val userName:EditText=findViewById(R.id.ETName)
        val btnReg: Button=findViewById(R.id.button_register)

        btnReg.setOnClickListener{
            val login=userLogin.text.toString()
            val password = userPass.text.toString()
            val phone=userPhone.text.toString()
            val passRepeat=userPassRepeat.text.toString()
            val name= userName.text.toString()

            if(login== ""|| password=="" || phone=="" || passRepeat ==""|| name == "")
                Toast.makeText(this, "Введены не все необходимые данные!", Toast.LENGTH_LONG).show()
            else if(password!=passRepeat)
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_LONG).show()
            else
            {
                val user = User(login = login, password = password, name = name, phoneNumber = phone)
                val userManager=UserManager(this)
                userManager.openDB()
                val newUserId = userManager.registerUser(user)
                if (newUserId != -1L) {
                    Toast.makeText(this, "Пользователь $login зарегистрирован", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Пользователь с таким логином уже существует", Toast.LENGTH_LONG).show()
                }
                userLogin.text.clear()
                userPass.text.clear()
                userPhone.text.clear()
                userName.text.clear()
                userPassRepeat.text.clear()

                userManager.closeDB()
                val intent = Intent(this, CardActivity::class.java)
                intent.putExtra("USER_ID", newUserId)
                startActivity(intent)
            }
        }

        tvAuth.setOnClickListener{
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}