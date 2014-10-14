package com.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class Proxy extends Thread
{
	private static final int C_BLOCK_SIZE = 16384;
	private Socket sockClient;
	private Socket sockServer;
	private InputStream isClient;
	private OutputStream osServer;
	private long sleepTime = 0L;
	private String sleepWhenFoundThisString = null;

	public String getSleepWhenFoundThisString() {
		return sleepWhenFoundThisString;
	}

	public void setSleepWhenFoundThisString(String sleepWhenFoundThisString) {
		this.sleepWhenFoundThisString = sleepWhenFoundThisString;
	}

	//Package level constructor only
	Proxy(Socket sockClient, Socket sockServer) throws IOException
	{
		this.sockClient = sockClient;
		this.sockServer = sockServer;
		this.isClient = this.sockClient.getInputStream();
		this.osServer = this.sockServer.getOutputStream();
	}

	public void run()
	{
		int bytesRead = -1;
		int blockSize = Proxy.C_BLOCK_SIZE;

		String sBlockSize = System.getProperty("BLOCKSIZE");
		if(sBlockSize != null)
		{
			try
			{
				blockSize = Integer.parseInt(sBlockSize);
			}
			catch(Exception e)
			{
				blockSize = Proxy.C_BLOCK_SIZE;
			}
		}

		System.out.println("\n\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> " +" Proxy from client address: " + this.sockClient.getInetAddress() + ", Local Port: " + this.sockClient.getLocalPort() + ", Remote Port: " + this.sockClient.getPort() + " setting internal buffer size to: " + blockSize);
		byte[] buf = new byte[blockSize];

		try
		{
			while((bytesRead = this.isClient.read(buf)) != -1)
			{
				this.log(buf, 0, bytesRead);
				String tempString = (new String(buf)).substring(0, bytesRead); 
				
				System.out.println("\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  + " Proxy read " + bytesRead + " bytes from " + this.sockClient.getInetAddress());
				
				
				if(sleepWhenFoundThisString == null || (sleepWhenFoundThisString != null && tempString.contains(sleepWhenFoundThisString)) )
				{
					System.out.println("\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  + " Proxy sleeping for " + sleepTime); 
					try
					{
						Thread.sleep(sleepTime);
					}
					catch(Exception ex)
					{
						
					}
					System.out.println("\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  + " Proxy resuming ");
				}
				
				this.osServer.write(buf, 0, bytesRead);
				this.osServer.flush();
				System.out.println("\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  + " Proxy read " + bytesRead + " bytes from " + this.sockClient.getInetAddress());
			}
			
		}
		catch (IOException e)
		{
			System.out.println("\n\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  + " Proxy from client address: " + this.sockClient.getInetAddress() + ", Local Port: " + this.sockClient.getLocalPort() + ", Remote Port: " + this.sockClient.getPort() + " encountered IOException: " + e);
		}
		System.out.println("\n\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  + " Proxy from client address: " + this.sockClient.getInetAddress() + ", Local Port: " + this.sockClient.getLocalPort() + ", Remote Port: " + this.sockClient.getPort() +  " is terminating");

		try{this.osServer.close();}catch(Exception ignore){}
		try{this.sockClient.close();}catch(Exception ignore){}
	}

	private void log(byte[] buf, int offset, int len)
	{
		if(buf != null)
		{
			System.out.println("\n@ " + (new Date()) + " <<Thead " + Thread.currentThread().getId() + ">> "  +  " Proxy read " + len + " bytes from " + this.sockClient.getInetAddress() + ", Local Port: " + this.sockClient.getLocalPort() + ", Remote Port: " + this.sockClient.getPort());
			System.out.println( " <<Thead " + Thread.currentThread().getId() + ">> " + "****************************************");
			for(int i = 0; i < len; i++)
			{
				if(buf[offset + i] >= 32 && buf[offset + i] < 127)
					System.out.print((char)buf[offset + i]);
				else
					System.out.print("[0x" + Integer.toHexString(buf[offset + i]) + "]");

			}
			System.out.println( " <<Thead " + Thread.currentThread().getId() + ">> "  + "\n****************************************");
		}
	}
	
	

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
}
