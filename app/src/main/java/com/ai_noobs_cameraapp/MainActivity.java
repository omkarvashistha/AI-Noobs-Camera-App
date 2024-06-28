package com.ai_noobs_cameraapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cameraapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private Button takePictureButton;
    private ImageView imageView;
    private Button checkButton;
    private EditText promptEditText;
    private TextView responseTextView;
    private File capturedImageFile;
//    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = findViewById(R.id.takePictureButton);
        imageView = findViewById(R.id.imageView);
        checkButton = findViewById(R.id.checkButton);
//        promptEditText = findViewById(R.id.promptEditText);
//        responseTextView = findViewById(R.id.responseTextView);
//        apiService = new ApiService();

        checkButton.setVisibility(View.GONE);

        Button homeButton = findViewById(R.id.homeButton);
        Button userProfileButton = findViewById(R.id.userProfileButton);

        homeButton.setOnClickListener(view -> {
            // Handle Home button click
            Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
        });

        userProfileButton.setOnClickListener(view -> {
            // Handle User Profile button click
            Toast.makeText(MainActivity.this, "User Profile clicked", Toast.LENGTH_SHORT).show();
        });

        takePictureButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            } else {
                dispatchTakePictureIntent();
            }
        });

        checkButton.setOnClickListener(view -> {
            String prompt = promptEditText.getText().toString();
//            apiService.getResponse(prompt, new ApiService.ApiCallback() {
//                @Override
//                public void onSuccess(String response) {
//                    runOnUiThread(() -> responseTextView.setText(response));
//                }
//
//                @Override
//                public void onFailure(String error) {
//                    runOnUiThread(() -> responseTextView.setText("Error: " + error));
//                }
//            });
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private static final String BASE_URL = "YOUR_BACKEND_URL";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            // Store the image
            try {
                capturedImageFile = storeImage(imageBitmap);
                checkButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error storing image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File storeImage(Bitmap imageBitmap) throws IOException {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File imageFile = new File(directory, "captured_image.jpg");
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        return imageFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
