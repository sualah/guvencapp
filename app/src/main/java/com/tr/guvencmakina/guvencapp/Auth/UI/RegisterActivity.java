package com.tr.guvencmakina.guvencapp.Auth.UI;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.EmailValidator;
import com.tr.guvencmakina.guvencapp.Welcome.ui.WelcomeActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.signup_btn)
    Button signup_btn;
    @BindView(R.id.login_link)
    TextView login_link;
    @BindView(R.id.email_field)
    TextInputEditText email_field;
    @BindView(R.id.password_field)
    TextInputEditText password_field;
    @BindView(R.id.confirm_password_field)
    TextInputEditText confirm_password_field;
    @BindView(R.id.phone_field)
    TextInputEditText phone_field;
    @BindView(R.id.user_name)
    TextInputEditText name_field;
    @BindView(R.id.loading_view)
    LinearLayout loading_view;
    @BindView(R.id.register_details_ll)
    LinearLayout register_details_ll;
    private FirebaseAuth mAuth;
    private String TAG = "register_activity";
    private String email,password,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //set transparency for some views
        signup_btn.setAlpha(0.7f);
        login_link.setAlpha(0.7f);

        //set click events for buttons and other views
        login_link.setOnClickListener(this);
        signup_btn.setOnClickListener(this);

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

    public  void signUp(String email, String password){
        showLoader();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user != null){
                                String uid = user.getUid();
                                String userName = name_field.getText().toString();
                                String email = user.getEmail();
                                //String photo = user.getPhotoUrl().toString();
                                String phone = phone_field.getText().toString();
                               // System.out.println(TAG + " " + photo);
                                DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                                HashMap<String,String> userMap = new HashMap<>();
                                userMap.put("name", userName);
                                userMap.put("email", email);
                                userMap.put("phone", phone);
                                userMap.put("type", "subscriber");
                                userMap.put("about", "");
                                userMap.put("image", "");
                                mDatabaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toasty.success(getApplicationContext(),getString(R.string.user_registration_successful),Toast.LENGTH_SHORT).show();
                                            updateUI(user);
                                        } else {
                                            Toasty.error(getApplicationContext(),getString(R.string.user_registration_failure),Toast.LENGTH_SHORT).show();
                                           hideLoader();
                                        }
                                    }
                                });

                            }

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
                                Log.w(TAG, "createUserWithEmail:failure", e.getCause());
                                Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }

                        // ...
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
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    public void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            WelcomeActivity.logged_in_user.setUid(currentUser.getUid());
//            WelcomeActivity.logged_in_user.setEmail(currentUser.getEmail());
//            WelcomeActivity.logged_in_user.setName(currentUser.getDisplayName());
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.login_link:
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
            case R.id.signup_btn:
                if(validated()){
                    email = email_field.getText().toString();
                    password = password_field.getText().toString();
                    phone = phone_field.getText().toString();
                    signUp(email,password);
                }
                break;
        }
    }

    public void showLoader(){
        loading_view.setVisibility(View.VISIBLE);
        register_details_ll.setVisibility(View.GONE);

    }

    public void hideLoader(){
        loading_view.setVisibility(View.GONE);
        register_details_ll.setVisibility(View.VISIBLE);

    }
    public boolean validated(){
        boolean valid = true;
        //make sure required fields are not empty
        if(name_field.getText().toString().isEmpty()) {
            name_field.setError(getString(R.string.name_empty_error));
            Toasty.error(RegisterActivity.this,getString(R.string.name_empty_error),Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(email_field.getText().toString().isEmpty()) {
            email_field.setError(getString(R.string.email_empty_error));
            Toasty.error(RegisterActivity.this,getString(R.string.email_empty_error),Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!EmailValidator.validateEmail(email_field.getText().toString())){
            Toasty.error(getApplicationContext(),getString(R.string.email_invalid_error),Toast.LENGTH_SHORT).show();
            valid = false;
        } else  if(password_field.getText().toString().isEmpty() ) {
            password_field.setError(getString(R.string.password_empty_error));
            Toasty.error(RegisterActivity.this,getString(R.string.password_empty_error),Toast.LENGTH_SHORT).show();
            valid = false;
        }else  if(password_field.getText().toString().length() < 6 ) {
            password_field.setError(getString(R.string.password_length_error));
            Toasty.error(RegisterActivity.this,getString(R.string.password_length_error),Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!password_field.getText().toString().equalsIgnoreCase(confirm_password_field.getText().toString())) {
            confirm_password_field.setError(getString(R.string.password_mismatch_error));
            Toasty.error(RegisterActivity.this,getString(R.string.password_mismatch_error),Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return  valid;
    }
}
