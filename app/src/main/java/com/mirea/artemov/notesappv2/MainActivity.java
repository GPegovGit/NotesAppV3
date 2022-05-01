package com.mirea.artemov.notesappv2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.RoomDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mirea.artemov.notesappv2.Adapters.NotesListAdapter;
import com.mirea.artemov.notesappv2.Database.RoomDB;
import com.mirea.artemov.notesappv2.Models.Notes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    //SearchView searchView_home;
    Notes selectedNote;
    Button button_activityLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler_home);
        fab_add=findViewById(R.id.fab_add);
        //searchView_home=findViewById(R.id.searchView_home);
        button_activityLogin=findViewById(R.id.button_activityLogin);

        database=RoomDB.getInstance(this);
        notes=database.mainDao().getAll();
        updateRecycler(notes);


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String username = getIntent().getStringExtra("user");
                Intent name =new Intent(MainActivity.this, NotesTakerActivity.class);
                name.putExtra("name", username);
                startActivity(name);
            }
        });

        button_activityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(MainActivity.class.getSimpleName(), "Result code: " +resultCode + "Request code " + requestCode);
        //Добавляем заметку в базу данных
        if(requestCode==101){
            if(resultCode == Activity.RESULT_OK){
                Notes new_notes= (Notes) data.getSerializableExtra("note");
                database.mainDao().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDao().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
        else if (requestCode==102){
            if(resultCode==Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDao().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDao().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)); //количество столбцов заметок
        notesListAdapter= new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
             Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
             intent.putExtra("old_note", notes);
             startActivityForResult(intent, 102);

        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            Log.d("MainActivity", "onLongClick" + notes);
            selectedNote=new Notes();
            selectedNote=notes;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.pin:
                if(selectedNote.isPinned()){
                    database.mainDao().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "Unpinnned!", Toast.LENGTH_SHORT).show();
                }
                else{
                    database.mainDao().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "Pinned!", Toast.LENGTH_SHORT).show();
                }

                notes.clear();
                notes.addAll(database.mainDao().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;

            case R.id.delete:
                database.mainDao().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        notes.clear();
        notes.addAll(database.mainDao().getAll());
        notesListAdapter.notifyDataSetChanged();

    }
}