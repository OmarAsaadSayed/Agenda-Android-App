package com.example.agenda.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda.R;
import com.example.agenda.database.NotesDatabase;
import com.example.agenda.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.xml.transform.Result;

public class Note_Edit extends AppCompatActivity {
    Button backBtn;
    private EditText notetitle, notesubtitle, notetext;
    private TextView notedate;
    private String selectedNoteColor;
    private ImageView imagenote;
    private TextView textWebUrl;
    private LinearLayout layoutWebUrl;
    private String selectedImagePath;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private AlertDialog dialogAddUrl;

    private AlertDialog dialogDeleteNote;

    private Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        notetitle = findViewById(R.id.NoteTitle);
        notesubtitle = findViewById(R.id.NoteSub);
        notetext = findViewById(R.id.NoteText);
        notedate = findViewById(R.id.dateofnote);
        imagenote = findViewById(R.id.imgnote);
        textWebUrl = findViewById(R.id.textweburl);
        layoutWebUrl = findViewById(R.id.layoutweburl);


        notedate.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );
        Button saveBtn = findViewById(R.id.savebtn);
        saveBtn.setOnClickListener(v -> {
            saveNote();
        });
        selectedNoteColor = "#653E3450";
        selectedImagePath = "";

        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        initMiscellaneous();
    }

    private void setViewOrUpdateNote(){
        notetitle.setText(alreadyAvailableNote.getTitle());
        notesubtitle.setText(alreadyAvailableNote.getSubtitle());
        notetext.setText(alreadyAvailableNote.getNoteText());
        notedate.setText(alreadyAvailableNote.getDateTime());

        if(alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
            imagenote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imagenote.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }

        if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
            textWebUrl.setText(alreadyAvailableNote.getWebLink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }
    }

    private void saveNote(){
        if(notetitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note title can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }else if(notesubtitle.getText().toString().trim().isEmpty()
                && notetext.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(notetitle.getText().toString());
        note.setSubtitle(notesubtitle.getText().toString());
        note.setNoteText(notetext.getText().toString());
        note.setDateTime(notedate.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);

        if(layoutWebUrl.getVisibility() == View.VISIBLE){
            note.setWebLink(textWebUrl.getText().toString());
        }

        if(alreadyAvailableNote != null){
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("staticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }
    private void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.editLayout);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.editbartext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        final ImageView imagecolor1 = layoutMiscellaneous.findViewById(R.id.imagecolor1);
        final ImageView imagecolor2 = layoutMiscellaneous.findViewById(R.id.imagecolor2);
        final ImageView imagecolor3 = layoutMiscellaneous.findViewById(R.id.imagecolor3);
        final ImageView imagecolor4 = layoutMiscellaneous.findViewById(R.id.imagecolor4);
        final ImageView imagecolor5 = layoutMiscellaneous.findViewById(R.id.imagecolor5);

        layoutMiscellaneous.findViewById(R.id.viewcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#653E3450";
                imagecolor1.setImageResource(R.drawable.ic_done);
                imagecolor2.setImageResource(0);
                imagecolor3.setImageResource(0);
                imagecolor4.setImageResource(0);
                imagecolor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFC900";
                imagecolor1.setImageResource(0);
                imagecolor2.setImageResource(R.drawable.ic_done);
                imagecolor3.setImageResource(0);
                imagecolor4.setImageResource(0);
                imagecolor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FF0000";
                imagecolor1.setImageResource(0);
                imagecolor2.setImageResource(0);
                imagecolor3.setImageResource(R.drawable.ic_done);
                imagecolor4.setImageResource(0);
                imagecolor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#EDD2F3";
                imagecolor1.setImageResource(0);
                imagecolor2.setImageResource(0);
                imagecolor3.setImageResource(0);
                imagecolor4.setImageResource(R.drawable.ic_done);
                imagecolor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#292C6D";
                imagecolor1.setImageResource(0);
                imagecolor2.setImageResource(0);
                imagecolor3.setImageResource(0);
                imagecolor4.setImageResource(0);
                imagecolor5.setImageResource(R.drawable.ic_done);
            }
        });

        if(alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#FFC900":
                    layoutMiscellaneous.findViewById(R.id.viewcolor2).performClick();
                    break;
                case "#FF5959":
                    layoutMiscellaneous.findViewById(R.id.viewcolor3).performClick();
                    break;
                case "#EDD2F3":
                    layoutMiscellaneous.findViewById(R.id.viewcolor4).performClick();
                    break;
                case "#292C6D":
                    layoutMiscellaneous.findViewById(R.id.viewcolor5).performClick();
                    break;

            }
        }

        layoutMiscellaneous.findViewById(R.id.layoutaddimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            Note_Edit.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else{
                    selectImg();
                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutaddurl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddUrlDialog();
            }
        });

        if(alreadyAvailableNote !=null){
            layoutMiscellaneous.findViewById(R.id.layoutdeletenote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutdeletenote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }

    private void showDeleteNoteDialog(){
        if (dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(Note_Edit.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutaddurlcontainer)
            );

            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textdeletenote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }

                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textcancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImg(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImg();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selecedImageUri = data.getData();
                if(selecedImageUri != null){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(selecedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imagenote.setImageBitmap(bitmap);
                        imagenote.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selecedImageUri);
                    }catch (Exception exception){
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null,null,null,null);
        if (cursor == null){
            filePath = contentUri.getPath();
        } else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showAddUrlDialog(){
        if(dialogAddUrl == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(Note_Edit.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutaddurlcontainer)
            );
            builder.setView(view);
            dialogAddUrl = builder.create();
            if(dialogAddUrl.getWindow() != null){
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputUrl = view.findViewById(R.id.inputurl);
            inputUrl.requestFocus();
            view.findViewById(R.id.textadd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputUrl.getText().toString().trim().isEmpty()){
                        Toast.makeText(Note_Edit.this,"Enter Url",Toast.LENGTH_SHORT).show();
                    } else if(!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()){
                        Toast.makeText(Note_Edit.this,"Enter Valid Url",Toast.LENGTH_SHORT).show();
                    }else{
                        textWebUrl.setText(inputUrl.getText().toString());
                        layoutWebUrl.setVisibility(view.VISIBLE);
                        dialogAddUrl.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textcancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddUrl.dismiss();
                }
            });
        }
        dialogAddUrl.show();
    }
}