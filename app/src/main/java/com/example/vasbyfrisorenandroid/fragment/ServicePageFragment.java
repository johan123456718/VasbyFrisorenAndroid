package com.example.vasbyfrisorenandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.service.OnServiceListener;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.service.ServiceAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ServicePageFragment extends Fragment implements OnServiceListener {
    private View rootView;
    //Service
    private List<Service> serviceList;
    private RecyclerView serviceRecyclerView;
    private RecyclerView.Adapter serviceAdapter;
    private LinearLayoutManager serviceLayoutManager;

    //Firebase
    private DatabaseReference dbServiceReference;

    //Other
    private SearchView searchService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.service_page, container, false);
        dbServiceReference = FirebaseDatabase.getInstance().getReference().child("Services");
        initServiceRecyclerView();
        View pagerView = inflater.inflate(R.layout.home, container, true);
        TabLayout tabLayout = rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pagerView.findViewById(R.id.pager), true);
        searchService = rootView.findViewById(R.id.search_service);

        return rootView;
    }


    public void onDestroy() {
        super.onDestroy();
        serviceList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        searchService.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void initServiceRecyclerView() {
        //If data doesn't exist
        dbServiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    serviceList = new ArrayList<>();
                    serviceList.add(new Service(R.drawable.klippning, "Dam klippning inkl. konsultation, tvätt med lätt huvudmassage, fin föning samt styling", 420));
                    serviceList.add(new Service(R.drawable.tvatt, "Dam klippning ”Pensionär”", 300));
                    serviceList.add(new Service(R.drawable.skagg, "Herr klippning inkl. tvätt med lätt huvudmassage", 350));
                    serviceList.add(new Service(R.drawable.nopp, "Herr klippning ”Pensionär”", 250));
                    serviceList.add(new Service(R.drawable.logo, "Barnklippning 0-10 år", 250));
                    serviceList.add(new Service(R.drawable.logo, "Ungdom 11-17år (Kille)", 290));
                    serviceList.add(new Service(R.drawable.logo, "Ungdom 11-17år (Tjej)", 340));
                    serviceList.add(new Service(R.drawable.logo, "Rakning huvud med maskin", 150));
                    serviceList.add(new Service(R.drawable.logo, "Rakning huvud med kniv", 200));
                    serviceList.add(new Service(R.drawable.logo, "Folieslingor", 1100));
                    serviceList.add(new Service(R.drawable.logo, "Slingor i hätta", 950));
                    serviceList.add(new Service(R.drawable.logo, "Hårfärg", 950));
                    serviceList.add(new Service(R.drawable.logo, "Toning", 890));
                    serviceList.add(new Service(R.drawable.logo, "Avfärgning", 890));
                    serviceList.add(new Service(R.drawable.logo, "Ögonbrynsfärgning inkl. ögonbrynsplock", 350));
                    serviceList.add(new Service(R.drawable.logo, "Ögonbrynsplockning", 200));
                    serviceList.add(new Service(R.drawable.logo, "Fransfärgning", 180));
                    serviceList.add(new Service(R.drawable.logo, "Tvätt & Fön", 300));
                    serviceList.add(new Service(R.drawable.logo, "Lugg klippning", 100));
                    serviceList.add(new Service(R.drawable.logo, "Hål tagning", 349));
                    dbServiceReference.setValue(serviceList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //If data exist
        dbServiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    serviceList = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        serviceList.add(ds.getValue(Service.class));
                    }
                    serviceRecyclerView = rootView.findViewById(R.id.recyclerView);
                    serviceRecyclerView.setNestedScrollingEnabled(false);
                    serviceLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    serviceRecyclerView.setLayoutManager(serviceLayoutManager);
                    serviceRecyclerView.setHasFixedSize(true);
                    serviceAdapter = new ServiceAdapter(serviceList, ServicePageFragment.this);
                    serviceRecyclerView.setAdapter(serviceAdapter);
                    serviceRecyclerView.addItemDecoration(new SpacesItemDecoration(60));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onServiceClick(View v, int position) {

        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        Fragment fragment = new BookingFragment();
        Service service = serviceList.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable("service", service);
        fragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    private void search(String searchQuery){
        List<Service> services = new ArrayList<>();
        for(Service service : serviceList){
            if(service.getServiceTitle().toLowerCase().contains(searchQuery.toLowerCase())){
                services.add(service);
            }
        }
        serviceAdapter = new ServiceAdapter(services, ServicePageFragment.this);
        serviceRecyclerView.setAdapter(serviceAdapter);
    }

}
