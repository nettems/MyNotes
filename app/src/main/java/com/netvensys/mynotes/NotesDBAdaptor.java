package com.netvensys.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Srinivasa.Nettem on 2/13/2016.
 */
public class NotesDBAdaptor {

    private static final String DATABASE_NAME = "notes,db";
    private static final int DATABASE_VERSION = 9;

    private static final String NOTE_TABLE = "note";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_MESSAGE = "mesage";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ALARM_DATETIME = "alarm";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_REMIND = "remind";
    private static final String COLUMN_UPDATED_DATE = "updated_date";
    private static final String COLUMN_ALARM_END_DATETIME = "alarm_end";

    private String[] allColumns = {COLUMN_ID, COLUMN_TITLE, COLUMN_MESSAGE, COLUMN_CATEGORY, COLUMN_DATE, COLUMN_ALARM_DATETIME, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_UPDATED_DATE, COLUMN_REMIND, COLUMN_ALARM_END_DATETIME};

    private static final String CREATE_TABLE_NOTE = "CREATE TABLE " + NOTE_TABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_MESSAGE + " TEXT NOT NULL, "
            + COLUMN_CATEGORY + " TEXT NOT NULL, "
            + COLUMN_DATE + ", "
            + COLUMN_ALARM_DATETIME + ", "
            + COLUMN_UPDATED_DATE + ", "
            + COLUMN_LATITUDE  + " TEXT "
            + COLUMN_LONGITUDE + " TEXT "
            + COLUMN_REMIND + " INTEGER DEFAULT 0 "
            + COLUMN_ALARM_END_DATETIME + " ); ";


    private static final String DATABASE_ALTER_ADD_LONGITUDE = "ALTER TABLE " + NOTE_TABLE + " ADD COLUMN " + COLUMN_LONGITUDE + " TEST;";
    private static final String DATABASE_ALTER_ADD_REMIND = "ALTER TABLE " + NOTE_TABLE + " ADD COLUMN " + COLUMN_REMIND + " INTEGER DEFAULT 0;";

    private static final String DATABASE_ALARM_END_DATETIME = "ALTER TABLE " + NOTE_TABLE + " ADD COLUMN " + COLUMN_ALARM_END_DATETIME + ";";

    private SQLiteDatabase sqlDB;
    private Context context;

    private NoteDBHelper noteDBHelper;

    public NotesDBAdaptor(Context ctx){
        context = ctx;
    }

    public NotesDBAdaptor open() throws android.database.SQLException{
        noteDBHelper = new NoteDBHelper(context);
        sqlDB = noteDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        noteDBHelper.close();
    }

    public Note createNote(String title, String message, Note.Category category){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_MESSAGE, message);
        contentValues.put(COLUMN_CATEGORY, category.name());
        contentValues.put(COLUMN_DATE, Calendar.getInstance().getTimeInMillis() + "");
        contentValues.put(COLUMN_UPDATED_DATE, Calendar.getInstance().getTimeInMillis() + "");

        long insertID = sqlDB.insert(NOTE_TABLE, null, contentValues);

        Cursor cursor = sqlDB.query(NOTE_TABLE, allColumns, COLUMN_ID + " = " + insertID, null, null, null, null, null);
        cursor.moveToFirst();

        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }

    public long updateNote(long idToUpdate, String newTitle, String newMessage, Note.Category newCategory){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, newTitle);
        contentValues.put(COLUMN_MESSAGE, newMessage);
        contentValues.put(COLUMN_CATEGORY, newCategory.name());
        contentValues.put(COLUMN_UPDATED_DATE, Calendar.getInstance().getTimeInMillis() + "");

        return sqlDB.update(NOTE_TABLE, contentValues, COLUMN_ID + " = " + idToUpdate, null);
    }

    public long updateNote(long idToUpdate, String alarmStartDateTime, String alarmEndDateTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ALARM_DATETIME, alarmStartDateTime);
        contentValues.put(COLUMN_ALARM_END_DATETIME, alarmEndDateTime);
        contentValues.put(COLUMN_UPDATED_DATE, Calendar.getInstance().getTimeInMillis() + "");

        return sqlDB.update(NOTE_TABLE, contentValues, COLUMN_ID + " = " + idToUpdate, null);
    }

    public long updateNote(long idToUpdate, double alarmLatitude, double alarmLongitude){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDE, alarmLatitude + "");
        contentValues.put(COLUMN_LONGITUDE, alarmLongitude + "");
        contentValues.put(COLUMN_UPDATED_DATE, Calendar.getInstance().getTimeInMillis() + "");

        return sqlDB.update(NOTE_TABLE, contentValues, COLUMN_ID + " = " + idToUpdate, null);
    }

    public long deleteNote(long idToDelete){
        return sqlDB.delete(NOTE_TABLE, COLUMN_ID + " = " + idToDelete, null);
    }

    public Note getNote(String noteId) {

        Cursor cursor = sqlDB.query(NOTE_TABLE, allColumns, COLUMN_ID + "=?",
                new String[] { noteId }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(cursor.getString(1), cursor.getString(2), Note.Category.valueOf(cursor.getString(3)),
                cursor.getLong(0), cursor.getLong(4), cursor.getLong(5), cursor.getLong(9), cursor.getDouble(6), cursor.getDouble(7), (cursor.getInt(8) == 1)? true : false);
        // return contact
        return note;
    }

    public ArrayList<Note> getAllNotes(){
        ArrayList<Note> notes = new ArrayList<Note>();
        Cursor cursor = sqlDB.query(NOTE_TABLE, allColumns, null, null, null, null, null, null);
        for(cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()){
            Note note = cursorToNote(cursor);
            notes.add(note);
        }
        cursor.close();

        return notes;
    }

    private Note cursorToNote(Cursor cursor){
        Note newNote;

        newNote = new Note(cursor.getString(1), cursor.getString(2), Note.Category.valueOf(cursor.getString(3)),
                cursor.getLong(0), cursor.getLong(4), cursor.getLong(5), cursor.getLong(9), cursor.getDouble(6), cursor.getDouble(7), (cursor.getInt(8) == 1)? true : false);
        return newNote;
    }


    private static class NoteDBHelper extends SQLiteOpenHelper{

        NoteDBHelper(Context ctx){
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_TABLE_NOTE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //db.execSQL("DROP TABLE IF EXISTS" + NOTE_TABLE);
            //onCreate(db);
            if(oldVersion < 8) {
                db.execSQL(DATABASE_ALTER_ADD_LONGITUDE);
                db.execSQL(DATABASE_ALTER_ADD_REMIND);
            }
            if(oldVersion < 9) {
                db.execSQL(DATABASE_ALARM_END_DATETIME);
            }
        }
    }

}
