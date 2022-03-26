package com.nikolai.education;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;

public class TestApi {


    private static final String CHAT_ID = "807078638";
    private static final String TOKEN = "5196434422:AAFy9o25KK219CVEKfNWzQQhIMxe0M3me_s";

    public static void main(String[] args) {

        TelegramBot bot = new TelegramBot(TOKEN);

        String text = "<a href=" + "https://vk.com/im" + ">click</a>";

        String link = "https://vk.com/im";

        SendMessage request = new SendMessage(CHAT_ID, "Привет всем!!!  <a href=\"" + link+  "\">inline URL</a>")
                .parseMode(ParseMode.HTML);


        bot.execute(request, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {

            }

            @Override
            public void onFailure(SendMessage request, IOException e) {

            }
        });


    }


}
