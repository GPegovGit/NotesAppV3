package com.mirea.artemov.notesappv2;

import androidx.cardview.widget.CardView;

import com.mirea.artemov.notesappv2.Models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
