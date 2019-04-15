package com.bcabuddies.fitsteps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PostRegisterFirst extends AppCompatActivity {


    private TextInputLayout fullName;
    private Button btnNext;
    private CircleImageView thumbImage;
    private String name, userId;
    private Uri thumb_downloadUrl = null;
    private StorageReference thumbImgRef;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap thumb_Bitmap = null;
    private Uri mainImageUri = null;
    private static String TAG = "PostRegFirst.java";
    private String profUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_first);

        initView();

        try {
            String fname = getIntent().getStringExtra("name");
            profUrl = getIntent().getStringExtra("profUrl");
            Log.e(TAG, "onCreate: preData " + name + "  " + profUrl);
            Objects.requireNonNull(fullName.getEditText()).setText(fname);
            Glide.with(this).load(profUrl).into(thumbImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profUrl == null) {
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/fitsteps-311ed.appspot.com/o/default_user_thumb%2Fdefault.png?alt=media&token=c2de219c-9430-48bf-84c1-b2ba0b37be66").into(thumbImage);
        }

        btnNext.setOnClickListener(v -> {
            name = fullName.getEditText().getText().toString();
            if (name.isEmpty()) {
                Log.e(TAG, "onCreate postreg: if" );
                Toast.makeText(PostRegisterFirst.this, "Please Enter your name ", Toast.LENGTH_SHORT).show();
            } else if (thumb_downloadUrl == null) {
                Log.e(TAG, "onCreate postreg: else if" );
                if (!(profUrl == null)) {
                    Log.e(TAG, "onCreate postreg: else if if" );
                    thumb_downloadUrl = Uri.parse(profUrl);
                    detailsUpload(name,thumb_downloadUrl);
                } else {
                    Log.e(TAG, "onCreate postreg: else if else");
                    thumb_downloadUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fitsteps-311ed.appspot.com/o/default_user_thumb%2Fdefault.png?alt=media&token=c2de219c-9430-48bf-84c1-b2ba0b37be66");
                }
                // Toast.makeText(PostRegisterFirst.this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
            } else {
                detailsUpload(name,thumb_downloadUrl);
            }
        });
        thumbImage.setOnClickListener(v -> ImagePicker());
    }

    private void detailsUpload(String name, Uri thumb_downloadUrl) {
        Log.e(TAG, "onCreate postreg: else" );
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("thumb_id", thumb_downloadUrl.toString());
        Log.e(TAG, "onClick: thumbUrl:" + thumb_downloadUrl.toString());
        try {
            firebaseFirestore.collection("Users").document(userId).set(map)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(PostRegisterFirst.this, PostRegisterSecond.class));
                        } else {
                            Toast.makeText(PostRegisterFirst.this, "some error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("nameError", "onClick: error " + e.getMessage());
        }
    }

    private void initView() {
        btnNext = findViewById(R.id.register2_btnNext);
        thumbImage = findViewById(R.id.register2_profile);
        fullName = findViewById(R.id.register2_fullNamelayout);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        thumbImgRef = FirebaseStorage.getInstance().getReference().child("Thumb_images");
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Log.e("userId", "onCreateView: " + userId);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void ImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                Log.v("mKeyReg", "mainUri= " + mainImageUri);
                File thumb_filePathUri = new File(mainImageUri.getPath());
                try {
                    thumb_Bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filePathUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
                final StorageReference thumb_filePath = thumbImgRef.child(userId + ".jpg");
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(taskSnapshot -> {
                    try {
                        thumb_filePath.getDownloadUrl().addOnSuccessListener(Uri -> {
                            Log.e(TAG, "onActivityResult: Uri " + Uri);
                            thumb_downloadUrl = Uri;
                            Log.v("mkey", "thumb download url: " + thumb_downloadUrl);
                            thumbImage.setImageURI(mainImageUri);
                            progressDialog.dismiss();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onActivityResult: exception download uri ");
                    }
                });
            }
        }
    }
}
