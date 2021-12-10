package com.example.vasbyfrisorenandroid.model.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.vasbyfrisorenandroid.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements View.OnClickListener {

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private TextView productType, productPrice;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.productImg);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }

    private List<Product> productList;

    public ProductAdapter(List<Product> productList){
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.img.setImageResource(product.getImgResource());
        holder.productType.setText(product.getTypeOfProduct());
        holder.productPrice.setText(String.valueOf(product.getPrice()) + ":-");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    @Override
    public void onClick(View view) {

    }
}
