package com.example.qrbarcodescanner; 

import android.content.Intent; 

import android.os.Bundle; 

import android.widget.Button; 

import android.widget.TextView; 

import androidx.annotation.Nullable; 

import androidx.appcompat.app.AppCompatActivity; 

import com.google.zxing.integration.android.IntentIntegrator; 

import com.google.zxing.integration.android.IntentResult; 

public class MainActivity extends AppCompatActivity { 

 private TextView resultTextView; 

 private Button scanButton; 

 @Override 

 protected void onCreate(Bundle savedInstanceState) { 

 super.onCreate(savedInstanceState); 

 setContentView(R.layout.activity_main); 

 resultTextView = findViewById(R.id.resultTextView); 

 scanButton = findViewById(R.id.scanButton); 

 // Set up the button click listener 

 scanButton.setOnClickListener(v -> startScanner()); 

 } 

 private void startScanner() { 

 IntentIntegrator integrator = new IntentIntegrator(this); 

 integrator.setOrientationLocked(true); 

 integrator.setPrompt("Scan a QR Code or Barcode"); 

 integrator.initiateScan(); // Starts the default scan activity without CaptureActivity 

 } 

 @Override 

 protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { 

 super.onActivityResult(requestCode, resultCode, data); 

 IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data); 

 if (result != null) { 

 if (result.getContents() != null) { 

 resultTextView.setText("Result: " + result.getContents()); 

 } else { 

 resultTextView.setText("No result found"); 

 } 

 } else { 

 super.onActivityResult(requestCode, resultCode, data); 

 } 

 } 

}
