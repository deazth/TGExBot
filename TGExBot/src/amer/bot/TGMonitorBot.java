/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amer.bot;

import amer.common.ConfigHandler;
import amer.common.PortalConnectionManager;
import amer.common.SSHManager;
import amer.common.dbHandler;
import com.portal.pcm.PortalContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author amer
 */
public class TGMonitorBot extends TelegramLongPollingBot {

  private Thread checker;
  private ExceptionBgWorker thebgslave;
  private long sleepDuration;
  private long NeblastMonitorSent;
  private final ArrayList<Long> NebMonitorList;
  private boolean NebMonitorSpamMode;

  public TGMonitorBot() {
    this.sleepDuration = 5000;
    NebMonitorSpamMode = false;

    NebMonitorList = new ArrayList<>();
  }

  @Override
  public String getBotUsername() {
    return "novaexceptbot";
  }

  @Override
  public String getBotToken() {
    return "791518552:AAFWw1xsH-15nttZ9Cq7Dac4gpl3pfYAQxw";
  }

  @Override
  public void onUpdateReceived(Update update) {
    // We check if the update has a message and the message has text
    if (update.hasMessage() && update.getMessage().hasText()) {
      // Set variables
      String message_text = update.getMessage().getText().trim().toLowerCase();
      long chat_id = update.getMessage().getChatId();
//      String sender = update.getMessage().getChat().getUserName();

      System.out.println("Received text: " + message_text);

      String[] param = message_text.split(" ");

      if (param.length == 0) {

      } else {
        switch (param[0].trim().toLowerCase()) {
          case "/neb":
            processCommandNeb(chat_id, param);
            break;
          case "/brm":
            processCommandBrm(chat_id, param);
            break;
          case "/help":
            giveHelp(chat_id);
            break;
          case "/eai":
            processCommandEai(chat_id, param);
            break;
          case "/bot":
            listCommandsBot(chat_id);
            break;
          default:
            if (param[0].startsWith("/")) {
              sendMsg(chat_id, "unrecognised command: " + param[0]);
            }
            break;
        }
      }

    }
  }

  @Override
  public void onUpdatesReceived(List<Update> list) {
    list.forEach((l) -> {
      onUpdateReceived(l);
    });
  }

  private void processCommandBrm(long chatid, String[] cmd) {

    if (cmd.length == 1) {
      listCommandsBrm(chatid);
    } else {

      if (cmd[1].startsWith("help")) {
        listCommandsBrm(chatid);
      } else if (cmd[1].startsWith("testnap")) {
        tryTestnap(chatid, false);
      } else if (cmd[1].startsWith("checkall")) {
        checkCM(chatid, false);
      } else if (cmd[1].startsWith("accepted")) {
        brmCountAccepted(chatid, false);
      } else if (cmd[1].startsWith("exception")) {
        brmcountException(chatid, false);
      }
    }
  }

  private void processCommandEai(long chatid, String[] cmd) {

    if (cmd.length == 1) {
      listCommandsEai(chatid);
    } else {

      if (cmd[1].startsWith("help")) {
        listCommandsEai(chatid);
      } else if (cmd[1].startsWith("orderrqi")) {
        eaiCountOrderRQI(chatid, false);
      }
    }
  }

  private void processCommandNeb(long chatid, String[] cmd) {

    if (cmd.length == 1) {
      listCommandsNeb(chatid);
    } else {

      if (cmd[1].startsWith("help")) {
        listCommandsNeb(chatid);
      } else if (cmd[1].startsWith("start")) {
        startChecker(chatid);
      } else if (cmd[1].startsWith("stop")) {
        stopChecker(chatid);
      } else if (cmd[1].startsWith("delay")) {
        if (cmd.length < 3) {
          sendMsg(chatid, "illegal parameter for 'delay'");
        } else {
          try {
            long del = Long.parseLong(cmd[2]);
            setDelay(chatid, del);
          } catch (NumberFormatException e) {
            sendMsg(chatid, "invalid input for 'delay' : " + cmd[2]);
          }
        }
      } else if (cmd[1].startsWith("spam")) {
        if (cmd.length < 3) {
          sendMsg(chatid, "illegal parameter for 'spam'");
        } else {
          try {
            int spam = Integer.parseInt(cmd[2]);
            setSpamMode(spam != 0);
          } catch (NumberFormatException e) {
            sendMsg(chatid, "invalid input for 'spam' : " + cmd[2]);
          }
        }
      }
    }

  }

  private void listCommandsNeb(long chatid) {

    String responds = "Monitoring bot manual.\n"
            + "Command: /neb <option>\n"
            + "List of available options:\n"
            + "-------------------------------\n"
            + "start       - start the monitoring\n"
            + "stop        - stop monitoring, obviously\n"
            + "delay <xx>  - set the delay between check in xx seconds\n"
            + "spam <0|1>  - set to 0 to only alert when got issue\n"
            + "-------------------------------\n";

    sendMsg(chatid, responds);

  }

  private void listCommandsBrm(long chatid) {

    String responds = "BRM Specific module.\n"
            + "Command: /brm <option>\n"
            + "List of available options:\n"
            + "-------------------------------\n"
            + "testnap     - try to establish a connectiont to CM\n"
            + "checkall    - 'check' all\n"
            + "accepted    - count accepted task?\n"
            + "exception   - count exception\n"
            + "-------------------------------\n";

    sendMsg(chatid, responds);

  }

  private void listCommandsEai(long chatid) {

    String responds = "EAI Specific module.\n"
            + "Command: /brm <option>\n"
            + "List of available options:\n"
            + "-------------------------------\n"
            + "orderrqi     - count the number of order since the last hour\n"
            + "-------------------------------\n";

    sendMsg(chatid, responds);

  }

  private void listCommandsBot(long chatid) {

    String responds = "Hi. I'm Your Friendly Spammer.\n"
            + "List of available commands:\n"
            + "-------------------------------\n"
            + "/neb       - monitoring related module\n"
            + "/brm       - BRM specific module\n"
            + "/eai       - EAI specific module\n"
            + "/help      - misc info\n"
            + "-------------------------------\n";

    sendMsg(chatid, responds);

  }

  private void giveHelp(long chatid) {

    String responds = "In need of help?\n"
            + "If you're injured, please visit the nearest hospital\n"
            + "If you're bored, go learn something new\n"
            + "If you need financial support, heh...\n";

    sendMsg(chatid, responds);

  }

  private void startChecker(long chatid) {
    subscribe(chatid);

    if (checker != null && checker.isAlive()) {
      sendMsg(chatid, "Monitor already started");
    } else {
      thebgslave = new ExceptionBgWorker();
      checker = new Thread(thebgslave);
      checker.start();
      System.out.println("new thread created");
    }

  }

  private void stopChecker(long chatid) {

    unsubscribe(chatid);

    if (checker != null && checker.isAlive()) {

      if (NebMonitorList.isEmpty()) {
        thebgslave.stop();
        checker.interrupt();
        System.out.println("No more subscriber. Terminating thread");
      }

    } else {
      sendMsg(chatid, "Monitor is not running");
    }

  }

  private void setDelay(long chatid, long ms) {
    sendMsg(chatid, "Changing delay from " + sleepDuration / 1000 + " to " + ms + " seconds");
    sleepDuration = ms * 1000;
    checker.interrupt();

//    sendMsg(chatid, "Check delay set to " + ms + " seconds");
  }

  private void checkCM(long chatid, boolean isMonitor) {
    SSHManager sm = new SSHManager("S53788", "Awesom01", "10.41.24.82", "");
    int errcount = 0;

    String err = sm.connect();

    if (err != null) {
      System.err.println(err);
      return;
    }

//    exec(sm, "/home/S53788/check_all.sh");
    System.out.println("executing check all");
    String out = sm.sendCommand("/home/S53788/check_all.sh");
    System.out.println("done check");
    System.out.println("output: " + out);
    String resp = "CM count:\n";

    if (out == null) {
      out = "ssh - no response";
      resp = "ssh - no response";
    } else {
      Scanner sc = new Scanner(out);

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        if (line.trim().isEmpty()) {
          continue;
        }

        String[] sp = line.split(":");
        if (sp.length > 2) {
          resp += sp[0] + " -> " + sp[2] + "\n";
        }

      }

    }

    if (out.contains("CRITICAL")) {
      errcount = 1;
    }

    if (isMonitor) {
      if (NebMonitorSpamMode || errcount > 0) {
        sendMonitorMsg(resp);
      }
    } else {
      sendMsg(chatid, resp);
    }

    sm.close();
  }

  private synchronized void setSpamMode(boolean spam) {
    NebMonitorSpamMode = spam;
    if (NebMonitorSpamMode) {
      sendMonitorMsg("Spam mode enabled. All test will be notified");
    } else {
      sendMonitorMsg("Spam mode disabled. Alert will only be sent when issue (subjective) occured");
    }

  }

  private void sendMsg(long chatid, String msg) {
    SendMessage message = new SendMessage() // Create a message object object
            .setChatId(chatid)
            .setText(msg);

    try {
      execute(message); // Sending our message object to user
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  private void sendMonitorMsg(String msg) {
    NebMonitorList.forEach((id) -> {
      sendMsg(id, msg);
    });
  }

  private String toDate(long ts) {
    Date d = new Date(ts);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

    return sdf.format(d);
  }

  private synchronized void subscribe(long chatid) {
    if (!NebMonitorList.contains(chatid)) {
      NebMonitorList.add(chatid);
    }

    String msg = "Subscribed to monitoring spam service\n"
            + "Last message sent: " + toDate(NeblastMonitorSent) + "\n"
            + "Delay between messages: " + (sleepDuration / 1000) + " seconds";

    sendMsg(chatid, msg);

  }

  private synchronized void unsubscribe(long chatid) {
    if (NebMonitorList.contains(chatid)) {
      NebMonitorList.remove(chatid);
    }

    sendMsg(chatid, "Unsubscribed from monitoring service");

  }

  // BRM related tasks
  private void tryTestnap(long chatid, boolean isMonitor) {
    String out;
    int errcount = 0;

//    for (int i = 1; i <= 5; i++) {
    try {
      PortalContext pc = PortalConnectionManager.getInstance().getConnection();
//        out += "#" + i + "- host=" + pc.getHost() + ":" + pc.getPort() + " - success\n";
      out = "Testnap attempt - host=" + pc.getHost() + ":" + pc.getPort() + " - success";
      pc.close(true);

    } catch (Exception e) {
//        out += "#" + i + "- " + e.getMessage() + "\n";
      out = "Testnap failed: " + e.getMessage();
      errcount++;
    }

//    }
//
//    out += "Done.";
    if (isMonitor) {
      if (NebMonitorSpamMode || errcount > 0) {
        sendMonitorMsg(out);
      }
    } else {
      sendMsg(chatid, out);
    }

  }

  private void brmCountAccepted(long chatid, boolean isMonitor) {
    String oneHourAgo = toDate(System.currentTimeMillis() - (1 * 60 * 60 * 1000));
    String sql = "SELECT count(1) asd\n"
            + "FROM \n"
            + "      OM_ORDER_HEADER OOH, OM_ORDER_FLOW OOF, OM_TASK OT, OM_PROCESS OP, OM_STATE OS, \n"
            + "      AUTOMATION_RULE AR, OM_ORDER_TYPE OOT\n"
            + "WHERE \n"
            + "      OOH.ORDER_SEQ_ID = OOF.ORDER_SEQ_ID\n"
            + "      AND OOT.ORDER_TYPE_ID = OOH.ORDER_TYPE_ID\n"
            + "      AND OOF.PROCESS_ID = OP.PROCESS_ID\n"
            + "      AND OOH.ORD_STATE_ID IN (4)\n"
            + "      AND OOF.CARTRIDGE_ID IN\n"
            + "      (SELECT CARTRIDGE_ID FROM OM_CARTRIDGE \n"
            + "      WHERE  (NAMESPACE_MNEMONIC = 'TM_HSBB'  OR  NAMESPACE_MNEMONIC = 'TM_NonConsumer')\n"
            + "      AND AR.NAMESPACE = NAMESPACE_MNEMONIC\n"
            + "      AND CARTRIDGE_ID = OOF.CARTRIDGE_ID)\n"
            + "      AND OOF.TASK_ID = OT.TASK_ID    \n"
            + "      AND OOF.STATE_ID = OS.STATE_ID \n"
            + "      AND OT.TASK_TYPE IN ('A','M')\n"
            + "      AND (OT.TASK_MNEMONIC = AR.TASK_MNEMONIC\n"
            + "      OR OT.TASK_MNEMONIC = 'Exception_'||AR.TASK_MNEMONIC\n"
            + "      OR OT.TASK_MNEMONIC LIKE 'Exception_%'||AR.TASK_MNEMONIC)\n"
            + "      AND ( AR.NAMESPACE = 'TM_HSBB' OR AR.NAMESPACE = 'TM_NonConsumer' )\n"
            + "      AND AR.EXECUTION_MODE = 'do'\n"
            + "      AND OT.TASK_MNEMONIC NOT LIKE '%Exception%'\n"
            + "      And Oof.Date_Pos_Started > To_Date('" + oneHourAgo + "','dd/MM/yyyy HH24:MI:SS')\n"
            + "      AND AR.EVENT_NAME NOT IN ('ActivityCreate','OrderCreationIcare')\n"
            + "      AND OOH.REFERENCE_NUMBER NOT LIKE '%Shakeout%'\n"
            + "      AND AR.EXT_SYS IN ('OBRM','BRM')\n"
            + "      AND OS.STATE_DESCRIPTION = 'Accepted'";

    dbHandler dbh = new dbHandler("OSM");
    dbh.setDBConnInfo(ConfigHandler.get("OSM.DBtns"));
    dbh.setUserPass(ConfigHandler.get("OSM.DBuser"), ConfigHandler.get("OSM.DBpassword"));

    String outmsg;
    int errcount = 0;

    try {
      dbh.openConnection();

      ResultSet rs = dbh.executeSelect(sql);

      if (rs.next()) {
        int count = rs.getInt(1);
        outmsg = "BRM - Accepted count: " + count;
        if (count > 100) {
          errcount++;
        }
      } else {
        outmsg = "No result when counting 'accepted' for BRM";
      }

    } catch (SQLException e) {
      outmsg = "Error getting data from OSM db: " + e.getMessage();
      errcount++;
    }

    try {
      dbh.closeConnection();
    } catch (Exception e) {
    }

    if (isMonitor) {
      if (NebMonitorSpamMode || errcount > 0) {
        sendMonitorMsg(outmsg);
      }
    } else {
      sendMsg(chatid, outmsg);
    }

  }

  private void brmcountException(long chatid, boolean isMonitor) {
    
    String sql = "SELECT \n"
            + "      (TRUNC(SYSDATE) - TRUNC(OOF2.DATE_POS_STARTED)) AS AGING\n"
            + "      , count(1) asd\n"
            + "FROM \n"
            + "      osm.om_order_flow oof2\n"
            + "      INNER JOIN OSM.OM_TASK OT2 ON (OOF2.TASK_ID = OT2.TASK_ID and OOF2.DATE_POS_STARTED > sysdate - 4)\n"
            + "      INNER JOIN osm.om_state os ON (os.STATE_ID = oof2.STATE_ID)\n"
            + "      INNER JOIN osm.om_cartridge oc2 ON (oc2.cartridge_id = oof2.cartridge_id)\n"
            + "      INNER JOIN osm.om_order_header ooh ON (oof2.order_seq_id = ooh.order_seq_id)\n"
            + "      INNER JOIN osm.om_ospolicy_state oos ON (ooh.ord_state_id = oos.ID)\n"
            + "      INNER JOIN osm.om_process op ON (OP.PROCESS_ID = oof2.PROCESS_ID)\n"
            + "      INNER JOIN osm.om_order_type oot ON (ooh.order_type_id = oot.order_type_id)\n"
            + "      LEFT JOIN (SELECT ooi.order_seq_id AS ooi_order_seq_id\n"
            + "           ,oof.order_seq_id AS oof_order_seq_id\n"
            + "           ,ooi.node_value_text AS ooi_node_value_text\n"
            + "           ,oof.state_id AS oof_state_id\n"
            + "           ,oof.hist_seq_id AS oof_hist_seq_id\n"
            + "             FROM osm.om_order_instance ooi\n"
            + "               JOIN osm.om_order_flow oof ON (ooi.order_seq_id = oof.order_seq_id)\n"
            + "               JOIN osm.om_order_data_dictionary oodd ON (ooi.data_dictionary_id = oodd.data_dictionary_id)\n"
            + "             WHERE oodd.data_dictionary_mnemonic = 'response_error_message'\n"
            + "               AND oof.task_type = 'M'\n"
            + "               AND ooi.hist_seq_id = CASE \n"
            + "               WHEN oof.state_id = '1'\n"
            + "               THEN (SELECT a.hist_seq_id_from\n"
            + "                   FROM osm.om_hist$flow a\n"
            + "                      JOIN osm.om_hist$flow b ON (a.hist_seq_id = b.hist_seq_id_from)\n"
            + "                   WHERE b.hist_seq_id = oof.hist_seq_id\n"
            + "                      AND b.order_seq_id = ooi.order_seq_id\n"
            + "                      AND a.order_seq_id = ooi.order_seq_id\n"
            + "                ) ELSE (SELECT a1.hist_seq_id\n"
            + "                   FROM osm.om_hist$flow a1\n"
            + "                     JOIN osm.om_hist$flow b1 ON (a1.hist_seq_id = b1.hist_seq_id_from)\n"
            + "                     JOIN osm.om_hist$flow c1 ON (b1.hist_seq_id = c1.hist_seq_id_from)\n"
            + "                     JOIN osm.om_hist$flow d1 ON (c1.hist_seq_id = d1.hist_seq_id_from)\n"
            + "                   WHERE a1.order_seq_id = ooi.order_seq_id\n"
            + "                     AND b1.order_seq_id = ooi.order_seq_id\n"
            + "                     AND c1.order_seq_id = ooi.order_seq_id\n"
            + "                     AND d1.order_seq_id = ooi.order_seq_id\n"
            + "                     AND d1.hist_seq_id = oof.hist_seq_id\n"
            + "                 ) END\n"
            + "      ) TEMP ON ( oof2.order_seq_id = TEMP.oof_order_seq_id\n"
            + "         AND oof2.hist_seq_id = TEMP.oof_hist_seq_id)\n"
            + "WHERE \n"
            + "      oos.mnemonic = 'in_progress'and\n"
            + "      OC2.cartridge_id = ot2.cartridge_id\n"
            + "      AND oc2.cartridge_id = ooh.cartridge_id\n"
            + "      AND oc2.cartridge_id = op.cartridge_id\n"
            + "      AND OOF2.TASK_TYPE = 'M'\n"
            + "      AND OP.PROCESS_ID_MNEMONIC <> 'Shakedown_Test'\n"
            + "      and (OT2.TASK_MNEMONIC like '%Billing%' or OT2.TASK_MNEMONIC = 'Exception_R2_Reset_Network_Penalty' \n"
            + "            or OT2.TASK_MNEMONIC = 'Exception_VD_Set_Bucket_Limit' or OT2.TASK_MNEMONIC = 'Exception_VD_Update_Bucket_Value'\n"
            + "            or OT2.TASK_MNEMONIC = 'Exception_A3.8_Update_Service_Attribute')\n"
            + "group by TRUNC(SYSDATE) - TRUNC(OOF2.DATE_POS_STARTED)            \n"
            + "ORDER BY AGING";

    dbHandler dbh = new dbHandler("OSM");
    dbh.setDBConnInfo(ConfigHandler.get("OSM.DBtns"));
    dbh.setUserPass(ConfigHandler.get("OSM.DBuser"), ConfigHandler.get("OSM.DBpassword"));

    String outmsg = "BRM - Exception count by aging:\n";
    int errcount = 0;

    try {
      dbh.openConnection();

      ResultSet rs = dbh.executeSelect(sql);

      while (rs.next()) {
        int count = rs.getInt(2);
        int aging = rs.getInt(1);
        outmsg += "Day " + aging + " : " + count + "\n";
//        if (count > 100) {
//          errcount++;
//        }
      } 

    } catch (SQLException e) {
      outmsg = "Error getting data from OSM db: " + e.getMessage();
      errcount++;
    }

    try {
      dbh.closeConnection();
    } catch (Exception e) {
    }

    if (isMonitor) {
      if (NebMonitorSpamMode || errcount > 0) {
        sendMonitorMsg(outmsg);
      }
    } else {
      sendMsg(chatid, outmsg);
    }

  }

  private void eaiCountOrderRQI(long chatid, boolean isMonitor) {

    String sql = "select AUDIT_PARAM_3, count(1) asd\n"
            + "from WLIPRD_CUSTOM.EAI_AUDIT_LOG\n"
            + "where EVENT_NAME in ('OrderCreate', 'OrderCreateBulk')\n"
            + "and AUDIT_TYPE = 'RQI'\n"
            + "and audit_date_time between sysdate - 1/24 and sysdate\n"
            + "group by AUDIT_PARAM_3\n"
            + "order by 1 desc";

    dbHandler dbh = new dbHandler("EAI");
    dbh.setDBConnInfo(ConfigHandler.get("EAI.TNS"));
    dbh.setUserPass(ConfigHandler.get("EAI.USER"), ConfigHandler.get("EAI.PASSWORD"));

    String outmsg = "EAI OrderCreate RQI count:\n";
    int errcount = 0;

    try {
      dbh.openConnection();

      ResultSet rs = dbh.executeSelect(sql);

      while (rs.next()) {
        int count = rs.getInt(2);
        String type = dbHandler.dbGetString(rs, 1);
        outmsg += type + " = " + count + "\n";
//        if (count > 100) {
//          errcount++;
//        }
      }

    } catch (SQLException e) {
      outmsg = "Error getting order rqi count from EAI db: " + e.getMessage();
      errcount++;
    }

    try {
      dbh.closeConnection();
    } catch (Exception e) {
    }

    if (isMonitor) {
      if (NebMonitorSpamMode || errcount > 0) {
        sendMonitorMsg(outmsg);
      }
    } else {
      sendMsg(chatid, outmsg);
    }
  }

  // thread classes
  class ExceptionBgWorker implements Runnable {

    private boolean StopRun;

    @Override
    public void run() {
      StopRun = false;

      while (!StopRun) {

        try {
          NeblastMonitorSent = System.currentTimeMillis();
//          sendMonitorMsg("Current Time: " + toDate(lastMonitorSent));

          tryTestnap(0, true);
          checkCM(0, true);
          brmCountAccepted(0, true);

          Thread.sleep(sleepDuration);

        } catch (InterruptedException e) {
        }

      }

    }

    public synchronized void stop() {
      StopRun = true;

    }

  }
}
