package com.example.poker;

import androidx.annotation.NonNull;

public class GameCard {
    public final CardSuit Suit;
    public final CardTitle Title;

    public int getPower(){
        return Title.getPower();
    }

    public CardColor getColor(){
        return Suit.Color;
    }

    public GameCard(CardSuit suit, CardTitle title) {
        Suit = suit;
        Title = title;
    }

    public void draw(CardFace face){
        face.TitleTop.setText(Title.toString());
        face.TitleBot.setText(Title.toString());
        face.SuitMid.setText(Suit.toString());
    }

    @NonNull
    @Override
    public String toString() {
        return Title.toString() + Suit.toString();
    }
}
