package com.example.motiondetectionapplication

import android.app.AlertDialog
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motiondetectionapplication.databinding.FragmentAccelerometerBinding
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.airbnb.lottie.utils.Utils
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*
import android.os.CountDownTimer





class AccelerometerFragment : Fragment() , SensorEventListener
{
    private lateinit var binding: FragmentAccelerometerBinding
    private var sensorManager: SensorManager? = null
    var budge : Boolean = false
    var ax = 0.0
    var ay:kotlin.Double = 0.0
    var az:kotlin.Double = 0.0

    var ax0 = 0.0
    var ay0:kotlin.Double = 0.0
    var az0:kotlin.Double = 0.0

    var targetTime : Long = 60000
    var systemTime : Long = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {binding =  FragmentAccelerometerBinding.inflate(inflater, container, false)
        return binding.root

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAccelerometer()
        addListeners()
        systemTimer()

    }

    fun initAccelerometer()
    {
        sensorManager = this.activity!!.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(arg0: Sensor?, arg1: Int) {}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0].toDouble()
            ay = event.values[1].toDouble()
            az = event.values[2].toDouble()
            var axx = BigDecimal(ax).setScale(2, RoundingMode.HALF_EVEN)
            var ayy = BigDecimal(ay).setScale(2, RoundingMode.HALF_EVEN)
            var azz = BigDecimal(az).setScale(2, RoundingMode.HALF_EVEN)
            if(  ( (ax-ax0) >= 2.0 || (ax-ax0) <= -2.0) || ( (ay-ay0) >= 2.0 || (ay-ay0) <= -2.0)  || ( (az-az0) >= 2.0 || (az-az0) <= -2.0) )
            {
                ax0 = ax
                ay0 = ay
                az0 = az
                budge = true
                Toast.makeText(requireContext(),"hareket algilandi",Toast.LENGTH_LONG).show()

                targetTime=targetTimer()

            }
            else
            {
                if (targetTime == systemTimer())
                {
                    alertMessage(view!!.rootView)
                    targetTime=targetTimer()
                    systemTime=systemTimer()
                    Log.v("closee","close AlertView")
                 //   builder.setCancelable(true)
                   
               

                }
            }
            binding.ax.text = axx.toString()
            binding.ay.text = ayy.toString()
            binding.az.text = azz.toString()

        }
    }

 

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addListeners() {
        binding.button.setOnClickListener { alertMessage(it) }
    }

 @RequiresApi(Build.VERSION_CODES.O)
 private fun update()
 {
     targetTime=targetTimer()
     systemTime=systemTimer()
 }


   @RequiresApi(Build.VERSION_CODES.O)
   private fun alertMessage(view: View)
   {
       var builder = AlertDialog.Builder(requireContext()).create()
       val design: View = layoutInflater.inflate(R.layout.item_alert_design, null)
       builder.setView(design)

       builder.setButton(Dialog.BUTTON_POSITIVE,"YES", DialogInterface.OnClickListener { _,_ ->

       })
       builder.setButton(Dialog.BUTTON_NEGATIVE,"CLOSE", DialogInterface.OnClickListener { _,_ ->

       })
       builder.show()

       val t = Timer()
       t.schedule(object : TimerTask() {
           override fun run() {
               builder.dismiss() // when the task active then close the dialog
               t.cancel() // also just top the timer thread, otherwise, you may receive a crash report
               update()
           }
       }, 5000)


       }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun targetTimer() : Long
    {
        val zero = LocalTime.parse("00:00:00")
        var sdf = SimpleDateFormat("hh:mm:ss")
        var currentDate = sdf.format(Date())
        val time = LocalTime.parse(currentDate)
        Log.v("timess","time : currentDate : $currentDate")

        val mils: Long = ChronoUnit.MILLIS.between(zero, time)
        targetTime = mils + (60000).toLong()
        Log.v("timess","mils : currentDate : $mils")
        Log.v("timess","mils : targetTime : $targetTime")
        return targetTime

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private  fun systemTimer() : Long
    {
        val zero = LocalTime.parse("00:00:00")
        var sdf = SimpleDateFormat("hh:mm:ss")
        var currentDate = sdf.format(Date())
        Log.v("systemTimer","systemTimer : currentDate : $currentDate")
        val time = LocalTime.parse(currentDate)
        val systemTimer: Long = ChronoUnit.MILLIS.between(zero, time)
        Log.v("systemTimer","systemTimer : currentDate : $systemTimer")
        systemTime = systemTimer
        return systemTime

    }



   }

