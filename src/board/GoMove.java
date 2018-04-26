package board;

import kgs_mcts.Move;

public class GoMove implements kgs_mcts.Move {
    private int x, y;

    public GoMove(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    @Override
    public int compareTo(Move o) {
        GoMove g = (GoMove) o;
        if(this.x == g.getX()){
            return this.x - g.getX();
        }
        return this.y - g.getY();
    }

    @Override
    public String toString(){
        return "place_move " + this.x + " " + this.y;
    }
}
