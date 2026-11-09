package com.karoke.myoyasing

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.gson.Gson

import com.karoke.myoyasing.presentation.navigation.routes.AppNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppNavGraph()
        }

        val gson = Gson()

//        val originalMelody = applicationContext.resources
//            .openRawResource(R.raw.orignal)
//            .bufferedReader()
//            .use {
//                gson.fromJson(
//                    it,
//                    KaraokeReferenceData::class.java
//                )
//            }
//
//        val recordedMelody = applicationContext.resources
//            .openRawResource(R.raw.recorded)
//            .bufferedReader()
//            .use {
//                gson.fromJson(
//                    it,
//                    KaraokeReferenceData::class.java
//                )
//            }
//
//        val result = KaraokeMelodyScorerNew.calculateScore(
//            original = originalMelody,
//            recorded = recordedMelody,
//            timeOffsetSeconds = 0.0
//        )

//        Log.d("KaraokeScore", "Total: ${result.totalScore}")
//        Log.d("KaraokeScore", "Pitch: ${result.pitchAccuracy}")
//        Log.d("KaraokeScore", "Coverage: ${result.coverage}")
//        Log.d("KaraokeScore", "Matched: ${result.matchedFrames}")
//        Log.d("KaraokeScore", "Missed: ${result.missedFrames}")
    }
}