import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Example extends TelegramLongPollingBot{

    Socket socket;
    DataOutputStream oos;
    DataInputStream ois;
    private static final String API_KEY = "trnsl.1.1.20180221T210655Z.da998608fdf30f55.a0d6513b7d5edec6ac5f1b3e72be7aec8bcc01d0\n";
    private static final String PATH = "https://translate.yandex.net/api/v1.5/tr.json/translate";


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

public String Translate (String lang, String txt) throws IOException {

    HttpClient client = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(PATH);
    httpPost.setHeader("User-Agent", "Mozilla/5.0");


    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("key", API_KEY));
    params.add(new BasicNameValuePair("lang", lang));
    params.add(new BasicNameValuePair("text", txt));

    httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

    HttpResponse httpResponse = client.execute(httpPost);
    BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse
            .getEntity().getContent(), "UTF-8"));
    String JSonWord = br.readLine();

        return JSonWord;
}


    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String txt = msg.getText();
        String ImgUrl  = "https://mafii.net/uploads/avatars/full/04May2013_11-36-07doniomafio.jpg";
        Pattern RuPattern = Pattern.compile("^[А-Яа-я]+", Pattern.CASE_INSENSITIVE);
        Pattern EnPattern = Pattern.compile("^[A-Za-z]+", Pattern.CASE_INSENSITIVE);
        Matcher Rumatcher = RuPattern.matcher(txt);
        Matcher Enmatcher = EnPattern.matcher(txt);
        String clientCommand = txt;


        if (txt.equals("/help")) {
            sendMsg(msg,"Меня зовут Дони и я помогаю мафии" + "\n"
                    + "/nick - покажет тебе мою кличку в кругах семьи" + "\n"
                    + "Или у тебя есть какие-то вопросы? Вперёд, спрашивай.");
        }
        else if (txt.equals("/token")) {
            sendMsg(msg, "460513032:AAExwH4pXFmf6ub8vIJzebijsYWmiAArat4");
        }
        else if (txt.equals("/nick")) {
            sendMsg(msg, "Doni junior");
        }


        else  {
            try {
                if (Rumatcher.find()) {

                    JsonObject jsonObject = new JsonParser().parse(Translate("ru-en", clientCommand)).getAsJsonObject();
                    oos.writeUTF(jsonObject.get("text").getAsString());
                    oos.flush();

                    System.out.println("Клиент отправил сообщение боту на сервер: " + clientCommand);
                    String in = ois.readUTF();
                    JsonObject jsonObject1 = new JsonParser().parse(Translate("en-ru", in)).getAsJsonObject();
                    sendMsg(msg, jsonObject1.get("text").getAsString());
                    System.out.println(jsonObject1.get("text").getAsString());

                } else if (Enmatcher.find()) {

                    oos.writeUTF(clientCommand);
                    oos.flush();
                    System.out.println("Клиент отправил сообщение боту на сервер: " + clientCommand);
                    String in = ois.readUTF();
                    sendMsg(msg, in);
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
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