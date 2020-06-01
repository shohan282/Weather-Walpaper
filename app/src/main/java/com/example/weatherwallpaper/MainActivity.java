package com.example.weatherwallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherwallpaper.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

//>>>>>>>>>>>>>>>>>>>>>    firebase link: https://console.firebase.google.com/u/0/project/weather-4437b/overview   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;
    String category;
    private String downloadImageUrl;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityMainBinding.inflate(getLayoutInflater());
        View view = b.getRoot();
        setContentView(view);

        storageReference = FirebaseStorage.getInstance().getReference().child("All_Pic");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Image");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("please wait we are uplaoding your photo...");
        progressDialog.setCanceledOnTouchOutside(false);

        b.clearImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                category = "clear";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);

            }
        });
        b.rainyImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                category = "rain";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);

            }
        });
        b.cloudyImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                category = "clouds";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);

            }
        });
        b.hazeImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                category = "haze";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                if (category.equals("haze")) {
                    Glide.with(this).load(resultUri).into(b.hazeImageID);
                    loadImageintoDatabase(resultUri,"haze");
                } else if (category.equals("clouds")) {
                    Glide.with(this).load(resultUri).into(b.cloudyImageID);
                    loadImageintoDatabase(resultUri,"clouds");
                } else if (category.equals("rain")) {
                    Glide.with(this).load(resultUri).into(b.rainyImageID);
                    loadImageintoDatabase(resultUri,"rain");
                } else {
                    Glide.with(this).load(resultUri).into(b.clearImageID);
                    loadImageintoDatabase(resultUri,"clear");
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(MainActivity.this, "Cropimage Error: " + error, Toast.LENGTH_LONG).show();
            }
        }

    }


    public void loadImageintoDatabase(Uri imageUri,String category) {

        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat current_date = new SimpleDateFormat("MMM dd, yyyy");
        String saveCurrentDate = current_date.format(calendar.getTime());
        SimpleDateFormat current_time = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = current_time.format(calendar.getTime());
        String randdomKey = saveCurrentDate + saveCurrentTime;


        StorageReference imageRef = storageReference.child(imageUri.getLastPathSegment() + randdomKey);
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(MainActivity.this, "Image Added...", Toast.LENGTH_SHORT).show();
                           progressDialog.dismiss();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {

                            throw task.getException();

                        }

                        downloadImageUrl = imageRef.getDownloadUrl().toString();

                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("Image Url", downloadImageUrl);
                            map.put("Category",category);

                            if (category.equals("haze")) {

                                databaseReference.child(category).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(MainActivity.this, "added in db", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    Toast.makeText(MainActivity.this, "mara khaiso", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            } else if (category.equals("clouds")) {
                                databaseReference.child(category).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(MainActivity.this, "added in db", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    Toast.makeText(MainActivity.this, "mara khaiso", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });


                            } else if (category.equals("rain")) {
                                databaseReference.child(category).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(MainActivity.this, "added in db", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    Toast.makeText(MainActivity.this, "mara khaiso", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            } else {
                                databaseReference.child(category).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(MainActivity.this, "added in db", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    Toast.makeText(MainActivity.this, "mara khaiso", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            }

                              startActivity(new Intent(getApplicationContext(),WeatherActivity.class));
                        }
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, e + "", Toast.LENGTH_SHORT).show();

            }
        });

    }


}
