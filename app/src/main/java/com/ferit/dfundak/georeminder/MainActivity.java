package com.ferit.dfundak.georeminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //UI
    private FloatingActionButton addButton;
    private ListView reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (FloatingActionButton) findViewById(R.id.add_button);
        reminderList = (ListView) this.findViewById(R.id.remindersLV);

        ArrayList<reminderItem> reminders = this.loadReminders();
        ReminderAdapter reminderAdapter = new ReminderAdapter(reminders);
        this.reminderList.setAdapter(reminderAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewItem.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<reminderItem> reminders = this.loadReminders();
        ReminderAdapter reminderAdapter = new ReminderAdapter(reminders);
        this.reminderList.setAdapter(reminderAdapter);
    }

    private ArrayList<reminderItem> loadReminders() {
        return DatabaseHandler.getInstance(this).getAllReminders();
    }


}
