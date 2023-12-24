import java.io.*;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class  BluejackGame {
//değişkenleri private ile tanımladık
    private static int DECK_SIZE = 40;
    private static int BOARD_SIZE = 10;
    private static int HAND_SIZE = 4;
    private static int MAX_COMPUTER_TURNS = 10; // stand case'inde bilgisayara sonsuz hak vermemek için bir sınır
//deck size uzunlupunda cardtan nesne saklayan dizi
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
    private static int overallUser = 0;
    private static int overallComp = 0;

    public static void main(String[] args) {
        Scanner scan = new Scanner( System.in);

        System.out.println("\nWelcome to BlueJack");

        System.out.println( "What is your name?");
        name = scan.nextLine();

        do {
            //round start
            gameDeck = new Card[DECK_SIZE];
            computerDeck = new Card[BOARD_SIZE];
            userDeck = new Card[BOARD_SIZE];

            computerHand = new Card[HAND_SIZE];
            userHand = new Card[HAND_SIZE];

            userBoard = new Card[9];
            computerBoard = new Card[9];
            initializeGameDeck();
            shuffleGameDeck();
            dealDecks();

            System.out.println("\n--------------------\n");

            System.out.println("Computer Deck");
            printComputerDeck(computerDeck);

            System.out.println("\n" + name + "'s Deck:");
            printDeck(userDeck);

            dealHands();
            System.out.println("\n--------------------\n");

            playRound();

            System.out.println("Round Over");
            showScores();
            String s = calculateWinner();
            //round end

            calculateOverall(s);
        } while (overallComp < 3 && overallUser < 3 && playRound()!=-1);
        // game end

        // print game results
        System.out.println("\n======================");
        System.out.println("GAME RESULTS:");
        System.out.println(name + ":" + overallUser + " - Computer:" + overallComp);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("game_history.txt", true))) {
            saveGameHistoryToFile(name, userScore, computerScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scan.close();
    }
//cardcount sayacı her seferinde arttılır,kartlar farklı
    //indekse yerleştirilir.for string colours
    //ile 4 kez yapar.
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

    // method to remove a card from the deck
    private static Card[] removeCardFromDeck(Card[] deck, int index) {
        Card[] newDeck = new Card[deck.length - 1];
        System.arraycopy(deck, 0, newDeck, 0, index);
        System.arraycopy(deck, index + 1, newDeck, index, deck.length - index - 1);
        return newDeck;
    }
//computer deckden seçilen bir kartı computer hande atar
    //CHANCE ile dağıtalacak kart hangi indeksten alınacak o belirlenir
    private static void dealHands() {
        int chance = random.nextInt(9);
        for (int i = 0; i < HAND_SIZE; i++) {
            computerHand[i] = computerDeck[chance];
            chance = random.nextInt(9);
            userHand[i] = userDeck[chance];
        }
    }
//kartları null değilse sırayla ve numaralandırarak bastırır n sayactır ve arttırır
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
//computer deckin kapalı olmasını istiyoruz
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
        int arrayIndex = cardIndex - 1; //oyuncunun seçtiği kartın gerçek indeksi
        Card[] newHand = new Card[hand.length - 1];//kart seçildikten sonra güncellenmiş dizi oluşturur

        for (int i = 0; i < 9; i++) { //seçilen kart için boş indeks arar
            if (board[i] == null) {

                if (arrayIndex >= 0 && arrayIndex < hand.length) { //kontrol eder indexi
                    board[i] = hand[arrayIndex]; //kartı yerleştirir

                    for (int j = arrayIndex; j < hand.length - 1; j++) {
                        hand[j] = hand[j + 1]; //kartı siler ve sola kaydırırlar kalanları(4 karttan oyanınca boşluk kalmasın diye
                    }

                    System.arraycopy(hand, 0, newHand, 0, newHand.length);
                    return newHand; //güncellenmiş kartları new hand dizisine kopyalar
                }
            }
        }

        return hand;
    }
//rastgele belirlelenen chance indeksindeki kartı gamedeckten alır
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
//score hesapla
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

    private static int playRound(){
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
            if ( selection == MENU_PLAY_1 && userHand.length>=1){//menüyü oynwtıyor
                userHand = playCard(1, userHand, userBoard);
                computerPlays();
                showScores();
            }
            else if ( selection == MENU_PLAY_2  && userHand.length>=2 ){ //?
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
            else if (selection == MENU_STAND) {
                int computerTurnCounter = 0;

                while (computerHand.length > 0 && !computerStand()) {//computer elinde kart varsa ve stand yapmadıkça devam et
                    if (isGameOver() || computerTurnCounter >= MAX_COMPUTER_TURNS) {//max hamle sınırına geldiyse break
                        break;
                    }

                    computerPlays();
                    computerTurnCounter++;
                }

                break;
            }

            else if ( selection == MENU_SCORES )
                showScores();

            else if ( selection == MENU_EXIT )
                return -1;
            else
                System.out.println("Unvalid choice, please try again");


            System.out.println("\n--------------------\n");

        } while ( selection != MENU_EXIT || !isGameOver() || overallComp<3 || overallUser<3 );

        return 0;
    }

    private static String calculateWinner(){//tek tur için hesaplar
        if ( userScore > 20 && computerScore < 20 ) {
            System.out.println("Computer won! You exceeded 20, bust!");
            return "C";
        }
        else if (computerScore > 20 && userScore < 20 ){
            System.out.println(name + " won! Computer exceeded 20, bust!");
            return "U";
        }
        else if (computerScore > 20 && userScore > 20 ){
            System.out.println("Tie! You both exceeded 20 at the same round");
            return "T";
        }
        else if (Math.abs(20 - userScore) > Math.abs(20 - computerScore)){

            boolean isBlue = true;

            if (computerScore == 20){ //maviyle 20 yaptıysa tüm turları alması için
                for(int i=0; i<computerBoard.length; i++ ){
                    if (computerBoard[i] != null && !computerBoard[i].getColor().equals("blue")){
                        isBlue = false;
                        break;
                    }
                }
                if (isBlue){
                    System.out.println("Bluejack for Computer!");
                    overallComp = 3;
                }
            } else {
                System.out.println("Computer won! It's score was closer to 20 😁");
            }

            return "C";
        }
        else if (Math.abs(20 - userScore) < Math.abs(20 - computerScore)){

            boolean isBlue = true;

            if (userScore == 20){ //maviyle 20de alması için
                for(int i=0; i<userBoard.length; i++ ){
                    if (userBoard[i] != null && !userBoard[i].getColor().equals("blue")){
                        isBlue=false;
                        break;
                    }
                }
                if (isBlue){
                    System.out.println("Bluejack for " + name + "!");
                    overallUser = 3;
                }
            } else {
                System.out.println(name + " won! Your score was closer to 20 😁");
            }

            return "U";
        }
        else{
            System.out.println("Tie!");
            return "T";
        }
    }

    private static void computerPlays(){
        Card selectedCard = selectBestCardForComputer();

        if (selectedCard != null) {
            int cardIndexInHand = findCardIndexInHand(selectedCard, computerHand);//kartın computerhanddeki konumunu döndürür
            computerHand = playCard(cardIndexInHand, computerHand, computerBoard);//kartı boarda yerleştirir
            return;
        }
    }

    private static Card selectBestCardForComputer() {
        int computerDistanceTo20 = 20 - computerScore;
        int playerDistanceTo20 = 20 - userScore;
        Card bestCard = null; //en iyi oynanacak kartı saklamak için yer açar
        int bestScoreImpact = Integer.MIN_VALUE;//kartın skora etkisi için  değişken tanımlar

        for (Card card : computerHand) {
            if (card != null) {//kart pozisyonu boş mu kontrolu
                int scoreImpact = calculateCardScoreImpact(card, computerScore, computerDistanceTo20, playerDistanceTo20);
                if (scoreImpact > bestScoreImpact) { //kart skora etki ediyorsa en iyi karttır
                    bestScoreImpact = scoreImpact;
                    bestCard = card;
                }
            }
        }
        return bestCard; //seçilen kart en iyiyse döndürür yoksa kart null kalır null döndürür
    }

    private static int calculateCardScoreImpact(Card card, int currentScore, int computerDistanceTo20,int playerDistanceTo20) {
        int scoreImpact = 0; //hesaplanan skoru saklar
        int cardValue = card.getValue();//değerlendirilen kartın değerlerini alır
        String cardSign = card.getSign();

        switch (cardSign) { //karta göre skora etkisini hesaplar
            case "+":
                scoreImpact = cardValue;
                break;
            case "-":
                scoreImpact = -cardValue;
                break;
            case "x2":
                scoreImpact = (currentScore + cardValue) * 2 - currentScore;
                break;
            case "+/-":
                scoreImpact = -currentScore;
                break;
            default:
                scoreImpact = cardValue;
                break;
        }

        int projectedScore = currentScore + scoreImpact;//kart pynanırsa bilgisayarın yeni scoreu hesaplar
        if (projectedScore > 20) {
            scoreImpact -= (projectedScore - 20) * 2;
        }//20nini üzerindeyse 2katı düşürülür

        if (computerDistanceTo20 <= 3) {
            if (cardSign.equals("+") && cardValue > computerDistanceTo20) { //eğer kart + ise ve 20ye olan uzaklığından fazlaysa kartın olumsuz etkisi scoredan çıkartılır
                scoreImpact -= cardValue;
            }
        } else if (playerDistanceTo20 <= 3) { //eğer oynayan 20ye yakınsa bilgisayarın stratejisi değişebilir
            if (cardSign.equals("+")) {
                scoreImpact += cardValue;
            }
        }

        return scoreImpact;
    }

    private static int findCardIndexInHand(Card card, Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null && hand[i].equals(card)) { //aranan kart null değilse ve o karta eşitse
                return i;//mevcut kart arananla aynı ise kartın indeksi döndürülür
            }
        }
        return -1;//elde değilse kart yok -1 döndürülür
    }

    private static boolean computerStand() {
        int computerDistanceTo20 = 20 - computerScore;
        int playerDistanceTo20 = 20 - userScore;

        if (computerDistanceTo20 <= 3 || (computerDistanceTo20 < playerDistanceTo20 && computerDistanceTo20 < 5)) {
            return true; //eğer 20ye 3 kaldıysa ve ya da oyunucudan daha avantajlı durumdayken 20ye 5ten az uzaklıktaysa risk almadan durabilir (bilgisayar)
        }

        return false;
    }


    private static void saveGameHistoryToFile(String playerName, int playerScore, int computerScore) {
        File file = new File("game_history.txt");
        String[] lines = new String[10];

        try {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    if (count < 10) {
                        lines[count++] = line;
                    } else {
                        System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                        lines[lines.length - 1] = line;
                    }
                }
                reader.close();
            }

            GameHistory gameHistory = new GameHistory(playerName, overallUser, overallComp, new Date());
            if (lines[0] != null) {
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
            }
            lines[lines.length - 1] = gameHistory.toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String historyLine : lines) {
                if (historyLine != null) {
                    writer.write(historyLine);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateOverall(String s){
        if(overallComp<3 && overallUser<3){ //3 tur kaxanan yoksa oyun devam ediyor
            if ( s.equals("C")){
                overallComp++;
            }
            else if (s.equals("U")){
                overallUser++;//eğer u ise kullanıcının kazandığı turu arttırır
            }
        }
    }

}
