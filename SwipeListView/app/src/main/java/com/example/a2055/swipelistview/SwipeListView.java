package com.example.a2055.swipelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SwipeListView extends ListView {
    String text = " Data: %d";

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        List<String> mDatas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mDatas.add(String.format(text, i));
        }

        DataAdapter adapter = new DataAdapter(getContext(), mDatas);
        setAdapter(adapter);
    }
}
