package com.example.tablayoutmvvm.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tablayoutmvvm.R;
import com.example.tablayoutmvvm.entity.Memo;
import com.example.tablayoutmvvm.entity.MemoDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Memo> items = new ArrayList<>();
    private Context mContext;
    private MemoDatabase db;

    public RecyclerAdapter(MemoDatabase db) {
        this.db = db;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Memo> getItems() {return items;}

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {

        viewHolder.onBind(items.get(position),position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvContents;
        private Button btnSave;
        private int index;

        public ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvContents = view.findViewById(R.id.tvContents);
            btnSave = view.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(v -> editData(tvContents.getText().toString()));

        }
        public void onBind(Memo memo, int position){
            index = position;
            tvTitle.setText(memo.getTitle());
            tvContents.setText(memo.getContents());
        }

        public void editData(String contents){
            new Thread(() -> {
                items.get(index).setContents(contents);
                db.memoDao().update(items.get(index));
            }).start();
            Toast.makeText(mContext,"저장완료", Toast.LENGTH_SHORT).show();
        }


    }

    public void setItem(List<Memo> data) {
        items = data;
        notifyDataSetChanged();
    }

}
