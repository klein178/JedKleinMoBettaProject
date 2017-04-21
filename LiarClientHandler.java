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
import java.util.Random;


public class LiarClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;
	private int[] player1Roll; 
	private int[] bets;
	private int invalidMove = 0;	
	private int[] player2Roll;
	private int order = 0;
	private int[] player1DiceCount;
	private int[] player2DiceCount;

	// Gloabl variables for the game


	LiarClientHandler(Socket sock, ArrayList<Socket> socketList, int[] player1Roll, int[] player2Roll, int[] bets, int order, int[] player1, int[] player2)
	{
		this.connectionSock = sock;
		this.socketList = socketList;
		this.player1Roll = player1Roll;
		this.player2Roll = player2Roll;
		this.bets = bets;
		this.order = order;
		this.player1DiceCount = player1;
		this.player2DiceCount = player2;
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
			for (Socket s : socketList)
			{
				DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
				if (s == connectionSock)
				{
					if (order == 1)
					{
						clientOutput.writeBytes("Your roll is: " + player1Roll[0] +", "+ player1Roll[1] +", "+ player1Roll[2] +", "+ player1Roll[3] +", "+ player1Roll[4] + "\n");
					}
					if (order == 2)
					{
						clientOutput.writeBytes("Your roll is: " + player2Roll[0] +", "+ player2Roll[1] +", "+ player2Roll[2] +", "+ player2Roll[3] +", "+ player2Roll[4] + "\n");
					}
				}
			}

			while (true)
			{
				// Get data sent from a client

				String clientText = clientInput.readLine();
				if (clientText != null)
				{
					
					String[] str = clientText.split(",");
					int diQuant = Integer.parseInt(str[0]);
					int diFace = Integer.parseInt(str[1]);

					if (diQuant > bets[0] && diFace <= 6)
					{
						bets[0] = diQuant;
						bets[1] = diFace;
						invalidMove = 0;
					}
			
					else if (diQuant == bets[0] && diFace > bets[1] && diFace <=6)
					{
						bets[0] = diQuant;
						bets[1] = diFace;
						invalidMove = 0;
					}

					else 
					{
						for (Socket s : socketList)
						{
							DataOutputStream checkBet = new DataOutputStream(s.getOutputStream());
							if (s == connectionSock)
							{
								DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
								if (diQuant != 0 && diFace != 0)
								{
									clientOutput.writeBytes("Invalid bet. must increase the number of die or the face value.\n");
									invalidMove = -1;
								}
							}
						}
					}

					System.out.println("Received: " + clientText);
					// Turn around and output this data
					// to all other clients except the one
					// that sent us this information
					for (Socket s : socketList)
					{
						if (s != connectionSock)
						{
							DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
							if (diQuant!= 0 && diFace != 0 && invalidMove != -1)
							{
								clientOutput.writeBytes("Opponent wagered " + diQuant + ": " + diFace + "s" + "\n");
	                      				 	//clientOutput.writeBytes(diQuant + "*" + diFace);
							}
							else if (diQuant == 0 && diFace ==0)
							{
								clientOutput.writeBytes("Opponent called bluff.\n");								
							}
						}
						if (diQuant == 0 && diFace ==0)
						{	
							DataOutputStream tellTheDice = new DataOutputStream(s.getOutputStream());
							tellTheDice.writeBytes("The dice rolled were: " + player1Roll[0] +", "+ player1Roll[1] +", "+ player1Roll[2] +", "+ player1Roll[3] +", "+ player1Roll[4]
							+", "+player2Roll[0] +", "+ player2Roll[1] +", "+ player2Roll[2] +", "+ player2Roll[3] +", "+ player2Roll[4] + "\n");
						}
					}
					if (diQuant == 0 && diFace ==0)	
					{
						int numCalled = 0;
						for(int i = 0; i < 5; ++i)
						{
								
							if (player1Roll[i] == bets[1])
							{
								numCalled += 1;
							}
							if (player2Roll[i] == bets[1])
							{
								numCalled += 1;
							}
						}
						System.out.println(numCalled);
						if (bets[0] > numCalled) 	//this client was right to call the bluff
						{
							if (order == 1)
							{
								player2DiceCount[0] -= 1;
							}
							if (order == 2)
							{
								player1DiceCount[0] -= 1;
							}
						}
						else if (bets[0] <= numCalled)	//this client was wrong to call the bluff
						{
							if (order == 2)
							{
								player2DiceCount[0] -= 1;
							}
							if (order == 1)
							{
								player1DiceCount[0] -= 1;
							}
						}
						System.out.println("player1: " + player1DiceCount[0] + "player2: " + player2DiceCount[0] + "\n");
						for (int i= 0; i < 5; ++i)
						{
							player1Roll[i] = 0;
						}
						for (int i= 0; i < 5; ++i)
						{
							player2Roll[i] = 0;
						}	
						if (order ==1)
						{
							for (Socket s : socketList)
							{
								if (s == connectionSock)
								{
									for (int i= 0; i < player1DiceCount[0]; ++i)
									{
										Random ran = new Random();
										player1Roll[i] = ran.nextInt(6)+1;
										System.out.println("player 1 dice roll: "+ player1Roll[i]+"\n");
									
									}
									DataOutputStream tellTheDice = new DataOutputStream(s.getOutputStream());
								tellTheDice.writeBytes("Your new roll is: " +player1Roll[0]+", "+player1Roll[1]+", "+player1Roll[2]+", "+player1Roll[3]+", "+player1Roll[4]+"\n");
								}
								if (s != connectionSock)
								{
								for (int i= 0; i < player2DiceCount[0]; ++i)
								{
									Random ran2 = new Random();
									player2Roll[i] = ran2.nextInt(6)+1;
									System.out.println("player 2 dice roll: "+ player2Roll[i]+"\n");
									
								}
								
								DataOutputStream tellTheDice = new DataOutputStream(s.getOutputStream());
								tellTheDice.writeBytes("Your new roll is: " +player2Roll[0]+", "+player2Roll[1]+", "+player2Roll[2]+", "+player2Roll[3]+", "+player2Roll[4]+"\n");
								}
							}
						}
						else if (order ==2)
						{
							for (Socket s : socketList)
							{
								if (s == connectionSock)
								{
								for (int i= 0; i < player2DiceCount[0]; ++i)
								{
									Random ran2 = new Random();
									player2Roll[i] = ran2.nextInt(6)+1;
									System.out.println("player 2 dice roll: "+ player2Roll[i]+"\n");
									
								}
								
								DataOutputStream tellTheDice = new DataOutputStream(s.getOutputStream());
								tellTheDice.writeBytes("Your new roll is: " +player2Roll[0]+", "+player2Roll[1]+", "+player2Roll[2]+", "+player2Roll[3]+", "+player2Roll[4]+"\n");
								}
								if (s != connectionSock)
								{
								for (int i= 0; i < player1DiceCount[0]; ++i)
								{
									Random ran2 = new Random();
									player1Roll[i] = ran2.nextInt(6)+1;
									System.out.println("player 1 dice roll: "+ player1Roll[i]+"\n");
									
								}
								
								DataOutputStream tellTheDice = new DataOutputStream(s.getOutputStream());
								tellTheDice.writeBytes("Your new roll is: " +player1Roll[0]+", "+player1Roll[1]+", "+player1Roll[2]+", "+player1Roll[3]+", "+player1Roll[4]+"\n");
								}
							}
						}								
						bets[0] = 0;
						bets[1] = 0;
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
