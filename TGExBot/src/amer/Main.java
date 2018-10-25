/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amer;

import amer.bot.TGMonitorBot;
import amer.common.ConfigHandler;
import amer.common.constant;
import java.io.IOException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author amer
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    
    try {
      ConfigHandler.loadConfig(constant.DBCONN_FILE);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    
    System.out.println("Init");
    ApiContextInitializer.init();

    System.out.println("api");
    TelegramBotsApi botsApi = new TelegramBotsApi();

    try {
      System.out.println("register");
      botsApi.registerBot(new TGMonitorBot());
      System.out.println("after register");
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
    
    
  }
  
}
