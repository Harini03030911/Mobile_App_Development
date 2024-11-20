package com.example.traffic; 

import android.content.Intent; 

import android.graphics.Bitmap; 

import android.graphics.BitmapFactory; 

import android.os.Bundle; 

import android.util.Log; 

import android.view.View; 

import android.widget.Button; 

import android.widget.ImageView; 

import android.widget.Toast; 

import androidx.annotation.Nullable;import androidx.appcompat.app.AppCompatActivity; 

import com.example.traffic.ml.TrafficSignModel; 

import org.tensorflow.lite.DataType; 

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer; 

import java.io.IOException; 

import java.io.InputStream; 

import java.nio.ByteBuffer; 

import java.nio.ByteOrder; 

public class MainActivity extends AppCompatActivity { 

 private static final int SELECT_PICTURE = 1; 

 private TrafficSignModel model; 

 private ImageView imageView; 

 private Bitmap selectedImage; 

 @Override 

 protected void onCreate(Bundle savedInstanceState) { 

 super.onCreate(savedInstanceState); 

 setContentView(R.layout.activity_main); 

 imageView = findViewById(R.id.imageView); 

 try { 

 model = TrafficSignModel.newInstance(this); 

 } catch (IOException e) { 

 Log.e("MainActivity", "Error initializing TensorFlow Lite model.", e); 

 Toast.makeText(this, "Model initialization failed.", Toast.LENGTH_SHORT).show(); 

 } 

 // Button to select an image 

 Button selectImageButton = findViewById(R.id.uploadButton); 

 selectImageButton.setOnClickListener(v -> openImageChooser()); 

 // Button to classify the selected image 

 Button classifyButton = findViewById(R.id.classify_button); 

 classifyButton.setOnClickListener(v -> { 

 if (selectedImage != null && model != null) { 

 classifyImage(selectedImage); 

 } else { 

 Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show(); 

 } 

 }); 

 } 

 private void openImageChooser() { 

 Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 

 intent.setType("image/*"); 

 startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE); 

 } 

 @Override 

 protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { 

 super.onActivityResult(requestCode, resultCode, data); 

 if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && 

data.getData() != null) { 

 try { 

 InputStream inputStream = getContentResolver().openInputStream(data.getData()); 

 selectedImage = BitmapFactory.decodeStream(inputStream); 

 imageView.setImageBitmap(selectedImage); 

 } catch (IOException e) { 

 e.printStackTrace(); 

 } 

 } 

 } 

 private void classifyImage(Bitmap image) { 

 Bitmap resizedImage = Bitmap.createScaledBitmap(image, 50, 50, true); 

 ByteBuffer byteBuffer = convertBitmapToByteBuffer(resizedImage); 

 // Create input tensor 

 TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 50, 50, 3}, 

DataType.FLOAT32); 

 inputFeature0.loadBuffer(byteBuffer); 

 try { 

 // Run inference 

 TrafficSignModel.Outputs outputs = model.process(inputFeature0); 

 TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer(); 

 displayResults(outputFeature0); 

 } catch (Exception e) { 

 Log.e("MainActivity", "Error during model inference", e); 

 Toast.makeText(this, "Error running inference", Toast.LENGTH_SHORT).show(); 

 } 

 } 

 private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) { 

 ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 50 * 50 * 3); 

 byteBuffer.order(ByteOrder.nativeOrder()); 

 int[] pixels = new int[50 * 50];bitmap.getPixels(pixels, 0, 50, 0, 0, 50, 50); 

 for (int pixel : pixels) { 

 int r = (pixel >> 16) & 0xFF; 

 int g = (pixel >> 8) & 0xFF; 

 int b = pixel & 0xFF; 

 // Normalize pixel values to [0, 1] and add to buffer 

 byteBuffer.putFloat(r / 255.0f); 

 byteBuffer.putFloat(g / 255.0f); 

 byteBuffer.putFloat(b / 255.0f); 

 } 

 return byteBuffer; 

 } 

 private void displayResults(TensorBuffer outputBuffer) { 

 float[] outputArray = outputBuffer.getFloatArray(); 

 int predictedClass = getMaxProbabilityIndex(outputArray); 

 // Get the traffic sign label from string resources 

 String[] trafficSignLabels = getResources().getStringArray(R.array.traffic_labels); 

 String predictedLabel = predictedClass < trafficSignLabels.length ? 

trafficSignLabels[predictedClass] : "Unknown class"; 

 // Display prediction result 

 Log.i("MainActivity", "Predicted Class: " + predictedLabel); 

 Toast.makeText(this, "Predicted Class: " + predictedLabel, Toast.LENGTH_SHORT).show(); 

 } 

 private int getMaxProbabilityIndex(float[] probabilities) { 

 int maxIndex = -1;

 float maxProbability = -1;

 for (int i = 0; i < probabilities.length; i++) { 

 if (probabilities[i] > maxProbability) { 

 maxProbability = probabilities[i]; 

 maxIndex = i; 

 } 

 } 

 return maxIndex; 

 } 

 @Override 

 protected void onDestroy() { 

 super.onDestroy(); 

 if (model != null) { 

 model.close(); 

 } 

 } 

   }bitmap.getPixels(pixels, 0, 50, 0, 0, 50, 50); 

 for (int pixel : pixels) { 

 int r = (pixel >> 16) & 0xFF; 

 int g = (pixel >> 8) & 0xFF; 

 int b = pixel & 0xFF; 

 // Normalize pixel values to [0, 1] and add to buffer 

 byteBuffer.putFloat(r / 255.0f); 

 byteBuffer.putFloat(g / 255.0f); 

 byteBuffer.putFloat(b / 255.0f); 

 } 

 return byteBuffer; 

 } 

 private void displayResults(TensorBuffer outputBuffer) { 

 float[] outputArray = outputBuffer.getFloatArray(); 

 int predictedClass = getMaxProbabilityIndex(outputArray); 

 // Get the traffic sign label from string resources 

 String[] trafficSignLabels = getResources().getStringArray(R.array.traffic_labels); 

 String predictedLabel = predictedClass < trafficSignLabels.length ? 

trafficSignLabels[predictedClass] : "Unknown class"; 

 // Display prediction result 

 Log.i("MainActivity", "Predicted Class: " + predictedLabel); 

 Toast.makeText(this, "Predicted Class: " + predictedLabel, Toast.LENGTH_SHORT).show(); 

 } 

 private int getMaxProbabilityIndex(float[] probabilities) { 

 int maxIndex = -1;

 float maxProbability = -1;

 for (int i = 0; i < probabilities.length; i++) { 

 if (probabilities[i] > maxProbability) { 

 maxProbability = probabilities[i]; 

 maxIndex = i; 

 } 

 } 

 return maxIndex; 

 } 

 @Override 

 protected void onDestroy() { 

 super.onDestroy(); 

 if (model != null) { 

 model.close(); 

 } 

 } 

   }
