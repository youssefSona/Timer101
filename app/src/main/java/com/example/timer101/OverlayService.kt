package com.example.timer101

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat


class OverlayService : Service() {

    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    private lateinit var params: WindowManager.LayoutParams

    //
    private lateinit var layoutTimers: LinearLayoutCompat
    private lateinit var layoutCounting: LinearLayoutCompat
    private lateinit var tvTimer: TextView

    private lateinit var countDownTimer: CountDownTimer

    //
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        // Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        layoutTimers =
            mFloatingView!!.findViewById<View>(R.id.root_container2) as LinearLayoutCompat
        layoutCounting =
            mFloatingView!!.findViewById<View>(R.id.root_container3) as LinearLayoutCompat
        tvTimer = mFloatingView?.findViewById<View>(R.id.tvTimer) as TextView

        val layoutFlag: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }

        // Specify the view position
        params.gravity =
            Gravity.TOP or Gravity.START // Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        // Add the view to the window
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager?.addView(mFloatingView, params)

        // Set the close button
        val closeButtonCollapsed =
            mFloatingView?.findViewById<ImageView>(R.id.close_btn)
        closeButtonCollapsed?.setOnClickListener {
            // Close the service and remove the view from the window
            stopSelf()
        }
        val resetButton =
            mFloatingView?.findViewById<ImageView>(R.id.reset_btn)

        resetButton?.setOnClickListener {
            countDownTimer.cancel()
            countDownTimer.onFinish()
        }

        val touchListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Remember the initial position.
                    initialX = params.x
                    initialY = params.y

                    // Get the touch location
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val Xdiff = (event.rawX - initialTouchX).toInt()
                    val Ydiff = (event.rawY - initialTouchY).toInt()

                    // The check for Xdiff < 10 && YDiff < 10 because sometime elements move a little while clicking.
                    // So that is a click event.
                    if (Xdiff < 10 && Ydiff < 10) {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("fromwhere", "ser")
                        startActivity(intent)
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    // Calculate the X and Y coordinates of the view.
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()

                    // Update the layout with new X & Y coordinate
                    mWindowManager?.updateViewLayout(mFloatingView, params)
                    true
                }

                else -> false
            }
        }

        mFloatingView?.apply {
            findViewById<View>(R.id.root_container1)?.setOnTouchListener(touchListener)
            findViewById<View>(R.id.root_container2)?.setOnTouchListener(touchListener)
            findViewById<View>(R.id.root_container3)?.setOnTouchListener(touchListener)
            findViewById<View>(R.id.buttonsContainer)?.setOnTouchListener(touchListener)
        }

        mFloatingView?.apply {
            findViewById<View>(R.id.btn30)?.setOnClickListener {
                layoutTimers.visibility = View.GONE
                layoutCounting.visibility = View.VISIBLE
                startCountdown(30)
            }
            findViewById<View>(R.id.btn1)?.setOnClickListener {
                layoutTimers.visibility = View.GONE
                layoutCounting.visibility = View.VISIBLE
                startCountdown(60)
            }
            findViewById<View>(R.id.btn2)?.setOnClickListener {
                layoutTimers.visibility = View.GONE
                layoutCounting.visibility = View.VISIBLE
                startCountdown(60 * 2)
            }
            findViewById<View>(R.id.btn3)?.setOnClickListener {
                layoutTimers.visibility = View.GONE
                layoutCounting.visibility = View.VISIBLE
                startCountdown(60 * 3)
            }
            findViewById<View>(R.id.btn4)?.setOnClickListener {
                layoutTimers.visibility = View.GONE
                layoutCounting.visibility = View.VISIBLE
                startCountdown(60 * 4)
            }
            findViewById<View>(R.id.btn5)?.setOnClickListener {
                layoutTimers.visibility = View.GONE
                layoutCounting.visibility = View.VISIBLE
                startCountdown(60 * 5)
            }
        }


    }

    private fun startCountdown(selectedTimeInSeconds: Int) {

        countDownTimer = object : CountDownTimer((selectedTimeInSeconds * 1000).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60

                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                tvTimer.text = timeFormatted
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                layoutCounting.visibility = View.GONE
                layoutTimers.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager?.removeView(mFloatingView)
    }

}