package com.mwen.bottle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentProvider;
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
import android.os.Environment;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    protected void get_picture(int type) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity((getPackageManager())) != null) {
            File photo = null;
            try {
                // add the name here
                Log.v("MWEN", "creating temp photo");
                photo = createImageFile(type);
            } catch (IOException ex) {
                // catch error
            }

            if (photo != null) {
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.android.fileprovider",
                        photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, 0);

            }
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
                get_picture(BOTTLE_IMG);
            }
        });
        Button b2 = findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_picture(PLASTIC_IMG);
            }
        });
        Button b3 = findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_picture(TRAIN_IMG);
            }
        });

    }

    String currentPhotoPath;
    private File createImageFile(int type) throws IOException {
        // Create an image file name
        Long milliscurr = System.currentTimeMillis();
        String img_name = milliscurr.toString() + "-" + "mwen";

        mDatabase = FirebaseDatabase.getInstance().getReference();
        BIMG bmp = new BIMG(img_name, type);
        mDatabase.child(img_name).setValue(bmp);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                img_name,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.v("MWEN", "Absolute Path: " + currentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                  CropImage.ActivityResult result = CropImage.getActivityResult(data);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                // look at last index of username character.
                String img_name = currentPhotoPath.substring(currentPhotoPath.lastIndexOf("n") + 1, currentPhotoPath.lastIndexOf(".")) + "-" + "mwen";
                StorageReference imgRef = storageRef.child(img_name);

                UploadTask uploadTask = imgRef.putFile(result.getUri());
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
            else {
                Log.v("RESULT", "It worked, we should perform crop");
                CropImage.activity(Uri.fromFile(new File(currentPhotoPath)))
                        .setActivityTitle("Crop Image")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .setRequestedSize(224, 224)
                        .start(this);
            }
        }
    }

}


