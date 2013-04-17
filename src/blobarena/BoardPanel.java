/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blobarena;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author Remosewa
 */
public class BoardPanel extends javax.swing.JPanel implements ActionListener {

    private Board board = null;
    private Integer[] selection = new Integer[2];
    private Player player;
    private MainWindow mainWindow;
private EngineDriver ed = new EngineDriver("javascript");
    public BoardPanel(Dimension d, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setPreferredSize(d);
        this.setBackground(Color.WHITE);
        this.setVisible(true);
        board = new Board();
        clearSel();
        newBoard();
        player = new Player();
        /*Thread t1 = new Thread(ed);
        t1.start();*/
    }
    //timer 
    public void actionPerformed(ActionEvent e){
        board = player.board;
        this.repaint();
        mainWindow.updateScores(board.getMyBlobs(0).size(), board.getMyBlobs(1).size());
    }

    public void playSolo() {
        Timer timer = new Timer(100,this);
        timer.start();
        Thread t1 = new Thread(player);
        t1.start();
    }

    public void clearSel() {
        selection[0] = -1;
        selection[1] = -1;
    }

    private void newBoard() {
        board.addBlob(new Pos(0, 0), 0);
        board.addBlob(new Pos(0, 7), 0);
        board.addBlob(new Pos(7, 0), 1);
        board.addBlob(new Pos(7, 7), 1);
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int colinc = this.getWidth() / 8;
        int rowinc = this.getHeight() / 8;
        int r = evt.getY() / rowinc;
        int c = evt.getX() / colinc;
        System.out.println("(" + r + "," + c + ")");
        if (board.getSpot(new Pos(r, c)) != null && board.getSpot(new Pos(r, c)).team == 1) {
            selection[0] = r;
            selection[1] = c;
        } else if (selection[0] != -1) {

            Pos from = new Pos(selection[0], selection[1]);
            Pos to = new Pos(r, c);
            if (board.canMoveTo(to) && board.getDistance(from, to) < 3) {
                board.makeMove(from, to, board.getSpot(from).team, false);
                System.out.println("valid move");
                this.repaint();
                clearSel();
                this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
                try {
                    ed.makeMove(from.row + "," + from.col + "," + to.row + "," + to.col);
                } catch (IOException ex) {
                    Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                String move;
                while ((move = ed.requestMove()) == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                String[] elems = move.split(",");
                System.out.println(elems[3]);
                Pos movf = new Pos(Integer.parseInt(elems[0]), Integer.parseInt(elems[1]));
                Pos movt = new Pos(Integer.parseInt(elems[2]), Integer.parseInt(elems[3]));
                board.makeMove(movf, movt, 0, false);

            }
            clearSel();
        }
        this.repaint();

    }

    public void updateBoard(Graphics g) {
        for (int r = 0; r < 8; r += 1) {
            for (int c = 0; c < 8; c += 1) {
                Blob b = board.getSpot(new Pos(r, c));
                if (b != null) {
                    g.setColor(new Color(255, 100, 150));
                    if (b.team == 0) {
                        g.setColor(new Color(0, 0, 255));
                    }
                    int xinc = getWidth() / 8;
                    int yinc = getHeight() / 8;
                    g.fillOval((c * yinc), (r * yinc), xinc, xinc);
                    //g.fillOval(c, c, yinc, yinc);
                }
            }
        }

    }

    private void draw_selection(Graphics g) {
        int xinc = getWidth() / 8;
        int yinc = getHeight() / 8;
        System.out.println("drawing");

        g.setColor(Color.lightGray);
        for (int r = selection[0] - 2; r <= selection[0] + 2; r += 1) {
            for (int c = selection[1] - 2; c <= selection[1] + 2; c += 1) {
                if (r >= 0 && c >= 0) {
                    g.fillRect(xinc * c, yinc * r, yinc, yinc);
                }
            }
        }
        g.setColor(Color.darkGray);
        for (int r = selection[0] - 1; r <= selection[0] + 1; r += 1) {
            for (int c = selection[1] - 1; c <= selection[1] + 1; c += 1) {
                if (r >= 0 && c >= 0) {
                    g.fillRect(xinc * c, yinc * r, yinc, yinc);
                }
            }
        }
        g.setColor(Color.BLACK);
        g.fillRect(xinc * selection[1], yinc * selection[0], yinc, yinc);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (selection[0] != -1) {
            draw_selection(g);
        }
        g.setColor(Color.black);
        for (int i = 0; i <= this.getWidth(); i += this.getWidth() / 8) {
            g.drawLine(i, 0, i, this.getHeight());
        }
        for (int i = 0; i <= this.getHeight(); i += this.getHeight() / 8) {
            g.drawLine(0, i, this.getWidth(), i);
        }
        updateBoard(g);

        //System.out.println(this.getHeight());
    }
}
