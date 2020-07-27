package com.homerianreyes.ac_twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    private EditText edtSignUpEmail;
    private EditText edtSignUpUsername;
    private EditText edtSignUpPassword;
    private Button btnSignUp;
    private TextView txtLogin;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();

        setTitle("Sign Up");

        edtSignUpEmail = findViewById(R.id.edtSignUpEmail);
        edtSignUpUsername = findViewById(R.id.edtSignUpUsername);
        edtSignUpPassword = findViewById(R.id.edtSignUpPassword);
        edtSignUpPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    signUpOnClicked(view);
                }

                return false;
            }
        });

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpOnClicked(view);
            }
        });

        txtLogin = findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginOnClicked(view);
            }
        });
    }//onCreate

    public void loginOnClicked(View view){
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        //txtLogin.setTextColor(Color.parseColor("#FFFFFF"));
        transitionToLoginActivity();
        finish();
    }

    public void signUpOnClicked(View view) {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (edtSignUpEmail.getText().toString().equals("") ||
            edtSignUpUsername.getText().toString().equals("") ||
            edtSignUpPassword.getText().toString().equals("")) {

            FancyToast.makeText(SignUp.this, "Please fill up the form",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
        } else {

            final ParseUser appUser = new ParseUser();
            appUser.setEmail(edtSignUpEmail.getText().toString());
            appUser.setUsername(edtSignUpUsername.getText().toString());
            appUser.setPassword(edtSignUpPassword.getText().toString());

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing up" + edtSignUpUsername.getText().toString() + ", Please wait...");
            progressDialog.show();

            appUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        FancyToast.makeText(SignUp.this, appUser.getUsername() + " Successfully signed up", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        FancyToast.makeText(SignUp.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                    edtSignUpEmail.getText().clear();
                    edtSignUpUsername.getText().clear();
                    edtSignUpPassword.getText().clear();
                    progressDialog.dismiss();
                    transitionToSocialMediaActivity();
                }
            });
        }
    }//signUpOnClicked

    public void rootLayoutTapped(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }//rootLayoutTapped

    private void transitionToLoginActivity(){
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }

    private void transitionToSocialMediaActivity(){
        Intent intent = new Intent(SignUp.this, SocialMediaActivity.class);
        startActivity(intent);
    }
}