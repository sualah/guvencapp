package com.tr.guvencmakina.guvencapp.Dashboard.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tr.guvencmakina.guvencapp.Listners.RecyclerViewClickListener;
import com.tr.guvencmakina.guvencapp.Products.data.Product;
import com.tr.guvencmakina.guvencapp.Products.data.ProductCategory;
import com.tr.guvencmakina.guvencapp.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsCardViewAdapter extends RecyclerView.Adapter<ProductsCardViewAdapter.CardHolder>{
    private Context context;
    private ArrayList<Product> products = new ArrayList<>();
    private RecyclerViewClickListener mListener;
    public ProductsCardViewAdapter(Context context) {
        this.context = context;
    }

    public void setOnClickListener(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void update(List<Product> productList) {
        products.clear();
        products.addAll(productList);
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
        return new CardHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Product product = products.get(position);
        holder.setDetails(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private ImageView imageView;
        private RecyclerViewClickListener mListener;

        public CardHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            name = itemView.findViewById(R.id.product_name);
            imageView = itemView.findViewById(R.id.product_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                mListener.onClick(view, pos);
            }

        }

        public void setDetails(Product product) {
            name.setText(product.getName());
            //  imageView.setImageResource(R.drawable.garbage_truck);
            Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_image_grey_700_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp).into(imageView);

        }
    }

}
