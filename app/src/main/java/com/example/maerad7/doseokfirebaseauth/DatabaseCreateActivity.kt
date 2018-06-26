package com.example.maerad7.doseokfirebaseauth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_database_create.*

class DatabaseCreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_create)

        //Create 데이터
        button_create_input.setOnClickListener {
            createData()
        }
    }
    //데이터 넣기
    fun createData(){
        var userDTO= UserDTO(editText_name.text.toString(),editText_age.text.toString().toInt(),editText_city.text.toString())
        //document안의 파라미터를 지우면 자동이름으로 생성
        FirebaseFirestore.getInstance().collection("users").document("0").set(userDTO).addOnSuccessListener {
            Toast.makeText(this,"데이터 입력이 성공하였습니다",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            exception -> Toast.makeText(this,exception.toString(),Toast.LENGTH_LONG).show()

        }
    }
}
