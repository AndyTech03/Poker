package com.example.poker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Combo {
    private String title = "";
    private int power = 1;
    private int extraPower = 1;

    public Combo(GameCard[] table, GameCard[] hand) {
        List<GameCard> cards = new ArrayList<>();
        cards.addAll(Arrays.asList(table));
        cards.addAll(Arrays.asList(hand));
        cards.sort(Comparator.comparingInt(GameCard::getPower));

        setGreatestCombo(cards);
    }

    private void setCombo(String title, int power, int extraPower) {
        this.title = title;
        this.power = power;
        this.extraPower = extraPower;
    }

    private Hashtable<Integer, Integer> findComboGroups(List<GameCard> combo) {
        Hashtable<Integer, Integer> groups = new Hashtable<>();
        for (GameCard card : combo) {
            int power = card.getPower();
            if (groups.containsKey(power)) {
                int count = groups.get(power);
                groups.replace(power, count + 1);
            } else {
                groups.put(power, 1);
            }
        }
        Log.d("CONVERT_TEST!!", groups.toString());

        Object[] keys = groups.keySet().toArray();
        for (Object key : keys) {
            if (Objects.equals(groups.get((Integer) key), 1))
                groups.remove(key);
        }

        return groups;
    }


    private void setGreatestCombo(List<GameCard> cards) {
        // power, count
        Hashtable<Integer, Integer> powerCounter = new Hashtable<>();
        // suit, count
        Hashtable<CardSuit, Integer> suitCounter = new Hashtable<>();
        // startPower, cards
        List<List<GameCard>> powerSequenceList = new ArrayList<>();
        for (GameCard card : cards) {
            int power = card.getPower();
            CardSuit suit = card.Suit;

            Integer containsCount = powerCounter.get(power);
            if (containsCount == null) {
                powerCounter.put(power, 1);
            } else {
                powerCounter.replace(power, containsCount + 1);
            }

            containsCount = suitCounter.get(suit);
            if (containsCount == null) {
                suitCounter.put(suit, 1);
            } else {
                suitCounter.replace(suit, containsCount + 1);
            }

            boolean notAdded = true;
            for (List<GameCard> list : powerSequenceList) {
                Optional<GameCard> element = list.stream().max(
                        Comparator.comparingInt(GameCard::getPower));
                int max = element.get().getPower();
                element = list.stream().min(
                        Comparator.comparingInt(GameCard::getPower));
                int min = element.get().getPower();
                if (power <= max + 1 && power >= min - 1) {
                    notAdded = false;
                    list.add(card);
                }
            }
            if (notAdded) {
                List<GameCard> list = new ArrayList<>();
                list.add(card);
                powerSequenceList.add(list);
            }
        }
        Log.i("POKER_COMBO_FINDER",
                "Cards: " + cards + "\n" +
                        "powerCounter:" + powerCounter + "\n" +
                        "suitCounter:" + suitCounter + "\n" +
                        "powerSequenceList:" + powerSequenceList + "\n");

        List<GameCard> fleshSequence = getMaxSequence(powerSequenceList);
        boolean flesh = fleshSequence != null;



        boolean set = powerCounter.containsValue(3);
        List<Integer> powerSets = getPowerKeys(3, powerCounter);
        if (set) {
            Optional<Integer> element = powerSets.stream().max(Integer::compareTo);
            int powerSet = element.get();
            setCombo("Тройка", 1, powerSet);
        }

        boolean pair = powerCounter.containsValue(2);
        List<Integer> powerPairs = getPowerKeys(2, powerCounter);
        if (pair) {
            Optional<Integer> element = powerPairs.stream().max(Integer::compareTo);
            int powerPair = element.get();
            setCombo("Пара", 1, powerPair);
        }

        // Старшая карта
        {
            Optional<GameCard> element = cards.stream().max(
                    Comparator.comparingInt(GameCard::getPower));
            int max = element.get().getPower();
            setCombo("Старшая карта", 0, max);
        }
    }

    @Nullable
    private List<GameCard> getMaxSequence(List<List<GameCard>> sequences){
        for (List<GameCard> sequence : sequences){
            if (sequence.size() < 5)
                continue;
            return sequence;
        }
        return null;
    }

    private List<Integer> getPowerKeys(Integer count, Hashtable<Integer, Integer> counter){
        List<Integer> result = new ArrayList<>();
        counter.forEach((key, value) -> {
            if (Objects.equals(value, count))
                result.add(key);
        });
        return result;
    }

    private List<CardSuit> getSuitKeys(Integer count, Hashtable<CardSuit, Integer> counter){
        List<CardSuit> result = new ArrayList<>();
        counter.forEach((key, value) -> {
            if (Objects.equals(value, count))
                result.add(key);
        });
        return result;
    }


    public String getTitle() {
        return title;
    }

    public int getPower() {
        return power;
    }

    public int getExtraPower() {
        return extraPower;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " (" + extraPower + ")";
    }

    public GameResult play(Combo combo) {
        return GameResult.Win;
    }
}
