/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blobarena;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Remosewa
 */
public class Player implements Runnable {

    private EngineDriver ed = new EngineDriver("java");
    //private EngineDriver ed2 = new EngineDriver("javascript");
    private EngineDriver ed2 = new EngineDriver("ruby");
    public volatile Board board = null;

    public Player() {
        board = new Board();
        newBoard();
        Thread t = new Thread(ed);
        t.start();
        Thread t2 = new Thread(ed2);
        t2.start();
    }

    @Override
    public void run() {
        playSolo();
    }

    public void playSolo() {
        while (true) {
            String move;
            while ((move = ed.requestMove()) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String[] elems = move.split(",");
            Pos movf = new Pos(Integer.parseInt(elems[0]), Integer.parseInt(elems[1]));
            Pos movt = new Pos(Integer.parseInt(elems[2]), Integer.parseInt(elems[3]));
            board.makeMove(movf, movt, 0, false);
            try {
                ed2.makeMove(movf.row + "," + movf.col + "," + movt.row + "," + movt.col);
            } catch (IOException ex) {
                Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            while ((move = ed2.requestMove()) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            elems = move.split(",");
            movf = new Pos(Integer.parseInt(elems[0]), Integer.parseInt(elems[1]));
            movt = new Pos(Integer.parseInt(elems[2]), Integer.parseInt(elems[3]));
            board.makeMove(movf, movt, 1, false);
            try {
                ed.makeMove(movf.row + "," + movf.col + "," + movt.row + "," + movt.col);
            } catch (IOException ex) {
                Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void newBoard() {
        board.addBlob(new Pos(0, 0), 0);
        board.addBlob(new Pos(0, 7), 0);
        board.addBlob(new Pos(7, 0), 1);
        board.addBlob(new Pos(7, 7), 1);
    }
}
