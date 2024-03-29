/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blobarena;

import java.util.LinkedList;

/**
 *
 * @author Remosewa
 */
public class Move {

    public Pos from = null;
    public Pos to = null;
    public int score = 0;
    public Board board = null;
    public LinkedList<Move> otherMoves = new LinkedList<>();
    public int team = 0;

    public Move(Pos from, Pos to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        Move m = (Move)o;
        if (m.from.equals(from) && m.to.equals(to)) {
            return true;
        } else {
            return false;
        }
    }
}
