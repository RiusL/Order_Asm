package com.example.quang.orderfood.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quang.orderfood.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import adapter.ListviewBillAdapter;
import adapter.ListviewDialogHistoryBill;
import adapter.ListviewHistoryBillAdapter;
import consts.Constants;
import objects.HistoryBill;
import objects.ItemMenu;
import objects.User;
import singleton.Singleton;

public class HistoryBillActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final String CLIENT_SEND_REQUEST_LIST_BILL = "CLIENT_SEND_REQUEST_LIST_BILL";
    private final String SERVER_SEND_LIST_BILL = "SERVER_SEND_LIST_BILL";

    private final String CLIENT_SEND_REQUEST_LIST_ITEM_BILL = "CLIENT_SEND_REQUEST_LIST_ITEM_BILL";
    private final String SERVER_SEND_LIST_ITEM_BILL = "SERVER_SEND_LIST_ITEM_BILL";

    Emitter.Listener onListBill;
    Emitter.Listener onListItemBill;
    {
        onListBill = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getListBill(args[0]);
            }
        };
        onListItemBill = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getListItemBill(args[0]);
            }
        };
    }



    private User user;
    private TextView tvIdStaff;
    private ListView listView;
    private ImageView imBack;
    private ListviewHistoryBillAdapter adapter;
    private ArrayList<HistoryBill> arrBill;
    private Dialog dialogListItem;
    private ArrayList<ItemMenu> arrItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_bill);

        getData();
        initSockets();
        findId();
        initViews();

    }

    private void initDialog() {
        dialogListItem = new Dialog(this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialogListItem.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogListItem.setContentView(R.layout.dialog_list_item_history_bill);

        ListView lvItem = dialogListItem.findViewById(R.id.lvItemDialogHistoryBill);
        ListviewDialogHistoryBill adap = new ListviewDialogHistoryBill(this,R.layout.item_dialog_history_bill,arrItem);
        lvItem.setAdapter(adap);
        adap.notifyDataSetChanged();
    }

    private void initSockets() {
        Singleton.Instance().getmSocket().emit(CLIENT_SEND_REQUEST_LIST_BILL,user.getId());
        Singleton.Instance().getmSocket().on(SERVER_SEND_LIST_BILL,onListBill);
        Singleton.Instance().getmSocket().on(SERVER_SEND_LIST_ITEM_BILL,onListItemBill);
    }


    private void findId() {
        tvIdStaff = findViewById(R.id.tvIdStaffHistoryBill);
        listView = findViewById(R.id.lvHistoryBill);
        imBack = findViewById(R.id.btnBackHistoryBill);
    }

    private void getData() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("u");
    }

    private void initViews() {
        tvIdStaff.setText("Mã nhân viên: "+ user.getId());
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView.setOnItemClickListener(this);
    }

    private void getListItemBill(final Object arg) {
        arrItem = new ArrayList<>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;
                for (int i=0; i<data.length(); i++)
                {
                    try {
                        JSONObject object = data.getJSONObject(i);
                        String name = object.getString("tenMonAn");
                        String count = object.getString("soLuong");
                        ItemMenu itemMenu = new ItemMenu("",name,"","","","");
                        itemMenu.setCount(Integer.parseInt(count));
                        arrItem.add(itemMenu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                initDialog();
                if (!isFinishing())
                {
                    dialogListItem.show();
                }
            }
        });
    }

    private void getListBill(final Object arg) {
        arrBill = new ArrayList<>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;
                for (int i=0; i<data.length(); i++)
                {
                    try {
                        JSONObject object = data.getJSONObject(i);
                        String idBill = object.getString("idHoaDon");
                        String idStaff = object.getString("idNhanVien");
                        int table = object.getInt("tenBan");
                        String time = object.getString("thoiGianThanhToan");
                        String total = object.getString("tongTien");
                        int people = object.getInt("soNguoi");
                        HistoryBill historyBill = new HistoryBill(idBill,table,people,total,time);
                        arrBill.add(historyBill);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new ListviewHistoryBillAdapter(HistoryBillActivity.this,R.layout.item_history_bill,arrBill);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Singleton.Instance().getmSocket().emit(CLIENT_SEND_REQUEST_LIST_ITEM_BILL,arrBill.get(i).getIdBill());
    }
}
