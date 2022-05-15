package com.example.facefinder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    lateinit var img: ImageView; //initalize the imageview where we display taken image
    lateinit var butt: FloatingActionButton; //initialize the button that will open camera to take picture
    lateinit var text: TextView; //initalize textview where we will display the number of faces found in an image
    lateinit var photo: Bitmap
    private val cameraRequest = 1888
    //setting up facedetector object
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    //initalizing face detector
    val detector = FaceDetection.getClient(options)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //check camera permission
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequest)

        img = findViewById(R.id.pic_taken); //set the value of img to pic_taken in activity_main
        butt = findViewById(R.id.take_pic); //set the value of butt to rake_pic in activity_main
        text = findViewById(R.id.num_faces); //set the value of text to num_faces in activity_main

        butt.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, cameraRequest)
        }



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequest) {
            photo = data?.extras?.get("data") as Bitmap
            img.setImageBitmap(photo)
            val image = InputImage.fromBitmap(photo, 0)
            detector.process(image).addOnSuccessListener{faces ->
                val drawingView = DrawRect(applicationContext, faces)
                drawingView.draw(Canvas(photo))

                text.setText("The number of faces in the photo is: " + faces.size.toString())
            }.addOnFailureListener{
            }

        }
    }


}

//function to draw a rectangle around the given face
class DrawRect(context: Context, var faceObject: List<Face>) : View(context) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pen = Paint()
        for (item in faceObject) {
            // draw bounding box
            pen.color = Color.YELLOW
            pen.strokeWidth = 4F
            pen.style = Paint.Style.STROKE
            val box = item.boundingBox
            canvas.drawRect(box, pen)
        }
    }
}