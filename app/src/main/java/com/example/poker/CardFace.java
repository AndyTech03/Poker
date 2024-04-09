package com.example.poker;

import android.view.View;
import android.widget.TextView;

public class CardFace {
    public final TextView TitleTop;

    public final TextView SuitMid;
    public final TextView TitleBot;

    public CardFace(View view) {
        TitleTop = view.findViewById(R.id.titleTop);
        SuitMid = view.findViewById(R.id.suitMid);
        TitleBot = view.findViewById(R.id.titleBot);
    }
}
