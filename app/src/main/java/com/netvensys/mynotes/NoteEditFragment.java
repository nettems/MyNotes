package com.netvensys.mynotes;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteEditFragment extends Fragment {


    private ImageButton noteCatButton;
    private Note.Category saveButtonCategory;
    private AlertDialog categoryDialogObject, confirmDialogObject;
    private EditText title, message;

    private Note.Category savedButtonCategory;
    private static final String MODIFIED_CATEGORY = "Modified Category";

    private boolean newNote = false;

    private long noteID = 0;

    //private Context;

    public NoteEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        if(bundle != null) {
        newNote = bundle.getBoolean(NoteDetailActivity.NEW_NOTE_EXTRA, false);
        }

        if(savedInstanceState != null) {
            savedButtonCategory = (Note.Category)savedInstanceState.get(MODIFIED_CATEGORY);
        }
        //Inflate our fragment edit layout
        View fragmentLayout = inflater.inflate(R.layout.fragment_note_edit, container, false);

        //Grab widget references from layout
        title = (EditText)fragmentLayout.findViewById(R.id.editNoteTitle);
        message = (EditText)fragmentLayout.findViewById(R.id.editNoteMessage);
        noteCatButton = (ImageButton)fragmentLayout.findViewById(R.id.editNoteButton);
        Button savedButton = (Button)fragmentLayout.findViewById(R.id.saveNote);

        //Populate widgets with note data.
        Intent intent = getActivity().getIntent();
        title.setText(intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA));
        message.setText(intent.getExtras().getString(MainActivity.NOTE_MESSAGE_EXTRA));
        noteID = intent.getExtras().getLong(MainActivity.NOTE_ID_EXTRA, 0);

        Context context = getContext();
        Resources res = context.getResources();
        LetterTileProvider tileProvider = new LetterTileProvider(context);

        if(savedButtonCategory != null)
        {
            //noteCatButton.setImageResource(Note.categoryToDrwawable(savedButtonCategory));
            noteCatButton.setImageBitmap(Note.categoryToDrwawable(res, tileProvider, savedButtonCategory));
            //Toast.makeText(getActivity().getApplicationContext(), savedButtonCategory.name(), Toast.LENGTH_SHORT).show();
        }
        else if (!newNote)
        {
            Note.Category noteCat = (Note.Category) intent.getSerializableExtra(MainActivity.NOTE_CATEGORY_EXTRA);
            saveButtonCategory = noteCat;
            //noteCatButton.setImageResource(Note.categoryToDrwawable(noteCat));
            noteCatButton.setImageBitmap(Note.categoryToDrwawable(res, tileProvider, noteCat));
        }


        buildCategoryDialog();
        buildConfirmDialog();

        noteCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialogObject.show();
            }
        });

        savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogObject.show();
            }
        });

        return fragmentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(MODIFIED_CATEGORY, saveButtonCategory);
    }

    private void buildCategoryDialog() {
        final String[] categories = new String[]{"Personal", "Technical", "Quote", "Finance"};
        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(getActivity());
        categoryBuilder.setTitle("Choose Note Type");

        Context context = getContext();
        Resources res = context.getResources();
        LetterTileProvider tileProvider = new LetterTileProvider(context);

        categoryBuilder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                try{
                categoryDialogObject.cancel();
                Context context = getContext();
                Resources res = context.getResources();
                LetterTileProvider tileProvider = new LetterTileProvider(context);
                switch (item) {
                    case 0:
                        saveButtonCategory = Note.Category.PERSONAL;
                        //noteCatButton.setImageResource(R.drawable.g);
                        noteCatButton.setImageBitmap(Note.categoryToDrwawable(res, tileProvider, "PERSONAL"));
                        break;
                    case 1:
                        saveButtonCategory = Note.Category.TECHNICAL;
                        //noteCatButton.setImageResource(R.drawable.g);
                        noteCatButton.setImageBitmap(Note.categoryToDrwawable(res, tileProvider, "TECHNICAL"));
                        break;
                    case 2:
                        saveButtonCategory = Note.Category.QUOTE;
                        //noteCatButton.setImageResource(R.drawable.g);
                        noteCatButton.setImageBitmap(Note.categoryToDrwawable(res, tileProvider, "QUOTE"));
                        break;
                    case 3:
                        saveButtonCategory = Note.Category.FINANCE;
                        //noteCatButton.setImageResource(R.drawable.g);
                        noteCatButton.setImageBitmap(Note.categoryToDrwawable(res, tileProvider, "FINANCE"));
                        break;
                }
            }
                catch (Exception e)
                {
                    Log.d("Error: ", e.getMessage());
                }
            }
        });

        categoryDialogObject = categoryBuilder.create();
    }

    private void buildConfirmDialog() {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle("Are you sure?");
        confirmBuilder.setMessage("Are you sure you want to save the note?");

        confirmBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.d("Save Note", "Note Title: " + title.getText() + " Note Message: " +
                        message.getText() + " Note Category: " + saveButtonCategory);

                if (title.getText().toString().trim().length() == 0) {
                    title.setError("Enter Title");
                } else if (message.getText().toString().trim().length() == 0) {
                    message.setError("Enter Message");
                } else {
                    NotesDBAdaptor dbAdaptor = new NotesDBAdaptor(getActivity().getBaseContext());
                    dbAdaptor.open();
                    if (newNote) {
                        dbAdaptor.createNote(title.getText() + "", message.getText() + "", (saveButtonCategory == null) ? Note.Category.PERSONAL : saveButtonCategory);
                    } else {
                        dbAdaptor.updateNote(noteID, title.getText() + "", message.getText() + "", saveButtonCategory);
                    }
                    dbAdaptor.close();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //Do nothing here.
            }
        });

        confirmDialogObject = confirmBuilder.create();
    }

}
