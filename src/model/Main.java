package model;

import model.*;

import java.util.List;
import java.util.Scanner;



// This file was created to test the execution of correct code.


public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        GameState gameState = new GameState();
        System.out.println("Welcome to Checkers!");
        System.out.println("BLACK goes first (pieces: B=normal, K=king)");
        System.out.println("RED pieces: R=normal, K=king");
        System.out.println("Enter moves as: fromRow fromCol toRow toCol (0-based)");
        System.out.println("Example: 2 1 3 0\n");

        while (!gameState.isGameOver()) {
            printBoard(gameState.getBoard());
            System.out.println("Turn: " + gameState.getCurrentTurn());
            System.out.println("Red captures: " + gameState.getRedScore() + ", Black captures: " + gameState.getBlackScore());

            List<Move> validMoves = GameLogic.getAllValidMovesForPlayer(
                    gameState.getBoard(),
                    gameState.getCurrentTurn()
            );

            if (validMoves.isEmpty()) {
                System.out.println("No valid moves! Game over.");
                break;
            }

            System.out.println("Valid moves: " + validMoves.size() + " (showing up to 5)");
            for (int i = 0; i < Math.min(5, validMoves.size()); i++) {
                Move m = validMoves.get(i);
                System.out.printf("  %d: (%d,%d) -> (%d,%d) %s%n",
                        i, m.getFromRow(), m.getFromCol(), m.getToRow(), m.getToCol(),
                        m.isCapture() ? "[CAPTURE]" : "");
            }
            if (validMoves.size() > 5)
                System.out.println("  ... and " + (validMoves.size()-5) + " more");

            Move selectedMove = null;
            while (selectedMove == null) {
                System.out.print("Your move (fromRow fromCol toRow toCol): ");
                try {
                    int fr = scanner.nextInt();
                    int fc = scanner.nextInt();
                    int tr = scanner.nextInt();
                    int tc = scanner.nextInt();
                    scanner.nextLine();

                    for (Move m : validMoves) {
                        if (m.getFromRow() == fr && m.getFromCol() == fc &&
                                m.getToRow() == tr && m.getToCol() == tc) {
                            selectedMove = m;
                            break;
                        }
                    }
                    if (selectedMove == null) {
                        System.out.println("Move not valid. Try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Enter 4 integers.");
                    scanner.nextLine();
                }
            }

            gameState = GameLogic.applyMove(gameState, selectedMove);
            System.out.println("Move executed.\n");
        }

        printBoard(gameState.getBoard());
        System.out.println("Game Over!");
        if (gameState.getWinner() != null)
            System.out.println("Winner: " + gameState.getWinner());
        else
            System.out.println("Draw!");
        System.out.println("Final scores - Red: " + gameState.getRedScore() + ", Black: " + gameState.getBlackScore());
        scanner.close();
    }

    private static void printBoard(Board board) {
        System.out.println("  Col: 0  1  2  3  4  5  6  7");
        for (int row = 0; row < 8; row++) {
            System.out.print("Row " + row + ": ");
            for (int col = 0; col < 8; col++) {
                Piece p = board.getPieceAt(row, col);
                char ch = '.';
                if (p != null) {
                    if (p.getColor() == Piece.Color.BLACK)
                        ch = p.isKing() ? 'K' : 'B';
                    else
                        ch = p.isKing() ? 'K' : 'R';
                }
                System.out.print(ch + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }
}