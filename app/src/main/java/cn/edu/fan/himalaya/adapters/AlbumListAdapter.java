package cn.edu.fan.himalaya.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fan.himalaya.R;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private final static String TAG = "AlbumListAdapter";

    private List<Album> mData = new ArrayList<>();
    private OnAlbumItemClickListener mItemClickListener = null;
    private OnAlbumItemLongClickListener mLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载View
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapter.InnerHolder holder, final int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPosition = (int)v.getTag();
                    mItemClickListener.onItemClick(clickPosition,mData.get(clickPosition));
                }
                Log.d(TAG,"holder.itemView.onClick --->" + v.getTag());
            }
        });
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mLongClickListener.onItemLongClick(mData.get(clickPosition));
                }
                //true表示消费掉该是事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            //如果数据不为空，先清除上次的数据
            mData.clear();
            //全部添加进去
            mData.addAll(albumList);
        }
        //更新一下UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑的封面
            ImageView albumCover = itemView.findViewById(R.id.album_cover);
            //标题
            TextView albumTitle = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDescription = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlay = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContent = itemView.findViewById(R.id.album_content_size);

            //喜马拉雅Android SDK Model层具体含义 接入文档
            //设置标题
            albumTitle.setText(album.getAlbumTitle());
            //设置描述
            albumDescription.setText(album.getAlbumIntro());
            //设置播放次数
            albumPlay.setText(album.getPlayCount() + "");
            //专辑的集数
            albumContent.setText(album.getIncludeTrackCount() + "");

            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCover);
            }else {
                albumCover.setImageResource(R.mipmap.logo);
            }

        }
    }

    //暴露方法实现接口
    public void setAlbumItemClickListener(OnAlbumItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnAlbumItemClickListener {
        void onItemClick(int clickPosition, Album album);
    }

    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener listener){
        this.mLongClickListener = listener;
    }

    //item长按的接口
    public interface OnAlbumItemLongClickListener {
        void onItemLongClick(Album album);
    }

}
