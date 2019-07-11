package com.tr.makina.guvencapp.Auth.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.tr.makina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.makina.guvencapp.R;
import com.tr.makina.guvencapp.Utils.EmailValidator;
import com.tr.makina.guvencapp.Welcome.ui.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.signup_link)
    TextView signup_link;
    @BindView(R.id.login_btn)
    Button login_btn;
    @BindView(R.id.email_field)
    TextInputEditText email_field;
    @BindView(R.id.password_field)
    TextInputEditText password_field;
    @BindView(R.id.loading_view)
    LinearLayout loading_view;
    private FirebaseAuth mAuth;
    private String TAG = "login_activity";
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        //set transparency for some views
        signup_link.setAlpha(0.7f);
        login_btn.setAlpha(0.7f);

        //set click events for buttons and other views
        login_btn.setOnClickListener(this);
        signup_link.setOnClickListener(this);

        email_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!email_field.getText().toString().isEmpty()){
                    if(!EmailValidator.validateEmail(email_field.getText().toString())){
                        Toasty.error(getApplicationContext(),getString(R.string.email_invalid_error),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public  void logIn(String email, String password){
        showLoader();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toasty.success(getApplicationContext(),getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            hideLoader();
                        } else {
                            // If sign in fails, display a message to the user.
                            hideLoader();
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                password_field.setError(getString(R.string.invalid_password));
                                password_field.requestFocus();
                                Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                                Log.w(TAG, "signInWithEmail:failure", e.getCause());
                                Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }

                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    public void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            WelcomeActivity.logged_in_user.setUid(currentUser.getUid());
            Toasty.success(getApplicationContext(),getString(R.string.welcome), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.login_btn:
                if(validated()){
                    email = email_field.getText().toString();
                    password = password_field.getText().toString();
                    logIn(email,password);
                }
                break;
            case R.id.signup_link:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    public void showLoader(){
        loading_view.setVisibility(View.VISIBLE);
        login_btn.setVisibility(View.GONE);
        signup_link.setVisibility(View.GONE);
    }

    public void hideLoader(){
        loading_view.setVisibility(View.GONE);
        login_btn.setVisibility(View.VISIBLE);
        signup_link.setVisibility(View.VISIBLE);
    }

    public boolean validated(){
        boolean valid = true;
        //make sure required fields are not empty
        if(email_field.getText().toString().isEmpty()) {
            email_field.setError(getString(R.string.email_empty_error));
            Toasty.error(getApplicationContext(),getString(R.string.email_empty_error),Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!EmailValidator.validateEmail(email_field.getText().toString())){
            Toasty.error(getApplicationContext(),getString(R.string.email_invalid_error),Toast.LENGTH_SHORT).show();
            valid = false;
        }  if(password_field.getText().toString().isEmpty()) {
            password_field.setError(getString(R.string.password_empty_error));
            Toasty.error(getApplicationContext(),getString(R.string.password_empty_error),Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return  valid;
    }


}
