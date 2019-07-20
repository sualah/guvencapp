package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tr.guvencmakina.guvencapp.Dashboard.adapter.ProductsCardViewAdapter;
import com.tr.guvencmakina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.guvencmakina.guvencapp.Products.data.Product;
import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllProductsActivity extends AppCompatActivity{
    private static final String TAG = "all_products" ;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.parentShimmerLayout)
    ShimmerFrameLayout shimmer_l;
    ProductsCardViewAdapter productsCardViewAdapter;
    public static Product product;
    List<Product> products = new ArrayList<>();
    DatabaseReference databaseReference;
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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        productsCardViewAdapter.setOnClickListener((view,position)->{
            TextView  nameTv = view.findViewById(R.id.product_name);
            for (Product p: products){
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
getAllProductsListiner();
    }

    public void getAllProductsListiner(){
        shimmer_l.startShimmerAnimation();
      //  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products");
        databaseReference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                products = new ArrayList<>();
                for (DataSnapshot dataSnapshot: dataSnapshots.getChildren()) {
                    // TODO: handle the post
                //    String productCategoryKey = dataSnapshot.getKey();
                    Product product = dataSnapshot.getValue(Product.class);
                    products.add(product);
                }
                productsCardViewAdapter.update(products);
                shimmer_l.stopShimmerAnimation();
                shimmer_l.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                shimmer_l.stopShimmerAnimation();
                shimmer_l.setVisibility(View.GONE);
                Log.w(TAG, "productCategories:onCancelled", databaseError.toException());
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
                for (Product p : products){
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
                for (Product p : products){
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