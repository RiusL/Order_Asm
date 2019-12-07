package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quang.orderfood.R;

import java.util.ArrayList;

import consts.Constants;
import objects.ItemMenu;

/**
 * Created by quang on 27-Jan-18.
 */

public class ListviewBillAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<ItemMenu> arrItem;


    public ListviewBillAdapter(Context context, int layout, ArrayList<ItemMenu> arrItem) {
        this.context = context;
        this.layout = layout;
        this.arrItem = arrItem;
    }

    private class ViewHolder{
        TextView tvName;
        TextView tvPrice;
        ImageView btnUp;
        ImageView btnDown;
        TextView tvCount;
    }

    @Override
    public int getCount() {
        return arrItem.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewRow = view;
        if (viewRow == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewRow = inflater.inflate(layout,viewGroup,false);
            ListviewBillAdapter.ViewHolder viewHolder = new ListviewBillAdapter.ViewHolder();

            viewHolder.tvName = viewRow.findViewById(R.id.tvNameOdFoodBill);
            viewHolder.tvPrice = viewRow.findViewById(R.id.tvPriceOfFoodBill);
            viewHolder.btnUp = viewRow.findViewById(R.id.btnUpBill);
            viewHolder.btnDown = viewRow.findViewById(R.id.btnDownBill);
            viewHolder.tvCount = viewRow.findViewById(R.id.tvCountBill);

            viewRow.setTag(viewHolder);
        }

        final ItemMenu itemMenu = arrItem.get(i);
        final ListviewBillAdapter.ViewHolder viewHolder = (ListviewBillAdapter.ViewHolder) viewRow.getTag();
        viewHolder.tvName.setText(itemMenu.getName());
        viewHolder.tvPrice.setText(itemMenu.getPrice());
        viewHolder.tvCount.setText(itemMenu.getCount()+"");
        viewHolder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = itemMenu.getCount();
                i = i + 1;
                viewHolder.tvCount.setText(String.valueOf(i));
                itemMenu.setCount(i);
            }
        });

        viewHolder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = itemMenu.getCount();
                if (i == 0)
                {
                    //viewHolder.tvCount.setText(i);
                    itemMenu.setCount(i);
                }
                else
                {
                    i = i - 1;
                    viewHolder.tvCount.setText(String.valueOf(i));
                    itemMenu.setCount(i);
                }
            }
        });
        return viewRow;
    }
}
