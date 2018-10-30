package com.bcabuddies.fitsteps;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterSecondFrag extends Fragment {

    private TextInputEditText fullName;
    private Button btnNext;
    private CircleImageView thumbImage;
    private String name, userId;
    private Uri thumb_downloadUrl = null;
    private StorageReference thumbImgRef;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private Context context;
    private Bitmap thumb_Bitmap = null;
    private Uri mainImageUri = null;

    public RegisterSecondFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_second, container, false);

        btnNext = view.findViewById(R.id.register2_btnNext);
        thumbImage = view.findViewById(R.id.register2_profile);
        fullName = view.findViewById(R.id.register2_fullName);
        auth = FirebaseAuth.getInstance();
        thumbImgRef = FirebaseStorage.getInstance().getReference().child("Thumb_images");
        userId = auth.getCurrentUser().getUid();
        context = getContext();
        Log.e("userid", "onCreateView: " + userId);
        firebaseFirestore = FirebaseFirestore.getInstance();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = fullName.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(context, "Please Enter your name ", Toast.LENGTH_SHORT).show();
                }else if(thumb_downloadUrl==null){
                    Toast.makeText(context, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
                }

                else {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("thumb_id",thumb_downloadUrl.toString());

                    try {
                        firebaseFirestore.collection("Users").document(userId).set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            RegisterThirdFrag registerThirdFrag = new RegisterThirdFrag();
                                            getFragmentManager().beginTransaction().replace(R.id.registerMain_frame, registerThirdFrag).commit();
                                        } else {
                                            Toast.makeText(context, "some error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("nameError", "onClick: error "+e.getMessage() );
                    }

                }
            }
        });


        thumbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker();
            }
        });

        return view;
    }

    private void ImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getContext(), this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == getActivity().RESULT_OK) {

                mainImageUri = result.getUri();
                Log.v("mkeyreg", "mianuri= " + mainImageUri);

                File thumb_filePathUri = new File(mainImageUri.getPath());
                try {
                    thumb_Bitmap = new Compressor(getActivity()).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filePathUri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                final StorageReference thumb_filePath = thumbImgRef.child(userId + ".jpg");

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        thumb_downloadUrl = taskSnapshot.getDownloadUrl();

                        Log.v("mkey", "thumb download url: " + thumb_downloadUrl);
                        thumbImage.setImageURI(mainImageUri);
                        progressDialog.dismiss();
                    }
                });


            }

        }


    }
}
