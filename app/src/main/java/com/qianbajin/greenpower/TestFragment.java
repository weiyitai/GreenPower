package com.qianbajin.greenpower;

import android.app.Instrumentation;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @author wWX407408
 * @Created at 2017/12/20  9:16
 * @des
 */

public class TestFragment extends Fragment {

    public static final String TAG = "TestFragment";

    private RecyclerView mRecyclerView;
    private ProHandler mProHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.rv);
        view.findViewById(R.id.moni_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProHandler.sendEmptyMessage(1);
            }
        });
        Log.d(TAG, DateFormat.getDateTimeInstance().format(new Date(1515340800235L)));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        HandlerThread handlerThread = new HandlerThread("adsd");
        handlerThread.start();
        mProHandler = new ProHandler(handlerThread.getLooper());

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public boolean mNeedLeftSpacing;
            public int mVerticalSpace = 6;
            public int mRowSize = 5;
            public int mHorizontalSpace = 6;

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int frameWidth = (int) ((parent.getWidth() - (float) mHorizontalSpace * (mRowSize - 1)) / mRowSize);
                int padding = parent.getWidth() / mRowSize - frameWidth;
                int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (itemPosition < mRowSize) {
                    outRect.top = 0;
                } else {
                    outRect.top = mVerticalSpace;
                }

                if (itemPosition % mRowSize == 0) {
                    outRect.left = 0;
                    outRect.right = padding;
                    mNeedLeftSpacing = true;
                } else if ((itemPosition + 1) % mRowSize == 0) {
                    mNeedLeftSpacing = false;
                    outRect.right = 0;
                    outRect.left = padding;
                } else if (mNeedLeftSpacing) {
                    mNeedLeftSpacing = false;
                    outRect.left = mHorizontalSpace - padding;
                    if ((itemPosition + 2) % mRowSize == 0) {
                        outRect.right = mHorizontalSpace - padding;
                    } else {
                        outRect.right = mHorizontalSpace / 2;
                    }
                } else if ((itemPosition + 2) % mRowSize == 0) {
                    mNeedLeftSpacing = false;
                    outRect.left = mHorizontalSpace / 2;
                    outRect.right = mHorizontalSpace - padding;
                } else {
                    mNeedLeftSpacing = false;
                    outRect.left = mHorizontalSpace / 2;
                    outRect.right = mHorizontalSpace / 2;
                }
                outRect.bottom = 0;

            }
        });
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add("item " + i);
        }

        RvAdapter adapter = new RvAdapter(list);
        mRecyclerView.setAdapter(adapter);
    }

    static class RvAdapter extends RecyclerView.Adapter {

        private final List<String> mList;

        public RvAdapter(List<String> list) {
            mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RvAdapter", "position:" + position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    class ProHandler extends Handler {

        public ProHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Instrumentation in = new Instrumentation();
            float minX = 100.f, maxX = 1080.f, minY = 285.f, maxY = 1159, intervalX = 200.f, intervalY = 96.f;
            for (float j = minY; j < maxY; j += intervalY) {
                for (float i = minX; i < maxX; i += intervalX) {
                    long millis = SystemClock.uptimeMillis();
                    in.sendPointerSync(MotionEvent.obtain(millis, millis, MotionEvent.ACTION_DOWN, i, j, 0));
                    in.sendPointerSync(MotionEvent.obtain(millis + 12L, millis + 12L, MotionEvent.ACTION_UP, i, j, 0));
                    Log.d(TAG, "handleMessage:i:" + i + "  j:" + j + "  millis:" + millis);
                    SystemClock.sleep(100L);
                }
            }
            Log.d(TAG, "handleMessage:模拟点击完成************");
        }
    }

}
