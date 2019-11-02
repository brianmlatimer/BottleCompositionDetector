package com.mwen.bottle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
//        {
//            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, requestCode);
//        }
        Button b = findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity((getPackageManager())) != null) {
                    startActivityForResult(intent, 1); // 1 equals to request image capture
                }
            }
        });
        Button b2 = findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity((getPackageManager())) != null) {
                    startActivityForResult(intent, 1); // 1 equals to request image capture
                }
            }
        });
        Button b3 = findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity((getPackageManager())) != null) {
                    startActivityForResult(intent, 1); // 1 equals to request image capture
                }
            }
        });
        // Brian Code on his own branch

    }

    private void performCrop() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(picUri, "image/*");

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 2);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 224);
        cropIntent.putExtra("outputY", 224);

        cropIntent.putExtra("return-data", true);
        onActivityResult(RESULT_OK, 2, cropIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.v("RESULT", "It worked, we got an image");
                picUri = data.getData();
                performCrop();

            }
            else if (requestCode == 2) {
                Bundle bundle = data.getExtras();
                Bitmap imagebmp = (Bitmap) bundle.get("data");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                Long milliscurr = System.currentTimeMillis();
                StorageReference imgRef = storageRef.child(milliscurr.toString() + "-" + "mwen");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagebmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] img_data = baos.toByteArray();

                UploadTask uploadTask = imgRef.putBytes(img_data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.v("Subama", "It failed Nicholas Foster");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Log.v("Subama", "It worked Nicholas Foster");
                    }
                });
            }

        }
    }
}


