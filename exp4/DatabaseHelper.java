package com.example.studentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Student";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_ROLL_NO = "roll_no";
    private static final String COLUMN_DEPARTMENT = "department";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_GPA = "gpa";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ROLL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DEPARTMENT + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_GPA + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert Student
    public boolean insertStudent(int rollNo, String name, String department, String address, float gpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ROLL_NO, rollNo); // Add roll_no to contentValues
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DEPARTMENT, department);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_GPA, gpa);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }



    // Update Student
    public boolean updateStudent(int rollNo, String name, String department, String address, float gpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DEPARTMENT, department);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_GPA, gpa);

        // Update student with the specified roll number
        int result = db.update(TABLE_NAME, contentValues, COLUMN_ROLL_NO + " = ?", new String[]{String.valueOf(rollNo)});
        return result > 0;
    }


    // Delete Student
    public int deleteStudent(int rollNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ROLL_NO + " = ?", new String[]{String.valueOf(rollNo)});
    }

    // Get All Students
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Student", null);
    }


    // Check if Student Exists
    public boolean doesStudentExist(int rollNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_ROLL_NO + " = ?", new String[]{String.valueOf(rollNo)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}







