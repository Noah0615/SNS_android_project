package com.example.week7_ex4

//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.week7_ex4.databinding.ActivityMainBinding
//import kotlinx.coroutines.*
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.channels.consumeEach
//import java.text.DecimalFormat
//import java.time.LocalTime
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//    private var isRunning = false
//    private var isPause = false
//    private var totalTimeInSeconds = 0
//    private var stopwatchJob: Job? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.start.setOnClickListener {
//            if (!isRunning && !isPause) {
//                isRunning = true
//                startStopwatch()
//            }
//        }
//
//        binding.stop.setOnClickListener {
//            isRunning = false
//            isPause = false
//            totalTimeInSeconds = 0
//            updateView(0, 0, 0)
//            stopwatchJob?.cancel()
//        }
//
//        binding.pause.setOnClickListener {
//            if (isRunning) {
//                isRunning = false
//                isPause = true
//                stopwatchJob?.cancel()
//            }
//        }
//
//        binding.resume.setOnClickListener {
//            if (!isRunning && isPause) {
//                isRunning = true
//                isPause = false
//                startStopwatch()
//            }
//        }
//    }
//
//    private fun startStopwatch() {
//        stopwatchJob = CoroutineScope(Dispatchers.Default).launch {
//            while (isRunning) {
//                delay(1000)
//                totalTimeInSeconds++
//                val hours = totalTimeInSeconds / 3600
//                val minutes = (totalTimeInSeconds % 3600) / 60
//                val seconds = totalTimeInSeconds % 60
//
//                withContext(Dispatchers.Main) {
//                    updateView(hours, minutes, seconds)
//                }
//            }
//        }
//    }
//
//    private fun updateView(hours: Int, minutes: Int, seconds: Int) {
//        binding.time.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        stopwatchJob?.cancel()
//    }
//}
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.week7_ex4.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val channel = Channel<String>()
    private var job: Job? = null // Coroutine Job을 관리하기 위한 변수
    private var time = 0 // 시간을 초 단위로 관리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.start.setOnClickListener {
            job?.cancel() // 기존에 실행 중인 Job이 있으면 취소
            time = 0 // 시간 초기화
            job = startStopwatch() // 스톱워치 시작
        }

        binding.stop.setOnClickListener {
            job?.cancel() // 스톱워치 정지
            time = 0 // 시간 초기화
            binding.time.text = "00:00:00" // 화면에 시간 초기화
        }

        binding.pause.setOnClickListener {
            job?.cancel() // 스톱워치 일시정지
        }

        binding.resume.setOnClickListener {
            job = startStopwatch() // 일시정지된 시간부터 스톱워치 재개
        }
    }

    private fun startStopwatch(): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val hours = time / 3600
                val minutes = (time % 3600) / 60
                val seconds = time % 60
                val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                withContext(Dispatchers.Main) {
                    binding.time.text = formattedTime
                }
                time++ // 시간을 1초 증가
                delay(1000) // 1초 대기
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // 액티비티가 종료될 때 스톱워치 정지
    }
}
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        var binding = ActivityMainBinding.inflate(layoutInflater)
//        super.onCreate(savedInstanceState)
//        setContentView(binding.activityMain)
//
//        val df00 = DecimalFormat("00")
//        var time: LocalTime = LocalTime.of(0,0,0)
//        var isPause = false
//        var isStop = true
//        val channel = Channel<Int>()
//        var countingJob: Job? = null
//        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
//
//        var mainScope = GlobalScope.launch(Dispatchers.Main) {
//            channel.consumeEach {
//                val hour = df00.format(it/3600)
//                val min_sec = it%3600
//                val minute = df00.format(min_sec/60)
//                val second = df00.format(min_sec%60)
//                binding.resultView.text = "$hour:$minute:$second"
//            }
//        }
//
//        binding.startBut.setOnClickListener {
//            /*
//            1) start 기능 구현하기
//            */
//            if(isStop){
//                isStop=false
//                countingJob=backgroundScope.launch {
//                    while(isActive){
//                        try {
//                            delay(1000)
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                        }
//                        time=time.plusSeconds(1)
//                        channel.send(time.toSecondOfDay())
//                    }
//                }
//
//            }
//            else{
//                Toast.makeText(this,"Stopwatch is not stopped now!!",Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.stopBut.setOnClickListener {
//            /*
//            2) stop 기능 구현하기
//            */
//            if(!isStop){
//                val hour = df00.format(0)
//                val min_sec = 0
//                val minute = df00.format(0)
//                val second = df00.format(0)
//                binding.resultView.text = "$hour:$minute:$second"
//                countingJob?.cancel()
//                time=LocalTime.of(0,0,0)
//
//                isStop=true
//
//            }
//        }
//
//        binding.pauseBut.setOnClickListener {
//            /*
//            3) pause 기능 구현하기
//            */
//            if(!isPause){
//                countingJob?.cancel()
//                isPause=true
//            }
//        }
//
//        binding.resumeBut.setOnClickListener {
//            /*
//            4) resume 기능 구현하기
//            */
//            if(isPause){
//                countingJob=backgroundScope.launch {
//                    while(isActive){
//                        try {
//                            delay(1000)
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                        }
//                        time=time.plusSeconds(1)
//                        channel.send(time.toSecondOfDay())
//                    }
//                }
//                isPause=false
//
//            }
//        }
//    }
//}