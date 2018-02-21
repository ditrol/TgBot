
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.*;


public class Example extends TelegramLongPollingBot{

    Socket socket;
    DataOutputStream oos;
    DataInputStream ois;
    {
        try {
            socket = new Socket("localhost", 3345);
            oos = new DataOutputStream(socket.getOutputStream());
            ois = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init(); // Инициализируем апи
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Example());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Doni_junior_bot";
        //возвращаем юзера
    }


    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String txt = msg.getText();
        String ImgUrl  = "https://mafii.net/uploads/avatars/full/04May2013_11-36-07doniomafio.jpg";
        Pattern pattern = Pattern.compile("^[A-Za-z]|[А-Яа-я]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);


            String clientCommand = txt;

        try {
            oos.writeUTF(clientCommand);
            oos.flush();
            System.out.println("Клиент отправил сообщение боту на сервер: " + clientCommand);
            String in = ois.readUTF();
            sendMsg(msg, in);
            System.out.println(in);



        } catch (IOException e1) {
            e1.printStackTrace();
        }



        if (txt.equals("/start")) {
            sendMsg(msg, "Привет!" + "\n" + "Меня зовут Дони и я помогаю мафии");
        }
        if (txt.equals("/token")) {
            sendMsg(msg, "460513032:AAExwH4pXFmf6ub8vIJzebijsYWmiAArat4");
        }
        if (txt.equals("/nick")) {
            sendMsg(msg, "Doni junior");
        }

        if (matcher.find()) {
                       sendMsg(msg, "Ушло на сервер");
                 }


    }

    @Override
    public String getBotToken() {
        return "460513032:AAExwH4pXFmf6ub8vIJzebijsYWmiAArat4";
        //Токен бота
    }

    public void sendImageFromUrl(String url, Message msg) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(msg.getChatId());
        sendPhotoRequest.setPhoto(url);
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation") // Означает то, что в новых версиях метод уберут или заменят
    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        s.setText(text);
        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(s);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

}