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

import objects.Table;

/**
 * Created by quang on 22-Jan-18.
 */

public class GridviewTableAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Table> arrTable;

    public GridviewTableAdapter(Context context, int layout, ArrayList<Table> arrTable) {
        this.context = context;
        this.layout = layout;
        this.arrTable = arrTable;
    }

    private class ViewHolder
    {
        TextView tvNum;
        ImageView imgCheck;
    }

    @Override
    public int getCount() {
        return arrTable.size();
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
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvNum = viewRow.findViewById(R.id.number);
            viewHolder.imgCheck = viewRow.findViewById(R.id.imCheck);

            viewRow.setTag(viewHolder);
        }

        Table table = arrTable.get(i);
        ViewHolder viewHolder = (ViewHolder) viewRow.getTag();
        viewHolder.tvNum.setText(table.getNumber()+"");
        if (table.getCheck() == 1)
        {
            viewHolder.imgCheck.setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder.imgCheck.setVisibility(View.VISIBLE);
        }
        return viewRow;
    }
}
