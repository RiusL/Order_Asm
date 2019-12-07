package com.example.quang.orderfood.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quang.orderfood.R;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.ListviewMenuAdapter;
import objects.ItemMenu;
import singleton.Singleton;

public class MenuActivity extends AppCompatActivity {

    private final String SERVER_SEND_MENU_DRINK = "SERVER_SEND_MENU_DRINK";
    private final String SERVER_SEND_MENU_FOOD = "SERVER_SEND_MENU_FOOD";
    private final String CLIENT_SEND_REQUEST_DRINNK = "CLIENT_SEND_REQUEST_DRINNK";
    private final String CLIENT_SEND_REQUEST_FOOD = "CLIENT_SEND_REQUEST_FOOD";

    Emitter.Listener onListDrink;
    Emitter.Listener onListFood;
    {
        onListDrink = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getListDrink(args[0]);
            }
        };
        onListFood = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getListFood(args[0]);
            }
        };
    }

    private Toolbar toolbar;
    private ImageButton btnBack;
    private TextView tvGoToBill;
    private ListView lvMenu;
    private ListviewMenuAdapter listviewMenuAdapter;

    private RadioGroup group;
    private RadioButton radioButtonFood;
    private RadioButton radioButtonDrink;
    private Spinner spinner;
    private ArrayList<String> arrItemSpinnerDrink = new ArrayList<>();
    private ArrayList<String> arrItemSpinnerFood = new ArrayList<>();


    private ArrayAdapter<String> adapter;
    private ArrayList<ItemMenu> drink = new ArrayList<>();
    private ArrayList<ItemMenu> food = new ArrayList<>();

    private ArrayList<ItemMenu> arrItemChecked = new ArrayList<>();

    private boolean checkDrink = true;

    private String drinkLast;
    private String foodLast;
    private String numPeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        initSockets();
        findId();
        getData();
        initViews();
    }

    private void getData() {
        Intent intent = getIntent();
        numPeo = intent.getStringExtra("numPeo");
    }

    private void findId() {
        toolbar = findViewById(R.id.toolbarMenu);
        btnBack = findViewById(R.id.btnBackMenu);
        tvGoToBill = findViewById(R.id.btnGoToBill);
        lvMenu = findViewById(R.id.lvMenu);
        group = findViewById(R.id.group);
        radioButtonDrink = findViewById(R.id.radioButton_drink);
        radioButtonFood = findViewById(R.id.radioButton_food);
        spinner = findViewById(R.id.spinner);
    }

    private void initSockets() {
        Singleton.Instance().getmSocket().emit(CLIENT_SEND_REQUEST_DRINNK,"123");
        Singleton.Instance().getmSocket().on(SERVER_SEND_MENU_DRINK,onListDrink);

        Singleton.Instance().getmSocket().emit(CLIENT_SEND_REQUEST_FOOD,"123");
        Singleton.Instance().getmSocket().on(SERVER_SEND_MENU_FOOD,onListFood);
    }

    private void initViews() {

        setSupportActionBar(toolbar);

        listviewMenuAdapter = new ListviewMenuAdapter(MenuActivity.this,R.layout.item_listview_menu,drink);
        lvMenu.setAdapter(listviewMenuAdapter);
        listviewMenuAdapter.notifyDataSetChanged();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvGoToBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i<drink.size();i++)
                {
                    if (drink.get(i).getCount() != 0)
                    {
                        arrItemChecked.add(drink.get(i));
                    }
                }

                for (int i=0; i<food.size();i++)
                {
                    if (food.get(i).getCount() != 0)
                    {
                        arrItemChecked.add(food.get(i));
                    }
                }
                if (BillActivity.CHECK_START_MENU)
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",arrItemChecked);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }else {
                    Intent intent = new Intent(MenuActivity.this,BillActivity.class);
                    intent.putExtra("list",arrItemChecked);
                    intent.putExtra("numPeo",numPeo);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    finish();
                }
            }
        });

        radioButtonDrink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listviewMenuAdapter = new ListviewMenuAdapter(MenuActivity.this,R.layout.item_listview_menu,food);
                lvMenu.setAdapter(listviewMenuAdapter);
                listviewMenuAdapter.notifyDataSetChanged();
                checkDrink = false;

                adapter = new ArrayAdapter<String>(MenuActivity.this
                        , android.R.layout.simple_spinner_item, arrItemSpinnerFood);
                adapter.setDropDownViewResource
                        (android.R.layout.simple_list_item_single_choice);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new MyProcessEvent());
            }
        });

        radioButtonFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ArrayList<ItemMenu> arr = new ArrayList<>();
                for (int j = 0; j<drink.size(); j++)
                {
                    if (drinkLast.equals(drink.get(j).getGroup()))
                    {
                        arr.add(drink.get(j));
                    }
                }
                listviewMenuAdapter = new ListviewMenuAdapter(MenuActivity.this,R.layout.item_listview_menu,arr);
                lvMenu.setAdapter(listviewMenuAdapter);
                listviewMenuAdapter.notifyDataSetChanged();
                checkDrink = true;

                adapter = new ArrayAdapter<String>(MenuActivity.this
                        , android.R.layout.simple_spinner_item, arrItemSpinnerDrink);
                adapter.setDropDownViewResource
                        (android.R.layout.simple_list_item_single_choice);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new MyProcessEvent());
            }
        });

    }

    private class MyProcessEvent implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (checkDrink)
            {
                drinkLast = arrItemSpinnerDrink.get(i);
                ArrayList<ItemMenu> arr = new ArrayList<>();
                for (int j = 0; j<drink.size(); j++)
                {
                    if (arrItemSpinnerDrink.get(i).equals(drink.get(j).getGroup()))
                    {
                        arr.add(drink.get(j));
                    }
                }
                listviewMenuAdapter = new ListviewMenuAdapter(MenuActivity.this,R.layout.item_listview_menu,arr);
                lvMenu.setAdapter(listviewMenuAdapter);
                listviewMenuAdapter.notifyDataSetChanged();
            }
            else{
                foodLast = arrItemSpinnerFood.get(i);
                ArrayList<ItemMenu> arr = new ArrayList<>();
                for (int j = 0; j<food.size(); j++)
                {
                    if (arrItemSpinnerFood.get(i).equals(food.get(j).getGroup()))
                    {
                        arr.add(food.get(j));
                    }
                }
                listviewMenuAdapter = new ListviewMenuAdapter(MenuActivity.this,R.layout.item_listview_menu,arr);
                lvMenu.setAdapter(listviewMenuAdapter);
                listviewMenuAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            if (checkDrink)
            {
                ArrayList<ItemMenu> arr = new ArrayList<>();
                for (int j = 0; j<drink.size(); j++)
                {
                    if (arrItemSpinnerDrink.get(0).equals(drink.get(j).getGroup()))
                    {
                        arr.add(drink.get(j));
                    }
                }
                listviewMenuAdapter = new ListviewMenuAdapter(MenuActivity.this,R.layout.item_listview_menu,arr);
                lvMenu.setAdapter(listviewMenuAdapter);
                listviewMenuAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getListDrink(final Object arg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;
                drink.clear();
                for (int i=0; i<data.length(); i++)
                {
                    try {
                        JSONObject object = data.getJSONObject(i);
                        String group = object.getString("tenNhom");
                        String name = object.getString("tenMonAn");
                        String price = object.getString("gia");
                        String unit = object.getString("tenDVTinh");
                        String check = object.getString("tinhTrang");
                        String img = object.getString("anhMonAn");
                        ItemMenu itemMenu = new ItemMenu(group,name,price,unit,check,img);
                        drink.add(itemMenu);
                        if (!arrItemSpinnerDrink.toString().contains(group))
                            arrItemSpinnerDrink.add(group);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter = new ArrayAdapter<String>(MenuActivity.this
                        , android.R.layout.simple_spinner_item, arrItemSpinnerDrink);
                adapter.setDropDownViewResource
                        (android.R.layout.simple_list_item_single_choice);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new MyProcessEvent());
            }
        });
    }

    private void getListFood(final Object arg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) arg;
                food.clear();
                for (int i=0; i<data.length(); i++)
                {
                    try {
                        JSONObject object = data.getJSONObject(i);
                        String group = object.getString("tenNhom");
                        String name = object.getString("tenMonAn");
                        String price = object.getString("gia");
                        String unit = object.getString("tenDVTinh");
                        String check = object.getString("tinhTrang");
                        String img = object.getString("anhMonAn");
                        ItemMenu itemMenu = new ItemMenu(group,name,price,unit,check,img);
                        food.add(itemMenu);
                        if (!arrItemSpinnerFood.toString().contains(group))
                            arrItemSpinnerFood.add(group);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
