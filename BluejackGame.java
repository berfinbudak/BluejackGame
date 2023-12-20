import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// github açma?

// The first player to win three sets is the winner. 
// However, if one of the players uses all blue cards to get a score of 20, they automatically win the game. 

// file log

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
        printComputerDeck(computerDeck);

        System.out.println("\n" + name + "'s Deck:");
        printDeck(userDeck);

        dealHands();
        System.out.println("\n--------------------\n");

        playRound();

        System.out.println("\n--------------------\n");
        System.out.println("Game Over");
        showScores();
        calculateWinner();

        /////buradaki set-game ilişkisini halletmek lazım önce
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("game_history.txt"))) {
            saveGameHistoryToFile(name, userScore, computerScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scan.close();
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
    
    // Helper method to remove a card from the deck
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

    private static void printComputerDeck(Card[] deck) {
        int n=1;
        for (Card card : deck) {
            if (card != null ) {
                System.out.print(n + "- ");
                System.out.println("X ");
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
            printComputerDeck(computerHand);
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
                computerPlays();
                showScores();
            }
            else if ( selection == MENU_PLAY_2  && userHand.length>=2 ){
                userHand = playCard(2, userHand, userBoard);
                computerPlays();
                showScores();
            }
            else if ( selection == MENU_PLAY_3  && userHand.length>=3 ){
                userHand = playCard(3, userHand, userBoard);
                computerPlays();
                showScores();
            }
            else if ( selection == MENU_PLAY_4  && userHand.length>=4 ){
                userHand = playCard(4, userHand, userBoard);
                computerPlays();
                showScores();
            }
            else if ( selection == MENU_PLAY_RANDOM && userHand.length > 0){
                for (int i = 0; i < DECK_SIZE; i++) {
                    if (gameDeck[i] == card ) {
                        gameDeck = playCard(i+1, gameDeck, userBoard);
                        break;
                    }
                }
                showScores();
            }
            else if ( selection == MENU_END){
                computerPlays();
            }
            else if ( selection == MENU_STAND ){
                while (computerHand.length>0 && !computerStand()){
                    computerPlays();
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
        // Strategy for selecting a card
        Card selectedCard = selectBestCardForComputer();

        // Play the selected card
        if (selectedCard != null) {
            // Assuming playCard takes the index of the card in hand and the board to play on
            int cardIndexInHand = findCardIndexInHand(selectedCard, computerHand);
            computerHand = playCard(cardIndexInHand, computerHand, computerBoard);
            calculateScores(); // Update scores after playing
        }
    }

    private static Card selectBestCardForComputer() {
        int computerDistanceTo20 = 20 - computerScore;
        int playerDistanceTo20 = 20 - userScore;
        Card bestCard = null;
        int bestScoreImpact = Integer.MIN_VALUE;
    
        for (Card card : computerHand) {
            if (card != null) {
                int scoreImpact = calculateCardScoreImpact(card, computerScore, computerDistanceTo20, playerDistanceTo20);
                if (scoreImpact > bestScoreImpact) {
                    bestScoreImpact = scoreImpact;
                    bestCard = card;
                }
            }
        }
        return bestCard;
    }
    
    private static int calculateCardScoreImpact(Card card, int currentScore, int computerDistanceTo20,int playerDistanceTo20) {
        int scoreImpact = 0;
        int cardValue = card.getValue();
        String cardSign = card.getSign();
    
        switch (cardSign) {
            case "+":
                scoreImpact = cardValue; // Positive impact on the score
                break;
            case "-":
                scoreImpact = -cardValue; // Negative impact on the score
                break;
            case "x2":
                // Doubling the score can be good or bad depending on the current score
                scoreImpact = (currentScore + cardValue) * 2 - currentScore;
                break;
            case "+/-":
                // Flipping the score's sign
                scoreImpact = -currentScore;
                break;
            default:
                // Normal card with no special effect
                scoreImpact = cardValue;
                break;
        }
    
        // Adjusting the impact based on proximity to 20
        int projectedScore = currentScore + scoreImpact;
        if (projectedScore > 20) {
            // Negative impact if it causes the score to exceed 20
            scoreImpact -= (projectedScore - 20) * 2;
        }

        if (computerDistanceTo20 <= 3) {
            // Play cautiously if close to 20
            if (cardSign.equals("+") && cardValue > computerDistanceTo20) {
                scoreImpact -= cardValue;  // Prefer not to exceed 20
            }
        } else if (playerDistanceTo20 <= 3) {
            // Play aggressively if the player is close to winning
            if (cardSign.equals("+")) {
                scoreImpact += cardValue;  // Try to catch up
            }
        }
    
        return scoreImpact;
    }    
    
    private static int findCardIndexInHand(Card card, Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null && hand[i].equals(card)) {
                return i;
            }
        }
        return -1; // Return -1 if the card is not found
    }    

    private static boolean computerStand() {
        int computerDistanceTo20 = 20 - computerScore;
        int playerDistanceTo20 = 20 - userScore;
    
        // Simple strategy: Stand if the score is close to 20 or if the risk of busting is high
        if (computerDistanceTo20 <= 3 || (computerDistanceTo20 < playerDistanceTo20 && computerDistanceTo20 < 5)) {
            return true;
        }
        
        return false; // Continue playing otherwise
    }
    

    private static void saveGameHistoryToFile(String playerName, int playerScore, int computerScore) {
        File file = new File("game_history.txt");
        List<String> lines = new ArrayList<>();
        try {
            if (file.exists()) {
                lines = Files.readAllLines(file.toPath());
                if (lines.size() >= 10) {
                    lines.remove(0);
                }
            }
            GameHistory gameHistory = new GameHistory(playerName, playerScore, computerScore, new Date());
            lines.add(gameHistory.toString());
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
