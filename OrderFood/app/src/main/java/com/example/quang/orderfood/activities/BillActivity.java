package com.example.quang.orderfood.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quang.orderfood.R;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import adapter.ListviewBillAdapter;
import objects.ItemMenu;
import singleton.Singleton;

public class BillActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private final String CLIENT_SEND_TEMP_BILL = "CLIENT_SEND_TEMP_BILL";
    private final String CLIENT_SEND_REQUEST_BILL = "CLIENT_SEND_REQUEST_BILL";
    private final String SERVER_SEND_BILL = "SERVER_SEND_BILL";
    private final String DELETE_BILL = "DELETE_BILL";

    Emitter.Listener onBill;
    {
        onBill = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getBillExists(args[0]);
            }
        };
    }

    public static boolean CHECK_START_MENU = false;

    private TextView tvTable;
    private TextView tvPeople;
    private TextView tvTime;

    private ListView listView;
    private ListviewBillAdapter listviewBillAdapter;
    private ArrayList<ItemMenu> arrItem;
    private String table;
    private String people;
    private String time;

    private TextView tvTotalBill;
    private ImageView imgAddItem;
    private ImageView imgPushBill;
    private ImageView imgPrintBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        findId();
        if (MainForWaiterActivity.CHECK_TABLE == false)
        {
            getDataCheckFalse();
            initViewsCheckFalse();
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    getPrice();
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(runnable);
        }
        else {
            getDataCheckTrue();
            initSockets();
        }

        clickEvents();

    }

    private void initSockets() {
        Singleton.Instance().getmSocket().emit(CLIENT_SEND_REQUEST_BILL,table);
        Singleton.Instance().getmSocket().on(SERVER_SEND_BILL,onBill);
    }

    private void getBillExists(final Object arg) {
        arrItem = new ArrayList<>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;

                JSONObject obj = null;
                try {
                    obj = data.getJSONObject(0);
                    String tab = obj.getString("tenBan");
                    String tim = obj.getString("thoiGianGoi");
                    String peo = obj.getString("soNguoi");
                    table = tab;
                    people = peo;
                    time = tim;
                    tvTable.setText("Bàn số: "+table);
                    tvPeople.setText("Số người: "+people);
                    tvTime.setText("Thời gian:  "+time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i=0; i<data.length(); i++)
                {
                    try {
                        JSONObject object = data.getJSONObject(i);
                        String nameOfFood = object.getString("tenMonAn");
                        int count = object.getInt("soLuong");
                        String group = object.getString("tenNhom");
                        String price = object.getString("gia");
                        String unit = object.getString("tenDVTinh");
                        String check = object.getString("tinhTrang");
                        String image = object.getString("anhMonAn");
                        ItemMenu itemMenu = new ItemMenu(group,nameOfFood,price,unit,check,image);
                        itemMenu.setCount(count);
                        arrItem.add(itemMenu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        getPrice();
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(runnable);
                listviewBillAdapter = new ListviewBillAdapter(BillActivity.this,R.layout.item_listview_bill,arrItem);
                listView.setAdapter(listviewBillAdapter);
                listviewBillAdapter.notifyDataSetChanged();
            }
        });
    }

    private void findId() {
        tvTable = findViewById(R.id.tvTableBill);
        tvPeople = findViewById(R.id.tvPeopleBill);
        tvTime = findViewById(R.id.tvTimeBill);
        listView = findViewById(R.id.lvItemBill);
        tvTotalBill = findViewById(R.id.tvTotalBill);
        imgAddItem = findViewById(R.id.btnAddItem);
        imgPushBill = findViewById(R.id.btnPushBill);
        imgPrintBill = findViewById(R.id.btnPrintBill);
    }

    private void showDialogConfigPrint()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BillActivity.this);
        builder.setTitle("XÁC NHẬN");
        builder.setMessage("Xuất hóa đơn?");
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Xuất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String t = tvTotalBill.getText().toString();
                String[] a = t.split("Tổng tiền:");
                a[1] = a[1].trim();
                queryDeleteBill(table,MainForWaiterActivity.ID_USER,arrItem,people,a[1]);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDialogConfigPush()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BillActivity.this);
        builder.setTitle("XÁC NHẬN");
        builder.setMessage("Đẩy hóa đơn?");
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Đẩy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int in) {
                String query = "";
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String[] str = timeStamp.split("_");
                String[] str2 = str[0].split("");
                String year = str2[1]+str2[2]+str2[3]+str2[4];
                String month = str2[5]+str2[6];
                String day = str2[7]+str2[8];

                String[] str3 = str[1].split("");
                String hour = str3[1]+str3[2];
                String minute = str3[3]+str3[4];
                String sec = str3[5]+str3[6];
                String time = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+sec;
                for (ItemMenu i: arrItem)
                {
                    String temp = "INSERT INTO `hoadonchothanhtoan` VALUES (null,"
                            +Integer.parseInt(table)+",'"+i.getName()+"',"+i.getCount()+",'"+time+"',"+people+")";
                    if (query.equalsIgnoreCase(""))
                    {
                        query = query + temp;
                    }
                    else {
                        query = query + ";" + temp;
                    }
                }

                Singleton.Instance().getmSocket().emit(CLIENT_SEND_TEMP_BILL,"DELETE FROM `hoadonchothanhtoan` WHERE `tenBan` ="+table+";"+query);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clickEvents() {
        imgAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CHECK_START_MENU = true;
                Intent intent = new Intent(BillActivity.this,MenuActivity.class);
                startActivityForResult(intent,111);
            }
        });

        imgPrintBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogConfigPrint();
            }
        });

        imgPushBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogConfigPush();
            }
        });

        listView.setOnItemLongClickListener(this);
    }

    private void queryDeleteBill(String table, String idStaff, ArrayList<ItemMenu> arr
            , String p, String total)
    {
        String query = "DELETE FROM `hoadonchothanhtoan` WHERE `tenBan` = "+table;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String[] str = timeStamp.split("_");
        String[] str2 = str[0].split("");
        String year = str2[1]+str2[2]+str2[3]+str2[4];
        String month = str2[5]+str2[6];
        String day = str2[7]+str2[8];

        String[] str3 = str[1].split("");
        String hour = str3[1]+str3[2];
        String minute = str3[3]+str3[4];
        String sec = str3[5]+str3[6];
        String t = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+sec;
        String idBill = idStaff+str[0]+str[1];

        query = query + ";"+"INSERT INTO `thongkehoadon` VALUES ('"+idBill+"','"+idStaff+"',"+table+",'"+t+"','"+total+"',"+p+")";

        for (ItemMenu itemMenu: arr)
        {
            long money = convert(itemMenu.getPrice()) * itemMenu.getCount();
            String m = getMoney(money);
            query = query + ";" + "INSERT INTO `dsmonantheohd` VALUES (null,'"+idBill+"','"+itemMenu.getName()+"',"+itemMenu.getCount()+",'"+m+"')";
        }

        query = query + ";" + "UPDATE `danhsachban` SET `tinhTrang`= 0 WHERE `tenBan`= "+table;

        Singleton.Instance().getmSocket().emit(DELETE_BILL,query);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if(resultCode == Activity.RESULT_OK){
                ArrayList<ItemMenu> arr = new ArrayList<>();
                arr = (ArrayList<ItemMenu>) data.getSerializableExtra("result");
                String str = "";
                for (ItemMenu item : arrItem)
                {
                    str += item.getName();
                }
                Log.e("--------------",str);
                for (int index = 0; index<arr.size(); index++)
                {
                    if (!str.contains(arr.get(index).getName()))
                    {
                        arrItem.add(arr.get(index));
                    }
                    else {
                        for (int j=0; j<arrItem.size(); j++)
                        {
                            if (arrItem.get(j).getName().equals(arr.get(index).getName()))
                            {
                                arrItem.get(j).setCount(arrItem.get(j).getCount()+arr.get(index).getCount());
                            }
                        }
                    }
                }
                CHECK_START_MENU = false;
                listviewBillAdapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void getDataCheckFalse() {
        arrItem = new ArrayList<>();
        Intent intent = getIntent();
        arrItem = (ArrayList<ItemMenu>) intent.getSerializableExtra("list");
        String numPeo = intent.getStringExtra("numPeo");
        String[] str = numPeo.split("-");
        table = str[0];
        people = str[1];
    }

    private void getDataCheckTrue() {

        Intent intent = getIntent();
        table = intent.getStringExtra("table");
    }

    private void initViewsCheckFalse() {
        tvTable.setText("Bàn số: "+table);
        tvPeople.setText("Số người: "+people);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String[] str = timeStamp.split("_");
        String[] str2 = str[0].split("");
        String year = str2[1]+str2[2]+str2[3]+str2[4];
        String month = str2[5]+str2[6];
        String day = str2[7]+str2[8];

        String[] str3 = str[1].split("");
        String hour = str3[1]+str3[2];
        String minute = str3[3]+str3[4];
        String sec = str3[5]+str3[6];
        tvTime.setText("Thời gian: "+day+"/"+month+"/"+year+"_"+hour+":"+minute+":"+sec);

        listviewBillAdapter = new ListviewBillAdapter(BillActivity.this,R.layout.item_listview_bill,arrItem);
        listView.setAdapter(listviewBillAdapter);
        listviewBillAdapter.notifyDataSetChanged();
    }

    public long convert(String str)
    {
        str = str.trim();
        String[] s1 = str.split("VND");
        String str2 = s1[0];
        str2 = str2.trim();
        String money = "";
        String[] s2 = str2.split(",");
        for (int i=0; i<s2.length; i++)
        {
            money = money + s2[i];
        }
        money = money.trim();
        return Long.parseLong(money);
    }

    private String getMoney(long x)
    {
        String str = x+"";
        String[] s1 = str.split("");
        String money = "";
        int count = 0;
        int c = 0;
        for (int i = s1.length - 1; i>=0; i--)
        {
            count++;
            c++;
            if (count == 3)
            {
                if (s1[i].equals(""))
                {
                    money = s1[i] +money;
                    count = 0;
                }
                else if (c < s1.length-1){
                    money = "," + s1[i] +money;
                    count = 0;
                }
                else{
                    money = s1[i] +money;
                    count = 0;
                }

            }
            else {
                money = s1[i] + money;
            }
        }

        return money+" VND";
    }

    private void getPrice()
    {
        long total = 0;
        for (int i=0; i<arrItem.size(); i++)
        {
            total += (convert(arrItem.get(i).getPrice())*arrItem.get(i).getCount());
        }

        tvTotalBill.setText("Tổng tiền:  "+getMoney(total));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BillActivity.this);
        builder.setTitle("XÁC NHẬN");
        builder.setMessage("Xóa món ăn?");
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                arrItem.remove(index);
                listviewBillAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return true;
    }
}
