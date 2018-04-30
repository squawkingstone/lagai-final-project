package board;

import kgs_mcts.Board;
import kgs_mcts.CallLocation;
import kgs_mcts.Move;
import java.util.Arrays;
import java.util.ArrayList;

/* Functionality to simulate the board, for the MCTS */

/* TODO:
 *  - Implement getMoves (should be pretty easy)
 *  - Implement scoring (have to implement Komi)
 *  - Implement Suicide Rule and Ko Rule detection
 */

public class GoBoard implements kgs_mcts.Board {

    private int mFoundLiberties;
    private boolean[][] mAffectedFields;
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

    private GoBoard(String[][] b, int p){
        this.board = new String[b.length][];
        for(int i = 0; i < b.length; i++){
            this.board[i] = Arrays.copyOf(b[i], b[i].length);
        }
        this.player = p;
    }

    @Override
    public Board duplicate() {
        return new GoBoard(this.board, this.player); // return a copy of the thing
    }

    @Override
    public ArrayList<Move> getMoves(CallLocation location) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j].equals(".")){
                    moves.add(new GoMove(i, j));
                }
            }
        }
        return moves;
    }

    // Should place the move on the board, do the captures, then X out all the Ko or
    //  Suicide move spots
    @Override
    public void makeMove(Move m) {
        GoMove move = (GoMove) m;
        String piece;
        if(this.player == 0){
            piece = "0";
        }else{
            piece = "1";
        }

        if(this.board[move.getX()][move.getY()].equals(".")) {
            this.board[move.getX()][move.getY()] = piece;
        }
    }

    private Boolean checkSuicideRule(int x, int y, String move) {
        mFoundLiberties = 0;
        boolean[][] mark = new boolean[this.board.length][this.board[0].length];
        for (int tx = 0; tx < this.board.length; tx++) {
            for (int ty = 0; ty < this.board[tx].length; ty++) {
                mAffectedFields[tx][ty] = false;
                mark[tx][ty] = false;
            }
        }
        flood(mark, x, y, move, 0);
        return (mFoundLiberties > 0);
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
        String r = "";
        int counter = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (counter > 0) {
                    r += ",";
                }
                r += board[x][y];
                counter++;
            }
        }
        System.out.println(r);
    }

    private void flood(boolean [][]mark, int x, int y, String srcColor, int stackCounter) {
        // Make sure row and col are inside the board
        if (x < 0) return;
        if (y < 0) return;
        if (x >= this.board.length) return;
        if (y >= this.board[0].length) return;

        // Make sure this field hasn't been visited yet
        if (mark[x][y]) return;

        // Make sure this field is the right color to fill
        if (!board[x][y].equals(srcColor)) {
            if (this.board[x][y].equals(".")) {
                mFoundLiberties++;
            }
            return;
        }
        // Fill field with target color and mark it as visited
        this.mAffectedFields[x][y] = true;
        mark[x][y] = true;

        // Recursively check surrounding fields
        if (stackCounter < 1024) {
            flood(mark, x - 1, y, srcColor, stackCounter+1);
            flood(mark, x + 1, y, srcColor, stackCounter+1);
            flood(mark, x, y - 1, srcColor, stackCounter+1);
            flood(mark, x, y + 1, srcColor, stackCounter+1);
        }
    }
}
