package com.huich.roque.app.recomiendo_app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.huich.roque.app.recomiendo_app.MainActivity;
import com.huich.roque.app.recomiendo_app.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {

    private EditText mName , mLastName, mTelefono, mAdreess;
    private LinearLayout mLinearLayout;
    private ImageButton mBtnGetAdreess;
    private Button mBtnSetup;
    private CircleImageView mSetupImage;
    private Uri mMainImageURI = null;
    private String user_id;
    private boolean isChanged = false;
    private ProgressBar mSetupProgress;

    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id = mFirebaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mName = (EditText) findViewById(R.id.edt_setup_nombre);
        mLastName = (EditText) findViewById(R.id.edt_setup_lastname);
        mTelefono = (EditText) findViewById(R.id.edt_setup_telefono);
        mAdreess = (EditText) findViewById(R.id.edt_setup_address);
        mBtnSetup = (Button) findViewById(R.id.btn_setup_addData);
        mBtnGetAdreess = (ImageButton) findViewById(R.id.img_setup_getAdreess);
        mSetupImage = (CircleImageView) findViewById(R.id.img_setup_profile);
        mSetupProgress = (ProgressBar) findViewById(R.id.pb_setup_progressbar);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_setup_content);

        mSetupProgress.setVisibility(View.VISIBLE);
        mBtnSetup.setEnabled(false);

        mFirebaseFirestore.collection("Usuarios").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String getName = task.getResult().getString("nombre");
                        String getImage = task.getResult().getString("avatar");
                        String getPhone = task.getResult().getString("telefono");
                        String getAdreess = task.getResult().getString("direccion");

                        mMainImageURI = Uri.parse(getImage);

                        mName.setText(getName);
                        mTelefono.setText(getPhone);
                        mAdreess.setText(getAdreess);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.ic_face_happy);

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(getImage).into(mSetupImage);


                    }else {
                        Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error)", Toast.LENGTH_LONG).show();
                    }

                    mSetupProgress.setVisibility(View.INVISIBLE);
                    mBtnSetup.setEnabled(true);
                }
            }
        });

        mBtnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = mName.getText().toString();
                final String user_lastname = mLastName.getText().toString();
                final String user_phone = mTelefono.getText().toString();
                final String user_adreess = mAdreess.getText().toString();

                if (!TextUtils.isEmpty(user_name)
                        && !TextUtils.isEmpty(user_lastname)
                        && !TextUtils.isEmpty(user_phone)
                        && !TextUtils.isEmpty(user_adreess)
                        && mMainImageURI != null){

                    mSetupProgress.setVisibility(View.VISIBLE);

                    if (isChanged){
                        user_id = mFirebaseAuth.getCurrentUser().getUid();
                        File newImageFile = new File(mMainImageURI.getPath());
                        try {

                            compressedImageFile = new Compressor(SetupActivity.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask image_path = mStorageReference.child("avatar").child(user_id + ".jpg").putBytes(thumbData);

                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name, user_lastname, user_phone,user_adreess);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    mSetupProgress.setVisibility(View.INVISIBLE);

                                }

                            }
                        });
                    }else {
                        storeFirestore(null, user_name, user_lastname, user_phone, user_adreess);
                    }
                }

            }
        });

        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task,
                                String user_name, String user_lastname, String user_phone, String user_adreess) {

        Uri download_uri;

        if(task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mMainImageURI;
        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("nombre", user_name);
        userMap.put("apellido", user_lastname);
        userMap.put("telefono", user_phone);
        userMap.put("direccion", user_adreess);

        userMap.put("avatar", download_uri.toString());

        mFirebaseFirestore.collection("Usuarios").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(SetupActivity.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

                mSetupProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mMainImageURI = result.getUri();
                mSetupImage.setImageURI(mMainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
