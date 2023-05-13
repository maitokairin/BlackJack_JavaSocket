import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {
    private static final int PORT_NUMBER = 7600;

    public static ArrayList<ArrayList<Integer>> players = new ArrayList<>();

    public static Deck deck = new Deck();

    public static boolean  status_player = true;

    public static int turn_player = 1;


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        System.out.println("Server started.");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Connected");

        InputStream input = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output,true);

        String message;

        while((message=reader.readLine())!=null) {
            switch (message) {
                case "Y", "y" -> {
                    setUpNewGame();
                    writer.println("1" + responseToClient());
                }
                case "hit" -> {
                    hitCard();
                    int new_card = players.get(1).get(players.get(1).size() - 1);
                    int new_score = players.get(1).get(0);
                    boolean status = status_player;
                    writer.println("2" + new_card + " " + new_score + " "+status);
                }
                case "stand" -> {
                    turn_player = 0;
                    System.out.println(turn_player);
                    writer.println("3" + "YOU STAND!!");
                }
                default -> writer.println("GG");
            }
        }
    }

    public static void setUpNewGame(){
        //clear data
        players.clear();
        deck.clearDeck();
        status_player = true;
        //set players

        for (int i=0;i<2;i++){
            ArrayList<Integer> cards_player = new ArrayList<>();
            cards_player.add(0);
            players.add(cards_player);
        }
        //built card deck
        deck.builtDeck();
        //[9, 1, 48, 31, 7, 0, 37, 40, 30, 25, 26, 2, 43, 6, 13, 19, 23, 50, 39, 33, 28, 46, 36, 8, 29, 42, 4, 16, 14, 17, 15, 51, 11, 32, 12, 47, 3, 5, 10, 24, 20, 49, 22, 45, 38, 34, 27, 21, 44, 41, 18, 35]
        System.out.println(Arrays.toString(deck.getDeck().toArray()));
        //deal card to player
        dealCardToPlayer();
        System.out.println(Arrays.toString(players.toArray()));

    }

    public static void dealCardToPlayer(){
        //[9, 1, 48, 31, 7, 0, 37, 40, 30, 25, 26, 2, 43, 6, 13, 19, 23, 50, 39, 33, 28, 46, 36, 8, 29, 42, 4, 16, 14, 17, 15, 51, 11, 32, 12, 47, 3, 5, 10, 24, 20, 49, 22, 45, 38, 34, 27, 21, 44, 41, 18, 35]
        //[[9, 48], [1, 31]]
        int index_player = 0;
        for (int i =0;i<4;i++){
            int num_card = deck.dealCard();
            players.get(index_player).add(num_card);
            updateSumScoreCard(index_player,num_card);
            index_player++;
            if(index_player==players.size()){
                index_player=0;
            }
        }
    }

    public static void hitCard(){
        int index_player = turn_player;
        int num_card = deck.dealCard();
        players.get(index_player).add(num_card);
        updateSumScoreCard(index_player,num_card);
    }

    public static void botStartProcess(){

    }

    public static void updateSumScoreCard(Integer index_player,Integer num_card){
        int sum_score = players.get(index_player).get(0);
        int new_value = deck.valueOfCard(num_card);
        int new_score = sum_score+new_value;
        if (new_score>21 && turn_player == 1){
            status_player = false;
        }
        players.get(index_player).set(0,new_score);
    }


    public static ArrayList<ArrayList<Integer>> responseToClient(){
        ArrayList<ArrayList<Integer>> response = players;
        for (int i =0;i<response.get(0).size();i++){
            if(i==0){
                response.get(0).set(i,deck.valueOfCard(response.get(0).get(1)));
            } else if (i>=2) {
                response.get(0).remove(2);
            }
        }
        return response;
    }



}
