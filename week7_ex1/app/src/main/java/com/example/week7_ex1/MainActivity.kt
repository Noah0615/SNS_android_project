package com.example.week7_ex1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.week7_ex1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var downloadId: Long = -1L  // 다운로드 ID를 저장하는 변수
    private lateinit var downloadManager: DownloadManager  // 다운로드 매니저 객체

    // 브로드캐스트 리시버 선언
    private val onDownloadComplete = object : BroadcastReceiver() {
        // 리시버 onReceive 구현
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)  // 인텐트에서 다운로드 ID를 가져옴
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.action)) {  // 다운로드 완료 액션인 경우
                if (downloadId == id) {  // 다운로드 ID가 일치하는 경우
                    val query: DownloadManager.Query = DownloadManager.Query()  // 쿼리 객체 생성
                    query.setFilterById(id)  // 쿼리에 다운로드 ID 설정
                    var cursor = downloadManager.query(query)  // 쿼리 실행
                    if (!cursor.moveToFirst()) {  // 커서를 첫 번째 행으로 이동
                        return
                    }

                    var columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)  // 상태 컬럼의 인덱스를 가져옴
                    var status = cursor.getInt(columnIndex)  // 상태 컬럼의 값을 가져옴
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {  // 다운로드 성공인 경우
                        Toast.makeText(context, "Download succeeded", Toast.LENGTH_SHORT).show()  // 토스트 메시지 출력
                    } else if (status == DownloadManager.STATUS_FAILED) {  // 다운로드 실패인 경우
                        Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()  // 토스트 메시지 출력
                    }
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.action)) {  // 알림 클릭 액션인 경우
                Toast.makeText(context, "Notification clicked", Toast.LENGTH_SHORT).show()  // 토스트 메시지 출력
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        var binding = ActivityMainBinding.inflate(layoutInflater)  // 뷰 바인딩 객체 생성
        super.onCreate(savedInstanceState)
        setContentView(binding.root)  // 뷰 설정

        // 다운로드 매니저 객체 생성
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        // 인텐트 필터 선언
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        // 리시버와 인텐트 필터 연결
        registerReceiver(onDownloadComplete, filter, RECEIVER_NOT_EXPORTED)

        binding.downloadBtn.setOnClickListener {  // 다운로드 버튼 클릭 리스너
            val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/"), "dev_submit.mp4")  // 다운로드할 파일 객체 생성

            // 다운받을 Url 주소
            val downloadUrl = "https://cse.pusan.ac.kr/sites/cse/download/201912_cse_newsletter_vol_29.pdf"
            val request = DownloadManager.Request(Uri.parse(downloadUrl))  // 다운로드 요청 객체 생성
            request.setDestinationUri(Uri.fromFile(file))  // 다운로드 경로 설정
            downloadId = downloadManager.enqueue(request)  // 다운로드 요청 전송
        }

        binding.cancelBtn.setOnClickListener {  // 취소 버튼 클릭 리스너
            downloadManager.remove(downloadId)  // 다운로드 취소
        }
    }

    // 리시버와 인텐트 필터 연결 해제
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }
}
