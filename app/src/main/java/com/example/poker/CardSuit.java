package com.example.poker;

import androidx.annotation.NonNull;

public enum CardSuit {
    Spades(CardColor.Black),
    Crosses(CardColor.Black),
    Hearts(CardColor.Red),
    Diamonds(CardColor.Red);
    public final CardColor Color;

    CardSuit(CardColor color) {
        Color = color;
    }

    public static CardSuit getSuit(int i) {
        switch (i) {
            case 0:
                return Spades;
            case 1:
                return Crosses;
            case 2:
                return Hearts;
            case 3:
                return Diamonds;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case Crosses:
                return "♣";
            case Diamonds:
                return "♦";
            case Hearts:
                return "♥";
            case Spades:
                return "♠";
        }
        throw new IllegalStateException("ВТФ?");
    }
}
