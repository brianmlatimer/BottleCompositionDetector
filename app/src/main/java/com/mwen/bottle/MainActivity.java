package com.mwen.bottle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Uri picUri;

    private DatabaseReference mDatabase;
    private static int BOTTLE_IMG  = 1;
    private static int PLASTIC_IMG = 2;
    private static int TRAIN_IMG   = 3;
    private static String[] BIMG_STRING = {"", "BOTTLE", "PLASTIC", ""};


    @IgnoreExtraProperties
    public class BIMG{

        public String name;
        public boolean train;
        public String type;

        public BIMG() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
            this.name = "";
            this.train = false;
            this.type = BIMG_STRING[0];
        }

        public BIMG(String name, int type) {
            this.train = type < 3;
            this.name = name;
            this.type = BIMG_STRING[type];
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity((getPackageManager())) != null) {
                        startActivityForResult(intent, BOTTLE_IMG + 5); // 1 equals to request image capture
                    }
            }
        });
        Button b2 = findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity((getPackageManager())) != null) {
                    startActivityForResult(intent, PLASTIC_IMG + 5); // 1 equals to request image capture
                }
            }
        });
        Button b3 = findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity((getPackageManager())) != null) {
                    startActivityForResult(intent, TRAIN_IMG + 5); // 1 equals to request image capture
                }

            }
        });
        // Brian Code on his own branch

    }

    private void performCrop(Bitmap bmp, int request) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        cropIntent.setDataAndType(picUri, "image/*");
        cropIntent.putExtra("data", bmp);
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 224);
        cropIntent.putExtra("outputY", 224);

        cropIntent.putExtra("return-data", true);
        onActivityResult(request - 5, RESULT_OK, cropIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode > 5) {
                Log.v("RESULT", "It worked, we should perform crop");
                performCrop((Bitmap) data.getExtras().get("data"), requestCode);
            }
            else {
                Log.v("RESULT", "It worked, we got an image");

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                Bitmap imagebmp = (Bitmap) bundle.get("data");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                Long milliscurr = System.currentTimeMillis();
                String img_name = milliscurr.toString() + "-" + "mwen";

                mDatabase = FirebaseDatabase.getInstance().getReference();
                BIMG bmp = new BIMG(img_name, requestCode);
                mDatabase.child(img_name).setValue(bmp);

                StorageReference imgRef = storageRef.child(img_name);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                assert imagebmp != null;
                imagebmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] img_data = baos.toByteArray();

                UploadTask uploadTask = imgRef.putBytes(img_data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.v("SUHANA", "It failed Nicholas Foster");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Log.v("SUHANA", "It worked Nicholas Foster");
                    }
                });

            }
        }

    }
}


