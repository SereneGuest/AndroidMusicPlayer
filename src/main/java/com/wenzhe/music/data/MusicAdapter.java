package com.wenzhe.music.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wenzhe.music.R;

import java.util.List;

/**
 * Created by wen-zhe on 2015/11/7.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<MusicInfo> list;
    private Context context;

    public static ListItemClickListener itemClickListener;

    public MusicAdapter(List<MusicInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public interface ListItemClickListener {
        void onItemClicked(int position);
    }

    public void setListItemListener(ListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvNumber;
        public TextView tvTitle;
        public TextView tvArtist;
        public TextView tvDuration;
        public ViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist);
            //tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (itemClickListener == null) {
                return;
            }
            itemClickListener.onItemClicked(getPosition());
        }
    }

    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MusicInfo musicInfo = list.get(position);
        holder.tvNumber.setText(position + 1 + ".");
        holder.tvArtist.setText(musicInfo.getArtist());
        holder.tvTitle.setText(musicInfo.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
