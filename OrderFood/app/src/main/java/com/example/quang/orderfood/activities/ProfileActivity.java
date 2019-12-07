package com.example.quang.orderfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quang.orderfood.R;

import consts.Constants;
import objects.Staff;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imAvatar;
    private TextView tvName;
    private TextView tvID;
    private TextView tvSex;
    private TextView tvDateOfBirth;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvDateStart;
    private TextView tvSalary;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findId();
        Intent intent = getIntent();
        if (intent.getStringExtra("key").equalsIgnoreCase("w"))
        {
            setData();
        }
        else if (intent.getStringExtra("key").equalsIgnoreCase("m"))
        {
            setDataManager();
        }


        setClicks();

    }

    private void setClicks() {
        btnBack.setOnClickListener(this);
    }

    private void setData() {
        Glide.with(this).load(Constants.PORT+MainForWaiterActivity.user.getImage())
                .into(imAvatar);
        tvName.setText(MainForWaiterActivity.user.getName());
        tvID.setText(MainForWaiterActivity.user.getId());
        tvSex.setText(MainForWaiterActivity.user.getSex());
        String[] arr = MainForWaiterActivity.user.getDateOfBirth().split("T");
        String[] a = arr[0].split("-");
        tvDateOfBirth.setText((Integer.parseInt(a[2])+1) + "-" + a[1] +"-"+ a[0]);
        tvAddress.setText(MainForWaiterActivity.user.getAddress());
        tvPhone.setText(MainForWaiterActivity.user.getPhone());
        String[] arr1 = MainForWaiterActivity.user.getDateOfstart().split("T");
        String[] b = arr1[0].split("-");
        tvDateStart.setText((Integer.parseInt(b[2])+1) + "-" + b[1] +"-"+ b[0]);
        tvSalary.setText(MainForWaiterActivity.user.getSalaryPerDay());
    }

    private void setDataManager()
    {
        Intent i = getIntent();
        Staff staff = (Staff) i.getSerializableExtra("staff");
        Glide.with(this).load(Constants.PORT+staff.getImage())
                .into(imAvatar);
        tvName.setText(staff.getName());
        tvID.setText(staff.getId());
        tvSex.setText(staff.getSex());
        String[] arr = staff.getDateOfBirth().split("T");
        String[] a = arr[0].split("-");
        tvDateOfBirth.setText((Integer.parseInt(a[2])+1) + "-" + a[1] +"-"+ a[0]);
        tvAddress.setText(staff.getAddress());
        tvPhone.setText(staff.getPhone());
        String[] arr1 = staff.getDateStart().split("T");
        String[] b = arr1[0].split("-");
        tvDateStart.setText((Integer.parseInt(b[2])+1) + "-" + b[1] +"-"+ b[0]);
        tvSalary.setText(staff.getSalary());
    }

    private void findId() {
        imAvatar = findViewById(R.id.imAvatar);
        tvName = findViewById(R.id.tvNamePro);
        tvID = findViewById(R.id.tvIDPro);
        tvSex = findViewById(R.id.tvSexPro);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirthPro);
        tvAddress = findViewById(R.id.tvAddressPro);
        tvPhone = findViewById(R.id.tvPhonePro);
        tvDateStart = findViewById(R.id.tvDateStartPro);
        tvSalary = findViewById(R.id.tvSalaryPro);
        btnBack = findViewById(R.id.btnBack);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
