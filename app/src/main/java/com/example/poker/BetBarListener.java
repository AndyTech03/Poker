package com.example.poker;

import android.annotation.SuppressLint;
import android.widget.SeekBar;
import android.widget.TextView;

public class BetBarListener implements SeekBar.OnSeekBarChangeListener{
    public final static int PROGRESS_STEP = 10;
    private final TextView label;

    public BetBarListener(TextView label){
        this.label = label;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.getProgress();
        label.setText("Ставка: " + (progress * PROGRESS_STEP));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
}
