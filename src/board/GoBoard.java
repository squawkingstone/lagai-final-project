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

// Lots of this code is borrowed from or slightly modified from the Riddles.io Go
// engine. All credit to them for the simulation, we just cannibalized it to make the
// simulation work.

public class GoBoard implements kgs_mcts.Board {

    private int mFoundLiberties;
    private boolean[][] mAffectedFields;
    private boolean[][] mCheckedFields;
    private int[] stones;
    private int mNrAffectedFields;
    private Boolean mIsTerritory = false;
    String[][] board;
    int width;
    int height;
    int player;
    int moves;
    int max_moves;

    public GoBoard(String[] field, int field_height, int field_width, int player, int max)
    {
        board = new String[field_width][field_height];
        width = field_width;
        height = field_height;
        for (int i = 0; i < field.length; i++)
        {
            board[i % field_width][i / field_height] = field[i];
        }
        mAffectedFields = new boolean[256][256];
        mCheckedFields = new boolean[256][256];
        this.player = player;
        stones = new int[2];
        stones[0] = stones[1] = 0;
        moves = 0;
        max_moves = max;
    }

    private GoBoard(GoBoard dupe){
        this.board = new String[dupe.board.length][];
        this.mAffectedFields = new boolean[256][256];
        this.mCheckedFields = new boolean[256][256];
        for(int i = 0; i < dupe.board.length; i++){
            this.board[i] = Arrays.copyOf(dupe.board[i], dupe.board[i].length);
        }
        for(int i = 0; i < 256; i++){
            for(int j = 0; j < 256; j++){
                this.mAffectedFields[i][j] = dupe.mAffectedFields[i][j];
                this.mCheckedFields[i][j] = dupe.mCheckedFields[i][j];
            }
        }
        this.mFoundLiberties = dupe.mFoundLiberties;
        this.mNrAffectedFields = dupe.mNrAffectedFields;
        this.mIsTerritory = dupe.mIsTerritory;
        this.width = dupe.width;
        this.height = dupe.height;
        this.player = dupe.player;
        this.stones = dupe.stones.clone();
        this.moves = dupe.moves;
        this.max_moves = dupe.max_moves;
    }

    public void setRound(int round){
        this.moves = round;
    }

    @Override
    public Board duplicate() {
        return new GoBoard( this ); // return a copy of the thing
    }

    public void setBoard(GoBoard brd)
    {
        mFoundLiberties = brd.mFoundLiberties;
        mAffectedFields = brd.mAffectedFields;
        mCheckedFields = brd.mCheckedFields;
        stones = brd.stones;
        mNrAffectedFields = brd.mNrAffectedFields;
        mIsTerritory = brd.mIsTerritory;
        board = brd.board;
        width = brd.width;
        height = brd.height;
        player = brd.player;
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

    // Heavily modified version of GoLogic.transformPlaceMove from the Go engine
    @Override
    public void makeMove(Move move) {

        System.err.println("Calling makeMove");

        GoMove m = (GoMove) move;

        boolean move_error = false;

        GoBoard original = (GoBoard)this.duplicate();
        moves++;

        if(moves >= max_moves){
            move_error = true;
        }

        if (m.getX() > width || m.getY() > height || m.getX() < 0 || m.getY() < 0) {
            move_error = true;
        }

        if (!this.get(m.getX(), m.getY()).equals(".")) { /*Field is not available */
            move_error = true;
        }

        this.set(m.getX(), m.getY(), this.playerString());

        int stonesTaken = checkCaptures(this, player);
        stones[player] += stonesTaken;

        // snatch this
        if (!checkSuicideRule(m.getX(), m.getY(), this.playerString())) { /* Check Suicide Rule */
            move_error = true;
        }

        // Oh, this basically resets the board if something went wrong, rather than
        // checking if something went wrong before editing the board state. maybe
        // replace the exceptions with a bool
        if (move_error == true) {
            setBoard(original);
        }
    }

    private int checkCaptures(GoBoard board, int playerId) {
        int stonesTaken = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                String field = this.get(x, y);
                if (!field.equals(".") && !field.equals(String.valueOf(playerId))) {
                    mFoundLiberties = 0;
                    boolean[][] mark = new boolean[width][height];
                    for (int tx = 0; tx < height; tx++) {
                        for (int ty = 0; ty < width; ty++) {
                            mAffectedFields[tx][ty] = false;
                            mark[tx][ty] = false;
                        }
                    }
                    flood(mark, x, y, this.board[x][y], 0);
                    if (mFoundLiberties == 0) { /* Group starves */
                        for (int tx = 0; tx < height; tx++) {
                            for (int ty = 0; ty < width; ty++) {
                                if (mAffectedFields[tx][ty]) {
                                    board.set(tx, ty, ".");
                                    stonesTaken++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return stonesTaken;
    }

    private boolean checkSuicideRule(int x, int y, String move) {
        mFoundLiberties = 0;
        boolean[][] mark = new boolean[width][height];
        for (int tx = 0; tx < width; tx++) {
            for (int ty = 0; ty < height; ty++) {
                mAffectedFields[tx][ty] = false;
                mark[tx][ty] = false;
            }
        }
        flood(mark, x, y, move, 0);
        return (mFoundLiberties > 0);
    }

    public boolean isBoardFull() {
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                for (int playerId = 0; playerId <= 1; playerId++)
                    if (board[x][y].equals(".") &&
                            checkSuicideRule(x, y, String.valueOf(playerId)))
                        return false;
        // No move can be played
        return true;
    }

    // Returns player score according to Tromp-Taylor Rules
    public int calculateScore(int playerId) {
        int score = this.getPlayerStones(playerId);

        if (score <= 0) return 0;

        if (this.getPlayerStones(2 - (playerId + 1)) == 0) { // opponent stones == 0
            if (score <= 1) {
                return score;
            }

            return height * width;
        }

        /* Add empty points that reach only playerId color */
        boolean[][] mark = new boolean[width][height];
        mIsTerritory = false;
        mNrAffectedFields = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                mCheckedFields[x][y] = false;
            }
        }

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if (board[x][y].equals(".") && !mCheckedFields[x][y]) {
                    for (int tx = 0; tx < width; tx++) {
                        for (int ty = 0; ty < height; ty++) {
                            mAffectedFields[tx][ty] = false;
                            mark[tx][ty] = false;

                        }
                    }

                    mIsTerritory = true;
                    mNrAffectedFields = 0;
                    floodFindTerritory(mark, x, y, String.valueOf(playerId), 0);

                    if (mIsTerritory) {
                        score += mNrAffectedFields;
                        for (int tx = 0; tx < width; tx++) {
                            for (int ty = 0; ty < height; ty++) {
                                if (mAffectedFields[tx][ty]) {
                                    mCheckedFields[tx][ty] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return score;
    }

    public int getPlayerStones(int value) {
        int stones = 0;
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[x][y].equals(String.valueOf(value))) {
                    stones++;
                }
            }
        }
        return stones;
    }

    private void set(int x, int y, String s)
    {
        board[x][y] = s;
    }

    private String get(int x, int y)
    {
        return board[x][y];
    }

    private String playerString()
    {
        if (player == 0) return "0";
        return "1";
    }

    @Override
    public boolean gameOver() {
        System.err.println("GAME OVER");
        return isBoardFull() || (moves >= max_moves);
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
        int p1 = calculateScore(0);
        int p2 = calculateScore(1);

        double[] score = new double[2];

        if(p1 > p2){
            score[0] = 1.0;
            score[1] = 0.0;
        }else if(p2 > p1){
            score[0] = 0.0;
            score[1] = 1.0;
        }else{
            score[0] = 0.5;
            score[1] = 0.5;
        }

        return score;
    }

    @Override
    public double[] getMoveWeights() {
        int spaces = 0;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(board[i][j].equals(".") && !checkSuicideRule(i, j, String.valueOf(this.player))){
                    spaces++;
                }
            }
        }

        double[] weights = new double[spaces];
        for(int i = 0; i < spaces; i++){
            weights[i] = 1.0;
        }
        return weights;
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

    private void floodFindTerritory(boolean [][]mark, int x, int y, String srcColor, int stackCounter) {
        /* Strategy:
         * If edge other than (playerid or 0 or board edge) has been found, then no territory.
         */
        // Make sure row and col are inside the board
        if (x < 0) return;
        if (y < 0) return;
        if (x >= board.length) return;
        if (y >= board[0].length) return;

        // Make sure this field hasn't been visited yet
        if (mark[x][y]) return;

        // Make sure this field is the right color to fill
        if (!board[x][y].equals(".")) {
            if (!board[x][y].equals(srcColor)) {
                mIsTerritory = false;
            }
            return;
        }
        mAffectedFields[x][y] = true;
        // Mark field as visited
        mNrAffectedFields++;
        mark[x][y] = true;
        if (stackCounter < 1024) {
            floodFindTerritory(mark, x - 1, y , srcColor, stackCounter+1);
            floodFindTerritory(mark, x + 1, y , srcColor, stackCounter+1);
            floodFindTerritory(mark, x, y - 1 , srcColor, stackCounter+1);
            floodFindTerritory(mark, x , y + 1, srcColor, stackCounter+1);
        }
    }
}
