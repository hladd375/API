import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


// video to load jar
//https://www.youtube.com/watch?v=QAJ09o3Xl_0

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.Border;

// Program for print data in JSON format.
public class Main {

    private int WIDTH=800;
    private int HEIGHT=700;
    private JFrame mainFrame;
    private String totlaJson;
    private String deckID;
    private String cardCode;
    private int cardTotal;
    private int aceValue = 11;
    private boolean isThereAnAce;
    private int playerScore;
    private int houseScore;
    private int pNumCards;
    private int hNumCards;
    private boolean listingAndShow = false;
    private boolean twoCardsEqual = false;
    private boolean splitCheck = false;
    private JLabel cardImage;
    private String listingCardImage;
    private JPanel bottomButtons;
    private JPanel display;
    private JPanel houseDisplay;
    private JPanel houseCardDisplay;
    private JPanel playerDisplay;
    private JPanel playerCardDisplay;
    private JTextArea playerStatusLabel;
    private JButton hit;
    private JButton stick;
    private JButton reset;
    private JButton split;


    public static void main(String args[]) throws ParseException {
        // In java JSONObject is used to create JSON object
        // which is a subclass of java.util.HashMap.


        JSONObject file = new JSONObject();
        Main readingIsWhat = new Main();

    }

    public Main() throws ParseException {
        prepareGUI();
        shuffle();
        deal();
    }

    public void housePlay () throws ParseException {
         playerScore = countCards("player");
        System.out.println("Player Score: " + playerScore);
         houseScore = countCards("house");
        System.out.println("House Score: " + houseScore);
        houseCardDisplay.removeAll();
        listingAndShow = true;
        listingCardsInPile("house");
        listingAndShow = false;
        while(houseScore <= 16) {
            drawACard(1,"house",true);
            houseScore = countCards("house");
        }

        if(playerScore>houseScore || houseScore > 21){
            System.out.println("YOU WIN");
            playerStatusLabel.setText("YOU WIN");
        }
        if(playerScore<houseScore && houseScore <= 21){
            System.out.println("HOUSE WINS");
            playerStatusLabel.setText("HOUSE WINS");
        }
        if(playerScore > 21){
            System.out.println("BUST");
            playerStatusLabel.setText("BUST");
        }
        if(playerScore==houseScore){
            System.out.println("PUSH TIE GAME");
            playerStatusLabel.setText("PUSH TIE GAME");
        }
    }
    public int countCards (String where) throws ParseException {
        cardTotal = 0;
        listingCardsInPile(where);
        if(isThereAnAce = true && cardTotal > 21){
            System.out.println(isThereAnAce);
            System.out.println(cardTotal);
            aceValue = 1;
            listingCardsInPile(where);
            System.out.println(cardTotal);
        } else{aceValue = 11;}
        System.out.println("Counting Cards: " + cardTotal);
        if(where == "player"){
            playerStatusLabel.setText("");
            playerStatusLabel.append("Total: " + cardTotal);
        }

        return  cardTotal;

    }
    public void prepareGUI (){
        mainFrame = new JFrame("Read Json");

        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new BorderLayout());

        display = new JPanel();
        display.setLayout(new GridLayout(2,0));

        houseDisplay = new JPanel();
        houseDisplay.setLayout(new BorderLayout());
        houseCardDisplay = new JPanel();
        houseCardDisplay.setLayout(new FlowLayout());

        playerDisplay = new JPanel();
        playerDisplay.setLayout(new BorderLayout());
        playerCardDisplay = new JPanel();
        playerCardDisplay.setLayout(new FlowLayout());
        playerStatusLabel = new JTextArea();

        bottomButtons = new JPanel();
        bottomButtons.setLayout(new GridLayout(2,3));

        hit = new JButton("hit");
        stick = new JButton("stick");
        reset = new JButton("reset");
        split = new JButton("split");

        mainFrame.add(display, BorderLayout.CENTER);
        display.add(houseDisplay);
        houseDisplay.add(houseCardDisplay, BorderLayout.CENTER);
        display.add(playerDisplay);
        playerDisplay.add(playerCardDisplay, BorderLayout.CENTER);
        playerDisplay.add(playerStatusLabel, BorderLayout.SOUTH);


        mainFrame.add(bottomButtons, BorderLayout.SOUTH);

        bottomButtons.add(hit);
        hit.setActionCommand("HIT");
        hit.addActionListener(new ButtonClickListener());

        bottomButtons.add(stick);
        stick.setActionCommand("STICK");
        stick.addActionListener(new ButtonClickListener());

        bottomButtons.add(split);
        split.setActionCommand("SPLIT");
        split.addActionListener(new ButtonClickListener());

        bottomButtons.add(reset);
        reset.setActionCommand("RESET");
        reset.addActionListener(new ButtonClickListener());


        mainFrame.setVisible(true);


    }
    public void shuffle() throws ParseException {
        String output = "abc";
        totlaJson="";


        try {

            String urlInput = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1";
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                totlaJson+=output;

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        System.out.println(jsonObject);

        try {
            deckID = (String)jsonObject.get("deck_id");
            System.out.println("");
            System.out.println("deckID: " + deckID);

        }

        catch (Exception e) {
            e.printStackTrace();
        }



    }
    public void deal() throws ParseException{
        split.setEnabled(false);
        splitCheck = true;
        drawACard(2,"player",true);
        splitCheck = false;
        drawACard(1,"house",false);
        drawACard(1,"house",true);
        listingCardsInPile("player");
        if(twoCardsEqual){
            split.setEnabled(true);
        }
        playerScore = countCards("player");
        if(playerScore == 21){
            playerStatusLabel.setText("BLACK JACK");
        }
    }
    public void drawACard(int count, String where, boolean faceup) throws ParseException { String output = "abc";
        totlaJson="";
        if(where == "house"){
            hNumCards = hNumCards + count;
        }
        if(where == "player"){
            pNumCards = pNumCards + count;
        }


        try {

            String urlInput = "https://deckofcardsapi.com/api/deck/"+deckID+"/draw/?count=" + count;
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                totlaJson+=output;

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        System.out.println(jsonObject);

        try {

            org.json.simple.JSONArray cards = (org.json.simple.JSONArray) jsonObject.get("cards");
            int n =   cards.size(); //(msg).length();
            for (int i = 0; i < n; ++i) {
                JSONObject card = (JSONObject) cards.get(i);
                System.out.println("");
                System.out.println(card);
                cardCode = (String)card.get("code");
                String image = (String) card.get("image");
                System.out.println(image);
                String backImage = "https://deckofcardsapi.com/static/img/back.png";


                ImageIcon originalCardIcon = new ImageIcon(new URL(image));
                ImageIcon originalbackCardImage =new ImageIcon(new URL(backImage));
                Image scaledCardImage = originalCardIcon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                Image scaledBackCardImage = originalbackCardImage.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                cardImage = new JLabel(new ImageIcon(scaledCardImage));
                JLabel backCardImage = new JLabel(new ImageIcon(scaledBackCardImage));


                if(where == "player") {
                    System.out.println(cardCode);
                    addToPiles("player");
                    if(faceup == true){
                        playerCardDisplay.add(cardImage);
                    } else {
                        playerCardDisplay.add(backCardImage);
                    }

                }
                if(where == "house") {
                    System.out.println(cardCode);
                    addToPiles("house");
                    if(faceup == true){
                        houseCardDisplay.add(cardImage);
                    }else {
                        houseCardDisplay.add(backCardImage);
                    }


                }


                playerCardDisplay.revalidate();
                playerCardDisplay.repaint();
                houseCardDisplay.revalidate();
                houseCardDisplay.repaint();

            }



        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void addToPiles(String pileName) throws ParseException { String output = "abc";
        totlaJson="";


        try {

            String urlInput = "https://deckofcardsapi.com/api/deck/"+deckID+"/pile/"+pileName+"/add/?cards="+ cardCode;
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                totlaJson+=output;

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        System.out.println(jsonObject);

        try {

        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void listingCardsInPile(String pileName) throws ParseException { String output = "abc";
        totlaJson="";
        isThereAnAce = false;
        cardTotal = 0;

        ArrayList<String> allCardCodes = new ArrayList<>();

        try {

            String urlInput = "https://deckofcardsapi.com/api/deck/"+deckID+"/pile/"+pileName+"/list/";
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println("Listing Cards Output: " + output);
                totlaJson+=output;

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        //System.out.println(jsonObject);

        try {


            int numberValue = 0;
            JSONObject piles =(JSONObject) jsonObject.get("piles");
            JSONObject player =(JSONObject) piles.get(pileName);
            org.json.simple.JSONArray cards = (org.json.simple.JSONArray) player.get("cards");
            int n =   cards.size();//(msg).length();

            for (int i = 0; i < n; ++i) {
                JSONObject card = (JSONObject) cards.get(i);
                System.out.println("");
                System.out.println(card);
                if(listingAndShow){
                    System.out.println("listing and show = true");
                    String image = (String) card.get("image");
                    ImageIcon originalCardIcon = new ImageIcon(new URL(image));
                    Image scaledCardImage = originalCardIcon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                    cardImage = new JLabel(new ImageIcon(scaledCardImage));
                    houseCardDisplay.add(cardImage);
                }


                String code = (String)card.get("code");
                System.out.println(pileName + code);
                String value = (String)card.get("value");

                allCardCodes.add(value);
                //System.out.println("Value: "+value);
                if(value.equals("JACK")  || value.equals("QUEEN") || value.equals("KING")){
                    numberValue = 10;
                } else if (value.equals("ACE")){
                    isThereAnAce = true;
                    numberValue = aceValue;
                    //System.out.println("inside listing cards Ace Value: " + aceValue);

                }else {
                    numberValue = Integer.parseInt(value);
                }
                cardTotal = cardTotal + numberValue;



                playerCardDisplay.revalidate();
                playerCardDisplay.repaint();
                houseCardDisplay.revalidate();
                houseCardDisplay.repaint();


            }
            System.out.println("All card codes: " + allCardCodes);

            if (splitCheck){
                if (allCardCodes.size() > 1) {
                    for (int i = 0; i < allCardCodes.size(); i++) {
                        for (int j = i + 1; j < allCardCodes.size(); j++) {
                            if (allCardCodes.get(i).equals(allCardCodes.get(j))) {
                                System.out.println("Duplicate codes found: " + allCardCodes.get(i));
                                twoCardsEqual = true;
                                split.setEnabled(true);

                            }
                        }
                    }
                }
            }


        }

        catch (Exception e) {
            e.printStackTrace();
        }



    }
    public void drawingFromPile(String pileName, int count) throws ParseException { String output = "abc";
        totlaJson="";


        try {

            String urlInput = "https://deckofcardsapi.com/api/deck/"+deckID+"/pile/"+pileName+"/draw/?count="+count;
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
                totlaJson+=output;

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        //System.out.println(jsonObject);

        try {
            org.json.simple.JSONArray cards = (org.json.simple.JSONArray) jsonObject.get("cards");
            int n =   cards.size(); //(msg).length();
            for (int i = 0; i < n; ++i) {
                JSONObject card = (JSONObject) cards.get(i);
                cardCode = (String)card.get("code");
                System.out.println(cardCode);

            }


        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("HIT")) {

                try {
                    drawACard(1,"player",true);
                    playerScore = countCards("player");
                    System.out.println("Player Score" + playerScore);
                    if(playerScore > 21){
                        playerStatusLabel.setText("BUST");
                        hit.setEnabled(false);
                        housePlay();
                    }


                }catch(Exception r){
                    System.out.println(r);
                }



            }
            if (command.equals("STICK")) {

                try {
                    hit.setEnabled(false);
                    housePlay();

                }catch(Exception r){
                    System.out.println(r);
                }



            }
            if (command.equals("RESET")) {
                listingAndShow = false;
                hit.setEnabled(true);
                System.out.println("Reset was clicked");

                try {

                    playerCardDisplay.removeAll();
                    playerCardDisplay.revalidate();
                    playerCardDisplay.repaint();
                    houseCardDisplay.removeAll();
                    houseCardDisplay.revalidate();
                    houseCardDisplay.repaint();

                    playerStatusLabel.setText("");
                    System.out.println("P NUM CARDS: " + pNumCards);
                    for(int x = 0; x < pNumCards; ++x){
                        drawingFromPile("player",1);
                        addToPiles("discard");
                        System.out.println("added to discard");

                    }
                    System.out.println("H Num Card: " + hNumCards);
                    for(int x = 0; x < hNumCards; ++x){
                        drawingFromPile("house",1);
                        addToPiles("discard");
                        System.out.println("added to discard");
                    }
                    pNumCards = 0;
                    hNumCards = 0;

                    deal();

                }catch(Exception r){
                    System.out.println(r);
                }



            }
            if (command.equals("SPLIT")) {

                try {

                }catch(Exception r){

                }



            }

        }
    }

}

