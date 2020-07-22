package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.floating_video_layout.view.*

class ChatHeadService : Service() {
    private var windowManager: WindowManager? = null
    private var chatHead: View? = null
    override fun onBind(intent: Intent): IBinder? {
        // Not used
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        Log.d("ChatHeadService", "onCreate: ")
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        chatHead = LayoutInflater.from(this).inflate(R.layout.floating_video_layout, null, false)
        val layoutFlag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
             WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100
        windowManager!!.addView(chatHead, params)
        chatHead!!.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> return true
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager!!.updateViewLayout(chatHead, params)
                        return true
                    }
                }
                return false
            }
        })
        //
        // val player = SimpleExoPlayer.Builder(this).build()
        // val mp4VideoUri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
        //
        // chatHead!!.playerView.player = player
        // // Produces DataSource instances through which media data is loaded.
        // val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
        //     this,
        //     Util.getUserAgent(this, "yourApplicationName")
        // )
        // // This is the MediaSource representing the media to be played.
        // val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        //     .createMediaSource(mp4VideoUri)
        // // Prepare the player with the source.
        // player.prepare(videoSource)
        // player.playWhenReady = true
        //
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        if (chatHead != null) windowManager!!.removeView(chatHead)
    }
}