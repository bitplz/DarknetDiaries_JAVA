package com.bug32.darknetdiaries;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.itemViewHolder>{

    private Context mContext;
    private ArrayList<Item> mItemList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnClickItemListener(OnItemClickListener listener){
        mListener = listener;
    }

    public itemAdapter (Context context, ArrayList<Item> itemsList){
        mContext = context;
        mItemList = itemsList;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.thumbnail, parent, false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        Item currentItem = mItemList.get(position);

        String title = currentItem.getmTitle();
        String pubDate = currentItem.getmPubDate();
        String imgUrl = currentItem.getmImgUrl();
        String duration = currentItem.getmDuration();

        holder.title.setText(title);
        holder.pubDate.setText(pubDate);
        holder.duration.setText(duration);
        Picasso.get().load(imgUrl).fit().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView pubDate;
        public TextView duration;
        public ImageView image;


        public itemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            pubDate = itemView.findViewById(R.id.pubDate);
            image = itemView.findViewById(R.id.image);
            duration = itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((mListener != null)){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public void setFilter(ArrayList<Item> newList) {
        mItemList = new ArrayList<>();
        mItemList.addAll(newList);
        notifyDataSetChanged();
    }
}
