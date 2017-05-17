package com.ferit.dfundak.georeminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class AddNewItem extends AppCompatActivity {

    private LinearLayout addTime;
    private LinearLayout addLocation;
    private LinearLayout addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        addImage = (LinearLayout) findViewById(R.id.add_image);
        addLocation = (LinearLayout) findViewById(R.id.add_location);
        addTime = (LinearLayout) findViewById(R.id.add_time);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                Intent intent = new Intent(AddNewItem.this, LocationActivity.class);
                startActivity(intent);
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();

            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();

            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void checkPermissions() {


    }
}
