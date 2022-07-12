package com.example.vasbyfrisorenandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.product.Product;
import com.example.vasbyfrisorenandroid.model.product.ProductAdapter;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.service.ServiceAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ProductPageFragment extends Fragment {

    private View rootView;

    //Product
    private List<Product> productList;
    private RecyclerView productRecyclerView;
    private RecyclerView.Adapter productAdapter;
    private GridLayoutManager productLayoutManager;

    //Other
    private SearchView searchProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.product_page, container, false);
        initProductRecyclerView();

        View pagerView = inflater.inflate(R.layout.home, container, true);
        TabLayout tabLayout = rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pagerView.findViewById(R.id.pager), true);
        searchProduct = rootView.findViewById(R.id.search_product);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        searchProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return true;
            }
        });
    }

    private void initProductRecyclerView() {

        productList = new ArrayList<>();
        productList.add(new Product(R.drawable.logo, "Schampo1", 230));
        productList.add(new Product(R.drawable.logo, "Schampo2", 230));
        productList.add(new Product(R.drawable.logo, "Schampo3", 230));
        productList.add(new Product(R.drawable.logo, "Schampo4", 230));
        productList.add(new Product(R.drawable.logo, "Schampo5", 230));
        productList.add(new Product(R.drawable.logo, "Schampo6", 230));

        productRecyclerView = rootView.findViewById(R.id.recyclerView2);
        productRecyclerView.setHasFixedSize(true);
        productLayoutManager = new GridLayoutManager(getContext(), 2);
        productAdapter = new ProductAdapter(productList);

        productRecyclerView.setLayoutManager(productLayoutManager);
        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.addItemDecoration(new SpacesItemDecoration(50));
    }

    private void search(String searchQuery){
        List<Product> products = new ArrayList<>();
        for(Product service : productList){
            if(service.getTypeOfProduct().toLowerCase().contains(searchQuery.toLowerCase())){
                products.add(service);
            }
        }
        productAdapter = new ProductAdapter(products);
        productRecyclerView.setAdapter(productAdapter);
    }
}
