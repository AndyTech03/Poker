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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Combo {
    private String title = "";
    private int priority = 1;
    private int power = 1;
    private int extraPower = 1;

    public Combo(GameCard[] table, GameCard[] hand) {
        List<GameCard> cards = new ArrayList<>();
        cards.addAll(Arrays.asList(table));
        cards.addAll(Arrays.asList(hand));
        cards.sort(Comparator.comparingInt(GameCard::getPower));

        setGreatestCombo(cards, hand);
    }

    private void setCombo(String title, int priority, int power, int extraPower) {
        this.title = title;
        this.priority = priority;
        this.power = power;
        this.extraPower = extraPower;
    }

    private void setGreatestCombo(List<GameCard> cards, GameCard[] hand) {
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

        List<GameCard> streetSequence = getMaxSequence(powerSequenceList);
        CardSuit flashSuit = getFlashSuit(suitCounter);

        boolean pair = powerCounter.containsValue(2);
        boolean set = powerCounter.containsValue(3);
        boolean street = streetSequence != null;
        boolean flash = flashSuit != null;
        boolean kare = powerCounter.containsValue(4);


        Log.i("POKER_COMBO_FINDER",
                "Cards: " + cards + "\n" +
                        "powerCounter:" + powerCounter + "\n" +
                        "suitCounter:" + suitCounter + "\n" +
                        "powerSequenceList:" + powerSequenceList + "\n" +
                        "pair\tset\t\tstreet\tflash\tkare:\n" +
                        pair + "\t" + set + "\t" + street + "\t" + flash + "\t" + kare + "\n\n" +
                        "streetSequence:" + streetSequence + "\n" +
                        "flashSuit:" + flashSuit + "\n");

        if (street && flash) {
            Stream<GameCard> streetFlashCards = cards.stream()
                    .filter(card -> card.Suit == flashSuit);
            if (streetFlashCards.count() >= 5) {
                int streetFlashPower = streetFlashCards
                        .max(Comparator.comparingInt(GameCard::getPower))
                        .get().getPower();

                int streetFlashExtraPower = cards.stream().filter(card -> streetFlashCards
                                .noneMatch(SFCard -> card != SFCard))
                        .max(Comparator.comparingInt(GameCard::getPower))
                        .get().getPower();
                if (streetFlashPower == CardTitle.Ace.getPower())
                    setCombo("Роял-Флеш", 8, streetFlashPower, streetFlashExtraPower);
                setCombo("Стрит-Флеш", 7, streetFlashPower, streetFlashExtraPower);
                return;
            }
        }

        if (kare) {
            Stream<GameCard> kareCards = cards.stream()
                    .filter(card -> powerCounter.getOrDefault(card.getPower(), 0) == 4);

            int karePower = kareCards.max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();
            int kareExtraPower = cards.stream().filter(card -> kareCards
                            .noneMatch(kareCard -> card != kareCard))
                    .max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();
            setCombo("Каре", 6, karePower, kareExtraPower);
            return;
        }

        if (pair && set) {
            List<GameCard> FHCards = cards.stream()
                    .filter(card -> powerCounter.getOrDefault(card.getPower(), 0) >= 2)
                    .collect(Collectors.toList());
            int fullHousePower = FHCards.stream().max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();
            int FHExtraPower = cards.stream().filter(card -> !FHCards.contains(card))
                    .max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();
            setCombo("Фулл-хаус", 5, fullHousePower, FHExtraPower);
            return;
        }

        if (flash) {
            List<GameCard> flashCards = cards.stream()
                    .filter(card -> card.Suit == flashSuit)
                    .collect(Collectors.toList());
            int flashPower = flashCards.stream().max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();

            int flashExtraPower = cards.stream().filter(card -> !flashCards.contains(card))
                    .max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();
            setCombo("Флеш", 4, flashPower, flashExtraPower);
            return;
        }

        if (street) {
            int streetExtraPower = cards.stream().filter(card -> !streetSequence.contains(card))
                    .max(Comparator.comparingInt(GameCard::getPower))
                    .get().getPower();
            setCombo("Стрит", 3, streetSequence.get(streetSequence.size()).getPower(),
                    streetExtraPower);
            return;
        }

        if (set) {
            List<Integer> powerSets = getPowerKeys(3, powerCounter);
            Optional<Integer> element = powerSets.stream().max(Integer::compareTo);
            int setPower = element.get();
            Integer setExtraPower = cards.stream().filter(card -> !powerSets.contains(card.getPower()))
                    .max(Comparator.comparingInt(GameCard::getPower))
                    .map(GameCard::getPower)
                    .orElse(0);
            setCombo("Тройка", 2, setPower, setExtraPower);
            return;
        }

        if (pair) {
            List<Integer> powerPairs = getPowerKeys(2, powerCounter);
            Optional<Integer> element = powerPairs.stream().max(Integer::compareTo);
            int pairPower = element.get();

            int pairExtraPower = cards.stream()
                    .map(GameCard::getPower)
                    .filter(cardPower -> !powerPairs.contains(cardPower))
                    .max(Integer::compareTo)
                    .orElse(0);
            setCombo("Пара", 1, pairPower, pairExtraPower);
            return;
        }

        // Старшая карта
        {
            Optional<GameCard> element = cards.stream().max(
                    Comparator.comparingInt(GameCard::getPower));
            int max = element.get().getPower();
            int maxHand = Arrays.stream(hand)
                    .filter(card -> card.getPower() != max)
                    .max(Comparator.comparingInt(GameCard::getPower))
                    .map(GameCard::getPower)
                    .orElse(0);
            setCombo("Старшая карта", 0, max, maxHand);
        }
    }

    @Nullable
    private CardSuit getFlashSuit(Hashtable<CardSuit, Integer> counter) {
        for (CardSuit key : counter.keySet()) {
            Integer count = counter.get(key);
            if (count == null || count < 5)
                continue;
            return key;
        }
        return null;
    }

    @Nullable
    private List<GameCard> getMaxSequence(List<List<GameCard>> sequences) {
        for (List<GameCard> sequence : sequences) {
            if (sequence.size() < 5)
                continue;

            ArrayList<Integer> powers = new ArrayList<>();
            for (GameCard card : sequence)
                if (!powers.contains(card.getPower()))
                    powers.add(card.getPower());

            if (powers.size() < 5)
                continue;

            return sequence;
        }
        return null;
    }

    private List<Integer> getPowerKeys(Integer count, Hashtable<Integer, Integer> counter) {
        List<Integer> result = new ArrayList<>();
        counter.forEach((key, value) -> {
            if (Objects.equals(value, count))
                result.add(key);
        });
        return result;
    }

    private List<CardSuit> getSuitKeys(Integer count, Hashtable<CardSuit, Integer> counter) {
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

    public int getPriority() {
        return priority;
    }

    public int getPower() {
        return power;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " (" + power + ", "+ extraPower+")";
    }

    public GameResult play(Combo combo) {
        Log.i("POKER_COMBO_FINDER_OPEN",
                "me: " + this + "\n" +
                "other: " + combo);

        if (combo.priority > this.priority)
            return GameResult.Lose;
        if (combo.priority < this.priority)
            return GameResult.Win;

        Log.i("POKER_COMBO_FINDER_OPEN",
        "priority equals!");

        if (combo.power > this.power)
            return GameResult.Lose;
        if (combo.power < this.power)
            return GameResult.Win;

        Log.i("POKER_COMBO_FINDER_OPEN",
                "power equals!");

        if (combo.extraPower > this.extraPower)
            return GameResult.Lose;
        if (combo.extraPower < this.extraPower)
            return GameResult.Win;

        return GameResult.Draw;
    }
}
