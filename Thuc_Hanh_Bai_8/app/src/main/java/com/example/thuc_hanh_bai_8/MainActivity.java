package com.example.thuc_hanh_bai_8;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText tenDangNhap, matKhau;
    CheckBox cbox;
    Button btnNhap, btnThoat;
    String tenThongTinDangNhap = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Lay thong tin
        tenDangNhap = findViewById(R.id.ed_dangNhap);
        matKhau = findViewById(R.id.ed_matKhau);
        btnNhap = findViewById(R.id.btn_nhap);
        btnThoat = findViewById(R.id.btn_Thoat);
        cbox = findViewById(R.id.cbox);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void SaveLoginState(){
        SharedPreferences sp = getSharedPreferences(tenThongTinDangNhap, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Username", tenDangNhap.getText().toString());
        editor.putString("Password", matKhau.getText().toString());
        editor.putBoolean("Save", cbox.isChecked());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(tenThongTinDangNhap, MODE_PRIVATE);
        String userName = sharedPreferences.getString("Username", "");
        String passWord = sharedPreferences.getString("Password", "");
        boolean save = sharedPreferences.getBoolean("Save", false);
        if(save){
            tenDangNhap.setText(userName);
            matKhau.setText(passWord);
            cbox.setChecked(save);
        }
    }
}