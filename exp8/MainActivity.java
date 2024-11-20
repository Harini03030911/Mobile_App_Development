import android.Manifest; 

import android.annotation.SuppressLint; 

import android.app.Activity; 

import android.content.Context; 

import android.content.DialogInterface; 

import android.content.Intent; 

import android.content.SharedPreferences; 

import android.content.pm.PackageManager; 

import android.content.res.AssetFileDescriptor; 

import android.graphics.Bitmap; 

import android.graphics.BitmapFactory; 

import android.graphics.Canvas; 

import android.graphics.Color; 

import android.graphics.ImageFormat; 

import android.graphics.Matrix; 

import android.graphics.Paint; 

import android.graphics.Rect; 

import android.graphics.RectF; 

import android.graphics.YuvImage; 

import android.media.Image; 

import android.net.Uri; 

import android.os.Build; 

import android.os.Bundle; 

import androidx.annotation.NonNull; 

import androidx.annotation.RequiresApi; 

import androidx.appcompat.app.AlertDialog; 

import androidx.camera.core.CameraSelector; 

import androidx.camera.core.ImageAnalysis; 

import androidx.camera.core.ImageProxy; 

import androidx.camera.core.Preview; 

import androidx.camera.lifecycle.ProcessCameraProvider; 

import com.google.android.gms.tasks.OnCompleteListener; 

import com.google.android.gms.tasks.OnFailureListener; 

import com.google.android.gms.tasks.OnSuccessListener; 

import com.google.android.gms.tasks.Task; 

import com.google.common.util.concurrent.ListenableFuture; 

import com.google.gson.Gson; 

import com.google.gson.reflect.TypeToken; 

import com.google.mlkit.vision.common.InputImage; 

import com.google.mlkit.vision.face.Face; 

import com.google.mlkit.vision.face.FaceDetection; 

import com.google.mlkit.vision.face.FaceDetector; 

import com.google.mlkit.vision.face.FaceDetectorOptions; 

import androidx.appcompat.app.AppCompatActivity; 

import androidx.camera.view.PreviewView; 

import androidx.core.content.ContextCompat; 

import androidx.lifecycle.LifecycleOwner; 

import android.os.ParcelFileDescriptor; 

import android.text.InputType; 

import android.util.Pair; 

import android.util.Size; 

import android.view.View; 

import android.widget.Button; 

import android.widget.EditText; 

import android.widget.ImageButton; 

import android.widget.ImageView; 

import android.widget.TextView; 

import android.widget.Toast; 

import org.tensorflow.lite.Interpreter; 

import java.io.ByteArrayOutputStream; 

import java.io.FileDescriptor; 

import java.io.FileInputStream; 

import java.io.IOException; 

import java.nio.ByteBuffer; 

import java.nio.ByteOrder; 

import java.nio.MappedByteBuffer; 

import java.nio.ReadOnlyBufferException; 

import java.nio.channels.FileChannel;import java.util.ArrayList; 

import java.util.HashMap; 

import java.util.List; 

import java.util.Map; 

import java.util.concurrent.ExecutionException; 

import java.util.concurrent.Executor; 

import java.util.concurrent.Executors; 

public class MainActivity extends AppCompatActivity { 

 FaceDetector detector; 

 private ListenableFuture<ProcessCameraProvider> cameraProviderFuture; 

 PreviewView previewView; 

 ImageView face_preview; 

 Interpreter tfLite; 

 TextView reco_name,preview_info,textAbove_preview; 

 Button recognize,camera_switch, actions; 

 ImageButton add_face; 

 CameraSelector cameraSelector; 

 boolean developerMode=false; 

 float distance= 1.0f; 

 boolean start=true,flipX=false; 

 Context context=MainActivity.this; 

 int cam_face=CameraSelector.LENS_FACING_BACK; //Default Back Camera 

 int[] intValues; 

 int inputSize=112; //Input size for model 

 boolean isModelQuantized=false; 

 float[][] embeedings; 

 float IMAGE_MEAN = 128.0f; 

 float IMAGE_STD = 128.0f; 

 int OUTPUT_SIZE=192; //Output size of model 

 private static int SELECT_PICTURE = 1; 

 ProcessCameraProvider cameraProvider; 

 private static final int MY_CAMERA_REQUEST_CODE = 100; 

 String modelFile="mobile_face_net.tflite"; //model name 

 private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved 

Faces 

 @RequiresApi(api = Build.VERSION_CODES.M) 

 @Override 

 protected void onCreate(Bundle savedInstanceState) { 

 super.onCreate(savedInstanceState); 

 registered=readFromSP(); //Load saved faces from memory when app starts 

 setContentView(R.layout.activity_main); 

 face_preview =findViewById(R.id.imageView); 

 reco_name =findViewById(R.id.textView); 

 preview_info =findViewById(R.id.textView2); 

 textAbove_preview =findViewById(R.id.textAbovePreview); 

 add_face=findViewById(R.id.imageButton); 

 add_face.setVisibility(View.INVISIBLE); 

 SharedPreferences sharedPref = getSharedPreferences("Distance",Context.MODE_PRIVATE); 

 distance = sharedPref.getFloat("distance",1.00f); 

 face_preview.setVisibility(View.INVISIBLE); 

 recognize=findViewById(R.id.button3); 

 camera_switch=findViewById(R.id.button5); 

 actions=findViewById(R.id.button2); 

 textAbove_preview.setText("Recognized Face:"); 

// preview_info.setText(" Recognized Face:"); 

 //Camera Permission 

 if (checkSelfPermission(Manifest.permission.CAMERA) != 

PackageManager.PERMISSION_GRANTED) { 

 requestPermissions(new String[]{Manifest.permission.CAMERA}, 

MY_CAMERA_REQUEST_CODE); 

 } 

 //On-screen Action Button 

 actions.setOnClickListener(new View.OnClickListener() { 

 @Override 

 public void onClick(View v) { 

 AlertDialog.Builder builder = new AlertDialog.Builder(context); 

 builder.setTitle("Select Action:"); 

 // add a checkbox list 

 String[] names= {"View Recognition List","Update Recognition List","Save 

Recognitions","Load Recognitions","Clear All Recognitions","Import Photo 

(Beta)","Hyperparameters","Developer Mode"}; 

 builder.setItems(names, new DialogInterface.OnClickListener() { 

 @Override 

 public void onClick(DialogInterface dialog, int which) { 

 switch (which) 

 { 

 case 0: 

 displaynameListview(); 

 break; 

 case 1:updatenameListview(); 

 break; 

 case 2: 

 insertToSP(registered,0); //mode: 0:save all, 1:clear all, 2:update all 

 break; 

 case 3: 

 registered.putAll(readFromSP()); 

 break; 

 case 4: 

 clearnameList(); 

 break; 

 case 5: 

 loadphoto(); 

 break; 

 case 6: 

 testHyperparameter(); 

 break; 

 case 7: 

 developerMode(); 

 break; 

 } 

 } 

 });

 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 

 @Override 

 public void onClick(DialogInterface dialog, int which) { 

 } 

 });

 builder.setNegativeButton("Cancel", null); 

 // create and show the alert dialog 

 AlertDialog dialog = builder.create(); 

 dialog.show(); 

 } 

 }); 

 //On-screen switch to toggle between Cameras. 

 camera_switch.setOnClickListener(new View.OnClickListener() { 

 @Override 

 public void onClick(View v) { 

 if (cam_face==CameraSelector.LENS_FACING_BACK) { 

 cam_face = CameraSelector.LENS_FACING_FRONT; 

 flipX=true; 

 } 

 else { 

 cam_face = CameraSelector.LENS_FACING_BACK; 

 flipX=false; 

 } 

 cameraProvider.unbindAll(); 

 cameraBind(); 

 } 

 }); 

 add_face.setOnClickListener((new View.OnClickListener() { 

 @Override 

 public void onClick(View v) { 

 addFace(); 

 } 

 })); 

 recognize.setOnClickListener(new View.OnClickListener() { 

 @Override 

 public void onClick(View v) { 

 if(recognize.getText().toString().equals("Recognize")) 

 { 

 start=true; 

 textAbove_preview.setText("Recognized Face:"); 

 recognize.setText("Add Face"); 

 add_face.setVisibility(View.INVISIBLE); 

 reco_name.setVisibility(View.VISIBLE); 

 face_preview.setVisibility(View.INVISIBLE); 

 preview_info.setText(""); 

 //preview_info.setVisibility(View.INVISIBLE); 

 } 

 else 

 { 

 textAbove_preview.setText("Face Preview: "); 

 recognize.setText("Recognize"); 

 add_face.setVisibility(View.VISIBLE); 

 reco_name.setVisibility(View.INVISIBLE); 

 face_preview.setVisibility(View.VISIBLE); 

 preview_info.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will 

appear here.\n\n3.Click Add button to save face.");model 

 try { 

 tfLite=new Interpreter(loadModelFile(MainActivity.this,modelFile)); 

 } catch (IOException e) { 

 e.printStackTrace(); 

 } 

 //Initialize Face Detector 

 FaceDetectorOptions highAccuracyOpts = 

 new FaceDetectorOptions.Builder() 

 .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) 

 .build(); 

 detector = FaceDetection.getClient(highAccuracyOpts); 

 cameraBind(); 

 } 

 private void testHyperparameter() 

 { 

 AlertDialog.Builder builder = new AlertDialog.Builder(context); 

 builder.setTitle("Select Hyperparameter:"); 

 // add a checkbox list 

 String[] names= {"Maximum Nearest Neighbour Distance"}; 

 builder.setItems(names, new DialogInterface.OnClickListener() { 

 @Override 

 public void onClick(DialogInterface dialog, int which) { 

 switch (which) 

 { 

 case 0: 

// Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show(); 

 hyperparameters(); 

 break; 

 } 

 } 

 }); 

 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 

 @Override 

 public void onClick(DialogInterface dialog, int which) { 

 } 

 }); 

 builder.setNegativeButton("Cancel", null); 

 // create and show the alert dialog 

 AlertDialog dialog = builder.create(); 

 dialog.show(); 

 } 

 private void developerMode() 

 { 

 if (developerMode) { 

 developerMode = false; 

 Toast.makeText(context, "Developer Mode OFF", Toast.LENGTH_SHORT).show(); 

 } 

 else { 

 developerMode = true; 

 Toast.makeText(context, "Developer Mode ON", Toast.LENGTH_SHORT).show(); 

 } 

   }
