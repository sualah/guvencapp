package com.tr.guvencmakina.guvencapp.Dashboard.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsCategoryCardViewAdapter extends RecyclerView.Adapter<ProductsCategoryCardViewAdapter.CardHolder>{
    private Context context;
    private ArrayList<ProductCategory> productCategories = new ArrayList<>();

    public ProductsCategoryCardViewAdapter(Context context) {
        this.context = context;
    }


    public void update(List<ProductCategory> productCategoryList) {
        productCategories.clear();
        productCategories.addAll(productCategoryList);
        notifyDataSetChanged();
    }


//    public void slideCard(RecyclerView recyclerView){
//        final Handler handler = new Handler();
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                int tcount = getItemCount() - 1 ;
//                if(loanCardPosition == tcount){
//                    loanCardPosition = 0;
//                } else {
//                    loanCardPosition++;
//                }
//                recyclerView.smoothScrollToPosition(loanCardPosition);
//                handler.postDelayed(this, 5000);
//            }
//        };
//
//        handler.postDelayed(runnable, 0);
//
//    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_card_item_row,parent, false);

        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        ProductCategory productCategory = productCategories.get(position);
        holder.setDetails(productCategory);
    }

    @Override
    public int getItemCount() {
        return productCategories.size();
    }

    class CardHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView imageView;

        public CardHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            imageView = itemView.findViewById(R.id.product_image);
        }

        public void setDetails(ProductCategory productCategory) {
            name.setText(productCategory.getName());
            imageView.setImageResource(R.drawable.garbage_truck);

        }

    }


}
