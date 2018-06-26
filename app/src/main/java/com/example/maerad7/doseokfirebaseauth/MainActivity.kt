package com.example.maerad7.doseokfirebaseauth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var authStateListener : FirebaseAuth.AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //구글 로그인 옵션
        var gso =  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        //구글 로그인 클래스를 만듬
        var googleSignInClient = GoogleSignIn.getClient(this,gso)


        //로그인 세션을 체크하는 부분
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //현재 로그인한 유저정보 받아오기
            var user = firebaseAuth.currentUser
            if(user != null){
                startActivity(Intent(this,HomeActivity::class.java))

            }
        }
        //구글 로그인
        button_google.setOnClickListener {
            var signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,1)
        }

        //패스워드 찾기
        button_findPassword.setOnClickListener{
            startActivity(Intent(this,FindPasswordActivity::class.java))
        }

        //회원가입
        button_sign_up.setOnClickListener{
            createEmailId()
        }

        //로그인
        button_login.setOnClickListener {
            loginId()
        }
    }
    //회원가입
    fun createEmailId(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(editText_email.text.toString(),editText_password.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"회원가입에 성공하였습니다.",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                }
    }
    //로그인
    fun loginId(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editText_email.text.toString(),editText_password.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"로그인에 성공하였습니다.",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener!!)
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode== Activity.RESULT_OK){

            //구글 로그인에 성공했을때 넘어오는 토큰 값을 가지고 있는 task
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)

            //ApiException 캐스팅
            var account = task.getResult(ApiException::class.java)

            //Credential만들어줌 구글 로그인에 성공했다는 인증서
            var credential= GoogleAuthProvider.getCredential(account.idToken,null)

            //파이어 베이스에 구글 사용자가 등록
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(this,"구글 아이디 연동 성공",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }
}
