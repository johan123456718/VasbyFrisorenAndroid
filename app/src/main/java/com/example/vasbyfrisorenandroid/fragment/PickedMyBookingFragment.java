package com.example.vasbyfrisorenandroid.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.example.vasbyfrisorenandroid.model.db.Database;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PickedMyBookingFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Service service;
    private BookedTime bookedTime;
    private TextView bookingService, bookingBarber, bookingDate, bookingPrice, bookingTime;
    private ImageView bookingImg, backButton, ivOutput;
    private Button cancelBookButton;
    private int selectedItemId;
    private String barber;
    private Database database;

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
        database = new Database();
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
            selectedItemId = bundle.getInt("id");
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
                database.removeBooking(selectedItemId, barber, bookedTime);
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
}
