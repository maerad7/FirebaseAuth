package com.example.maerad7.doseokfirebaseauth

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_motify_database.*

class MotifyDatabaseActivity : AppCompatActivity() {
    var getList = ArrayList<UserDTO>()
    var getKeyList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motify_database)
        motify_database_recyclerview.adapter = ModifyAdapter(getList)
        motify_database_recyclerview.layoutManager = LinearLayoutManager(this)
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            getList.clear()
            getKeyList.clear()
            for (item in querySnapshot.documents) {
                getList.add(item.toObject(UserDTO::class.java))
                getKeyList.add(item.id)
            }
            (motify_database_recyclerview.adapter as ModifyAdapter).notifyDataSetChanged()
        }

    }

    //리사이클러뷰 옵션 설정
   inner class ModifyAdapter(initList:ArrayList<UserDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var list:ArrayList<UserDTO>? = initList
        /* init{
             list = initList
         }*/

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0!!.context).inflate(R.layout.item_recyclerview,p0,false)
            return CustomViewHoder(view)
        }

      inner  class CustomViewHoder(view: View?) : RecyclerView.ViewHolder(view!!) {
            var textView_name = view!!.findViewById<TextView>(R.id.textView_name)
            var textView_age = view!!.findViewById<TextView>(R.id.textView_age)
            var textView_city= view!!.findViewById<TextView>(R.id.textView_city)

        }

        override fun getItemCount(): Int {

            return list!!.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var customViewHoder = p0 as CustomViewHoder
            customViewHoder.textView_name.text= list!!.get(p1).name
            customViewHoder.textView_age.text= list!!.get(p1).age.toString()
            customViewHoder.textView_city.text= list!!.get(p1).city
            customViewHoder.itemView.setOnClickListener{
                modifyItem(p1)

            }
            customViewHoder.itemView.setOnLongClickListener {
                deleteItem(p1)
                true

            }


        }

        //alertDialog 회원정보 수정
        fun modifyItem(position:Int){
            var inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.dialog_modify,null)

            var editText_name = view.findViewById<EditText>(R.id.dialog_editText_name)
            var editText_age = view.findViewById<EditText>(R.id.dialog_editText_age)
            var editText_city = view.findViewById<EditText>(R.id.dialog_editText_city)

            var alertDialog = AlertDialog.Builder(this@MotifyDatabaseActivity)
                    .setTitle("회원정보 수정")
                    .setMessage("회원정보를 수정할 값을 넣어주세요")
                    .setView(view)
                    .setPositiveButton("확인",{dialogInterface, i ->
                        var map = HashMap<String,Any>()
                        map["name"] = editText_name.text.toString()
                        map["age"] = editText_age.text.toString().toInt()
                        map["city"] = editText_city.text.toString()
                        map["profile_image_url"]="스토리지 시간에 입력할 예정입니다."

                        FirebaseFirestore.getInstance().collection("users").document(getKeyList[position]).update(map)
                    })
                    .setNegativeButton("취소",{dialogInterface, i ->  })
            alertDialog.show()

        }

        //alertdialog 데이터베이스 삭제
        fun deleteItem(position: Int){
            var alertDialog = AlertDialog.Builder(this@MotifyDatabaseActivity)
                    .setTitle("회웍정보 삭제")
                    .setMessage("회원정보를 삭제하시겠습니까")
                    .setPositiveButton("확인",{dialogInterface, i ->
                        FirebaseFirestore.getInstance().collection("users").document(getKeyList[position]).delete()
                    })
                    .setNegativeButton("취소",{dialogInterface, i ->  })
            alertDialog.show()
        }
    }
}
