package board;

import kgs_mcts.Board;
import kgs_mcts.CallLocation;
import kgs_mcts.Move;

import java.util.ArrayList;

public class GoBoard implements kgs_mcts.Board {

    /* Functionality to simulate the board, for the MCTS */

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
