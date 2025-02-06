package com.example.petstore.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petstore.R;
import com.example.petstore.anotherActivity.GDMapActivity;
import com.example.petstore.pojo.Bean;

import java.util.List;

/**
 * PoiAdapter 是一个自定义适配器，用于显示一组 Bean 对象的数据。
 * 继承自 BaseAdapter，可以将数据绑定到 ListView 的每一项。
 */
public class PoiAdapter extends BaseAdapter {
    // Bean对象列表，代表要显示的数据集合
    private List<Bean> poiItemList;
    // 上下文对象，用于加载布局资源等
    private final Context context;
    // 当前选中项的位置，默认为 0
    private int selectPosition = 0;
    private GDMapActivity gdMapActivity;

    /**
     * 构造方法，初始化适配器的基本数据。
     *
     * @param poiItemList 要显示的数据集合
     * @param context     上下文对象
     */
    public PoiAdapter(List<Bean> poiItemList, Context context, GDMapActivity gdMapActivity) {
        this.poiItemList = poiItemList;
        this.context = context;
        this.gdMapActivity = gdMapActivity;
    }

    /**
     * 获取数据项的总数量。
     *
     * @return 数据项数量
     */
    @Override
    public int getCount() {
        return poiItemList == null ? 0 : poiItemList.size();
    }

    /**
     * 获取指定位置的数据项。
     *
     * @param position 数据项的位置
     * @return 指定位置的数据项对象
     */
    @Override
    public Object getItem(int position) {
        return poiItemList.get(position);
    }

    /**
     * 获取指定位置的数据项 ID。
     *
     * @param position 数据项的位置
     * @return 数据项 ID（位置）
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取指定位置的视图，设置视图中的数据及样式。
     *
     * @param position    视图对应的数据项位置
     * @param convertView 可复用的视图
     * @param parent      父视图（ListView）
     * @return 视图对象，显示指定位置的数据项
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 检查是否有可复用的视图，若没有则创建新的视图
        if (convertView == null) {
            // 加载布局文件
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
            // 初始化 ViewHolder，存放控件的引用
            holder = new ViewHolder();
            holder.text1 = convertView.findViewById(android.R.id.text1);
            holder.text2 = convertView.findViewById(android.R.id.text2);
            // 将 ViewHolder 存放在视图中，以便复用
            convertView.setTag(holder);
        } else {
            // 复用已存在的 ViewHolder，避免重复 findViewById 操作
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前数据项对应的 Bean 对象
        Bean bean = poiItemList.get(position);
        // 设置文本内容为 Bean 的 name 和 address
        holder.text1.setText(bean.getName());
        holder.text2.setText(bean.getAddress());

        // 根据是否为选中项，设置不同的文字颜色
        if (selectPosition == position) {
            holder.text1.setTextColor(context.getColor(R.color.green));
            holder.text2.setTextColor(context.getColor(R.color.green));
        } else {
            holder.text1.setTextColor(context.getColor(R.color.black));
            holder.text2.setTextColor(context.getColor(R.color.black));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bean selectedBean = poiItemList.get(position); // 获取当前选中的地址
                gdMapActivity.setAddress(
                        selectedBean.getName(),
                        selectedBean.getAddress(),
                        selectedBean.getLongitude(),
                        selectedBean.getLatitude());
                gdMapActivity.setComponent();

            }
        });



        return convertView;
    }

    /**
     * 设置选中的项位置，并通知 ListView 数据已更改。
     *
     * @param selectPosition 选中的项位置
     */
    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public void setData(List<Bean> list){
        poiItemList = list;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder 内部类，用于存放视图中的控件引用。
     */
    static class ViewHolder {
        TextView text1, text2;
    }
}
