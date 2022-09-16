package com.example.a2048


import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.example.a2048.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var Score: Int = 2
    private lateinit var detector: GestureDetectorCompat
    private val colorMap = mutableMapOf<String, Int>()
    lateinit var binding: ActivityMainBinding
    var nums: Array<Array<Int>> = Array(4) { Array(4) {0} }
    var S: Array<Array<TextView>>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val colors = resources.getIntArray(R.array.Colors_2048)

        detector = GestureDetectorCompat(this, DiaryGestureListener())

        //Заполняем мапу
        var toMapIn = 2
        for(i in 0..10){
            colorMap.put(toMapIn.toString(), colors[i])
            toMapIn *= 2
        }

        binding.Score.text = "Score : $Score"

        binding.Restart.setOnClickListener{
            toRestart()
        }



        S = Array(4) { Array(4) {binding.textView11} }

        var textId: Array<Array<String>> = Array(4) { Array(4) {"0"} }

        for(i in 0..3){
            for(j in 0..3){

                textId[i][j] = "textView${i + 1}${j + 1}"
                val findId = resources.getIdentifier(textId[i][j], "id", packageName)
                S!![i][j] = findViewById(findId)
            }
        }
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

    private fun toRestart(){
        nums = Array(4) { Array(4) {0} }
        Score = 0
        binding.Score.text = "Score : $Score"
        randomS()
        rename()
    }

    private fun setScore(score: Int){
        if (Score < score){
            Score = score
            binding.Score.text = "Score : $Score"
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
                        setScore(nums[i][j])
                    }
                }
                else if (param == 'T' || param == 'B'){
                    i = c
                    j = s
                    next = nums[i + add][j]
                    if (nums[i][j] == next && nums[i][j] != 0){
                        nums[i][j] *= 2
                        nums[i + add][j] = 0
                        setScore(nums[i][j])
                    }
                }

            }
        }

    }

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
                    else if(nums[i][j] != 0 && zero != i && zero != -1){
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
                if (S!![i][j].text != "0"){
                    val color = colorMap.getValue(nums[i][j].toString())
                    S!![i][j].setBackgroundColor(color)
                }
                else{
                    val color = ContextCompat.getColor(this, R.color.white)
                    S!![i][j].setBackgroundColor(color)
                }

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
        newShift('B')
        //Toast.makeText(this, "BottomSwipe", Toast.LENGTH_LONG).show()
        binding.Last.text = "Last : Bottom"
    }

    private fun onSwipeTop() {
        newShift('T')
        //Toast.makeText(this, "TopSwipe", Toast.LENGTH_LONG).show()
        binding.Last.text = "Last : Top"
    }

    private fun onSwipeLeft() {
        newShift('L')
        //Toast.makeText(this, "LeftSwipe", Toast.LENGTH_LONG).show()
        binding.Last.text = "Last : Left"
    }

    private fun onSwipeRigth() {
        newShift('R')
        //Toast.makeText(this, "RightSwipe", Toast.LENGTH_LONG).show()
        binding.Last.text = "Last : Right"

    }


}