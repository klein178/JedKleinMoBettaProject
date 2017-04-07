/**
 * NimClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class LiarClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;

	// Gloabl variables for the game


	LiarClientHandler(Socket sock, ArrayList<Socket> socketList)
	{
		this.connectionSock = sock;
		this.socketList = socketList;
	}

	public void run()
	{
    // Get data from a client and send it to everyone else
		try
		{
			System.out.println("Connection made with socket " + connectionSock + "\n");
			BufferedReader clientInput = new BufferedReader(
				new InputStreamReader(connectionSock.getInputStream()));

				DataOutputStream localOutput = new DataOutputStream(connectionSock.getOutputStream());

				localOutput.writeBytes("Welcome to Liar's Dice!" +"\n");

			while (true)
			{
				// Get data sent from a client

				String clientText = clientInput.readLine();
				if (clientText != null)
				{

					String[] str = clientText.split(",");
					int diQuant = Integer.parseInt(str[0]);
					int diFace = Integer.parseInt(str[1]);

					System.out.println("Received: " + clientText);
					// Turn around and output this data
					// to all other clients except the one
					// that sent us this information
					for (Socket s : socketList)
					{
						if (s != connectionSock)
						{
							DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());

							clientOutput.writeBytes("Opponent wagered " + diQuant + ": " + diFace + "s" + "\n");
						}
					}
				}
				else
				{
				  // Connection was lost
				  System.out.println("Closing connection for socket " + connectionSock);
				   // Remove from arraylist
				   socketList.remove(connectionSock);
				   connectionSock.close();
				   break;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}
} // ClientHandler for NimServer.java
