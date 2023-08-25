package com.example.agenda.listeners;

import com.example.agenda.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note,int position);

}
