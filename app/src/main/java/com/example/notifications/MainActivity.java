package com.example.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import java.util.ArrayList;
import java.util.List;

import static com.example.notifications.App.CHANNEL_1_ID;
import static com.example.notifications.App.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManagerCompat;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private MediaSessionCompat mediaSessionCompat;

    static List<Message> MESSAGES = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextMessage = findViewById(R.id.edit_text_message);
        mediaSessionCompat = new MediaSessionCompat(this, "tag");
        MESSAGES.add(new Message("Good morning!", "Jim"));
        MESSAGES.add(new Message("Hello", null));
        MESSAGES.add(new Message("Hi!", "Jenny"));
    }

    public void sendOnChannel1(View v) {
        sendChannel1Notification(this);
    }

    public static void sendChannel1Notification(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

//        Intent broadcastIntent = new Intent(this, DirectReplyReceiver.class);
//        broadcastIntent.putExtra("toastMessage", message);
//        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
//                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_icon);

        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel("Your answer...")
                .build();

        Intent replyIntent;
        PendingIntent replyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            replyIntent = new Intent(context, DirectReplyReceiver.class);
            replyPendingIntent = PendingIntent.getBroadcast(context,
                    0, replyIntent, 0);
        } else {
            // start chat activity instead (PendingIntent.getActivity)
            // cancel notification with notificationManagerCompat.cancel(id)
        }
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_reply,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle("Me");
        messagingStyle.setConversationTitle("Group Chat");

        for (Message chatMessage : MESSAGES) {
            NotificationCompat.MessagingStyle.Message notificationMessage =
                    new NotificationCompat.MessagingStyle.Message(
                            chatMessage.getText(),
                            chatMessage.getTimestamp(),
                            chatMessage.getSender()
                    );
            messagingStyle.addMessage(notificationMessage);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_looks_one_black_24dp)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setLargeIcon(largeIcon)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Meant to be long")
//                        .setBigContentTitle("Big Content Title")
//                        .setSummaryText("Summary Text"))
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(largeIcon)
//                        .bigLargeIcon(null))
                .setStyle(messagingStyle)
                .addAction(replyAction)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
//                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);
    }

    public void sendOnChannel2(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_looks_two_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(artwork)
                .addAction(R.drawable.ic_dislike, "Dislike", null)
                .addAction(R.drawable.ic_previous, "Previous", null)
                .addAction(R.drawable.ic_pause, "Pause", null)
                .addAction(R.drawable.ic_next, "Next", null)
                .addAction(R.drawable.ic_like, "Like", null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setSubText("Sub Text")
//                .setStyle(new NotificationCompat.InboxStyle()
//                        .addLine("This is line 1")
//                        .addLine("This is line 2")
//                        .addLine("This is line 3")
//                        .addLine("This is line 4")
//                        .addLine("This is line 5")
//                        .addLine("This is line 6")
//                        .addLine("This is line 7")
//                        .setBigContentTitle("Big Content Title")
//                        .setSummaryText("Summary Text"))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManagerCompat.notify(2, notification);
    }
}
