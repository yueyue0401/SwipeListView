package com.example.a2055.swipelistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class DataAdapter extends BaseAdapter {
    // the content of ListView
    private List<String> mDatas;
    private LayoutInflater mInflater;

    public DataAdapter(Context context, List<String> datas) {
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.swipe_item, null);
        TextView textView = (TextView) convertView.findViewById(R.id.TextView1);
        textView.setText(mDatas.get(position));

        // get Button
        final Button buttonHide = (Button) convertView.findViewById(R.id.buttonHide);
        final Button buttonDel = (Button) convertView.findViewById(R.id.buttonDelete);
        final Button buttonHead1 = (Button) convertView.findViewById(R.id.buttonHead1);
        final Button buttonHead2 = (Button) convertView.findViewById(R.id.buttonHead2);
        final RelativeLayout wrapper = (RelativeLayout) convertView.findViewById(R.id.wrapper);
        final SwipeLayout mSwipe = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);

        wrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                mSwipe.addVelocityTracker(ev);

                int rightButton1Position = buttonHide.getLeft();
                int rightButton2Position = buttonDel.getLeft();
                int leftButton1Position = buttonHead1.getRight();
                int leftButton2Position = buttonHead2.getRight();

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        // set button pressed to set background.
                        if (ev.getX() > rightButton2Position) {
                            buttonDel.setPressed(true);
                        } else if (ev.getX() > rightButton1Position) {
                            buttonHide.setPressed(true);
                        } else if (ev.getX() < leftButton2Position) {
                            buttonHead2.setPressed(true);
                        } else if (ev.getX() < leftButton1Position) {
                            buttonHead1.setPressed(true);
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mSwipe.doMove(ev);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        // put the button action here
                        if (buttonHide.isPressed()) {
                            buttonHide.setPressed(false);

                        } else if (buttonDel.isPressed()) {
                            mDatas.remove(position);
                            notifyDataSetChanged();
                            buttonDel.setPressed(false);

                        } else if (buttonHead1.isPressed()) {
                            buttonHead1.setPressed(false);

                        } else if (buttonHead2.isPressed()) {
                            buttonHead2.setPressed(false);
                        }

                        mSwipe.computeVelocityTracker();
                        mSwipe.doReset();
                        mSwipe.releaseTracker();

                        return true;
                }

                return false;
            }
        });
        return convertView;
    }

}
