package com.example.maerad7.doseokfirebaseauth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_find_password.*

class FindPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)
        button_find_password.setOnClickListener{
            findPassword()
        }
    }

    //비밀번호 재설정
    fun findPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(editText_city.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"비밀번호 재설정 메일을 보냈습니다.",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                }
    }
}
