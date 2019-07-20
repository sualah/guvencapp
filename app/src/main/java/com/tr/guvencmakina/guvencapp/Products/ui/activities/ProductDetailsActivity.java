package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.tr.guvencmakina.guvencapp.Dashboard.adapter.ProductsCardViewAdapter;
import com.tr.guvencmakina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.guvencmakina.guvencapp.Products.data.Product;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.UiHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailsActivity extends AppCompatActivity{
    public Product product;
    @BindView(R.id.product_name_tv)
    TextView product_name_tv;
    @BindView(R.id.product_price_tv)
    TextView product_price_tv;
    @BindView(R.id.product_material_tv)
    TextView product_material_tv;
    @BindView(R.id.product_safety_tv)
    TextView product_safety_tv;
    @BindView(R.id.product_weight_tv)
    TextView product_weight_tv;
    @BindView(R.id.product_length_tv)
    TextView product_length_tv;
    @BindView(R.id.product_width_tv)
    TextView product_width_tv;
    @BindView(R.id.product_category_tv)
    TextView product_category_tv;
    @BindView(R.id.product_other_details_tv)
    TextView product_other_details_tv;
    @BindView(R.id.location_tv)
    TextView location_tv;
    @BindView(R.id.product_performance_tv)
    TextView product_performance_tv;
    @BindView(R.id.product_capacity_tv)
    TextView product_capacity_tv;
    @BindView(R.id.product_height_tv)
    TextView product_height_tv;
    @BindView(R.id.product_image)
    ImageView product_image;
    String previous_activity = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle b = getIntent().getExtras();
        if(b != null){
            previous_activity = b.getString("activity_name","");

        }

        if(previous_activity.equalsIgnoreCase("all_products")){
            product = AllProductsActivity.product;
        } else if(previous_activity.equalsIgnoreCase("products")){
            product = ProductsActivity.product;
        }


        if(product != null){
            product_name_tv.setText("Name: " + product.getName());
            product_price_tv.setText("Price: " + product.getPrice());
            product_material_tv.setText("Material: " + product.getMaterial());
            product_weight_tv.setText("Weight: " + product.getWeight());
            product_length_tv.setText("Length: " + product.getLength());
            product_width_tv.setText("Width: " + product.getWidth());
            product_height_tv.setText("Height: " + product.getHeight());
            product_category_tv.setText("Category: " + product.getCategory());
            product_other_details_tv.setText(product.getOther_details());
            product_safety_tv.setText("Safety: " + product.getSafety());
            location_tv.setText("Location: " + product.getLocation());
            product_performance_tv.setText("Performance: " + product.getPerformance());
            product_capacity_tv.setText("Capacity: " + product.getCapacity());

            Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_image_grey_700_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp).into(product_image);
        }


//        if(UiHelper.USERTYPE.equalsIgnoreCase("admin")){
//            edit_icon.setVisibility(View.VISIBLE);
//            delete_icon.setVisibility(View.VISIBLE);
//        } else {
//            edit_icon.setVisibility(View.GONE);
//            delete_icon.setVisibility(View.GONE);
//        }
//        edit_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ProductLocationMapActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//            }
//        });
//        delete_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ProductLocationMapActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//            }
//        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}