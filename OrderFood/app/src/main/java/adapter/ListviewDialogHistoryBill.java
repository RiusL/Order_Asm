package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quang.orderfood.R;

import java.util.ArrayList;

import objects.HistoryBill;
import objects.ItemMenu;

/**
 * Created by quang on 30-Jan-18.
 */

public class ListviewDialogHistoryBill extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<ItemMenu> arrItem;


    public ListviewDialogHistoryBill(Context context, int layout, ArrayList<ItemMenu> arrItem) {
        this.context = context;
        this.layout = layout;
        this.arrItem = arrItem;
    }

    private class ViewHolder{
        TextView tvName;
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
            ListviewDialogHistoryBill.ViewHolder viewHolder = new ListviewDialogHistoryBill.ViewHolder();

            viewHolder.tvName = viewRow.findViewById(R.id.tvNameItemDialog);
            viewHolder.tvCount = viewRow.findViewById(R.id.tvCountItemDialog);

            viewRow.setTag(viewHolder);
        }

        final ItemMenu itemMenu = arrItem.get(i);
        final ListviewDialogHistoryBill.ViewHolder viewHolder = (ListviewDialogHistoryBill.ViewHolder) viewRow.getTag();
        viewHolder.tvName.setText( itemMenu.getName());
        viewHolder.tvCount.setText("Số lượng: "+itemMenu.getCount()+"");
        return viewRow;
    }
}