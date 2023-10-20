package com.noxapps.dinnerroulette3

import android.Manifest
import android.app.Activity
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Math.log
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.random.Random
import kotlin.time.toKotlinDuration


class NotificationService(private val context: Context) {
    val notificationContent = listOf(
        listOf(
            Pair("Not sure what to make?", "Generate a new recipe now!"),//full rulette
            Pair("Whats for dinner?", "Play Chef Roulette and find out!"),
            Pair("Need some inspiration?", "Play Chef Roulette and get inspired!"),
            Pair("time for something different?", "Get a brand new recipe today")
        ),
        listOf(
            Pair("Something on your mind?", "Get a recipe now!"), //specific request
            Pair("Got a craving?", "Get a recipe with Chef Roulette"),
            Pair("Craving something specific?", "Get a recipe with Chef Roulette"),
            Pair("Not sure how to make something?", "Get the recipe with Chef Roulette")
        ),
        listOf(
            Pair("Got an idea in mind?", "Build a recipe with Chef Roulette"),//classic
            Pair("Need to use up some ingredients?", "Build a recipe with Chef Roulette"),
            Pair("Whats in the pantry?", "Build a recipe with Chef Roulette"),
            Pair("Customise ", "Build a recipe with Chef Roulette")
        ),
        listOf(
            Pair("browse", "browse"),//browse
            Pair("browse", "browse"),
            Pair("browse", "browse"),
            Pair("browse", "browse"),
        )
    )


    fun createReminderNotification() {
        //  No back-stack when launched
        val rand = Random(Date().time)
        val rand1 = rand.nextInt() % 4
        val rand2 = rand.nextInt() % notificationContent[rand1].size
        val destination = when (rand1) {
            0 -> "Home"
            1 -> "SpecificRecipeInput"
            2 -> "NewInput"
            3 -> "Search"
            else -> "Home"
        }

        val intent = Intent(
            Intent.ACTION_MAIN,
            "chefroulette://noximilionapplications.com/page/${destination}".toUri()
        )

        val pending: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        createNotificationChannel(context) // This won't create a new channel everytime, safe to call

        val builder = NotificationCompat.Builder(context, "chef_roulette_notification")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(notificationContent[rand1][rand2].first)//rand%notificationContent.size-1].first)
            .setContentText(notificationContent[rand1][rand2].second)//rand%notificationContent.size-1].second)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pending) // For launching the MainActivity
            .setAutoCancel(true) // Remove notification when tapped
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0
                )
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminders"
            val descriptionText = "This channel sends daily reminders to add your transactions"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel("chef_roulette_notification", name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

class ReminderNotificationWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        NotificationService(appContext).createReminderNotification()
        return Result.success()
    }
    companion object {

        /**
         * @param hourOfDay the hour at which daily reminder notification should appear [0-23]
         * @param minute the minute at which daily reminder notification should appear [0-59]
         */
        fun schedule(appContext: Context, hourOfDay: Int, minute: Int) {
            val now = LocalDateTime.now()
            val target = LocalDateTime.of(
                now.toLocalDate().let {
                    if (now.hour < hourOfDay) it
                    else it.plusDays(1)
                },
                LocalTime.of(hourOfDay, minute)
            )
            val delta = Duration.between(now, target)
            val delay = delta.toKotlinDuration().inWholeSeconds

            val notificationRequest = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                .addTag("reminder_notification_worker_2")
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(appContext)
                .enqueueUniqueWork(
                    "reminder_notification_work",
                    ExistingWorkPolicy.REPLACE,
                    notificationRequest
                )
        }
    }
}