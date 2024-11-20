package com.example.studentapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText editTextName, editTextRollNo, editTextDepartment, editTextAddress, editTextGPA;
    Button buttonInsert, buttonUpdate, buttonDelete, buttonViewAll;
    TableLayout tableLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextRollNo = findViewById(R.id.editTextRollNo);
        editTextDepartment = findViewById(R.id.editTextDepartment);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextGPA = findViewById(R.id.editTextGPA);

        buttonInsert = findViewById(R.id.buttonInsert);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonViewAll = findViewById(R.id.buttonViewAll);

        tableLayout = findViewById(R.id.tableLayout);

        insertStudent();
        updateStudent();
        deleteStudent();
        viewStudents();
    }

    private void insertStudent() {
        buttonInsert.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String department = editTextDepartment.getText().toString();
            String address = editTextAddress.getText().toString();
            float gpa;
            int rollNo;
            try {
                gpa = Float.parseFloat(editTextGPA.getText().toString());
                rollNo = Integer.parseInt(editTextRollNo.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = db.insertStudent(rollNo, name, department, address, gpa);
            Toast.makeText(MainActivity.this, isInserted ? "Student Inserted" : "Insert Failed", Toast.LENGTH_SHORT).show();
        });
    }


    private void updateStudent() {
        buttonUpdate.setOnClickListener(v -> {
            int rollNo;
            try {
                rollNo = Integer.parseInt(editTextRollNo.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid Roll Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!db.doesStudentExist(rollNo)) {
                Toast.makeText(MainActivity.this, "Roll Number Not Found", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = editTextName.getText().toString();
            String department = editTextDepartment.getText().toString();
            String address = editTextAddress.getText().toString();
            float gpa;
            try {
                gpa = Float.parseFloat(editTextGPA.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid GPA", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUpdated = db.updateStudent(rollNo, name, department, address, gpa);
            Toast.makeText(MainActivity.this, isUpdated ? "Student Updated" : "Update Failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteStudent() {
        buttonDelete.setOnClickListener(v -> {
            int rollNo;
            try {
                rollNo = Integer.parseInt(editTextRollNo.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid Roll Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!db.doesStudentExist(rollNo)) {
                Toast.makeText(MainActivity.this, "Roll Number Not Found", Toast.LENGTH_SHORT).show();
                return;
            }

            int deletedRows = db.deleteStudent(rollNo);
            Toast.makeText(MainActivity.this, deletedRows > 0 ? "Student Deleted" : "Delete Failed", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("Range")
    private void viewStudents() {
        buttonViewAll.setOnClickListener(v -> {
            tableLayout.removeAllViews();

            Cursor res = db.getAllStudents();
            if (res.getCount() == 0) {
                Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add header row
            TableRow headerRow = new TableRow(this);
            headerRow.addView(createTextView("Roll No"));
            headerRow.addView(createTextView("Name"));
            headerRow.addView(createTextView("Dept"));
            headerRow.addView(createTextView("Address"));
            headerRow.addView(createTextView("GPA"));
            tableLayout.addView(headerRow);

            // Add data rows
            while (res.moveToNext()) {
                String rollNo = res.getString(res.getColumnIndex("roll_no"));
                Log.d("MainActivity", "Displaying Roll No: " + rollNo); // Log roll number
                TableRow row = new TableRow(this);
                row.addView(createTextView(rollNo));
                row.addView(createTextView(res.getString(res.getColumnIndex("Name"))));
                row.addView(createTextView(res.getString(res.getColumnIndex("department"))));
                row.addView(createTextView(res.getString(res.getColumnIndex("address"))));
                row.addView(createTextView(String.valueOf(res.getFloat(res.getColumnIndex("gpa")))));

                tableLayout.addView(row);
            }
            res.close();
        });
    }


    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
}












