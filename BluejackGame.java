import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private static String name = "";
    private static List<GameHistory> gameHistoryList = new ArrayList<>(); 
    
    public static void main(String[] args) {
        
        Scanner scan = new Scanner( System.in);

        initializeGameDeck();
        shuffleGameDeck();
        dealDecks();

        System.out.println("\nWelcome to BlueJack");

        System.out.println( "What is your name?");
            name = scan.nextLine();

        System.out.println("\n--------------------\n");

        System.out.println("Computer Deck");
        printDeck(computerDeck);

        System.out.println("\n" + name + "'s Deck:");
        printDeck(userDeck);

        dealHands();
        System.out.println("\n--------------------\n");

        playRound();

        System.out.println("\n--------------------\n");
        System.out.println("Game Over");
        showScores();
        calculateWinner();

        saveGameHistoryToFile(name, userScore, computerScore);
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
            gameDeck = removeCardFromDeck(gameDeck, i); 
            shuffleGameDeck();
            userDeck[i] = gameDeck[gameDeck.length - 1 - i];
            gameDeck = removeCardFromDeck(gameDeck, gameDeck.length - 1 - i); 
        }
    
        for (int i = 5; i < 8; i++) {
            shuffleGameDeck();
            Card card1 = gameDeck[i];
            if (chance == 1) {
                card1.setSign("-");
            } else {
                card1.setSign("+");
            }
            computerDeck[i] = card1;
            gameDeck = removeCardFromDeck(gameDeck, i); 
    
            shuffleGameDeck();
            Card card2 = gameDeck[i];
            if (chance == 1) {
                card2.setSign("-");
            } else {
                card2.setSign("+");
            }
            userDeck[i] = card2;
            gameDeck = removeCardFromDeck(gameDeck, i); 
        }
    
        for (int i = 8; i < 9; i++) {
            chance = random.nextInt(1, 101);
            if (chance <= 80) {
                computerDeck[i] = gameDeck[i];
                computerDeck[i + 1] = gameDeck[i + 1];
                gameDeck = removeCardFromDeck(gameDeck, i);
                gameDeck = removeCardFromDeck(gameDeck, i + 1);
            } else {
                chance = random.nextInt(2);
                if (chance == 1)
                    computerDeck[i] = new Card("", 0, "x2");
                else
                    computerDeck[i] = new Card("", 0, "+/-");
    
                chance = random.nextInt(2);
                if (chance == 1)
                    computerDeck[i + 1] = new Card("", 0, "x2");
                else
                    computerDeck[i + 1] = new Card("", 0, "+/-");
    
                gameDeck = removeCardFromDeck(gameDeck, i); 
                gameDeck = removeCardFromDeck(gameDeck, i + 1);
            }
        }
    
        shuffleGameDeck();
    
        for (int i = 8; i < 9; i++) {
            chance = random.nextInt(1, 101);
            if (chance <= 80) {
                userDeck[i] = gameDeck[i];
                userDeck[i + 1] = gameDeck[i + 1];
                gameDeck = removeCardFromDeck(gameDeck, i); 
                gameDeck = removeCardFromDeck(gameDeck, i + 1);
            } else {
                chance = random.nextInt(2);
                if (chance == 1)
                    computerDeck[i] = new Card("", 0, "x2");
                else
                    computerDeck[i] = new Card("", 0, "+/-");
    
                chance = random.nextInt(2);
                if (chance == 1)
                    computerDeck[i + 1] = new Card("", 0, "x2");
                else
                    computerDeck[i + 1] = new Card("", 0, "+/-");
    
                gameDeck = removeCardFromDeck(gameDeck, i); 
                gameDeck = removeCardFromDeck(gameDeck, i + 1);
            }
        }
    }
    
    private static Card[] removeCardFromDeck(Card[] deck, int index) {
        Card[] newDeck = new Card[deck.length - 1];
        System.arraycopy(deck, 0, newDeck, 0, index);
        System.arraycopy(deck, index + 1, newDeck, index, deck.length - index - 1);
        return newDeck;
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
            if (card != null ) {
                System.out.print(n + "- ");
                System.out.println(card);
                n++;
            }
        }
    }

    private static Card[] playCard(int cardIndex, Card[] hand, Card[] board) {
        int arrayIndex = cardIndex - 1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == null) {

                if (arrayIndex >= 0 && arrayIndex < hand.length) {
                    board[i] = hand[arrayIndex];

                    for (int j = arrayIndex; j < hand.length - 1; j++) {
                        hand[j] = hand[j + 1];
                    }

                    Card[] newHand = new Card[hand.length - 1];
                    System.arraycopy(hand, 0, newHand, 0, newHand.length);

                    return newHand;
                }
            }
        }

        return hand;
    }

    private static Card randomCard(){
        int chance = random.nextInt(gameDeck.length);
        Card card = gameDeck[chance];
        return card;
    }

    private static boolean isGameOver(){
        if (userScore >= 20 || computerScore >= 20 || userHand.length<=0 || computerHand.length<=0)
            return true;
        else
            return false;
    }

    private static void calculateScores() {
        userScore = 0;
        computerScore = 0;

        for (int i = 0; i < userBoard.length; i++) {
            if (userBoard[i] != null) {
                int value = userBoard[i].getValue();
                String sign = userBoard[i].getSign();

                if ("-".equals(sign)) {
                    value = -value;
                    userScore += value;
                } else if ("x2".equals(sign)) {
                    userScore = userScore*2;
                } else if ("+/-".equals(sign))
                    userScore = -userScore;
                else
                    userScore += value;

            } else {
                break;
            }
        }

        for (int i = 0; i < computerBoard.length; i++) {
            if (computerBoard[i] != null) {
                int value = computerBoard[i].getValue();
                String sign = computerBoard[i].getSign();

                if ("-".equals(sign)) {
                    value = -value;
                    computerScore += value;
                } else if ("x2".equals(sign)) {
                    computerScore = computerScore*2;
                } else if ("+/-".equals(sign))
                    computerScore = -computerScore;
                else
                    computerScore += value;

            } else {
                break;
            }
        }
    }


    private static void showScores(){
        calculateScores();
        System.out.println(name + ": " + userScore);
        System.out.println("Computer: " + computerScore);
    }

    private static void playRound(){

        Scanner scan = new Scanner( System.in);
        int selection;

        int MENU_EXIT = 0;
        int MENU_PLAY_1 = 1;
        int MENU_PLAY_2 = 2;
        int MENU_PLAY_3 = 3;
        int MENU_PLAY_4 = 4;
        int MENU_PLAY_RANDOM  = 5;
        int MENU_END = 6;
        int MENU_STAND = 7;
        int MENU_SCORES = 8;

        do
        {

            if (isGameOver())
                break;

            System.out.println();

            System.out.println("Computer Hand: ");
            printDeck(computerHand); 
            System.out.println();

            
            System.out.println("Computer Board: ");
            printDeck(computerBoard);
            System.out.println();
            
            System.out.println(name + "'s Board: ");
            printDeck(userBoard);
            System.out.println();
            
            System.out.println(name + "'s Hand: ");
            printDeck(userHand);
            System.out.println();

            Card card = randomCard();

            // display menu

            for (int i = 1; i <= userHand.length; i++) {
                System.out.println( i + " - to play card " + i  );
            }
            System.out.println( MENU_PLAY_RANDOM + " - to play the random card (" + card + ")" );
            System.out.println( MENU_END + " - to end turn" );
            System.out.println( MENU_STAND + " - to stand" );
            System.out.println( MENU_SCORES + " - for Scores" );

            // ask for and get selection
            System.out.println();
            System.out.println( "Selection (" + MENU_EXIT + " to exit): ");
            selection = scan.nextInt();

            // process selection
            if ( selection == MENU_PLAY_1 && userHand.length>=1){
                userHand = playCard(1, userHand, userBoard);
                computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_2  && userHand.length>=2 ){
                userHand = playCard(2, userHand, userBoard);
                computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_3  && userHand.length>=3 ){
                userHand = playCard(3, userHand, userBoard);
                computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_4  && userHand.length>=4 ){
                userHand = playCard(4, userHand, userBoard);
                computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_PLAY_RANDOM && userHand.length > 0){
                for (int i = 0; i < DECK_SIZE; i++) {
                    if (gameDeck[i] == card ) {
                        gameDeck = playCard(i+1, gameDeck, userBoard);
                        break;
                    }
                }
                computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
                showScores();
            }
            else if ( selection == MENU_END){
                computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
            }
            else if ( selection == MENU_STAND ){
                while (computerHand.length>0 && !computerStand()){
                    computerHand = playCard(random.nextInt(HAND_SIZE), computerHand, computerBoard);
                }
            } 
            else if ( selection == MENU_SCORES )
                showScores();

            else if ( selection == MENU_EXIT )
                break;
            else
                System.out.println("Unvalid choice, please try again");


            System.out.println("\n--------------------\n");

        } while ( selection != MENU_EXIT || !isGameOver() );

        scan.close();
        return;
    }

    private static void calculateWinner(){
        if ( userScore >= 20 )
            System.out.println("Computer won! You exceeded 20, bust!");
        else if (computerScore >= 20)
            System.out.println(name + " won! Computer exceeded 20, bust!");
        else if (computerScore >= 20 && userScore >= 20 )
            System.out.println("Tie! You both exceeded 20 at the same round");
        else if (Math.abs(20 - userScore) > Math.abs(20 - computerScore))
            System.out.println("Computer won! It's score was closer to 20");
        else if (Math.abs(20 - userScore) < Math.abs(20 - computerScore))
            System.out.println(name + " won! Your score was closer to 20");
        else
            System.out.println("An unexpected case occured, please try to re-run the game");
    }

    private static void computerPlays(){
        //to-do
    }

    private static boolean computerStand(){
        // to-do
        return true;
    }

    private static void saveGameHistoryToFile(String playerName, int playerScore, int computerScore) {
        GameHistory gameHistory = new GameHistory(playerName, playerScore, computerScore, new Date());

        gameHistoryList.add(0, gameHistory);

        if (gameHistoryList.size() > 10) {
            gameHistoryList = gameHistoryList.subList(0, 10);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("game_history.txt"))) {
  
            for (GameHistory history : gameHistoryList) {
                writer.write(history.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
