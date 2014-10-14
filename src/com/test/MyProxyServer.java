package com.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyProxyServer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int localPort = 0;
		int remotePort = 0;
		String remoteHost = null;
		long sleepTime = 0;
		String sleepString = null;
		
		if(args.length < 4)
		{
			System.out.println("USAGE: MyProxyServer localPort remoteHost remotePort sleepTime <SleepWhenThisStringMatches>");
			return;
		}
		
		try
		{
			localPort = Integer.parseInt(args[0]);
			remotePort = Integer.parseInt(args[2]);
			remoteHost = args[1];
			sleepTime = Long.parseLong(args[3]);
			if(args.length > 4)
			{
				sleepString = args[4];
			}
			System.out.println("Passed values "
					+ "\nLocal port: [" + localPort +"] "
					+ "\nRemote Host: [" + remoteHost +"] "
					+ "\nRemote port: [" + remotePort +"] "
					+ "\nSleep Time: [" + sleepTime +"] "
					+ "\nSleep String: [" + sleepString +"] ");
		}
		catch(NumberFormatException exNumFormat)
		{
			System.out.println("Error starting proxy server due to invalid formatted argument: " + exNumFormat);
			return;
		}
		
		try
		{
			ServerSocket listener = new ServerSocket(localPort);
			System.out.println("MyProxyServer waiting for connection on port: " + localPort);
			
			while(true)
			{
    			Socket sockClient = listener.accept();
    			Socket sockRemote = new Socket(remoteHost, remotePort);
    			
    			Proxy proxyClient = new Proxy(sockClient, sockRemote);
    			proxyClient.setSleepTime(sleepTime);
    			proxyClient.setSleepWhenFoundThisString(sleepString);
    			Proxy proxyServer = new Proxy(sockRemote, sockClient);
    			proxyServer.setSleepTime(sleepTime);
    			proxyServer.setSleepWhenFoundThisString(sleepString);
    			
    			proxyClient.start();
    			proxyServer.start();
			}
		} 
		catch (IOException e)
		{
			System.out.println("Error starting proxy server: " + e);
			return;
		}
	}
}
