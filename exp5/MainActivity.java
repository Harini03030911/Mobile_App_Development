package com.example.validationapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, idEditText;
    private Button validateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        idEditText = findViewById(R.id.idEditText);
        validateButton = findViewById(R.id.validateButton);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        String username = usernameEditText.getText().toString().trim();
        String id = idEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!username.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "Username must contain only alphabets", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!id.matches("\\d{4}")) {
            Toast.makeText(this, "ID must be a 4-digit number", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Validation Successful", Toast.LENGTH_SHORT).show();
    }
}
