import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


// video to load jar
//https://www.youtube.com/watch?v=QAJ09o3Xl_0

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.sound.midi.Soundbank;
import javax.swing.*;

// Program for print data in JSON format.
public class ReadJson {

    private int WIDTH=800;
    private int HEIGHT=700;
    private JFrame mainFrame;
    private JPanel top;
    private JPanel middle;
    private JPanel bottom;
    private JTextArea name;
    private JTextArea otherInfo;
    private JTextArea searchBar;
    private JButton searchButton;
    private String totlaJson;
    private String searchTerm;

    public static void main(String args[]) throws ParseException {
        // In java JSONObject is used to create JSON object
        // which is a subclass of java.util.HashMap.


        JSONObject file = new JSONObject();
        file.put("Full Name", "Ritu Sharma");
        file.put("Roll No.", 1704310046);
        file.put("Tution Fees", 65400);


        // To print in JSON format.
        System.out.print(file.get("Tution Fees"));
        ReadJson readingIsWhat = new ReadJson();

    }

    public ReadJson(){
        prepareGUI();

    }

    public void prepareGUI (){
        mainFrame = new JFrame("Read Json");
        top = new JPanel();
        middle = new JPanel();
        middle.setLayout(new GridLayout(0,2));
        bottom = new JPanel();
        bottom.setLayout(new GridLayout(0,2));
        name = new JTextArea();
        otherInfo = new JTextArea();
        searchBar = new JTextArea();
        searchButton = new JButton("Search");

        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new BorderLayout());
        //mainFrame.add(top);
        mainFrame.add(middle, BorderLayout.CENTER);
        mainFrame.add(bottom, BorderLayout.SOUTH);
        middle.add(name);
        name.setEditable(false);
        middle.add(otherInfo);
        otherInfo.setEditable(false);
        bottom.add(searchBar);
        bottom.add(searchButton);
        searchButton.setActionCommand("SEARCH");
        searchButton.addActionListener(new ButtonClickListener());



        mainFrame.setVisible(true);
    }

    public  void pull() throws ParseException {

        //int indexOfname = totlaJson.indexOf(searchTerm);
        String output = "abc";
        totlaJson="";
       // System.out.println(indexOfname);


        try {

            String urlInput = "https://pokeapi.co/api/v2/pokemon/" + searchTerm;
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
        //System.out.println(str);
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        System.out.println(jsonObject);

        try {

            String name = (String)jsonObject.get("name");
            System.out.println("");
            System.out.println("name: " + name);
            otherInfo.append("Name: " + name + "\n");

            org.json.simple.JSONArray abilities = (org.json.simple.JSONArray) jsonObject.get("abilities");
            int n =   abilities.size(); //(msg).length();

            otherInfo.append("Abilities: ");
            for (int i = 0; i < n; ++i) {
                JSONObject test =(JSONObject) abilities.get(i);
                System.out.println(test);

                JSONObject ability =(JSONObject) test.get("ability");
                System.out.println(ability);
                String abilityName = (String)ability.get("name");
                System.out.println(abilityName);
                otherInfo.append(abilityName + " ");





                // System.out.println(person.getInt("key"));
            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }




    }
    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("SEARCH")) {
                searchTerm = searchBar.getText();
                System.out.println(searchTerm);
                try {
                    pull();
                }catch(Exception r){
                    System.out.println(r);
                }



            }
        }
    }

}


