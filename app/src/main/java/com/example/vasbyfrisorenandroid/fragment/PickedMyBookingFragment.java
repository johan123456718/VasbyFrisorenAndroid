package com.example.vasbyfrisorenandroid.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.booking.BookedTime;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeSlot;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class PickedMyBookingFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Service service;
    private BookedTime bookedTime;

    private TextView bookingService, bookingBarber, bookingDate, bookingPrice, bookingTime;
    private ImageView bookingImg, backButton, ivOutput;
    private Button cancelBookButton;
    private int selectedItemId;//, timeSlotIndex;
    private String barber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_picked_mybooking, container, false);

        bookingService = rootView.findViewById(R.id.selected_booking_service);
        bookingBarber = rootView.findViewById(R.id.selected_booking_barber);
        bookingDate = rootView.findViewById(R.id.selected_booking_date);
        bookingPrice = rootView.findViewById(R.id.selected_booking_price);
        bookingTime = rootView.findViewById(R.id.selected_booking_time);
        bookingImg = rootView.findViewById(R.id.selected_booking_img);
        cancelBookButton = rootView.findViewById(R.id.cancelBook_button);
        backButton = rootView.findViewById(R.id.backbutton);
        ivOutput = rootView.findViewById(R.id.iv_output);
        Bundle bundle = this.getArguments();


        if (bundle != null) {
            barber = bundle.getString("barber");
            service = bundle.getParcelable("service");
            bookedTime = bundle.getParcelable("bookedTime");
            bookingService.setText(service.getServiceTitle());
            bookingBarber.setText(barber);
            bookingDate.setText(bookedTime.getBookedDate());
            bookingPrice.setText(String.valueOf(service.getPrice()) + " kr");
            bookingTime.setText(bookedTime.getTimeTaken());
            bookingImg.setImageResource(service.getImgResource());
            selectedItemId = bundle.getInt("selectedItemId");
            Log.d("selectedItemId", String.valueOf(selectedItemId));
            Log.d("barber", String.valueOf(barber));
            Log.d("bookedTime.getWeek", String.valueOf(bookedTime.getWeek()));
            Log.d("bookedTime.getBookedDay", bookedTime.getBookedDay());
            initQRCode();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        backButton.setOnClickListener(this);
        cancelBookButton.setOnClickListener(this);
    }

    private void initQRCode() {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            String encodedText = encode(selectedItemId + " " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            BitMatrix matrix = writer.encode(encodedText,
                    BarcodeFormat.QR_CODE,
                    350,
                    350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            ivOutput.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private String encode(String str) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backbutton:
                Fragment fragment = new MyBookingsFragment();
                Bundle b = this.getArguments();
                if (b != null) {
                    int notifications = b.getInt("bookingCount");
                    b.putInt("bookingCount", notifications);
                    fragment.setArguments(b);
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                break;

            case R.id.cancelBook_button:

                DatabaseReference dbUsersReference = FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("bookings")
                        .child(String.valueOf(selectedItemId));

                dbUsersReference.child("bookedTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("timeTaken")) {
                            String timeTaken = (String) snapshot.child("timeTaken").getValue(String.class);
                            if(timeTaken != null) {
                                Log.d("[PickedMyBookingFragment].timeTaken", String.valueOf(timeTaken));
                                Log.d("[PickedMyBookingFragment].barber", String.valueOf(barber));
                                Log.d("[PickedMyBookingFragment].bookedTime.getWeek", String.valueOf(bookedTime.getWeek()));
                                Log.d("[PickedMyBookingFragment].bookedTime.getBookedDay", bookedTime.getBookedDay());
                                DatabaseReference dbTimeSlotReference = FirebaseDatabase
                                        .getInstance()
                                        .getReference("Timeslot")
                                        .child(barber)
                                        .child(String.valueOf(bookedTime.getWeek()))
                                        .child(bookedTime.getBookedDay());
                                dbTimeSlotReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int timeSlotIndex = getTimeSlotIndex(timeTaken, snapshot);
                                        DatabaseReference dbTimeSlotReference = FirebaseDatabase
                                                .getInstance()
                                                .getReference("Timeslot")
                                                .child(barber)
                                                .child(String.valueOf(bookedTime.getWeek()))
                                                .child(bookedTime.getBookedDay())
                                                .child(String.valueOf(timeSlotIndex))
                                                .child("available");
                                        dbUsersReference.removeValue().addOnCompleteListener(v -> enableTimeSlotAgain(dbTimeSlotReference));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{
                                Log.d("[PickedMyBookingFragment].timeTaken", "value of child node 'timeTaken' is null");
                            }
                        }else {
                            Log.d("[PickedMyBookingFragment].timeTaken", "child node 'timeTaken' does not exist in the snapshot");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                fragment = new MyBookingsFragment();
                b = this.getArguments();
                if (b != null) {
                    if (getFragmentManager() != null) {
                        int notifications = b.getInt("bookingCount") - 1;
                        b.putInt("bookingCount", notifications);
                        fragment.setArguments(b);
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, fragment)
                                .commit();
                    }
                }
                break;
        }
    }

    private void enableTimeSlotAgain(DatabaseReference dbTimeSlotReference) {
        dbTimeSlotReference.setValue(true);
    }

    private int getTimeSlotIndex(String timeTaken, DataSnapshot snapshot) {
        int timeSlotIndex = 0;
        for (DataSnapshot data : snapshot.getChildren()) {
            String time = data.child("time").getValue(String.class);
            if (timeTaken.equals(time)) {
                return timeSlotIndex;
            }
            timeSlotIndex++;
        }
        // If the timeTaken is not found, return -1 or throw an exception
        return -1;
    }
}
