  What I still need to accomplish:
 
 What I have accomplished so far:
  - block all invalid input
  - display a message when the game is over
  - ordering the clients as they connect to the server
  - check for valid user input
  - implement method to check dice when a user calls another users bluff
  - lots of updates to the client handler
  - display all the dice to both clients when someone calls a bluff
  - removing dice as players lose a round
  - rerolling the dice every round, allowing for multiple rounds to be played according to the rules.
  
Instructions for executing code:

- once in the directory via command prompt or terminal:
- javac *.java
- Run LiarServer
- (Seperate Window) Run LiarClient
- (Seperate Window) Run LiarClient
  
  This program implements a interface for playing Liars Dice.
 
  Liars Dice is played where every player rolls 5 dice, with the values known only to the player who rolled.
  The Players then take turns guessing how many of a face of the die is present in all the die combined, 
  including the dice of the other players, which are unknown values. The next player can either claim that there is
  a higher number of die, or keep the number the same and increase the number of the face of the die.
  For example, Player 1 says "four 5s"; Player 2 can either say "five 5s, (or any number greater than 5)", or
  "four 6s"
  If the player thinks the previous player is wrong/lying, they can call their bluff and if they are right, the first
  player loses a die. If the they are wrong, than the person calling the bluff loses a die.
 
  The game continues until only one player has die left.
 
  Authors: Jed Klein and Connor Ford
 

 
