/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blobarena;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Remosewa
 */
public class EngineDriver implements Runnable {

    private volatile String move = "";
    private BufferedWriter writer = null;
    private String language;

    public EngineDriver(String language) {
        this.language = language;
    }

    public String requestMove() {
        if (move.trim().equals("")) {
            return null;
        } else {
            String cp = move;
            System.out.println("making move = " + move);
            move = "";
            return cp.trim();
        }
    }

    public void makeMove(String move) throws IOException {
        System.out.println("wrote move " + move);
        writer.write(move);
        writer.newLine();
        writer.flush();
    }

    private String requestMoveJava() {
        String javaloc = "C:\\Users\\Remosewa\\Documents\\NetBeansProjects\\BlobArena\\engines\\BlobSolver.jar";
        try {
            ProcessBuilder builder = new ProcessBuilder("java", "-jar", javaloc);
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader errorReader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            writer =
                    new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));

            String output = "";
            int c;
            while ((c = reader.read()) != -1) {
                char cc = (char) c;
                if (cc == '\n') {
                    move = output;
                    output = "";
                } else {
                    output += cc;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EngineDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private String requestMoveJavaScript() {
        String javaloc = "C:\\Users\\Remosewa\\Documents\\NetBeansProjects\\BlobArena\\engines\\SuperBlobSolver.js";
        try {
            ProcessBuilder builder = new ProcessBuilder("cscript", javaloc);
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader errorReader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            writer =
                    new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));

            String output = "";
            int c;
            while ((c = reader.read()) != -1) {
                char cc = (char) c;
                if (cc == '\n') {
                    move = output;
                    output = "";
                } else {
                    output += cc;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EngineDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private String requestMoveRuby() {
        String rubyloc = "C:\\Users\\Remosewa\\Documents\\NetBeansProjects\\BlobArena\\engines\\BlobSolver\\blob_solver.rb";
        try {
            System.out.println("ruby proc = " + "ruby " + rubyloc);
            ProcessBuilder builder = new ProcessBuilder("ruby", "-run",rubyloc);
            builder.redirectErrorStream(true);
            Process proc = builder.start();



            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            // BufferedReader errorReader =
            //        new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            writer =
                    new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            String output = "";
            int c;
            System.out.println("ruby while");
            while ((c = reader.read()) != -1) {
                char cc = (char) c;
                if (cc == '\n') {
                    move = output;
                    output = "";
                } else {
                    output += cc;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EngineDriver.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("crap");
        }
        System.out.println("returned");
        return "";
    }

    @Override
    public void run() {
        if (language.equals("java")) {
            System.out.println("running java engine");
            requestMoveJava();
        } else if(language.equals("javascript")) {
            System.out.println("running javascript engine");
            requestMoveJavaScript();
        }else{
            System.out.println("running ruby engine");
            requestMoveRuby();
        }
    }
}
