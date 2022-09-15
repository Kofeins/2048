package com.example.a2048

import android.accessibilityservice.GestureDescription
import android.content.Context
import android.gesture.Gesture
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import com.example.a2048.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var detector: GestureDetectorCompat
    lateinit var binding: ActivityMainBinding
    var nums: Array<Array<Int>> = Array(4) { Array(4) {0} }
    var S: Array<Array<TextView>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detector = GestureDetectorCompat(this, DiaryGestureListener())

        S = Array(4) { Array(4) {binding.textView11} }

        var textId: Array<Array<String>> = Array(4) { Array(4) {"0"} }
        var n: Int = 4


        for(i in 0..3){
            for(j in 0..3){
                textId[i][j] = "textView${i + 1}${j + 1}"
                val findId = resources.getIdentifier(textId[i][j], "id", packageName)
                S!![i][j] = findViewById(findId)
            }
        }



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
                        this@MainActivity.onSwipeRigth()
                    }
                    else{
                        //left swipe
                        this@MainActivity.onSwipeLeft()
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
                        this@MainActivity.onSwipeBottom()

                    }
                    else{
                        //left swipe
                        this@MainActivity.onSwipeTop()
                    }
                    true
                } else{
                        super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }
                }




        }
    }

    private fun newShift(param: Char){
        var range1: IntProgression? = null
        var range2: IntProgression? = null

        var add: Int = 1


        when(param){
            ('L') -> {//i j
                range1 = (0..3)
                range2 = (0..2)
                add = 1
            }
            'R' -> {//i j
                range1 = (3 downTo 0)
                range2 = (3 downTo 1)
                add = -1

            }
            'T' -> {//j i
                range1 = (0..3)
                range2 = (0..2)
                add = 1
            }
            'B' -> {//j i
                range1 = (3 downTo 0)
                range2 = (3 downTo 1)
                add = -1

            }

        }

        findPair(range1!!, range2!!, param, add)
        Log.d("MyLog", "ERROR HERE last work")
        turnTo(range1!!, param, add)

        randomS()
        rename()

    }
// РАБОТАЕТ ИДЕАЛЬНО
    private fun findPair(range1: IntProgression, range2: IntProgression, param: Char, add: Int) {
        //L 0 2 3

        var i: Int = 0 //stroke
        var j: Int = 0 // colomns
        var next: Int = 0
        for (s in range1){

            for (c in range2){

                if (param == 'L' || param == 'R'){
                    i = s
                    j = c

                    next = nums[i][j + add]
                    if (nums[i][j] == next && nums[i][j] != 0){
                        nums[i][j] *= 2
                        nums[i][j + add] = 0
                    }
                }
                else if (param == 'T' || param == 'B'){
                    i = c
                    j = s
                    next = nums[i + add][j]
                    if (nums[i][j] == next && nums[i][j] != 0){
                        nums[i][j] *= 2
                        nums[i + add][j] = 0
                    }
                }
            }
        }

    }
/// ТУТ КАКАЯ ТО ОБИШКА
    private fun turnTo(range1: IntProgression, param: Char, add: Int){
        var i: Int = 0 //stroke
        var j: Int = 0 // colomns
    var zero: Int = -1
        for (s in range1){
            zero = -1
            for (c in range1){
                if (param == 'L' || param == 'R'){

                    i = s
                    j = c
                    if (nums[i][j] == 0 && zero == -1){
                        zero = j
                    }
                    else if(nums[i][j] != 0 && zero != j && zero != -1){
                        Log.d("MyLog", "nums[$i][$j] = ${nums[i][j]} -----> zero = $zero, PRE")
                        nums[i][zero] = nums[i][j]
                        nums[i][j] = 0
                        zero += add
                        Log.d("MyLog", "nums[$i][$j] = ${nums[i][j]}, zero = $zero, param = $param")

                    }

                }
                else if (param == 'T' || param == 'B'){
                    i = c
                    j = s
                    if (nums[i][j] == 0 && zero == -1){
                        zero = i
                    }
                    else if(nums[i][j] != 0 && zero != j && zero != -1){
                        nums[zero][j] = nums[i][j]
                        nums[i][j] = 0
                        zero += add
                    }
                }

            }
        }
    }


    private fun rename(){
        for(i in 0..3){
            for (j in 0..3) {
                S!![i][j].text = nums[i][j].toString()
                //if(S!![i][j].text == "0"){
                   // S!![i][j].setBackgroundColor(Color.parseColor("##FFD700"))


                //}
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

    private fun onSwipeBottom() {

        //shift(0, 0, 3, 4, 1, 0)
        newShift('B')
        Toast.makeText(this, "BottomSwipe", Toast.LENGTH_LONG).show()
    }

    private fun onSwipeTop() {

        //shift(3, 0, 0, 4, -1, 0)
        newShift('T')
        Toast.makeText(this, "TopSwipe", Toast.LENGTH_LONG).show()
    }

    private fun onSwipeLeft() {

        //shift(0, 3, 4, 0, 0, -1)
        newShift('L')
        Toast.makeText(this, "LeftSwipe", Toast.LENGTH_LONG).show()
    }

    private fun onSwipeRigth() {

        //shift(0, 0, 4, 3, 0, 1)
        newShift('R')
        Toast.makeText(this, "RightSwipe", Toast.LENGTH_LONG).show()

    }


}