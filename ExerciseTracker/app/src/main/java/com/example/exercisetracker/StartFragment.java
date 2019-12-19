package com.example.exercisetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class StartFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "StartFragment";

    Button startButton;
    TextView recordProgressMessage;

    static MainActivity mainActivity;
    static ProgressDialog stopDialog;

    public StartFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);

        //Set the nav drawer item highlight
        mainActivity = (MainActivity)getActivity();


        //Set actionbar title
        mainActivity.setTitle("Start");

        //Get form text view element and set
        recordProgressMessage = (TextView) view.findViewById(R.id.start_recording_progress);

        //Set onclick listener for save button
        startButton = (Button) view.findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        //Set button state depending on whether recording has been started and/or stopped
        if(MainActivity.dataRecordStarted){
            if(MainActivity.dataRecordCompleted){
                //started and completed: disable button completely
                startButton.setEnabled(false);
                startButton.setText(R.string.start_button_label_stop);
            } else {
                //started and not completed: enable STOP button
                startButton.setEnabled(true);
                startButton.setText(R.string.start_button_label_stop);
            }
        } else {
            //Haven't started: enable START button
            startButton.setEnabled(true);
            startButton.setText(R.string.start_button_label_start);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (!MainActivity.dataRecordStarted){
                //Set recording progress messages
                mainActivity.addFragment(new TrackingFragment(), true);
                MainActivity.dataRecordStarted = true;
                startButton.setText(R.string.start_button_label_stop);


        } else {
            MainActivity.dataRecordCompleted = true;
            startButton.setEnabled(false);
            recordProgressMessage.setText("");
        }
    }

    //Message handler for service
    public static Handler messageHandler = new MessageHandler();

    public static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            int state = message.arg1;
            switch (state) {
                case 0:
                    //Dismiss dialog
                    stopDialog.dismiss();
                    Log.d(TAG, "Stop dialog dismissed");
                    break;

                case 1:
                    //Show stop dialog
                    stopDialog = new ProgressDialog(mainActivity);
                    stopDialog.setTitle("Stopping sensors");
                    stopDialog.setMessage("Please wait...");
                    stopDialog.setProgressNumberFormat(null);
                    stopDialog.setCancelable(false);
                    stopDialog.setMax(100);
                    stopDialog.show();
                    Log.d(TAG, "Stop dialog displayed");
                    break;
            }
        }
    }
}
