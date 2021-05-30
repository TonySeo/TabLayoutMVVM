package com.example.tablayoutmvvm.ui.main;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tablayoutmvvm.R;
import com.example.tablayoutmvvm.entity.Memo;
import com.example.tablayoutmvvm.entity.MemoDatabase;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private Button btnPlus;

    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    private MemoDatabase db;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        btnPlus = root.findViewById(R.id.btnPlus);
        recyclerView = root.findViewById(R.id.rv_view);

        initSwipe();

        btnPlus.setOnClickListener(v -> {
            // title 입력 다이얼로그를 호출한다.
            // title 입력하여 리사이클러뷰 addItem
            final EditText edittext = new EditText(getContext());

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Item 추가");
            builder.setMessage("제목을 입력해 주세요.");
            builder.setView(edittext);
            builder.setPositiveButton("입력",
                    (dialog, which) -> {
                        //제목 입력, DB추가
                        if (!edittext.getText().toString().isEmpty()) {
                            new Thread(() -> {
                                Memo memo = new Memo(edittext.getText().toString(),null);
                                db.memoDao().insert(memo);
                            }).start();
                        }
                    });
            builder.setNegativeButton("취소",
                    (dialog, which) -> {
                        //취소버튼 클릭
                    });
            builder.show();

        });

        db = MemoDatabase.getDatabase(getContext());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(db);
        recyclerView.setAdapter(adapter);

        //UI 갱신 (라이브데이터 Observer 이용, 해당 디비값이 변화가생기면 실행됨)
        db.memoDao().getAll().observe(getViewLifecycleOwner(), new Observer<List<Memo>>() {
            @Override
            public void onChanged(List<Memo> data) {
                adapter.setItem(data);
            }
        });

        return root;
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /* | ItemTouchHelper.RIGHT */) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.memoDao().delete(adapter.getItems().get(position));
                        }

                    }).start();

                }else {
                    //오른쪽으로 밀었을때.
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE ) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        //오른쪽으로 밀었을 때

                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        /*
                         * icon 추가할 수 있음.
                         */
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_png); //vector 불가!
                        // RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        //c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}