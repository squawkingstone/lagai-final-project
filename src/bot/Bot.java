package bot;

import board.GoBoard;
import kgs_mcts.MCTS;

import java.util.Scanner;

/* This is kind of working. I'm getting in the board and and printing out the next move
 * and it's currently pretty shite. But I now have a thing that basically works. I should
 * probably break up stuff into other components (for parsing, and MCTS training and
 * move decision, etc) but I'll leave it for now.
 */

// NOTES:
//  - The AI Workspace doesn't appear to implement Ko rule or Suicide rule '-1' board
//    tile thing, so I should implement something to at least handle this.

public class Bot {

    private Scanner scan = new Scanner(System.in);
    private GoBoard board;
    private MCTS mcts;
    private int player;

    public Bot()
    {
        mcts = new MCTS();
    }

    public void run()
    {
        while (scan.hasNextLine())
        {
            String line = scan.nextLine();

            if (line.length() == 0) continue;

            String[] arg = line.split(" ");
            switch (arg[0])
            {
                case "settings":
                    if (arg[1].equals("your_botid")) player = Integer.parseInt(arg[2]);
                    break;
                case "update":
                    if (arg[1].equals("game") && arg[2].equals("field"))
                    {
                        board = new GoBoard(arg[3].split(",").clone(), 19, 19, player);
                    }
                    System.err.println("WORM");
                    break;
                case "action":
                    System.err.println("Actually running");
                    System.out.println(mcts.runMCTS_UCT(board, 1, true).toString());
                    System.err.println("Done MCTS");
                    break;
                default:
                    System.err.println("ERROR: Cmd not found \"" + line + "\"");
            }

        }
    }

    public static void main(String[] args) {
        (new Bot()).run();
    }

};