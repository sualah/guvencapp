package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tr.guvencmakina.guvencapp.BuildConfig;
import com.tr.guvencmakina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.guvencmakina.guvencapp.Enums.ImagePickerEnum;
import com.tr.guvencmakina.guvencapp.Listners.IImagePickerLister;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.FileUtils;
import com.tr.guvencmakina.guvencapp.Utils.UiHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.CAMERA_STORAGE_REQUEST_CODE;
import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.ONLY_CAMERA_REQUEST_CODE;
import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.ONLY_STORAGE_REQUEST_CODE;

public class AddProductCategoryImage extends AppCompatActivity implements IImagePickerLister {

    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
//    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
//    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
//    public static final int ONLY_STORAGE_REQUEST_CODE = 613;
    private String currentPhotoPath = "";
    private UiHelper uiHelper = new UiHelper();

    @BindView(R.id.add_img_ll)
    LinearLayout add_img_ll;
    @BindView(R.id.image_view)
    ImageView image_view;
    @BindView(R.id.loading_view)
    LinearLayout loading_view;
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.save)
    TextView save;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    boolean image_selected = false;
    Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_category_image);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        add_img_ll.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (uiHelper.checkSelfPermissions(this))
                    uiHelper.showImagePickerDialog(this, this);
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(image_selected && image_uri != null){
                 uploadImageFile(image_uri);
             } else {
                 Toasty.error(getApplicationContext(),"Please select an image.").show();
             }
//
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddProductCategoryName.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AddProductCategoryName.class);
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
                Intent intent = new Intent(getApplicationContext(), AddProductCategoryName.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, this);
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                uiHelper.toast(this, "ImageCropper needs Storage access in order to store your profile picture.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                uiHelper.toast(this, "ImageCropper needs Camera access in order to take profile picture.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                uiHelper.toast(this, "ImageCropper needs Camera and Storage access in order to take profile picture.");
                finish();
            }
        } else if (requestCode == ONLY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, this);
            else {
                uiHelper.toast(this, "ImageCropper needs Camera access in order to take profile picture.");
                finish();
            }
        } else if (requestCode == ONLY_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, this);
            else {
                uiHelper.toast(this, "ImageCropper needs Storage access in order to store your profile picture.");
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {

            if (data != null) {
                Uri uri = UCrop.getOutput(data);
                image_uri = UCrop.getOutput(data);
                image_selected = true;
                uploadImageFile(uri);
                showImage(uri);
            } else {
                Toasty.error(getApplicationContext(),"Please select another image.").show();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri sourceUri = data.getData();
                File file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);
                openCropActivity(sourceUri, destinationUri);
            } catch (Exception e) {
                uiHelper.toast(this, "Please select another image");
            }
        }
    }

    private void openImagesDocument() {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,PICK_IMAGE_GALLERY_REQUEST_CODE);

//        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        pictureIntent.setType("image/*");
//        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
//            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        }
//        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);
    }

    private void showImage(Uri imageUri) {
        try {
            File file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file = FileUtils.getFile(this, imageUri);
            } else {
                file = new File(currentPhotoPath);
            }

            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            image_view.setImageBitmap(bitmap);
        } catch (Exception e) {
            uiHelper.toast(this, "Please select different profile picture.");
        }
    }

    @Override
    public void onOptionSelected(ImagePickerEnum imagePickerEnum) {
        if (imagePickerEnum == ImagePickerEnum.FROM_CAMERA)
            openCamera();
        else if (imagePickerEnum == ImagePickerEnum.FROM_GALLERY)
            openImagesDocument();
    }

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file;
        try {
            file = getImageFile(); // 1
        } catch (Exception e) {
            e.printStackTrace();
            uiHelper.toast(this, "Please take another image");
            return;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(150, 150)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

public void saveCategory(String image_url){
    if(!AddProductCategoryName.categoryName.isEmpty()){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("product_categories").child(AddProductCategoryName.categoryName);
        HashMap<String,String> userMap = new HashMap<>();
        userMap.put("name", AddProductCategoryName.categoryName);
        userMap.put("image", image_url );
        mDatabaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    }

}

    public void uploadImageFile(Uri fileUri){
        //Uri fileUri = Uri.fromFile(file);
        //  Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        showLoader();
        StorageReference riversRef = storageReference.child("product_category_images/" + AddProductCategoryName.categoryName + ".jpg");
        UploadTask uploadTask  = riversRef.putFile(fileUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                if (!task.isSuccessful()) {
                 //   throw task.getException();
                    System.out.println(task.getException());
                    hideLoader();
                    Toasty.error(getApplicationContext(),"Image failed to upload.").show();

                }
                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    System.out.println("image_url " + downloadUri.toString());
                    Toasty.success(getApplicationContext(),"Image uploaded successful").show();
                    saveCategory(downloadUri.toString());
                } else {
                    // Handle failures
                    hideLoader();
                   Toasty.error(getApplicationContext(),"Image failed to upload.").show();
                }
            }
        });
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                        hideLoader();
//                        Toasty.error(getApplicationContext(),"Image failed to upload.").show();
//                    }
//                });
    }

    public void showLoader(){
        loading_view.setVisibility(View.VISIBLE);
        add_img_ll.setVisibility(View.GONE);
    }

    public void hideLoader(){
        loading_view.setVisibility(View.GONE);
        add_img_ll.setVisibility(View.VISIBLE);
    }
}