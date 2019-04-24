package com.company;

import javax.swing.*;
import java.awt.*;

public class TicTacToe extends JFrame {

    private Button[][] boardButtons;
    private Button resetButton;
    private int board[][]= new int[3][3];
    private int cells[][] = new int[3][3];
    GridLayout bl = new GridLayout(4,3, 0,2);
    TextArea area, area2;

    int x = 50, y = 50;
    int turnCounter = 0, winCounter = 0, lossCounter = 0;

    boolean gameover = false;

    public TicTacToe(){
        super("TicTacToe");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(bl);
        setSize(600,600);
        setVisible(true);

        boardButtons=new Button[3][3];

        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 3; ++j){
                boardButtons[i][j] = new Button("");
                add(boardButtons[i][j]);
                boardButtons[i][j].setBackground(Color.darkGray);
                boardButtons[i][j].setFont(new Font("Tahoma", Font.BOLD, 100));
                board[i][j]=0;
                cells[i][j] = 1;
            }
        }

        area = new TextArea("Wins: " + "\n" + winCounter);
        area2 = new TextArea("Losses: " + "\n" + lossCounter);
        area.setFont(new Font("Tahoma", Font.BOLD, 40));
        area2.setFont(new Font("Tahoma", Font.BOLD, 40));
        resetButton = new Button("Reset");
        add(area2);
        add(resetButton);
        add(area);

    }

    void reset() {
        for(int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                boardButtons[i][j].setLabel("");
                boardButtons[i][j].setFont(new Font("Tahoma", Font.BOLD, 100));
                board[i][j] = 0;
            }
        }

        switchButtons(true);
        turnCounter = 0;
    }

    void clicked(int posX, int posY){
        if(board[posX][posY] == 0){
            ++turnCounter;

            if(turnCounter % 2 == 1){
                boardButtons[posX][posY].setLabel("X");
                boardButtons[posX][posY].setForeground(Color.BLACK);
                board[posX][posY] = 1;
            }

            else {
                boardButtons[posX][posY].setLabel("O");
                boardButtons[posX][posY].setForeground(Color.YELLOW);
                board[posX][posY] = 2;
            }

            checkIt(posX,posY);

            if(!gameover && turnCounter % 2 == 1)
                brain(2);
        }
    }

    void endGame(){
        switchButtons(false);
        if(turnCounter % 2 == 1) {
            for(int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    boardButtons[i][j].setFont(new Font("Tahoma", Font.BOLD, 30));
                    boardButtons[i][j].setLabel("VICTORY");
                }
            }
            ++winCounter;
            area.setText("Wins: " + "\n" + winCounter);
            JOptionPane.showMessageDialog(this, "You're a winner!");
        }

        else {
            for(int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    boardButtons[i][j].setFont(new Font("Tahoma", Font.BOLD, 30));
                    boardButtons[i][j].setLabel("DEFEAT");
                }
            }
            ++lossCounter;
            area2.setText("Losses: " + "\n" + lossCounter);
            JOptionPane.showMessageDialog(this, "Ooops, something went wrong. Please try again!");
        }
    }

    void draw(){
        switchButtons(false);
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                boardButtons[i][j].setFont(new Font("Tahoma", Font.PLAIN, 30));
                boardButtons[i][j].setLabel("DRAW");
            }
        }
        JOptionPane.showMessageDialog(this, "It's a draw!");
    }

    void switchButtons(boolean check){
        if(!check)
            gameover = true;
        else
            gameover = false;

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                boardButtons[i][j].setEnabled(check);
            }
        }
    }

    void brain(int depth){
        System.arraycopy(board, 0, cells, 0, board.length);
        int result[] = minimax(depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

        clicked(result[1], result[2]);
    }

    int[] minimax(int depth, boolean opponent, int alpha, int beta) {

        int move[][] = generateMoves();
        int player, score = 0, bestRow = -1, bestCol = -1;

        if(opponent)
            player = 2;
        else
            player = 1;


        if(depth == 0 || move.length == 0) {
            score = evaluate();
            gameover = false;

            return new int[] {score, bestRow, bestCol};

        }
        else {
            for(int i = 0; i < move.length; ++i){
                cells[move[i][0]][move[i][1]] = player;
                if(player == 2) {
                    score = minimax(depth - 1, false, alpha, beta)[0];
                    if(score > alpha) {
                        alpha = score;
                        bestRow = move[i][0];
                        bestCol = move[i][1];
                    }
                }
                else {
                    score = minimax(depth - 1, true, alpha, beta)[0];
                    if (score < beta) {
                        beta = score;
                    }
                }

                cells[move[i][0]][move[i][1]] = 0;

                if(alpha >= beta)
                    break;
            }

            if(player == 2)
                return new int[] {alpha, bestRow, bestCol};
            else
                return new int[] {beta, bestRow, bestCol};
        }
    }

    void checkIt(int posX, int posY){
        int checker = board[posX][posY];
        int countX = 0, countY = 0;
        int temp1 = 1, temp2 = 1, temp3 = 1, temp4 = 1;
        int completed = 0;

        for(int i = 1; i < 3; ++i){
            countX = posX + i;
            countY = posY + i;

            if(countY <= 2)
                if(checker == board[posX][countY])
                    ++temp1;

            if(countX <= 2)
                if(checker == board[countX][posY])
                    ++temp2;

            if(countX <= 2 && countY <= 2)
                if(checker == board[countX][countY])
                    ++temp3;

            countX = posX - i;

            if(countX >= 0 && countY <= 2)
                if(checker == board[countX][countY])
                    ++temp4;

            countY = posY - i;

            if(countY >= 0)
                if(checker == board[posX][countY])
                    ++temp1;

            if(countX >= 0)
                if(checker == board[countX][posY])
                    ++temp2;

            if(countX >= 0 && countY >= 0)
                if(checker == board[countX][countY])
                    ++temp3;

            countX = posX + i;

            if(countX <= 2 && countY >= 0)
                if(checker == board[countX][countY])
                    ++temp4;

            if(temp1 > 2 | temp2 > 2 | temp3 > 2 | temp4 > 2){
                endGame();

                return;
            }
        }

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if (board[i][j] > 0)
                    ++completed;
            }
        }

        if(completed == 9)
            draw();
    }

    int[][] generateMoves() {
        evaluate();

        if(gameover){
            gameover = false;
            return new int[0][0];
        }

        int counter = 0;
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if (cells[i][j] == 0)
                    ++counter;
            }
        }

        int[][] nextMoves = new int[counter][2];

        counter = 0;

        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 3; ++col) {
                if(cells[row][col] == 0) {
                    nextMoves[counter][0] = row;
                    nextMoves[counter][1] = col;

                    ++counter;
                }
            }
        }
        return nextMoves;
    }

    int evaluate(){
        int score = 0, p = 0, c = 0;
        int completed = 0;

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(cells[i][j] > 0)
                    ++completed;
            }
        }

        if(completed == 9)
            gameover = true;

        //Rows
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(cells[i][j] == 1)
                    p++;
                else if(cells[i][j] == 2)
                    c++;
            }
            score += calScore(p, c);
            p = c = 0;
        }

        //Columns
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(cells[j][i] == 1)
                    p++;
                else if(cells[j][i] == 2)
                    c++;
            }
            score += calScore(p, c);
            p = c = 0;
        }

        //Diagonal
        for(int i = 0; i < 3; ++i) {
            if(cells[i][2-i] == 1)
                p++;
            else if(cells[i][2-i] == 2)
                c++;
        }

        score += calScore(p, c);
        p = c = 0;

        //Still diagonal
        for(int i = 0; i < 3; ++i){
            if(cells[i][i] == 1)
                p++;
            else if(cells[i][i] == 2)
                c++;
        }
        score += calScore(p, c);
        p = c = 0;

        return score;
    }

    int calScore(int p, int c){
        if(p == 3) {
            gameover = true;
            return -1000;
        }
        else if(c == 3) {
            gameover = true;
            return 1000;
        }
        else if(p + c == 0)
            return 1;
        else if(p > 0 && c > 0)
            return 0;
        else if(p == 1 && c == 0)
            return -10;
        else if(p == 0 && c == 1)
            return 10;
        else if(p == 2 && c == 0)
            return -100;
        else if(p == 0 && c == 2)
            return 100;

        return 0;
    }

    public boolean action(Event event, Object object){
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(event.target == boardButtons[i][j]) {
                    x = i;  y = j;
                    clicked(x, y);
                }
            }
        }
        if(event.target == resetButton)
            reset();
        return true;
    }
}
