package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quang.orderfood.R;

import java.util.ArrayList;

import objects.HistoryBill;
import objects.ItemMenu;

/**
 * Created by quang on 30-Jan-18.
 */

public class ListviewHistoryBillAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<HistoryBill> arrItem;


    public ListviewHistoryBillAdapter(Context context, int layout, ArrayList<HistoryBill> arrItem) {
        this.context = context;
        this.layout = layout;
        this.arrItem = arrItem;
    }

    private class ViewHolder{
        TextView tvIdBill;
        TextView tvTable;
        TextView tvPeople;
        TextView tvTotal;
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
            ListviewHistoryBillAdapter.ViewHolder viewHolder = new ListviewHistoryBillAdapter.ViewHolder();

            viewHolder.tvIdBill = viewRow.findViewById(R.id.tvIdBillHB);
            viewHolder.tvTable = viewRow.findViewById(R.id.tvTableHB);
            viewHolder.tvPeople = viewRow.findViewById(R.id.tvPeopleHB);
            viewHolder.tvTotal = viewRow.findViewById(R.id.tvPriceHB);

            viewRow.setTag(viewHolder);
        }

        final HistoryBill historyBill = arrItem.get(i);
        final ListviewHistoryBillAdapter.ViewHolder viewHolder = (ListviewHistoryBillAdapter.ViewHolder) viewRow.getTag();
        viewHolder.tvIdBill.setText("Mã hóa đơn: "+ historyBill.getIdBill());
        viewHolder.tvTable.setText("Bàn số: "+historyBill.getTable()+"");
        viewHolder.tvPeople.setText("Số khách: "+historyBill.getPeople()+"");
        viewHolder.tvTotal.setText("Tổng tiền: "+historyBill.getTotal()+"");
        return viewRow;
    }
}