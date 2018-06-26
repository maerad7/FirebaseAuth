

package com.example.maerad7.doseokfirebaseauth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_read_database.*

//가상 안드로이드에서 안됨
class ReadDatabaseActivity : AppCompatActivity() {
    var getarrayList = arrayListOf<UserDTO>()
    var realTimeArrayList = arrayListOf<UserDTO>()
    var realTimeKeyArrayList = arrayListOf<String>()
    var listForFilter : ArrayList<UserDTO>? =null
    var city:String?= null
    var age:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_database)

        recycleView_read_database.adapter = ReadRecyclerAdapter(getarrayList)
        recycleView_read_database.layoutManager=LinearLayoutManager(this)

        //비동기방식 GET
        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener{
            querySnapshot ->
            for (item in querySnapshot.documents) {
                var userDTO = item.toObject(UserDTO::class.java)
                getarrayList.add(userDTO)
            }
            (recycleView_read_database.adapter as ReadRecyclerAdapter).notifyDataSetChanged()

        }
        recycleView_read_database_realtime.adapter = ReadRecyclerAdapter(realTimeArrayList)
        recycleView_read_database_realtime.layoutManager=LinearLayoutManager(this)
     /*  //동기방식 스냅샷
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            realTimeArrayList.clear()
            for(item in querySnapshot.documents){
                var userDTO = item.toObject(UserDTO::class.java)
                realTimeArrayList.add(userDTO)
            }
            (recycleView_read_database_realtime.adapter as ReadRecyclerAdapter).notifyDataSetChanged()

        }*/
        // 동기방식 스냅샷
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for(item in querySnapshot.documentChanges){
                when(item.type){

                    //데이터가 추가 될때 호출, 처음에 호출
                    DocumentChange.Type.ADDED -> {
                        realTimeArrayList.add(item.document.toObject(UserDTO::class.java))
                        realTimeKeyArrayList.add(item.document.id)
                    }
                    //데이터 수정될 때
                    DocumentChange.Type.MODIFIED -> modifyItem(item.document.id,item.document.toObject(UserDTO::class.java))
                    //데이터 지워질때
                    DocumentChange.Type.REMOVED -> deleteItem(item.document.id)
                }

                //다시그리기 새로고침코드
                (recycleView_read_database_realtime.adapter as ReadRecyclerAdapter).notifyDataSetChanged()
            }

        }

        //검색스피너 - 시티
        read_database_activity_spinner_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                city=p0!!.getItemAtPosition(p2) as String
                listBySpinner()
            }

        }

        //검색스피너 - 나이
        read_database_activity_spinner_age.onItemSelectedListener =object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                age = p0!!.getItemAtPosition(p2) as String
                listBySpinner()
            }

        }
        read_database_activity_edittext.addTextChangedListener(object : TextWatcher{

            //글자값이 변경될때 마다 호출
            override fun afterTextChanged(p0: Editable?) {
                searchList(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    //like 구현하기
    fun searchList(filterString:String){

        //필터 -> 조건문 안에 있는 네임과 필터스트링을 비교하여 참인것을 리턴
        var filterList = listForFilter!!.filter { userDTO ->
            //like 구현하기
            //userDTO.name!!.contains(filterString)
            checkCharacter(userDTO.name!!,filterString)
        }

        getarrayList.clear()
        getarrayList.addAll(filterList)
        recycleView_read_database.adapter!!.notifyDataSetChanged()
    }

    //or 구현하기
    fun checkCharacter(name:String,searchString:String):Boolean{
        //edit 텍스트의 파라미터로 분류하고 어레이로 만듬
        var array = searchString.split(" ")
        for(item in array){
            if(name.contains(item))
                return true
        }
        return false
    }
    //스피너 검색 쿼리 설정
    fun listBySpinner(){
        if(city!=null && age!=null){
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("city",city)
                    .whereGreaterThanOrEqualTo("age",age!!.toInt())
                    .get().addOnSuccessListener {
                querySnapshot ->
                getarrayList.clear()
                for(item in querySnapshot.documents){
                    var userDTO = item.toObject(UserDTO::class.java)
                    getarrayList.add(userDTO)
                }

                        //clone을 넣지 않으면 getarraylist의 주소값만 복사된다.
                        listForFilter = getarrayList.clone() as ArrayList<UserDTO>
                recycleView_read_database.adapter!!.notifyDataSetChanged()
            }.addOnFailureListener {
                        exception ->
                        println(exception.toString())
                    }
        }
    }

    //데이터베이스 내용 수정하기
    fun modifyItem(modifyItem:String,userDTO: UserDTO){
        for((position,item) in realTimeKeyArrayList.withIndex()){
            if(item == modifyItem){
                realTimeArrayList[position]=userDTO
            }
        }
    }

    //데이터배이스 내용 지우기
    fun deleteItem(deleteKey:String){
        for((position,item)in realTimeKeyArrayList.withIndex())
            if(deleteKey == item){
                realTimeKeyArrayList.removeAt(position)
                break
            }
        }

    //리사이클러뷰 옵션 설정
    class ReadRecyclerAdapter(initList:ArrayList<UserDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var list:ArrayList<UserDTO>? = initList
       /* init{
            list = initList
        }*/

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0!!.context).inflate(R.layout.item_recyclerview,p0,false)
            return CustomViewHoder(view)
        }

        class CustomViewHoder(view: View?) : RecyclerView.ViewHolder(view!!) {
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


        }

    }
}
