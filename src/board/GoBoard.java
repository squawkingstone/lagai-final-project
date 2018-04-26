package board;

import kgs_mcts.Board;
import kgs_mcts.CallLocation;
import kgs_mcts.Move;

import java.util.ArrayList;

/* Functionality to simulate the board, for the MCTS */

/* TODO:
 *  - Implement getMoves (should be pretty easy)
 *  - Implement scoring (have to implement Komi)
 *  - Implement Suicide Rule and Ko Rule detection
 */

public class GoBoard implements kgs_mcts.Board {

    String[][] board;

    public GoBoard(String[] field, int field_height, int field_width)
    {
        board = new String[field_width][field_height];
        for (int i = 0; i < field.length; i++)
        {
            board[i % field_width][i / field_height] = field[i];
        }
    }

    @Override
    public Board duplicate() {
        return null;
    }

    @Override
    public ArrayList<Move> getMoves(CallLocation location) {
        return null;
    }

    @Override
    public void makeMove(Move m) {

    }

    @Override
    public boolean gameOver() {
        return false;
    }

    @Override
    public int getCurrentPlayer() {
        return 0;
    }

    @Override
    public int getQuantityOfPlayers() {
        return 0;
    }

    @Override
    public double[] getScore() {
        return new double[0];
    }

    @Override
    public double[] getMoveWeights() {
        return new double[0];
    }

    @Override
    public void bPrint() {

    }

}
