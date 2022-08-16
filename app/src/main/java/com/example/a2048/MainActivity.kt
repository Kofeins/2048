package com.example.a2048

import android.accessibilityservice.GestureDescription
import android.gesture.Gesture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat

class MainActivity : AppCompatActivity() {
    private lateinit var detector: GestureDetectorCompat

    var nums: Array<Array<Int>> = Array(4) { Array(4) {0} }
    var S: Array<Array<TextView>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        detector = GestureDetectorCompat(this, DiaryGestureListener())

        S = Array(4) { Array(4) {findViewById(R.id.textView11)} }


        var n: Int = 4

        S!![0][0] = findViewById(R.id.textView11)
        S!![0][1] = findViewById(R.id.textView12)
        S!![0][2] = findViewById(R.id.textView13)
        S!![0][3] = findViewById(R.id.textView14)

        S!![1][0] = findViewById(R.id.textView21)
        S!![1][1] = findViewById(R.id.textView22)
        S!![1][2] = findViewById(R.id.textView23)
        S!![1][3] = findViewById(R.id.textView24)

        S!![2][0] = findViewById(R.id.textView31)
        S!![2][1] = findViewById(R.id.textView32)
        S!![2][2] = findViewById(R.id.textView33)
        S!![2][3] = findViewById(R.id.textView34)

        S!![3][0] = findViewById(R.id.textView41)
        S!![3][1] = findViewById(R.id.textView42)
        S!![3][2] = findViewById(R.id.textView43)
        S!![3][3] = findViewById(R.id.textView44)

        //R shift(0, 0, 4, 3, 0, 1)
        //L shift(0, 3, 4, 0, 0, -1)
        //Down shift(0, 0, 3, 4, 1, 0)
        //Up shift(3, 0, 0, 4, -1, 0)

        randomS()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (detector.onTouchEvent(event)){
            true
        }else{
            super.onTouchEvent(event)
        }

    }

    inner class DiaryGestureListener : GestureDetector.SimpleOnGestureListener(){
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOSITY_THRESHOLD = 100

        override fun onFling(
            downEvent: MotionEvent?,
            moveEvent: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var diffx = moveEvent?.x?.minus(downEvent!!.x) ?: 0.0F
            var diffy = moveEvent?.y?.minus(downEvent!!.y) ?: 0.0F

            return if(Math.abs(diffx) > Math.abs(diffy)){
                if(Math.abs(diffx) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOSITY_THRESHOLD){
                    if (diffx > 0){
                        //right swipe
                        this@MainActivity.OnSwipeRigth()
                    }
                    else{
                        //left swipe
                        this@MainActivity.OnSwipeLeft()
                    }
                    true
                } else {
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }
            }
            else{
                if(Math.abs(diffy) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOSITY_THRESHOLD){
                    if (diffx > 0){
                        //right swipe
                        this@MainActivity.OnSwipeBottom()

                    }
                    else{
                        //left swipe
                        this@MainActivity.OnSwipeTop()
                    }
                    true
                } else{
                        super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }
                }




        }
    }

// АЛГОРИТМ -- ГОВНО. ПЕРЕДЕЛАТЬ!!!!!!! НУ и да. Пресчитать цифры!))))
    private fun shift(p1: Int, p2 :Int, p3 :Int, p4 :Int, s1: Int, s2: Int){
        val m1: Int = p3
        val m2: Int = p4
        var i: Int = p1
        var j: Int = p2
        val si: Int = s1 // изменение i
        val sj: Int = s2 // изменение j
        while (i != m1){
            j = p2
            while (j != m2){
                if (nums[i][j] == nums[i + si][j + sj]){
                    nums[i + si][j + sj] = nums[i][j] * 2
                    nums[i][j] = 0
                }
                else if(nums[i + si][j + sj] == 0){
                    nums[i + si][j + sj] = nums[i][j]
                    nums[i][j] = 0
                }
                if (sj != 0){
                    j += sj
                }
                else{
                    j++
                }

            }
            if (si != 0){
                i += si
            }
            else{
                i++
            }
        }
        randomS()
        rename()
    }

    private fun rename(){
        for(i in 0..3){
            for (j in 0..3) {
                S!![i][j].text = nums[i][j].toString()
            }
        }
    }

    private fun randomS(){
        val i: Int = (0..3).random()
        val j: Int = (0..3).random()
        if (nums[i][j] != 0){
            randomS()
        }
        else{
            nums[i][j] = 2
            rename()
            return
        }
    }

    //R shift(0, 0, 4, 3, 0, 1)
    //L shift(0, 3, 4, 0, 0, -1)
    //Down shift(0, 0, 3, 4, 1, 0)
    //Up shift(3, 0, 0, 4, -1, 0)

    private fun OnSwipeBottom() {
        Toast.makeText(this, "BottomSwipe", Toast.LENGTH_LONG).show()
        shift(0, 0, 3, 4, 1, 0)
    }

    private fun OnSwipeTop() {
        Toast.makeText(this, "TopSwipe", Toast.LENGTH_LONG).show()
        shift(3, 0, 0, 4, -1, 0)
    }

    private fun OnSwipeLeft() {
        Toast.makeText(this, "LeftSwipe", Toast.LENGTH_LONG).show()
        shift(0, 3, 4, 0, 0, -1)
    }

    private fun OnSwipeRigth() {
        Toast.makeText(this, "RightSwipe", Toast.LENGTH_LONG).show()
        shift(0, 0, 4, 3, 0, 1)

    }


}