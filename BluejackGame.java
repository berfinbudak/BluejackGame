import java.util.Random;

public class BluejackGame {

    private static final int DECK_SIZE = 40;
    private static final int HAND_SIZE = 5;
    private static Card[] gameDeck = new Card[DECK_SIZE];
    private static Card[] computerDeck = new Card[HAND_SIZE];
    private static Card[] userDeck = new Card[HAND_SIZE];

    public static void main(String[] args) {
        initializeGameDeck();
        shuffleGameDeck();
        dealCards();

        System.out.println("Computer's cards:");
        printDeck(computerDeck);

        System.out.println("\nUser's cards:");
        printDeck(userDeck);
    }

    private static void initializeGameDeck() {
        String[] colors = {"blue", "yellow", "red", "green"};
        int cardCount = 0;
        for (String color : colors) {
            for (int value = 1; value <= 10; value++) {
                gameDeck[cardCount++] = new Card(color, value, "");
            }
        }
    }

    private static void shuffleGameDeck() {
        Random random = new Random();
        for (int i = 0; i < gameDeck.length; i++) {
            int randomIndex = random.nextInt(gameDeck.length);
            Card temp = gameDeck[i];
            gameDeck[i] = gameDeck[randomIndex];
            gameDeck[randomIndex] = temp;
        }
    }

    private static void dealCards() {
        for (int i = 0; i < HAND_SIZE; i++) {
            computerDeck[i] = gameDeck[i];
            userDeck[i] = gameDeck[DECK_SIZE - 1 - i];
        }
    }

    private static void printDeck(Card[] deck) {
        for (Card card : deck) {
            System.out.println(card);
        }
    }
}
