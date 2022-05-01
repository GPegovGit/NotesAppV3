package com.mirea.artemov.notesappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mirea.artemov.notesappv2.Database.RoomDB;
import com.mirea.artemov.notesappv2.Models.Notes;
import com.mirea.artemov.notesappv2.Models.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editText_title,editText_notes;
    ImageView imageView_save;
    Notes notes;
    boolean isOldNote = false;
    Switch switch_mapPoint;
    public DatabaseReference DB;
    private RoomDB database;
    private String username;
    private String markerPosition;
    public String USER_KEY = "Note";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);
        Log.d("MainActivity", "onCreate");
        imageView_save=findViewById(R.id.imageView_save);
        editText_title=findViewById(R.id.editText_title);
        editText_notes=findViewById(R.id.editText_notes);
        switch_mapPoint=findViewById(R.id.switch_mapPoint);
        DB = FirebaseDatabase.getInstance().getReference(USER_KEY);
        database= RoomDB.getInstance(this);


        username = getIntent().getStringExtra("name");
        markerPosition = getIntent().getStringExtra("markerPosition");
        // устанавливаем аттрибуты
        Log.d("NotesTasker", "Marker position: " + markerPosition);


        notes = (Notes) getIntent().getSerializableExtra("old_note");
        if (notes != null) {
               editText_title.setText(notes.getTitle());
               editText_notes.setText(notes.getNotes());
        }
        isOldNote=true;

//        imageView_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//            }
//        });

//        switch_mapPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b==true){
//                    Intent intent = new Intent(NotesTakerActivity.this, MapsActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    public void invokeMapActivity(View view) {
        Intent intent = new Intent(NotesTakerActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveNote(View view) {
        String title = editText_title.getText().toString();
        String description = editText_notes.getText().toString();

        if(description.isEmpty()){
            Toast.makeText(NotesTakerActivity.this,"Please add some text to the note", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat formatter =new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        //EEE - День недели, d- дата, MMM - месяц, yyyy- год, HH - час, mm- минуты, a - am/pm
        Date date = new Date();

        //Инициализируем здесь, только если заметка старая
        if(notes != null){
            notes=new Notes();
        }
        Log.d(NotesTakerActivity.class.getSimpleName(), "Title" + title
                + "description" + description
                + "username" + username
                + "markerPosition" + markerPosition);
        notes.setTitle(title);
        notes.setNotes(description);
        notes.setDate(formatter.format(date)); // конвертируем объект date в String
        notes.setUsername(username);
        notes.setPosition(markerPosition);
        DB.push().setValue(notes);



        //взаимодействие между различными объектами activity
//                Intent intent = new Intent();
//                intent.putExtra("note", notes); // передается только сериализуемый объект
//                setResult(Activity.RESULT_OK, intent);
        database.mainDao().insert(notes);
        Log.d(NotesTakerActivity.class.getSimpleName(),"Notes:" + database.mainDao().getAll());
        Intent intent = new Intent(NotesTakerActivity.this, MainActivity.class);
        startActivity(intent);
    }
}