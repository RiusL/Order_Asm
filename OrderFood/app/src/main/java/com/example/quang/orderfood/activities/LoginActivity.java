package com.example.quang.orderfood.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.quang.orderfood.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import consts.Constants;
import objects.User;
import singleton.Singleton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String CLIENT_SEND_REQUEST_LOGIN = "CLIENT_SEND_REQUEST_LOGIN";
    private final String SERVER_SEND_RESULT = "SERVER_SEND_RESULT";

    Socket mSocket;
    Emitter.Listener onResult;
    {
        try {
            mSocket = IO.socket(Constants.PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        onResult = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleResultFromServer(args[0]);
            }
        };
    }



    private EditText edtUser;
    private EditText edtPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initSockets();

        findId();

        initViews();

        Singleton.Instance().setmSocket(mSocket);
        Singleton.Instance().setOnResult(onResult);
    }

    private void handleResultFromServer(final Object arg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;
                if (data.length() == 0)
                {
                    Snackbar snackbar = Snackbar
                            .make(edtPass, "Tài khoản hoặc mật khẩu chưa chính xác!", Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.DKGRAY);
                    snackbar.show();
                }
                else {
                    try {
                        JSONObject object = data.getJSONObject(0);
                        String id = object.getString("idNhanVien");
                        String name = object.getString("tenNhanVien");
                        String sex = object.getString("gioiTinh");
                        String dateOfBirth = object.getString("ngaySinh");
                        String address = object.getString("queQuan");
                        String phone = object.getString("soDienThoai");
                        String position = object.getString("chucVu");
                        String dateOfStart = object.getString("ngayVao");
                        String salary = object.getString("luongNgay");
                        String up = object.getString("userPass");
                        String image = object.getString("anhDaiDien");

                        User user = new User(id,name,sex,dateOfBirth,address
                                ,phone,position,dateOfStart,salary,up,image);

                        if (user.getPosition().equalsIgnoreCase("QL"))
                        {
                            Intent intent = new Intent(LoginActivity.this,MainForManagerActivity.class);
                            intent.putExtra(Constants.KEY_PUSH_USER,user);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            //mSocket.disconnect();
                            //Singleton.Instance().getmSocket().emit("CLIENT_SEND_REQUEST_LIST_STAFF","123");

                            finish();
                        }else if (user.getPosition().equalsIgnoreCase("BB")){
                            Intent intent = new Intent(LoginActivity.this,MainForWaiterActivity.class);
                            intent.putExtra(Constants.KEY_PUSH_USER,user);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            //mSocket.disconnect();
                            //Singleton.Instance().getmSocket().emit("CLIENT_SEND_REQUEST_LIST_STAFF","123");

                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initSockets() {
        mSocket.connect();
        mSocket.on(SERVER_SEND_RESULT,onResult);
    }

    private void initViews() {
        btnLogin.setOnClickListener(this);
    }

    private void findId() {
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);

        btnLogin = findViewById(R.id.btnLogin);
    }

    @Override
    public void onClick(View view) {
        String user = edtUser.getText().toString();
        String pass = edtPass.getText().toString();
        if (user.isEmpty() || pass.isEmpty())
        {
            Snackbar snackbar = Snackbar
                    .make(edtPass, "Bạn cần nhập đủ thông tin!", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.DKGRAY);
            snackbar.show();
            return;
        }

        mSocket.emit(CLIENT_SEND_REQUEST_LOGIN,user+"-"+pass);
    }
}
