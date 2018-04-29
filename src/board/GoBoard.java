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
    int player;

    public GoBoard(String[] field, int field_height, int field_width, int player)
    {
        board = new String[field_width][field_height];
        for (int i = 0; i < field.length; i++)
        {
            board[i % field_width][i / field_height] = field[i];
        }
        this.player = player;
    }

    @Override
    public Board duplicate() {
        return null; // return a copy of the thing
    }

    @Override
    public ArrayList<Move> getMoves(CallLocation location) {
        return null;
    }

    // Should place the move on the board, do the captures, then X out all the Ko or
    //  Suicide move spots
    @Override
    public void makeMove(Move m) {

    }

    private void getCaptures() {

    }

    private void eliminateSuicideMoves() {
        // any time a move would result in a capture
    }

    private void eliminateKoMoves() {
        // basically, store the move two moves back and don't let them place that there
    }

    // Not sure how to implement this...
    @Override
    public boolean gameOver() {
        return false;
    }

    @Override
    public int getCurrentPlayer() {
        return player;
    }

    @Override
    public int getQuantityOfPlayers() {
        return 2;
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
