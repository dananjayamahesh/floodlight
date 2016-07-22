package net.floodlightcontroller.wifioffload;

import java.net.*;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class WifiOffloadRestClient implements Runnable {
	protected static Logger logger;
	
	public WifiOffloadRestClient(){
		logger = LoggerFactory.getLogger(WifiOffloadRestClient.class);
		logger.info("WifiOfflaodRestClient");
	}
	
	
	public static String httpGet(String urlStr) throws IOException {
		long startTime=System.nanoTime();  
		
		URL url = new URL(urlStr);
		  
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();
		  
		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }
    
		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  
		  long endTime = System.nanoTime();
		   logger.info("Time Taken for Check REST Operation: "+(((double)(endTime-startTime))/1000000000));
		  return sb.toString();
		}
	
	public static String httpGet(String urlStr,String paramStr) throws IOException {
		 long startTime = System.nanoTime();
		  URL url = new URL(urlStr);		  
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();
		  conn.setRequestMethod("GET");
		  conn.setDoOutput(true);
		  conn.setDoInput(true);
		  
		  OutputStream out = conn.getOutputStream();
		  Writer writer = new OutputStreamWriter(out, "UTF-8");
		 
		    writer.write(URLEncoder.encode(paramStr, "UTF-8"));
		  
		  writer.close();
		  out.close();
          
		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }
      
		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  long endTime = System.nanoTime();
		   logger.info("Time Taken for Check REST Operation: "+(((double)(endTime-startTime))/1000000000));

		  return sb.toString();
		}
	
	public static String httpPost(String urlStr, String[] paramName,
			String[] paramVal) throws Exception {
			  long startTime = System.nanoTime();
		      URL url = new URL(urlStr);
			  HttpURLConnection conn =
			      (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("POST");
			  conn.setDoOutput(true);
			  conn.setDoInput(true);
			  conn.setUseCaches(false);
			  conn.setAllowUserInteraction(false);
			  conn.setRequestProperty("Content-Type",
			      "application/x-www-form-urlencoded");

			  // Create the form content
			  OutputStream out = conn.getOutputStream();
			  Writer writer = new OutputStreamWriter(out, "UTF-8");
			  for (int i = 0; i < paramName.length; i++) {
			    writer.write(paramName[i]);
			    writer.write("=");
			    writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
			    writer.write("&");
			  }
			  writer.close();
			  out.close();

			  if (conn.getResponseCode() != 200) {
			    throw new IOException(conn.getResponseMessage());
			  }

			  // Buffer the result into a string
			  BufferedReader rd = new BufferedReader(
			      new InputStreamReader(conn.getInputStream()));
			  StringBuilder sb = new StringBuilder();
			  String line;
			  while ((line = rd.readLine()) != null) {
			    sb.append(line);
			  }
			  rd.close();

			  conn.disconnect();
			  
			  long endTime = System.nanoTime();
			   logger.info("Time Taken for Check REST Operation: "+(((double)(endTime-startTime))/1000000000));

			  return sb.toString();
			}

	
	public static String httpPost1(String urlStr, String[] paramName,
			String[] paramVal) throws Exception {
			  
		long startTime = System.nanoTime();
		      URL url = new URL(urlStr);
			  HttpURLConnection conn =
			      (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("POST");
			  conn.setDoOutput(true);
			  conn.setDoInput(true);
			  conn.setUseCaches(false);
			  conn.setAllowUserInteraction(false);
			  conn.setRequestProperty("Content-Type",
			      "application/x-www-form-urlencoded");

			  // Create the form content
			  OutputStream out = conn.getOutputStream();
			  Writer writer = new OutputStreamWriter(out, "UTF-8");
			  writer.write("{");
			  for (int i = 0; i < paramName.length; i++) {
			    writer.write('"'+ paramName[i]+'"');
			    writer.write(":");
			    writer.write('"'+paramVal[i]+'"');
			    //writer.write("&");
			  }
			  writer.write("}");
			  writer.close();
			  out.close();

			  if (conn.getResponseCode() != 200) {
			    throw new IOException(conn.getResponseMessage());
			  }

			  // Buffer the result into a string
			  BufferedReader rd = new BufferedReader(
			      new InputStreamReader(conn.getInputStream()));
			  StringBuilder sb = new StringBuilder();
			  String line;
			  while ((line = rd.readLine()) != null) {
			    sb.append(line);
			  }
			  rd.close();

			  conn.disconnect();
			  
			  long endTime = System.nanoTime();
			   logger.info("Time Taken for Check REST Operation: "+(((double)(endTime-startTime))/1000000000));

			  return sb.toString();
			}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Scanner scn = new Scanner(System.in);
		
		while(true){
			System.out.println("Reading....");
			String uri = scn.nextLine();
			//String param = scn.nextLine();
			try{
				String response = WifiOffloadRestClient.httpGet(uri);
				logger.info("RESPONSE: "+response);
				
			}
			catch(IOException e){
				logger.info(e.toString());
			}
		}
		
	}
	

}
