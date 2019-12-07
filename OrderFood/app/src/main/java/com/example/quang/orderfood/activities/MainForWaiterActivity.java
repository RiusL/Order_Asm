package com.example.quang.orderfood.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quang.orderfood.R;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import adapter.GridviewTableAdapter;
import consts.Constants;
import de.hdodenhof.circleimageview.CircleImageView;
import objects.HistoryBill;
import objects.Table;
import objects.User;
import singleton.Singleton;

public class MainForWaiterActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener {

    private static String SERVER_SEND_LIST_TABLE = "SERVER_SEND_LIST_TABLE";
    private static String CLIENT_SEND_REQUEST_TABLE = "CLIENT_SEND_REQUEST_TABLE";
    private static String LOG_OUT = "LOG_OUT";
    private static String REQUEST_BOOK = "REQUEST_BOOK";

    public static boolean CHECK_TABLE = false;
    public static String ID_USER = "";

    //Socket mSocket;
    Emitter.Listener onListTable;
    {
//        try {
//            mSocket = IO.socket(Constants.PORT);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        onListTable = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getListTable(args[0]);
            }
        };
    }

    public static User user;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private GridView gridView;
    private GridviewTableAdapter gridviewTableAdapter;
    private ArrayList<Table> arrTable;

    private SearchView searchView;

    private Button btnShowListFree;
    private Button btnShowListBooked;
    private Button btnShowAll;

    private View line1;
    private View line2;
    private View line3;

    private CircleImageView avatar;
    private TextView tvName;
    private Button btnProfile;
    private Button btnListBill;
    private Button btnLogOut;

    private Dialog dialogPeople;
    private int number;
    private String people;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_waiter);

        initSockets();
        getUser();
        findId();
        initViews();
        getData();
        initDialogPeople();
    }

    private void initDialogPeople() {
        dialogPeople = new Dialog(this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialogPeople.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPeople.setContentView(R.layout.dialog_check_number);
        dialogPeople.setCancelable(false);
        final EditText edtPeople = dialogPeople.findViewById(R.id.edtPeopleDialog);
        Button btnExit = dialogPeople.findViewById(R.id.btnExitDialog);
        Button btnDone = dialogPeople.findViewById(R.id.btnDoneDialog);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPeople.dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                people = edtPeople.getText().toString();
                Singleton.Instance().getmSocket().emit(REQUEST_BOOK,number);
                Intent intent = new Intent(MainForWaiterActivity.this, MenuActivity.class);
                intent.putExtra("numPeo",number+"-"+people);
                startActivity(intent);
                dialogPeople.dismiss();
            }
        });
    }

    private void initSockets() {
        //mSocket.connect();
        Singleton.Instance().getmSocket().emit(REQUEST_BOOK,"-1");
        Singleton.Instance().getmSocket().on(SERVER_SEND_LIST_TABLE,onListTable);
        Singleton.Instance().getmSocket().emit("CLIENT_SEND_REQUEST_LIST_STAFF","123");

    }

    private void getData() {
        arrTable = new ArrayList<>();
        gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,arrTable);
        gridView.setAdapter(gridviewTableAdapter);
        gridviewTableAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(this);
        btnShowAll.setOnClickListener(this);
        btnShowListFree.setOnClickListener(this);
        btnShowListBooked.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnListBill.setOnClickListener(this);
        Glide.with(this).load(Constants.PORT+MainForWaiterActivity.user.getImage())
                .into(avatar);
        tvName.setText(user.getName());
    }

    private void getUser()
    {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.KEY_PUSH_USER);
        ID_USER = user.getId();
    }

    private void getListTable(final Object arg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;
                arrTable.clear();
                for (int i=0; i<data.length(); i++)
                {
                    try {
                        JSONObject object = data.getJSONObject(i);
                        int num = object.getInt("tenBan");
                        String position = object.getString("viTri");
                        int numOfChair = object.getInt("soGhe");
                        String note = object.getString("ghiChu");
                        int check = object.getInt("tinhTrang");
                        Table table = new Table(num,position,numOfChair,check,note);
                        arrTable.add(table);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                gridviewTableAdapter.notifyDataSetChanged();
                //Singleton.Instance().getmSocket().emit(CLIENT_SEND_REQUEST_TABLE,"123");
            }
        });
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,0,0);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);

        line3.setVisibility(View.VISIBLE);
        btnShowAll.setTextColor(Color.parseColor("#ef4b4c"));

    }

    private void findId() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        gridView = findViewById(R.id.gridview);
        btnShowAll = findViewById(R.id.btnShowListTable);
        btnShowListFree = findViewById(R.id.btnShowListTableFree);
        btnShowListBooked = findViewById(R.id.btnShowListTableBooked);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);

        avatar = findViewById(R.id.profile_image);
        tvName = findViewById(R.id.tvNameStaff);
        btnProfile = findViewById(R.id.btnProfile);
        btnListBill = findViewById(R.id.btnListHistoryBill);
        btnLogOut = findViewById(R.id.btnLogOut);
    }

    private void setLine(View v, Button b)
    {
        line1.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.INVISIBLE);
        line3.setVisibility(View.INVISIBLE);

        btnShowAll.setTextColor(Color.parseColor("#90000000"));
        btnShowListBooked.setTextColor(Color.parseColor("#90000000"));
        btnShowListFree.setTextColor(Color.parseColor("#90000000"));

        v.setVisibility(View.VISIBLE);
        b.setTextColor(Color.parseColor("#ef4b4c"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId())
        {
            case R.id.gridview:
                Table table = arrTable.get(i);
                int check = table.getCheck();
                number = table.getNumber();
                if (check == 0)
                {
                    MainForWaiterActivity.CHECK_TABLE = false;
                    dialogPeople.show();
                }
                else if (check == 1)
                {
                    MainForWaiterActivity.CHECK_TABLE = true;
                    Intent intent = new Intent(MainForWaiterActivity.this,BillActivity.class);
                    intent.putExtra("table",number+"");
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
        //set OnQueryTextListener cho search view để thực hiện search theo text
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)){
            gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,arrTable);
            gridView.setAdapter(gridviewTableAdapter);
            gridviewTableAdapter.notifyDataSetChanged();

        }else if (newText.equalsIgnoreCase("all")){
            gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,arrTable);
            gridView.setAdapter(gridviewTableAdapter);
            gridviewTableAdapter.notifyDataSetChanged();
        }
        else if (newText.equalsIgnoreCase("free"))
        {
            ArrayList<Table> tables = new ArrayList<>();
            for (Table t: arrTable)
            {
                if (t.getCheck() == 0)
                {
                    tables.add(t);
                }
            }
            gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,tables);
            gridView.setAdapter(gridviewTableAdapter);
            gridviewTableAdapter.notifyDataSetChanged();
        }
        else if (newText.equalsIgnoreCase("book"))
        {
            ArrayList<Table> tables = new ArrayList<>();
            for (Table t: arrTable)
            {
                if (t.getCheck() == 1)
                {
                    tables.add(t);
                }
            }
            gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,tables);
            gridView.setAdapter(gridviewTableAdapter);
            gridviewTableAdapter.notifyDataSetChanged();
        }
        else
        {
            ArrayList<Table> tables = new ArrayList<>();
            for (Table t: arrTable)
            {
                if (String.valueOf(t.getNumber()).contains(newText) == true)
                {
                    tables.add(t);
                }
            }
            gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,tables);
            gridView.setAdapter(gridviewTableAdapter);
            gridviewTableAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnShowListTable:
                gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,arrTable);
                gridView.setAdapter(gridviewTableAdapter);
                gridviewTableAdapter.notifyDataSetChanged();
                setLine(line3,btnShowAll);
                break;
            case R.id.btnShowListTableFree:
                ArrayList<Table> tables = new ArrayList<>();
                for (Table t: arrTable)
                {
                    if (t.getCheck() == 0)
                    {
                        tables.add(t);
                    }
                }
                gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,tables);
                gridView.setAdapter(gridviewTableAdapter);
                gridviewTableAdapter.notifyDataSetChanged();
                setLine(line1,btnShowListFree);
                break;
            case R.id.btnShowListTableBooked:
                ArrayList<Table> tables1 = new ArrayList<>();
                for (Table t: arrTable)
                {
                    if (t.getCheck() == 1)
                    {
                        tables1.add(t);
                    }
                }
                gridviewTableAdapter = new GridviewTableAdapter(this,R.layout.item_gridview,tables1);
                gridView.setAdapter(gridviewTableAdapter);
                gridviewTableAdapter.notifyDataSetChanged();
                setLine(line2,btnShowListBooked);
                break;
            case R.id.btnProfile:
                Intent intent = new Intent(MainForWaiterActivity.this,ProfileActivity.class);
                intent.putExtra("key","w");
                startActivity(intent);
                break;
            case R.id.btnLogOut:
                Singleton.Instance().getmSocket().emit(LOG_OUT,user.getId());
                Intent intent1 = new Intent(MainForWaiterActivity.this,LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.btnListHistoryBill:
                Intent in = new Intent(MainForWaiterActivity.this, HistoryBillActivity.class);
                in.putExtra("u",user);
                startActivity(in);
                break;
        }
    }
}
