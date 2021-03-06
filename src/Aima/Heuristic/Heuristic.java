package Aima.Heuristic;

import Aima.RadikalChessGame;
import Aima.RadikalChessState;
import Model.ChessPiece;
import Model.Movement;
import Model.PieceMoveRange;
import Model.Player;
import java.util.ArrayList;

public abstract class Heuristic {

    public abstract double getHeuristic(RadikalChessState state, Player player);

    public double threatenedAdversarialPieces(RadikalChessState state, Player player) {
        //ArrayList<Movement> actions = new ArrayList<>();
        boolean whiteKingAlive = false, blackKingAlive = false;
        for (int i = 0; i < state.getChessBoard().getRow(); i++) {
            for (int j = 0; j < state.getChessBoard().getColumn(); j++) {
                if (state.getChessBoard().getCell()[i][j].getChessPiece() != null) {
                    if (state.getChessBoard().getCell()[i][j].getChessPiece().getName().equals("King")
                            && state.getChessBoard().getCell()[i][j].getChessPiece().getColour().equals("White")) {
                        whiteKingAlive = true;
                    }
                    if (state.getChessBoard().getCell()[i][j].getChessPiece().getName().equals("King")
                            && state.getChessBoard().getCell()[i][j].getChessPiece().getColour().equals("Black")) {
                        blackKingAlive = true;
                    }

                }
                /*if (state.getChessBoard().getCell()[i][j].getChessPiece().getColour().
                 equals(state.getPlayer().getPlayerName())){
                 actions.addAll(PieceMoveRange.getInstance().selectMove(state.getChessBoard().
                 getCell()[i][j].getChessPiece(), state));
                 for (Movement movement : actions) {
                 if (state.destinationCell(movement).getChessPiece() != null) {
                 threatValue += state.destinationCell(movement).getChessPiece().getValue();
                 }
                 }
                 } else {
                 RadikalChessState clonedState = state.clone();
                 clonedState.setPlayer((clonedState.getPlayer().getPlayerName().equals("White"))
                 ? new Player("Black") : new Player("White"));
                 actions.addAll(PieceMoveRange.getInstance().selectMove(state.getChessBoard().
                 getCell()[i][j].getChessPiece(), clonedState));
                 for (Movement movement : actions) {
                 if (state.destinationCell(movement).getChessPiece() != null) {
                 threatValue -= state.destinationCell(movement).getChessPiece().getValue();
                 }
                 }
                 }
                 }*/
                //actions.clear();
            }
        }
        if (whiteKingAlive && !blackKingAlive) {
            if (player.getPlayerName().equals("White")) {
                return 10000;
            } else if (player.getPlayerName().equals("Black")) {
                return -10000;
            }
        }
        //if (whiteKingAlive && !blackKingAlive && state.getPlayer().getPlayerName().equals("White")) {
        //    return -10000;
        //}
        if (!whiteKingAlive && blackKingAlive) {
            if (player.getPlayerName().equals("Black")) {
                return 10000;
            } else if (player.getPlayerName().equals("White")){
                return -10000;
            }
        }
        //if (!whiteKingAlive && blackKingAlive && state.getPlayer().getPlayerName().equals("Black")) {
        //    return -10000;
        //}
         return 0;
    }

    public double numberOfAttackedPieces(RadikalChessState state, Player player, ChessPiece chessPiece) {
        double threatValue = 0;
        ArrayList<Movement> actions = new ArrayList<>();
        for (int i = 0; i < state.getChessBoard().getRow(); i++) {
            for (int j = 0; j < state.getChessBoard().getColumn(); j++) {
                if (state.getChessBoard().getCell()[i][j].getChessPiece() != null) {
                    if (state.getChessBoard().getCell()[i][j].getChessPiece().getColour().
                            equals(state.getPlayer().getPlayerName())) {
                        actions.addAll(PieceMoveRange.getInstance().selectMove(state.getChessBoard().
                                getCell()[i][j].getChessPiece(), state));
                        for (Movement movement : actions) {
                            if (state.destinationCell(movement).getChessPiece() != null) {
                                if (state.destinationCell(movement).getChessPiece().getName().equals("King")) {
                                    return Double.POSITIVE_INFINITY;
                                }
                                threatValue += 1;
                            }
                        }
                    } else {
                        RadikalChessState clonedState = state.clone();
                        clonedState.setPlayer((clonedState.getPlayer().getPlayerName().equals("White"))
                                ? new Player("Black") : new Player("White"));
                        actions.addAll(PieceMoveRange.getInstance().selectMove(state.getChessBoard().
                                getCell()[i][j].getChessPiece(), clonedState));
                        for (Movement movement : actions) {
                            if (state.destinationCell(movement).getChessPiece() != null) {
                                if (state.destinationCell(movement).getChessPiece().getName().equals("King")) {
                                    return Double.NEGATIVE_INFINITY;
                                }
                                threatValue -= 1;
                            }
                        }
                    }
                }
                actions.clear();
            }
        }
        return threatValue;
    }
}