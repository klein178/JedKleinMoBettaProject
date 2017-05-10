/**
 * LiarServer.java
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
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class LiarServer
{
	// Maintain list of all client sockets for broadcast
	private ArrayList<Socket> socketList;	
	private int bets[] = new int[2];
	private int player1Roll[] = new int[5];
	private int player2Roll[] = new int[5];
	private int player1DiceCount[] = new int[1];
	private int player2DiceCount[] = new int[1];

 
	public LiarServer()
	{
		socketList = new ArrayList<Socket>();
	}

	private void getConnection()
	{
		player1DiceCount[0] = 5;
		player2DiceCount[0] = 5;
		for (int i = 0; i < player1Roll.length; ++i)
		{
			Random ran = new Random();
			player1Roll[i] = ran.nextInt(6) +1;
		}
		for (int i = 0; i < player2Roll.length; ++i)
		{
			Random ran = new Random();
			player2Roll[i] = ran.nextInt(6) +1;
		}
		// Wait for a connection from the client
		try
		{
			System.out.println("Waiting for client connections on port 7654.");
			ServerSocket serverSock = new ServerSocket(7654);
			int connectionCount = 0;

			Socket Client1 = null;
			Socket Client2 = null;

			// This is an infinite loop, the user will have to shut it down
			// using control-c

			while (true)
			{
				if (connectionCount < 2)
				{ 
					Socket connectionSock = serverSock.accept();
					// Add this socket to the list
					socketList.add(connectionSock);
					connectionCount++;
					
					if (connectionCount ==1)
					{
						try
						{
							TimeUnit.SECONDS.sleep(1);
						}
						catch (InterruptedException e){}

						Client1 = connectionSock;
						DataOutputStream Client1Output = new DataOutputStream(connectionSock.getOutputStream());
						Client1Output.writeBytes("You are first! Please wait for Player 2 to connect.\n");
					}
					else if (connectionCount ==2)
					{
						try
						{
							TimeUnit.SECONDS.sleep(1);
						}
						catch (InterruptedException e){}

						Client2 = connectionSock;
						DataOutputStream Client2Output = new DataOutputStream(connectionSock.getOutputStream());
						Client2Output.writeBytes("You are second! Please wait for player 1's move...\n");

						DataOutputStream Client1Output = new DataOutputStream(Client1.getOutputStream());
						Client1Output.writeBytes("Player 2 has connected. Make the first move. . .\n");
					}
				


					// Send to ClientHandler the socket and arraylist of all sockets
					LiarClientHandler handler = new LiarClientHandler(connectionSock, this.socketList, this.player1Roll, this.player2Roll, this.bets, connectionCount, this.player1DiceCount, 						this.player2DiceCount);
					Thread theThread = new Thread(handler);
					theThread.start();
				}
			}
			// Will never get here, but if the above loop is given
			// an exit condition then we'll go ahead and close the socket
			//serverSock.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args)
	{
		LiarServer server = new LiarServer();
		server.getConnection();
	}
} // LiarServer
