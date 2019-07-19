package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tr.guvencmakina.guvencapp.BuildConfig;
import com.tr.guvencmakina.guvencapp.Dashboard.adapter.ProductsCardViewAdapter;
import com.tr.guvencmakina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.guvencmakina.guvencapp.Enums.ImagePickerEnum;
import com.tr.guvencmakina.guvencapp.Listners.IImagePickerLister;
import com.tr.guvencmakina.guvencapp.Products.data.Product;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.FileUtils;
import com.tr.guvencmakina.guvencapp.Utils.UiHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.CAMERA_STORAGE_REQUEST_CODE;
import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.ONLY_CAMERA_REQUEST_CODE;
import static com.tr.guvencmakina.guvencapp.Utils.UiHelper.ONLY_STORAGE_REQUEST_CODE;

public class ProductsActivity extends AppCompatActivity{
    private static final String TAG = "products";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.parentShimmerLayout)
    ShimmerFrameLayout shimmer_l;
    ProductsCardViewAdapter productsCardViewAdapter;
    public static Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productsCardViewAdapter = new ProductsCardViewAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //   recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(productsCardViewAdapter);
        shimmer_l.startShimmerAnimation();
        productsCardViewAdapter.update(MainActivity.products);
        shimmer_l.stopShimmerAnimation();
        shimmer_l.setVisibility(View.GONE);
        productsCardViewAdapter.setOnClickListener((view,position)->{
            TextView  nameTv = view.findViewById(R.id.product_name);
            for (Product p: MainActivity.products){
               if(p.getName().equalsIgnoreCase(nameTv.getText().toString())){
                   product = p;
                   System.out.println("Product selected is " + product.toString());
                   Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                   intent.putExtra("activity_name",TAG);
                   startActivity(intent);
                   overridePendingTransition(R.anim.fadein, R.anim.fadeout);
               }
           }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.main, menu);
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Products");
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("search query " + query);
                // productsCategoryCardViewAdapter.getFilter().filter(query);
                ArrayList<Product> newList=new ArrayList<>();
                for (Product p : MainActivity.products){
                    String name = p.getName().toLowerCase();
                    if (name.contains(query.toLowerCase())){
                        newList.add(p);
                    }
                }
                productsCardViewAdapter.update(newList);
                //   searchProductCategories(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //productsCategoryCardViewAdapter.getFilter().filter(newText);
                System.out.println("search query " + newText);
                //   searchProductCategories(newText);
                ArrayList<Product> newList=new ArrayList<>();
                for (Product p : MainActivity.products){
                    String name = p.getName().toLowerCase();
                    if (name.contains(newText.toLowerCase())){
                        newList.add(p);
                    }
                }
                productsCardViewAdapter.update(newList);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}