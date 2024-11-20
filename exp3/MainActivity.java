package com.example.textstylechanger;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Spinner spinnerFont;
    private Spinner spinnerColor;
    private Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        textView = findViewById(R.id.textView);
        spinnerFont = findViewById(R.id.spinnerFont);
        spinnerColor = findViewById(R.id.spinnerColor);
        btnChange = findViewById(R.id.btnChange);

        // Set up font spinner
        ArrayAdapter<CharSequence> fontAdapter = ArrayAdapter.createFromResource(this,
                R.array.fonts_array, android.R.layout.simple_spinner_item);
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFont.setAdapter(fontAdapter);

        // Set up color spinner
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.colors_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);

        // Set button click listener
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get selected font
                String selectedFont = spinnerFont.getSelectedItem().toString();

                // Change font based on selection
                switch (selectedFont) {
                    case "Sans Serif":
                        textView.setTypeface(Typeface.SANS_SERIF);
                        break;
                    case "Serif":
                        textView.setTypeface(Typeface.SERIF);
                        break;
                    case "Monospace":
                        textView.setTypeface(Typeface.MONOSPACE);
                        break;
                }

                // Get selected color
                String selectedColor = spinnerColor.getSelectedItem().toString();

                // Change text color based on selection
                switch (selectedColor) {
                    case "Red":
                        textView.setTextColor(getResources().getColor(R.color.red));
                        break;
                    case "Green":
                        textView.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case "Blue":
                        textView.setTextColor(getResources().getColor(R.color.blue));
                        break;
                    case "Teal 200":
                        textView.setTextColor(getResources().getColor(R.color.teal_200));
                        break;
                    case "White":
                        textView.setTextColor(getResources().getColor(R.color.white));
                        break;
                }

                // Display toast message
                Toast.makeText(MainActivity.this, "Font and color changed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
