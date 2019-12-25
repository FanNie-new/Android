package cn.edu.fan.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.fan.himalaya.R;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {

    private static final String TAG = "TrackListAdapter";
    private List<Track> mDetailList = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mUpdateDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener = null;
    private ItemLongClickListener mItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //载入
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //找到控件，设置数据
        View itemView = holder.itemView;
        //顺寻ID
        TextView orderTv = itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        //播放量
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
        //时长
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        //更新日期
        TextView updateDateTv = itemView.findViewById(R.id.detail_item_update_time);

        //设置数据
       final Track track =  mDetailList.get(position);
       orderTv.setText((position + 1) + "");
       titleTv.setText(track.getTrackTitle());
       playCountTv.setText(track.getPlayCount() + "");

       int durationMil = track.getDuration() * 1000;
       String duration = mDurationFormat.format(durationMil);
       durationTv.setText(duration);
//     Log.d(TAG,"getSampleDuration --->" + track.getDuration());

       String updateTimeText = mUpdateDateFormat.format(track.getUpdatedAt());
       updateDateTv.setText(updateTimeText);

       //设置item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),"你点击了第 " + position + " 个item",Toast.LENGTH_SHORT).show();
                if (mItemClickListener != null) {
                    //参数需要有列表和位置
                    mItemClickListener.onItemClick(mDetailList,position);
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailList.size();
    }

    public void setData(List<Track> tracks) {
        //清楚原来的数据
        mDetailList.clear();
        //添加新的数据
        mDetailList.addAll(tracks);
        //更新数据
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemClick(List<Track> detailDta, int position);
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public interface ItemLongClickListener{
        void onItemLongClick(Track track);
    }
}
