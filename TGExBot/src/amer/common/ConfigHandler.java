/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package amer.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Read th
 * @author S53788
 */
public class ConfigHandler {

  private static String job_id;
  private static Properties prop = new Properties();
  
  private Properties localProp;
  
  public ConfigHandler(){
    localProp = new Properties();
  }
  
  public void loadLocalConfig(String cfgFile) throws IOException{
    localProp.load(new FileInputStream(cfgFile));
//    CommonUtility.log(cfgFile + " loaded.", 3);
  }
  
  public String getLocal(String propertyKey) throws Exception{
    
    String retval = localProp.getProperty(propertyKey);
    
    if(!(retval == null || retval.isEmpty())){
      //System.out.println("property found: " + propertyKey);
    } else {
      throw new Exception("Property not found " + propertyKey);
    }
    
    return retval;
  }
  
  public static void loadConfig(String cfgFile) throws IOException{
    
    prop.load(new FileInputStream(cfgFile));
    Utilities.log(cfgFile + " loaded.", 3);
//    prop.storeToXML(System.out, "com");
  }
  
  public static String get(String propertyKey) throws NullPointerException{
    
    String retval = prop.getProperty(propertyKey);
    
    if(!(retval == null || retval.isEmpty())){
      //System.out.println("property found: " + propertyKey);
    } else {
      throw new NullPointerException("Property not found " + propertyKey);
    }
    
    return retval;
  }


  // set functions
  public static void setJobId(String jobid){
    job_id = jobid;
    System.out.println("Current Job ID: " + jobid);
  }
  
  public static String getJobId(){
    return job_id;
  }
  

}
