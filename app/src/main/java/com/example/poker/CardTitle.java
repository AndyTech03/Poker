package com.example.poker;

import androidx.annotation.NonNull;

public enum CardTitle {
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9),
    Ten(10),
    Jack(11),
    Queen(12),
    King(13),
    Ace(14);

    private final Integer power;

    public Integer getPower() {
        return power;
    }

    public boolean higherThen(CardTitle other){
        return power > other.power;
    }

    CardTitle(int power){
        this.power = power;
    }
    public static CardTitle getCard(int i){
        switch (i){
            case 2:
                return Two;
            case 3:
                return Three;
            case 4:
                return Four;
            case 5:
                return Five;
            case 6:
                return Six;
            case 7:
                return Seven;
            case 8:
                return Eight;
            case 9:
                return Nine;
            case 10:
                return Ten;
            case 11:
                return Jack;
            case 12:
                return Queen;
            case 13:
                return King;
            case 14:
                return Ace;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case Two:
            case Three:
            case Four:
            case Five:
            case Six:
            case Seven:
            case Eight:
            case Nine:
            case Ten:
                return power.toString();
            case Jack:
                return "J";
            case Queen:
                return "Q";
            case King:
                return "K";
            case Ace:
                return "A";
        }
        throw new IllegalStateException("ВТФ?");
    }
}
