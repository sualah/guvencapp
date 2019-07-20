package com.tr.guvencmakina.guvencapp.Dashboard.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tr.guvencmakina.guvencapp.Auth.Data.User;
import com.tr.guvencmakina.guvencapp.Auth.UI.LoginActivity;
import com.tr.guvencmakina.guvencapp.Dashboard.adapter.ProductsCategoryCardViewAdapter;
import com.tr.guvencmakina.guvencapp.Products.data.Product;
import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.AddProductCategoryName;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.AddProductDetailsActivity;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.AllProductsActivity;
import com.tr.guvencmakina.guvencapp.Products.ui.activities.ProductsActivity;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.UiHelper;
import com.tr.guvencmakina.guvencapp.Welcome.ui.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.parentShimmerLayout)
    ShimmerFrameLayout shimmer_l;
    List<ProductCategory> productCategories = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();
    public static List<String> product_category_names = new ArrayList<>();

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
        productsCategoryCardViewAdapter.setOnClickListener((view, position) -> {
            TextView name = view.findViewById(R.id.product_name);
            shimmer_l.setVisibility(View.VISIBLE);
            shimmer_l.startShimmerAnimation();
            getProductsListner(name.getText().toString());
        });

        add_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProductCategoryName.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProductDetailsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // get & set nav_header_view views
        View nav_header_view = navigationView.getHeaderView(0);
        ImageView user_image_view = nav_header_view.findViewById(R.id.user_image_view);
        TextView user_name_tv = nav_header_view.findViewById(R.id.user_name_tv);
        TextView user_email_tv = nav_header_view.findViewById(R.id.email_tv);
        LinearLayout header_loader = nav_header_view.findViewById(R.id.loading_view);


        if (WelcomeActivity.logged_in_user != null) {
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
                    if (user_value != null) {
                        header_loader.setVisibility(View.GONE);
                        user_name_tv.setVisibility(View.VISIBLE);
                        user_email_tv.setVisibility(View.VISIBLE);
                        user_image_view.setVisibility(View.VISIBLE);
                        user_name_tv.setText("Hi, " + user_value.getName());
                        user_email_tv.setText(user_value.getEmail());

                        UiHelper.USERTYPE = user_value.getType();
                        if (user_value.getType().equalsIgnoreCase("admin")) {
                            add_menu.setVisibility(View.VISIBLE);
                        } else {
                            add_menu.setVisibility(View.GONE);
                        }
                        //    user_image_view.setImageResource(R.drawable.flag_burkina_faso);

                        Log.d(TAG, "Value is: " + user_value.toString());
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //   Log.w(TAG, "Failed to read value.", error.toException());
                    Toasty.error(MainActivity.this, error.getMessage()).show();
                }
            });

        }
        shimmer_l.startShimmerAnimation();
        getCategoryListiner();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getCategoryListiner() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("product_categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                productCategories = new ArrayList<>();
                for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                    // TODO: handle the post
                    String productCategoryKey = dataSnapshot.getKey();
                    ProductCategory productCategory = dataSnapshot.getValue(ProductCategory.class);
                    productCategory.setUid(productCategoryKey);
                    System.out.println("Added category " + productCategory.toString());
                    product_category_names.add(productCategory.getName());
                    if (!productCategories.contains(productCategory)) {
                        productCategories.add(productCategory);
                    }
                }
                productsCategoryCardViewAdapter.update(productCategories);
                // productsCategoryCardViewAdapter.notifyDataSetChanged();
                shimmer_l.stopShimmerAnimation();
                shimmer_l.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //     databaseReference.removeEventListener(null);
                Log.w(TAG, "productCategories:onCancelled", databaseError.toException());
            }
        });

    }

    public void getProductsListner(String category) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        Query query = databaseReference.child("products").orderByChild("category").equalTo(category);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                products = new ArrayList<>();
                for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                    // TODO: handle the post
                    String productCategoryKey = dataSnapshot.getKey();
                    Product product = dataSnapshot.getValue(Product.class);
                    // product.setUid(productCategoryKey);
                    //     System.out.println("Added product " + product.toString());
                    products.add(product);
                }

                shimmer_l.stopShimmerAnimation();
                shimmer_l.setVisibility(View.GONE);
                if (products.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    Toasty.info(getApplicationContext(), "Sorry " + category + " has no products.").show();
                }

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
        //  getMenuInflater().inflate(R.menu.main, menu);
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Product Categories");
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("search query " + query);
                // productsCategoryCardViewAdapter.getFilter().filter(query);
                ArrayList<ProductCategory> newList = new ArrayList<>();
                for (ProductCategory p : productCategories) {
                    System.out.println("ProductCategory " + p.toString());
                    String name = p.getUid().toLowerCase();
                    if (name.contains(query.toLowerCase())) {
                        newList.add(p);
                    }
                }
                productsCategoryCardViewAdapter.update(newList);
                //   searchProductCategories(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //productsCategoryCardViewAdapter.getFilter().filter(newText);
                System.out.println("search query " + newText);
                //   searchProductCategories(newText);
                ArrayList<ProductCategory> newList = new ArrayList<>();
                for (ProductCategory p : productCategories) {
                    System.out.println("ProductCategory " + p.toString());
                    String name = p.getUid().toLowerCase();
                    System.out.println(" p.getUid() " + p.getUid());
                    if (name.contains(newText.toLowerCase())) {
                        newList.add(p);
                    }
                }
                productsCategoryCardViewAdapter.update(newList);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.all_products) {
            Intent intent = new Intent(MainActivity.this, AllProductsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (id == R.id.help) {
           callPhoneNumber("+905412596344");
        }  else if (id == R.id.log_out) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void callPhoneNumber(String phone) {
        try {
            if(Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
