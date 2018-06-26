package com.example.maerad7.doseokfirebaseauth

import android.content.Intent
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.method.PasswordTransformationMethod
import android.widget.EdgeEffect
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*
import javax.security.auth.login.LoginException

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //로그아웃 버튼
        button_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        //패스워드 변경 버튼
        button_change_password.setOnClickListener {

            var editTextNewPassword = EditText(this)
            //입력된 글자가 자동적으로 보안성있게 바꿔주는 코드
            editTextNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            //패스워드 변경 다이어로그 창
            var alertDialog = AlertDialog.Builder(this)

            alertDialog.setTitle("패스워드 변경")
            alertDialog.setView(editTextNewPassword)
            alertDialog.setPositiveButton("변경", { dialogInterface, i ->
                changePassword(editTextNewPassword.text.toString())
            })
            alertDialog.setNegativeButton("취소", { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()
        }
        //이메일 체크 버튼
        button_check_email.setOnClickListener {
            sendEmailVerification()
        }

        //이메일 주소 변경
        button_change_email.setOnClickListener {
            var editTextNewId = EditText(this)
            var alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setView(editTextNewId)
            alertDialog.setMessage("변경하고 싶은 아이디를 입력해 주세요.")
            alertDialog.setPositiveButton("확인", { dialogInterface, i -> changetId(editTextNewId.text.toString()) })
            alertDialog.setNegativeButton("취소", { dialogInterface, i -> })
            alertDialog.show()

        }

        //탈퇴하기 버튼
        button_delete.setOnClickListener {
            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("알림")
            alertDialog.setMessage("아이디를 삭제하시겠습니까?")
            alertDialog.setPositiveButton("확인", { dialogInterface, i -> deleteId() })
            alertDialog.setNegativeButton("취소", { dialogInterface, i -> })
            alertDialog.show()
        }

        //데이터베이스
        button_database.setOnClickListener{
            startActivity(Intent(this,DatabaseActivity::class.java))
        }

        //Storage
        button_storage.setOnClickListener{
            startActivity(Intent(this,StorageActivity::class.java))
        }

    }


    //패스워드 변경
    fun changePassword(password: String) {
        FirebaseAuth.getInstance().currentUser!!.updatePassword(password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "비밀번호 변경 완료", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    //이메일 체크
    fun sendEmailVerification() {
        if (FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
            Toast.makeText(this, "이메일 인증이 완료", Toast.LENGTH_LONG).show()
            return
        }
        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "확인 메일을 보냈습니다.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    //이메일주소 변경
    fun changetId(email: String) {
        FirebaseAuth.getInstance().currentUser!!.updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "이메일 변경 완료,", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
    }

    //탈퇴하기
    fun deleteId() {
        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //세션종료
                FirebaseAuth.getInstance().signOut()
                //LoginManger.getInstance().logOut()
                Toast.makeText(this, "탈퇴하기 완료,", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
//구글 로그인 인증모듈  추가
