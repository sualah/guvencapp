package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tr.guvencmakina.guvencapp.BuildConfig;
import com.tr.guvencmakina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.guvencmakina.guvencapp.Enums.ImagePickerEnum;
import com.tr.guvencmakina.guvencapp.Listners.IImagePickerLister;
import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.FileUtils;
import com.tr.guvencmakina.guvencapp.Utils.UiHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.CAMERA_STORAGE_REQUEST_CODE;
import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.ONLY_CAMERA_REQUEST_CODE;
import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.ONLY_STORAGE_REQUEST_CODE;

public class AddProductCategoryName extends AppCompatActivity{

    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    private static final String TAG = "AddProductCategoryName";
    //    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
//    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
//    public static final int ONLY_STORAGE_REQUEST_CODE = 613;
    private String currentPhotoPath = "";
    private UiHelper uiHelper = new UiHelper();

    @BindView(R.id.add_name_ll)
    LinearLayout add_name_ll;
    @BindView(R.id.loading_view)
    LinearLayout loading_view;
    @BindView(R.id.category_name_field)
    TextInputEditText category_name_field;
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.save)
    TextView save;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_category_name);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLoader();
                String name = category_name_field.getText().toString();
                if(!name.isEmpty()){
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("product_categories");
                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("image", "");

//                    String key = mDatabaseReference.child("posts").push().getKey();
//                    Post post = new Post(userId, username, title, body);
//                    Map<String, Object> postValues = post.toMap();
//
//                    Map<String, Object> childUpdates = new HashMap<>();
//                    childUpdates.put("/posts/" + key, postValues);
//                    childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
                    mDatabaseReference.push().setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toasty.success(getApplicationContext(),getString(R.string.category_added_successful), Toast.LENGTH_SHORT).show();
                                hideLoader();
                             //   getCategoryListiner(mDatabaseReference);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            } else {
                                System.out.println("adding category failed " + task.getException().getMessage());
                                Toasty.error(getApplicationContext(),getString(R.string.category_addition_failure),Toast.LENGTH_SHORT).show();
                                hideLoader();
                            }
                        }
                    });

                } else {
                    category_name_field.setError(getString(R.string.category_name_empty));
                    Toasty.error(AddProductCategoryName.this,getString(R.string.category_name_empty)).show();
                }

//
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddProductCategoryImage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

    }  public void showLoader(){
        loading_view.setVisibility(View.VISIBLE);
        add_name_ll.setVisibility(View.GONE);
    }

    public void hideLoader(){
        loading_view.setVisibility(View.GONE);
        add_name_ll.setVisibility(View.VISIBLE);
    }

    public void getCategoryListiner(DatabaseReference databaseReference){
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                String productCategoryKey = dataSnapshot.getKey();
                ProductCategory productCategory = dataSnapshot.getValue(ProductCategory.class);
                productCategory.setUid(productCategoryKey);
                System.out.println("Added category " + productCategory.toString());



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                ProductCategory productCategory = dataSnapshot.getValue(ProductCategory.class);
                String productCategoryKey = dataSnapshot.getKey();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String productCategoryKey = dataSnapshot.getKey();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                ProductCategory productCategory = dataSnapshot.getValue(ProductCategory.class);
                String productCategoryKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "addcategory:onCancelled", databaseError.toException());
                Toasty.error(getApplicationContext(), getString(R.string.category_addition_failure),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AddProductCategoryImage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), AddProductCategoryImage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}