package bot;

import board.GoBoard;
import board.GoMove;
import kgs_mcts.FinalSelectionPolicy;
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
    private int max_rounds;
    private int round;

    public Bot()
    {
        mcts = new MCTS();
        mcts.setExplorationConstant(0.2);
        mcts.setTimeDisplay(false);
        mcts.setOptimisticBias(0.0);
        mcts.setPessimisticBias(0.0);
        mcts.setMoveSelectionPolicy(FinalSelectionPolicy.robustChild);
        mcts.enableRootParallelisation(1);
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
                    if (arg[1].equals("max_rounds")) max_rounds = Integer.parseInt(arg[2]);
                    break;
                case "update":
                    if (arg[1].equals("game") && arg[2].equals("field"))
                    {
                        board = new GoBoard(arg[3].split(",").clone(), 19, 19, player, max_rounds);
                        board.setRound(round);
                    }
                    if (arg[1].equals("game") && arg[2].equals("round"))
                    {
                        round = Integer.parseInt(arg[3]);
                    }
                    break;
                case "action":
                    System.err.println(board.getRound());
                    GoMove m = (GoMove)mcts.runMCTS_UCT(board, 20, false);
                    System.out.println(m.toString());
                    System.err.println(m.toString());
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