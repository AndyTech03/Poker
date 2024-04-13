package com.example.poker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    public static final Integer MATCHES_COUNT = 2;
    public static final Integer MIN_BET = 10;
    public static final Integer CARDS_COUNT = 4 * 13;
    public static final Integer START_SCORE = 500;
    private static final Random random = new Random();

    private final GameCard[] cardsSet = new GameCard[CARDS_COUNT];
    private final List<GameCard> cardDeck = new ArrayList<>();

    private Integer playerScore;
    private Integer computerScore;
    private Integer round;
    private boolean canReshuffle;

    private final GameCard[] tableCards = new GameCard[5];
    private final GameCard[] playerCards = new GameCard[2];
    private final GameCard[] computerCards = new GameCard[2];

    private final CardFace[] uiTableCards = new CardFace[5];
    private final CardFace[] uiHandCards = new CardFace[2];
    private TextView betText;
    private SeekBar betBar;

    private Button reshuffleButton;

    private AlertDialog.Builder restartMessageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restartMessageBox = new AlertDialog.Builder(MainActivity.this);
        restartMessageBox.setPositiveButton("OK", (dialog, which) -> {
            restartGame();
        });
        restartMessageBox.setNegativeButton("Выйти", (dialog, which) -> {
            finishAndRemoveTask();
            System.exit(0);
        });
        restartMessageBox.setCancelable(true);

        uiTableCards[0] = new CardFace(findViewById(R.id.table1));
        uiTableCards[1] = new CardFace(findViewById(R.id.table2));
        uiTableCards[2] = new CardFace(findViewById(R.id.table3));
        uiTableCards[3] = new CardFace(findViewById(R.id.table4));
        uiTableCards[4] = new CardFace(findViewById(R.id.table5));

        uiHandCards[0] = new CardFace(findViewById(R.id.hand1));
        uiHandCards[1] = new CardFace(findViewById(R.id.hand2));


        betText = findViewById(R.id.betTitle);
        betBar = findViewById(R.id.bestChoice);
        betBar.setOnSeekBarChangeListener(new BetBarListener(betText));


        reshuffleButton = findViewById(R.id.reshuffleButton);
        reshuffleButton.setOnClickListener(v -> {
            if (canReshuffle) {
                canReshuffle = false;
                reshuffleButton.setEnabled(false);
                getPlayerCards();
            }

        });

        findViewById(R.id.playButton).setOnClickListener(v -> {
            onPlayMatch();
        });

        int i = 0;
        for (int s = 0; s < 4; s++) {
            CardSuit suit = CardSuit.getSuit(s);
            for (int t = CardTitle.Two.getPower(); t <= CardTitle.Ace.getPower(); t++) {
                cardsSet[i++] = new GameCard(suit, CardTitle.getCard(t));
            }
        }

        restartGame();
    }

    private void restartGame() {
        round = 0;
        List<GameCard> cardPool = new ArrayList<>(Arrays.asList(cardsSet));
        cardDeck.clear();
        for (int i = 0; i < CARDS_COUNT; i++) {
            int index = random.nextInt(cardPool.size());
            cardDeck.add(cardPool.get(index));
            cardPool.remove(index);
        }
        playerScore = computerScore = START_SCORE;
        betBar.setProgress(0, false);
        betBar.setProgress(MIN_BET / BetBarListener.PROGRESS_STEP * 3, true);
        betBar.setMax(START_SCORE / BetBarListener.PROGRESS_STEP);
        betBar.setMin(MIN_BET / BetBarListener.PROGRESS_STEP);
        beginMatch();
    }

    private void onPlayMatch() {

        Log.i("POKER_COMBO_FINDER", "------\nPlayer combo:\n");
        Combo playerCombo = new Combo(tableCards, playerCards);
        Log.i("POKER_COMBO_FINDER", "------\nComputer combo:\n");
        Combo computerCombo = new Combo(tableCards, computerCards);

        int bet = betBar.getProgress() * BetBarListener.PROGRESS_STEP;
        String winnerCombo = "";
        String result = "";
        switch (playerCombo.play(computerCombo)){
            case Win:
                winnerCombo = playerCombo.toString();
                result = "Вы победили!";
                playerScore += bet;
                computerScore -= bet;
                break;
            case Lose:
                winnerCombo = computerCombo.toString();
                result = "Вы проиграли!";
                playerScore -= bet;
                computerScore += bet;
                break;
            case Draw:
                result = "Ничья!";
                break;
        }

        if (round++ < MATCHES_COUNT && playerScore >= MIN_BET && computerScore >= MIN_BET) {
            betBar.setMax((playerScore > computerScore ? computerScore : playerScore) / BetBarListener.PROGRESS_STEP);
            beginMatch();
        } else {
            if (playerScore.equals(computerScore)) {
                restartMessageBox.setMessage(
                        "Никто не одержал победу в этой партии, ничья.\nНачать новую?");
            } else {
                restartMessageBox.setMessage((
                        playerScore > computerScore ? "Вы одержали" : "Компьютер одержал") +
                        " победу в этой партии.\nНачать новую?");
            }
            restartMessageBox.setTitle("Игра окончена.");
            restartMessageBox.create().show();
        }

        showMessage("Вы:\n" + playerCombo +
                "\n\nКомпьютер:\n" + computerCombo, "Открываемся");

        showMessage(winnerCombo, result);
    }

    private void beginMatch() {
        canReshuffle = true;
        reshuffleButton.setEnabled(true);

        Iterator<GameCard> cards = cardDeck.stream().iterator();
        for (int i = 0; i < 5; i++)
            tableCards[i] = cards.next();

        for (int i = 0; i < 2; i++)
            playerCards[i] = cards.next();

        for (int i = 0; i < 2; i++)
            computerCards[i] = cards.next();
        cardDeck.removeAll(cardDeck.subList(0, 5 + 2 + 2));
        showCards();
    }

    private void getPlayerCards() {
        Iterator<GameCard> cards = cardDeck.stream().iterator();
        for (int i = 0; i < 2; i++)
            playerCards[i] = cards.next();
        cardDeck.removeAll(cardDeck.subList(0, 2));
        showCards();
    }

    private void showCards() {
        for (int i = 0; i < 5; i++)
            tableCards[i].draw(uiTableCards[i]);

        for (int i = 0; i < 2; i++)
            playerCards[i].draw(uiHandCards[i]);
    }

    private void showMessage(String message, String title) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}