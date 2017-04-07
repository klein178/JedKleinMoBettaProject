/**
 * LiarClientHandler.java
 *
 * This program implements a interface for playing Liars Dice.
 *
 * Liars Dice is played where every player rolls 5 dice, with the values known only to the player who rolled.
 * The Players then take turns guessing how many of a face of the die is present in all the die combined,
 * including the dice of the other players, which are unknown values. The next player can either claim that there is
 * a higher number of die, or keep the number the same and increase the number of the face of the die.
 * For example, Player 1 says "four 5s"; Player 2 can either say "five 5s, (or any number greater than 5)", or
 * "four 6s"
 * If the player thinks the previous player is wrong/lying, they can call their bluff and if they are right, the first
 * player loses a die. If the they are wrong, than the person calling the bluff loses a die.
 *
 * The game continues until only one player has die left.
 *
 * Authors: Jed Klein and Connor Ford
 *
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.
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
				localOutput.writeBytes("Input the amount of dice you would like to roll in 1,3 format meaning there is one 3 faced di"
				+ "\n");

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
} // ClientHandler for LiarServer.java
