/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blobarena;

/**
 *
 * @author Remosewa
 */
public class Pos {

    public int row = 0;
    public int col = 0;

    public Pos(int r, int c) {
        row = r;
        col = c;
    }

    @Override
    public boolean equals(Object o) {
        Pos p = (Pos) o;
        if (p.row == row && p.col == col) {
            return true;
        } else {
            return false;
        }
    }
}
