package com.example.maerad7.doseokfirebaseauth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_storage.*
import java.io.ByteArrayOutputStream

class StorageActivity : AppCompatActivity() {
    var bitmap:Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        //로컬 이미지뷰
        storage_imageView_local.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        //이미지 업로드
        storage_button_upload.setOnClickListener{
            if(bitmap!=null) {
                uploadImage(bitmap!!)
            }
        }

        //이미지삭제
        storage_button_delete.setOnClickListener{
            deleteImage()
        }

        //이미지 로딩
        storage_button_image_read.setOnClickListener{
            imageLoad()
        }

    }

    //OnActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&resultCode==Activity.RESULT_OK){
            var imageUrl = data!!.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUrl)
            storage_imageView_local.setImageBitmap(bitmap)
        }
    }

    //이미지 업로드
    fun uploadImage(bitmap: Bitmap) {
        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        var data = baos.toByteArray()

        //child -> database 하위폴더
        FirebaseStorage.getInstance().reference.child("users").child(storage_editText_filename.text.toString()).putBytes(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "업로드에 성공하였습니다.", Toast.LENGTH_LONG).show()
                        saveUrlToDatabase(task.result.downloadUrl.toString())
                    }
                }

    }

    //업로드성공시 image Url DB에 저장
    fun saveUrlToDatabase(url:String){
        var map = HashMap<String,Any>()
        map["profile_image_url"]=url
        FirebaseFirestore.getInstance().collection("users").document(storage_editText_documtentId.text.toString()).update(map)
    }

    //이미지 삭제
    fun deleteImage(){
        FirebaseStorage.getInstance().reference.child("users").child(storage_editText_filename.text.toString()).delete().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                Toast.makeText(this,"파일삭제 완료",Toast.LENGTH_LONG).show()
                var map = HashMap<String,Any>()
                //이미지 경로 삭제
                map["profile_image_url"] = FieldValue.delete()
                FirebaseFirestore.getInstance().collection("users").document(storage_editText_documtentId.text.toString()).update(map)
            }
        }
    }

    //이미지 읽기
    fun imageLoad(){
        FirebaseFirestore.getInstance().collection("users").document(storage_editText_documentId_read.text.toString()).get().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                var userDTO=task.result.toObject(UserDTO::class.java)
                println(userDTO.profile_image_url)
                Picasso.get().load(userDTO.profile_image_url).into(storage_imageView_server)
            }
        }
    }

}
