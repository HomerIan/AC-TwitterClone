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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtLoginEmail;
    private EditText edtLoginPassword;
    private Button btnLogin;
    private TextView txtSignUp;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Log In");

        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        edtLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    onClick(btnLogin);
                }

                return false;
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        txtSignUp = findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(this);

        if(ParseUser.getCurrentUser() != null){
            transitionToSocialMediaActivity();
        }
    }//onCreate

    @Override
    public void onClick(View view) {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){

            case R.id.btnLogin:

                if ( edtLoginEmail.getText().toString().equals("") ||
                    edtLoginPassword.getText().toString().equals("")){

                    FancyToast.makeText(this,"E-mail and Password required",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    ParseUser.logInInBackground(edtLoginEmail.getText().toString(), edtLoginPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            if (user != null && e == null){

                                FancyToast.makeText(LoginActivity.this, user.getUsername() + " Successfully Log In", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                edtLoginEmail.getText().clear();
                                edtLoginPassword.getText().clear();
                                transitionToSocialMediaActivity();
                            } else {
                                FancyToast.makeText(LoginActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }

                break;

            case R.id.txtSignUp:
                transitionToSignUpActivity();
                break;
        }
    }

    private void transitionToSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(intent);
        finish();
    }

    private void transitionToSocialMediaActivity(){
        Intent intent = new Intent(LoginActivity.this, SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }

    public void rootLayoutTapped(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }//rootLayoutTapped
}