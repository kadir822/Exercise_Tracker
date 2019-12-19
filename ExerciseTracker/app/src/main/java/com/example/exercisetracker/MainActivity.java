package com.example.exercisetracker;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    SensorManager sensorManager;

    public static Boolean dataRecordStarted;
    public static Boolean dataRecordCompleted;

    public final static short TYPE_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    public final static short TYPE_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    public final static short TYPE_GRAVITY = Sensor.TYPE_GRAVITY;
    public final static short TYPE_MAGNETIC = Sensor.TYPE_MAGNETIC_FIELD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        addFragment(new StartFragment(), true);

        //UI


        // flags
        dataRecordStarted = false;
        dataRecordCompleted = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //TODO confirm closing app

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFragment(Fragment fragment, Boolean addToBackStack){
        if(fragment!=null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //Check getFragements() == null to prevent initial blank
            // fragment (before "New" fragment is displayed) from being added to the backstack
            if (fragmentManager.getFragments() == null || !addToBackStack){
                fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commit();
            }
            else{
                fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
            }
        }
    }

    public String getSensorAvailable(short sensor_type){
        Sensor curSensor = sensorManager.getDefaultSensor(sensor_type);
        if (curSensor != null){
            return("YES " + "(" + curSensor.getVendor() + ")");
        }
        else{
            return("NO");
        }
    }
}
