package com.neelraja.assaultprevention;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by saarthaksharma on 3/25/18.
 */

public class NotificationService extends Service {

    final String TAG = "Notification Service";
    String android_id ;
    private FirebaseDatabase database;

    public IBinder onBind(Intent arg){
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    public void onCreate(){
        android_id = Settings.Secure.getString(NotificationService.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        database = FirebaseDatabase.getInstance();

        DatabaseReference contact = database.getReference("users/"+android_id+"/contact");
        contact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String contactid = dataSnapshot.getValue(String.class);
                // Toast.makeText(getApplicationContext(), "Latitude: " + value, Toast.LENGTH_SHORT).show();

                DatabaseReference contactlongitude = database.getReference("users/" +contactid+"/long");
                DatabaseReference contactlatitude = database.getReference("users/" +contactid+"/long");

                Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
                notificationIntent.setData(Uri.parse("http://www.google.com/maps/search/?api=1&query=" + contactlatitude + "," + contactlongitude));
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0, notificationIntent, 0 );
                Notification notification = new NotificationCompat.Builder(getApplicationContext()).setTicker("").setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle("Panic").setContentText("Someone in your area needs help.").setContentIntent(pi).setAutoCancel(false).build();

                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, notification);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                //Toast.makeText(getApplicationContext(), "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onDestroy(){

    }


}
