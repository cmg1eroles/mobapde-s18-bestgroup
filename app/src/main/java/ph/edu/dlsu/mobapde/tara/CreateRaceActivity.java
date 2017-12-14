package ph.edu.dlsu.mobapde.tara;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateRaceActivity extends AppCompatActivity {
    EditText etTitle;

    LinearLayout llDate;
    LinearLayout llTime;

    Button buttonLocation;
    Button buttonDate;
    Button buttonTime;
    Button buttonCreateMeeting;

    int LOCATION_REQUEST = 0; // location request code for intent

    static int chosenMonth, chosenDay, chosenYear;
    static int chosenHour, chosenMinute;
    static String sdate, stime;

    SimpleDateFormat sdfDate;

    // attributes for race being created
    Race currentRace;
    String currentTitle;
    Date currentDate;
    Place currentPlace;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_race);

        // ADDED TOOLBAR
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // ADDED TOOLBAR END

        etTitle = (EditText) findViewById(R.id.et_title);

        llDate = (LinearLayout) findViewById(R.id.ll_pickdate);
        llTime = (LinearLayout) findViewById(R.id.ll_picktime);

        buttonLocation = (Button) findViewById(R.id.button_location);
        buttonDate = (Button) findViewById(R.id.button_date);
        buttonTime =(Button) findViewById(R.id.button_time);
        buttonCreateMeeting = (Button) findViewById(R.id.button_createmeeting);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationClick();
            }
        });

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateClick();
            }
        });

        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeClick();
            }
        });

        buttonCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onCreateRaceButtonClick();
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onCreateRaceButtonClick() throws ParseException {
        String currMonth, currDay;

        if(chosenDay < 10) {
            currDay = "0" + chosenDay;
        } else {
            currDay = "" + chosenDay;
        }

        if(chosenMonth < 10) {
            currMonth = "0" + chosenMonth;
        } else {
            currMonth = "" + chosenMonth;
        }

        sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDate = sdfDate.parse(chosenYear + "-" + (chosenMonth + 1) + "-" + chosenDay
                    + " " + chosenHour + ":" + chosenMinute + ":59");

        currentTitle = etTitle.getText().toString();

        String placeName = currentPlace.getName().toString();

        currentRace = new Race(currentTitle, new LatLng(1.0, 4.0), currentDate, placeName);

        db = FirebaseDatabase.getInstance().getReference();

        String key = db.child("races").push().getKey();
        db.child("races").child(key).setValue(currentRace.toMap());

        Toast t = Toast.makeText(getBaseContext(), "Created: " + currentRace.toString(), Toast.LENGTH_LONG);
        t.show();

        Intent i = new Intent(CreateRaceActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public void onLocationClick() {
        PlacePicker.IntentBuilder ppBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent i = ppBuilder.build(this);
            startActivityForResult(i, LOCATION_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onDateClick() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        chosenDay = dayOfMonth;
                        chosenMonth = monthOfYear;
                        chosenYear = year;

                        buttonDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void onTimeClick() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        chosenHour = hourOfDay;
                        chosenMinute = minute;

                        buttonTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    // accepts and processes results from intent
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCATION_REQUEST) {
            if(resultCode == RESULT_OK) {
                currentPlace = PlacePicker.getPlace(getBaseContext(), data);
                String address = currentPlace.getName().toString();
                buttonLocation.setText(address);
            }
        }
    }

    // ADDED TOOLBAR BACK OPTIONS
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();

        return true;
    }
}
