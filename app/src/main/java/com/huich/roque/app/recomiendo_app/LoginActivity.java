package com.huich.roque.app.recomiendo_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.huich.roque.app.recomiendo_app.activities.RegisterActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleButton;
    private Button mEmailLogin;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore mFirestore;

    private ProgressBar mProgressBar;
    private ProgressDialog pDialog;
    private EditText mInputUsername;
    private EditText mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mGoogleButton = (SignInButton) findViewById(R.id.btn_login_googlebutton);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_login_progressbar);
        mEmailLogin = (Button) findViewById(R.id.btn_login_email);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(googleIntent, RC_SIGN_IN_CODE);
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null){
                    goMainScreen();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            goMainScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_CODE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            hadleSignInResult(googleSignInResult);
        }
    }

    private void hadleSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()){
            firebaseAuthWithGoogle(googleSignInResult.getSignInAccount());
        }else {
            Toast.makeText(this,"Error al iniciar session con google", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        mProgressBar.setVisibility(View.VISIBLE);
        mGoogleButton.setVisibility(View.GONE);
        mEmailLogin.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mProgressBar.setVisibility(View.GONE);
                mGoogleButton.setVisibility(View.VISIBLE);
                mEmailLogin.setVisibility(View.VISIBLE);

                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "No se inició sesion con firebase", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void firebaseAuthWithEmail(){
        final String email = mInputUsername.getText().toString();
        String password = mInputPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            pDialog.setMessage("Ingresando ...");
            showDialog();
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        goMainScreen();
                    }else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    hideDialog();
                }
            });
        }
    }

    public  void showDialog(View view){

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_login);

        Button btnRegister = dialog.findViewById(R.id.btn_dialoglogin_register);
        Button btnLogin = dialog.findViewById(R.id.btn_dialoglogin_login);
        ImageButton btnClose = dialog.findViewById(R.id.img_dialoglogin_close);
        mInputUsername =  dialog.findViewById(R.id.edt_dialoglogin_email);
        mInputPassword = dialog.findViewById(R.id.edt_dialoglogin_password);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Cancel process!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuthWithEmail();
            }
        });

        dialog.show();
    }

    private void goMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goRegister(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion!", Toast.LENGTH_SHORT).show();
        Log.e("GoogleSignIn", "OnConnectionFailed: " + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void checkUserExist(){
        final String user_uid = mFirebaseAuth.getCurrentUser().getUid();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        final Map<String,Object> userMap = new HashMap<>();
        userMap.put("nombre",user.getDisplayName());
        userMap.put("avatar", user.getPhotoUrl().toString());
        userMap.put("apelido", "");
        userMap.put("telefono", user.getPhoneNumber());
        userMap.put("email", user.getEmail());
        userMap.put("direccion", "");
        userMap.put("dni", "");
        userMap.put("descripcion", "Hola Mundo! Soy "+ user.getDisplayName());

        mFirestore.collection("Usuarios").document(user_uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("ERROR", e.getMessage());
                    return;
                }

                if(documentSnapshot.exists()){
                    goMainScreen();
                }else {
                    mFirestore.collection("Usuarios").document(user_uid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                goMainScreen();

                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"FIRESTORE error: "+ error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
