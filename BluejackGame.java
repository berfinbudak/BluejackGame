import java.util.Random;
import java.util.Scanner;
public class BluejackGame {

    private static int DECK_SIZE = 40;
    private static int BOARD_SIZE = 10;
    private static int HAND_SIZE = 4;

    private static Card[] gameDeck = new Card[DECK_SIZE];
    private static Card[] computerDeck = new Card[BOARD_SIZE];
    private static Card[] userDeck = new Card[BOARD_SIZE];
    
    private static Card[] computerHand = new Card[HAND_SIZE];
    private static Card[] userHand = new Card[HAND_SIZE];

    private static Card[] userBoard = new Card[9];
    private static Card[] computerBoard = new Card[9];
    private static int userScore = 0;
    private static int computerScore = 0;

    static Random random = new Random();

    public static void main(String[] args) {

        initializeGameDeck();
        shuffleGameDeck();
        dealDecks();

        System.out.println("\nStart of BlueJack");
        
        System.out.println("\n--------------------\n");

        System.out.println("Computer Deck");
        printDeck(computerDeck);

        System.out.println("\nUser Deck:");
        printDeck(userDeck);

        dealHands();
        System.out.println("\n--------------------\n");

        playRound();
        
        System.out.println("\n--------------------\n");
        System.out.println("Game Over");
        showScores();
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
        for (int i = 0; i < gameDeck.length; i++) {
            int randomIndex = random.nextInt(gameDeck.length);
            Card temp = gameDeck[i];
            gameDeck[i] = gameDeck[randomIndex];
            gameDeck[randomIndex] = temp;
        }
    }

    private static void dealDecks() {
        int chance = random.nextInt(2);

        for (int i = 0; i < 5; i++) {
            shuffleGameDeck();
            computerDeck[i] = gameDeck[i];
            shuffleGameDeck();
            userDeck[i] = gameDeck[DECK_SIZE - 1 - i];
        }

        for (int i = 5; i < 8; i++) {
            shuffleGameDeck();
            Card card1 = gameDeck[i];
            if (chance == 1){
                card1.setSign("-");
            } else {
                card1.setSign("+");
            }
            computerDeck[i] = card1;

            shuffleGameDeck();
            Card card2 = gameDeck[i];
            if (chance == 1){
                card2.setSign("-");
            } else {
                card2.setSign("+");
            }           
            userDeck[i] = card2;
        }

        for (int i = 8; i < 9; i++) {
            chance = random.nextInt(1, 101);
            if ( chance <= 80 ) {
                computerDeck[i] = gameDeck[i];
                computerDeck[i+1] = gameDeck[i+1];
            } 
            else {
                computerDeck[i] = new Card("", 0, "x2");
                computerDeck[i+1] = new Card("", 0, "x2");
            }
        }

        shuffleGameDeck();

        for (int i = 8; i < 9; i++) {
            chance = random.nextInt(1, 101);
            if ( chance <= 80 ) {
                userDeck[i] = gameDeck[i];
                userDeck[i+1] = gameDeck[i+1];
            } 
            else {
                userDeck[i] = new Card("", 0, "x2");
                userDeck[i+1] = new Card("", 0, "x2");
            }
        }
    }

    private static void dealHands() {
        int chance = random.nextInt(9);
        for (int i = 0; i < HAND_SIZE; i++) {
            computerHand[i] = computerDeck[chance];
            chance = random.nextInt(9);
            userHand[i] = userDeck[chance];
        }
    }

    private static void printDeck(Card[] deck) {
        int n=1;
        for (Card card : deck) {
                System.out.print(n + "- ");
                System.out.println(card);
                n++;
        }
    }

    private static void playCard(int cardIndex, Card[] hand, Card[] board) {
        int arrayIndex = cardIndex - 1;
    
        for (int i = 0; i < 9; i++) {
            if (board[i] == null) {
                board[i] = hand[arrayIndex];
                break;
            }
        }
    
        for (int i = arrayIndex; i < hand.length - 1; i++) {
            hand[i] = hand[i + 1];
        }
    
        Card[] newHand = new Card[hand.length-1];

        for ( int i = 0; i < hand.length-1; i++){
            newHand[i] = hand[i];
        }

        hand = newHand;

    }

    private static Card randomCard(){
        int chance = random.nextInt(DECK_SIZE);
        Card card = gameDeck[chance];
        return card;
    }

    private static boolean isGameOver(){
        if (userScore >= 20 || computerScore >= 20 )
            return true;
        else 
            return false;
    }

    private static void calculateScores(){
        for (int i = 0; i < userBoard.length; i++) {
            if (userBoard[i] != null) {
                userScore += userBoard[i].getValue();
            }
            else 
                break;
        }

        for (int i = 0; i < computerBoard.length; i++) {
            if (computerBoard[i] != null) {
                computerScore += computerBoard[i].getValue();
            }
            else 
                break;
        }
    }

    private static void showScores(){
        calculateScores();
        System.out.println("User: " + userScore);
        System.out.println("Computer: " + computerScore);
    }

    private static void playRound(){
        
        Scanner scan = new Scanner( System.in);
        int selection;
        
        // CONSTANTS
        int MENU_EXIT = 0;
        int MENU_PLAY_1 = 1;
        int MENU_PLAY_2 = 2;
        int MENU_PLAY_3 = 3;
        int MENU_PLAY_4 = 4;
        int MENU_PLAY_RANDOM  = 5;
        int MENU_SCORES = 6;

        // display menu, get and process selection, until exit
        do
        {
            
            System.out.println("\n--------------------\n");

            System.out.println("\nComputer Hand:");
            printDeck(computerHand);

            System.out.println("\nUser Hand:");
            printDeck(userHand);
            Card card = randomCard();

            // display menu
            System.out.println();
            
            System.out.println();
            System.out.println( MENU_PLAY_1 + " - to play card 1"  );
            System.out.println( MENU_PLAY_2 + " - to play card 2"  );
            System.out.println( MENU_PLAY_3 + " - to play card 3"  );
            System.out.println( MENU_PLAY_4 + " - to play card 4" );
            System.out.println( MENU_PLAY_RANDOM + " - to play the random card (" + card + ")" );
            System.out.println( MENU_SCORES + " - for Scores" );
            
            // ask for and get selection
            System.out.println();
            System.out.println( "Selection (" + MENU_EXIT + " to exit): ");
            selection = scan.nextInt();
            
            // process selection
            if ( selection == MENU_PLAY_1 ){
                playCard(1, userHand, userBoard);
                playCard(random.nextInt(BOARD_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_2 ){
                playCard(2, userHand, userBoard);
                playCard(random.nextInt(BOARD_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_3 ){
                playCard(3, userHand, userBoard);
                playCard(random.nextInt(BOARD_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_4 ){
                playCard(4, userHand, userBoard);
                playCard(random.nextInt(BOARD_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_RANDOM ){
                playCard(5, userHand, userBoard);
                playCard(random.nextInt(BOARD_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_SCORES )
                showScores();

            else if ( selection == MENU_EXIT )
                break;
            
        } while ( selection != MENU_EXIT || !isGameOver() ); 

        return;
    }
}
