package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gy.refreshtolistview.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/18.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> allValues;

    public ListViewAdapter(Context mContext, List<String> allValues) {
        this.mContext = mContext;
        this.allValues = allValues;
    }

    @Override
    public int getCount() {
        return allValues.size();
    }

    @Override
    public Object getItem(int i) {
        return allValues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_item, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.tv);
        textView.setText(allValues.get(i));
        return view;
    }

    public void onDateChange(List<String> values) {
        this.allValues = values;
        this.notifyDataSetChanged();
    }
}
