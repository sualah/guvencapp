package com.tr.makina.guvencapp.Welcome.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tr.makina.guvencapp.Auth.Data.User;
import com.tr.makina.guvencapp.Auth.UI.LoginActivity;
import com.tr.makina.guvencapp.Auth.UI.RegisterActivity;
import com.tr.makina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.makina.guvencapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
@BindView(R.id.signup_btn)
Button signup_btn;
@BindView(R.id.login_btn)
Button login_btn;
private FirebaseAuth mAuth;
public static User logged_in_user = new User();
LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

         signup_btn.setAlpha(0.7f);
         login_btn.setAlpha(0.7f);
         login_btn.setOnClickListener(this);
         signup_btn.setOnClickListener(this);
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
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

    }
    @Override
    public void onClick(View view) {
        Intent intent;
         switch (view.getId()){
             case R.id.login_btn:
                 intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                     startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                 } else {
//                 }
                 startActivity(intent);

                 overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                 break;
             case R.id.signup_btn:
                 intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
//                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                     startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                 } else {
//                     startActivity(intent);
//                 }
                 startActivity(intent);
                 overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                 break;
         }
    }
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }
}
