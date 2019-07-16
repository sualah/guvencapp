package com.tr.guvencmakina.guvencapp.Dashboard.ui;

import android.content.Intent;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tr.guvencmakina.guvencapp.Auth.Data.User;
import com.tr.guvencmakina.guvencapp.Auth.UI.LoginActivity;
import com.tr.guvencmakina.guvencapp.Dashboard.adapter.ProductsCategoryCardViewAdapter;
import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.AddProductCategoryImage;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.ProductCategoryStepperActivity;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.ProductsStepperActivity;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Settings.SettingsActivity;
import com.tr.guvencmakina.guvencapp.Welcome.ui.WelcomeActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "main_activity";
    DatabaseReference myRef;
    ProductsCategoryCardViewAdapter productsCategoryCardViewAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.add_menu)
    FloatingActionMenu add_menu;
    @BindView(R.id.add_category)
    FloatingActionButton add_category_btn;
    @BindView(R.id.add_product)
    FloatingActionButton add_product_btn;
    List<ProductCategory> productCategories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        productsCategoryCardViewAdapter = new ProductsCategoryCardViewAdapter(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
     //   recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(productsCategoryCardViewAdapter);


        add_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProductCategoryImage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductsStepperActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

//        productCategories.add(new ProductCategory("Garbage Truck",R.drawable.garbage_truck));
//        productCategories.add(new ProductCategory("Dump",R.drawable.dump));
//        productCategories.add(new ProductCategory("Fire Department",R.drawable.fire_department));
//        productCategories.add(new ProductCategory("Remove",R.drawable.remove));
//        productCategories.add(new ProductCategory("Mobile Workshop",R.drawable.mobile_workshop));
//        productCategories.add(new ProductCategory("Platform with Basket",R.drawable.platform_with_basket));
//        productCategories.add(new ProductCategory("Vacuum Road Sweeper",R.drawable.vacuum_road_sweeper));
//        productCategories.add(new ProductCategory("Garbage Container",R.drawable.garbage_container));
//        productCategories.add(new ProductCategory("Semi Trailer Garbage Transfer",R.drawable.semi_trailer_garbage_transfer));
//        productCategories.add(new ProductCategory("Combined Grooving",R.drawable.combined_grooving));
//        productCategories.add(new ProductCategory("Cesspool",R.drawable.cesspool));
//        productCategories.add(new ProductCategory("Minipak Trash",R.drawable.minipak_trash));
//        productCategories.add(new ProductCategory("Fuel Tanker",R.drawable.fuel_tanker));
//        productCategories.add(new ProductCategory("Intermediate Transmission Group",R.drawable.intermediate_transmission_group));
//        productCategories.add(new ProductCategory("PTO Gearbox",R.drawable.pto_gearbox));
//        productCategories.add(new ProductCategory("Hydraulic Pump",R.drawable.hydraulic_pump));
//        productCategories.add(new ProductCategory("Vacuum Pump",R.drawable.vacuum_pump));
//        productCategories.add(new ProductCategory("Directional Valves",R.drawable.directional_valves));
//        productCategories.add(new ProductCategory("Hydraulic Cylinder",R.drawable.hydraulic_cylinder));


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // get & set nav_header_view views
        View nav_header_view = navigationView.getHeaderView(0);
        CircleImageView user_image_view= nav_header_view.findViewById(R.id.user_image_view);
        TextView user_name_tv = nav_header_view.findViewById(R.id.user_name_tv);
        TextView user_email_tv = nav_header_view.findViewById(R.id.email_tv);
        LinearLayout header_loader = nav_header_view.findViewById(R.id.loading_view);


        if(WelcomeActivity.logged_in_user != null){
            String uid = WelcomeActivity.logged_in_user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            header_loader.setVisibility(View.VISIBLE);
            user_name_tv.setVisibility(View.GONE);
            user_email_tv.setVisibility(View.GONE);
            user_image_view.setVisibility(View.GONE);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    User user_value = dataSnapshot.getValue(User.class);
                    if(user_value != null){
                        header_loader.setVisibility(View.GONE);
                        user_name_tv.setVisibility(View.VISIBLE);
                        user_email_tv.setVisibility(View.VISIBLE);
                        user_image_view.setVisibility(View.VISIBLE);
                        user_name_tv.setText("Hi, " + user_value.getName());
                        user_email_tv.setText(user_value.getEmail());
                    //    user_image_view.setImageResource(R.drawable.flag_burkina_faso);

                        Log.d(TAG, "Value is: " + user_value.toString());
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                 //   Log.w(TAG, "Failed to read value.", error.toException());
                    Toasty.error(MainActivity.this,error.getMessage()).show();
                }
            });

        }
        getCategoryListiner();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getCategoryListiner(){
        System.out.println("getCategoryListiner");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("product_categories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                System.out.println("getCategoryListiner dataSnapshots.exists() " + dataSnapshots.exists());

                productCategories = new ArrayList<>();
                for (DataSnapshot dataSnapshot: dataSnapshots.getChildren()) {
                    // TODO: handle the post
                    String productCategoryKey = dataSnapshot.getKey();
                    ProductCategory productCategory = dataSnapshot.getValue(ProductCategory.class);
                    productCategory.setUid(productCategoryKey);
                    System.out.println("Added category " + productCategory.toString());
                    if(!productCategories.contains(productCategory)){
                        productCategories.add(productCategory);
                    }
                }

                productsCategoryCardViewAdapter.update(productCategories);
                productsCategoryCardViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "productCategories:onCancelled", databaseError.toException());
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
          //  super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.profile) {

        } else if (id == R.id.help) {

        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (id == R.id.log_out) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
