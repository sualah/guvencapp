package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tr.guvencmakina.guvencapp.Dashboard.ui.MainActivity;
import com.tr.guvencmakina.guvencapp.Products.adapter.CustomSpinnerAdapter;
import com.tr.guvencmakina.guvencapp.Products.data.Product;
import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AddProductDetailsActivity extends AppCompatActivity{

    private static final String TAG = "AddProductCategoryName";
    @BindView(R.id.add_product_details_ll)
    LinearLayout add_product_details_ll;
    @BindView(R.id.add_product_details_cv)
    CardView add_product_details_cv;
    @BindView(R.id.product_name_field)
    TextInputEditText product_name_field;
    @BindView(R.id.product_price_field)
    TextInputEditText product_price_field;
    @BindView(R.id.product_length_field)
    TextInputEditText product_length_field;
    @BindView(R.id.product_width_field)
    TextInputEditText product_width_field;
    @BindView(R.id.product_weight_field)
    TextInputEditText product_weight_field;
    @BindView(R.id.product_material_field)
    TextInputEditText product_material_field;
    @BindView(R.id.product_other_details_field)
    TextInputEditText product_other_details_field;
    @BindView(R.id.product_performance_field)
    TextInputEditText product_performance_field;
    @BindView(R.id.product_capacity_field)
    TextInputEditText product_capacity_field;
    @BindView(R.id.product_location_field)
    TextInputEditText product_location_field;
    @BindView(R.id.product_height_field)
    TextInputEditText product_height_field;
    @BindView(R.id.product_safety_radio)
    RadioGroup product_safety_radio;
    @BindView(R.id.categories_spinner)
    Spinner categories_spinner;
    @BindView(R.id.next)
    TextView next;
    static Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        List<String> categories_list = new ArrayList<>();
        categories_list.add(0,"SELECT PRODUCT CATEGORY");
        categories_list.addAll(MainActivity.product_category_names);
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this,categories_list);
        categories_spinner.setAdapter(customSpinnerAdapter);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                  //  categoryName = category_name_field.getText().toString();
                    product = new Product();
                    RadioButton checkedRadio = findViewById(product_safety_radio.getCheckedRadioButtonId());
                    product.setName(product_name_field.getText().toString());
                    product.setMaterial(product_material_field.getText().toString());
                    product.setPrice(product_price_field.getText().toString());
                    product.setOther_details(product_other_details_field.getText().toString());
                    product.setWidth(product_width_field.getText().toString());
                    product.setLength(product_length_field.getText().toString());
                    product.setWeight(product_weight_field.getText().toString());
                    product.setPerformance(product_performance_field.getText().toString());
                    product.setCapacity(product_capacity_field.getText().toString());
                    product.setLocation(product_location_field.getText().toString());
                    product.setHeight(product_height_field.getText().toString());
                    product.setSafety(checkedRadio.getText().toString());
                    product.setCategory(categories_spinner.getSelectedItem().toString());

                    System.out.println("ProductDetails " + product.toString());
                    Intent intent = new Intent(getApplicationContext(), AddProductImage.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }

            }
        });


    }
//    public void showLoader(){
//        loading_view.setVisibility(View.VISIBLE);
//        add_name_ll.setVisibility(View.GONE);
//    }
//
//    public void hideLoader(){
//        loading_view.setVisibility(View.GONE);
//        add_name_ll.setVisibility(View.VISIBLE);
//    }

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

    public boolean validate(){
        boolean valid = true;
        if(product_name_field.getText().toString().isEmpty()){
            product_name_field.setError(getString(R.string.product_name_empty));
            Toasty.error(getApplicationContext(),getString(R.string.product_name_empty)).show();
            valid = false;
        } else if(categories_spinner.getSelectedItemPosition() == 0){
            Toasty.error(getApplicationContext(),getString(R.string.select_product_category)).show();
            valid = false;
        }
        return  valid;
    }


}