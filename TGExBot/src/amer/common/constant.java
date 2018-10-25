/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package amer.common;

/**
 * Contain all constants used within this program
 * 
 * @author S53788
 */
public class constant {

  public static final String DBCONN_FILE = "config/CConnection.ini";
//  public static final String LOGGER_FILE_NAME = "../config/log4j.properties";  
  
  // return values
  public static final int RET_MISSING_FILE = 101;
  // return values : exception
  public static final int RET_PARSE_EXCEPTION = 201;
  public static final int RET_SQL_EXCEPTION = 202;
  // file move option
  public static final int MOVE_OVERWRITE = 10001;
  public static final int MOVE_NEW_NAME = 10002;
  public static final int MOVE_NO_OVERWRITE = 10003;

  // run mode
  public static final int RUN_DEBUG = 0;
  public static final int RUN_ALL = 1;
  public static final int RUN_BILL = 2;
  public static final int RUN_PYMT = 3;
  public static final int RUN_ALMOST_DUE_3 = 4;
  public static final int RUN_ALMOST_DUE_1 = 5;
  public static final int RUN_PASS_DUE_1 = 6;

  // misc
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");

  // log type
  public static final int MESSAGE   = 0;
  public static final int ERROR   = 1;
  public static final int WARNING = 2;
  public static final int DEBUG   = 3;
  
  
}
