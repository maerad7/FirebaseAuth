package com.example.maerad7.doseokfirebaseauth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import kotlinx.android.synthetic.main.activity_database.*
import kotlinx.android.synthetic.main.activity_home.*

class DatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        //데이터 베이스 만들기
        button_create.setOnClickListener{
            startActivity(Intent(this,DatabaseCreateActivity::class.java))
        }

        //데이터 베이스 읽기
        button_read.setOnClickListener {
            startActivity(Intent(this,ReadDatabaseActivity::class.java))

        }

        //데이터베이스 orderby
        button_read_orderby.setOnClickListener{
            startActivity(Intent(this,ReadOrderbyDatabaseAcitivity::class.java))
        }

        //데이터 베이스 업데이트 델리트
        button_update_delete.setOnClickListener{
            startActivity(Intent(this,MotifyDatabaseActivity::class.java))
        }

    }
}
