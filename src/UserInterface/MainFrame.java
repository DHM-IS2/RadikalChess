package UserInterface;

import Aima.RadikalChessGame;
import Aima.RadikalChessState;
import Aima.Search.AdversarialSearch;
import Aima.Search.MinimaxSearch;
import Model.Cell;
import Model.ChessBoard;
import Model.ChessPiece;
import Model.Image;
import Model.Movement;
import Model.Player;
import Model.Position;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainFrame extends JFrame {

    private final ArrayList<ChessPiece> whiteChessPieces, blackChessPieces, allChessPieces;
    private int row = 6;
    private int column = 4;
    private int numberOfMovementsSearch = 0;
    private int numberOfMovements = 0;
    private boolean buttonPressed;
    private ChessBoardPanel boardPanel;
    private CellButton firstClicked;
    private Player player = new Player("White");
    private RadikalChessState currentState;
    private RadikalChessGame radikalChessGame = new RadikalChessGame();
    private JTextField nodesExpanded, time, pathCost;
    private JTextArea movements;

    public MainFrame(ArrayList<ChessPiece> whiteChessPieces,
            ArrayList<ChessPiece> blackChessPieces,
            ArrayList<ChessPiece> allChessPieces) {
        this.whiteChessPieces = whiteChessPieces;
        this.blackChessPieces = blackChessPieces;
        this.allChessPieces = allChessPieces;
        this.setTitle("RadikalChess");
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.createComponent();
        fillBoard();
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void createComponent() {
        this.add(createToolbar(), BorderLayout.NORTH);
        this.add(createBoardPanel(), BorderLayout.CENTER);
        this.add(createMovementsPanel(), BorderLayout.EAST);
        this.add(createResult(), BorderLayout.SOUTH);
    }

    private JPanel createToolbar() {
        JPanel panel = new JPanel();
        panel.add(createDifficulty());
        panel.add(createAlgorithm());
        panel.add(createResetButton());
        panel.add(createProposeMoveButton());
        return panel;
    }

    private JComboBox createDifficulty() {
        final JComboBox difficulty = new JComboBox(new String[]{"Easy", "Medium", "Hard"});
        difficulty.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                if (difficulty.getSelectedItem().equals("Easy")) {
                    if (difficulty.getSelectedItem().equals("Medium")) {
                        if (difficulty.getSelectedItem().equals("Hard")) {
                            return;
                        }
                    }
                }
            }
        });
        return difficulty;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    private JComboBox createAlgorithm() {
        final JComboBox algorithm = new JComboBox(new String[]{"MinimaxSearch", "AlphaBetaSearch"});
        algorithm.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                if (algorithm.getSelectedItem().equals("MinimaxSearch")) {
                    if (algorithm.getSelectedItem().equals("AlphaBetaSearch")) {
                        return;
                    }
                }
            }
        });
        return algorithm;
    }

    private JButton createResetButton() {
        JButton reset = new JButton("Reset");
        return reset;
    }

    private JButton createProposeMoveButton() {
        JButton proposeMove = new JButton("Propose Move");
        proposeMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!radikalChessGame.isTerminal(currentState)) {
                    AdversarialSearch<RadikalChessState, Movement> search;
                    Movement action;
                    search = MinimaxSearch.createFor(radikalChessGame);
                    Player actualPlayer = new Player(currentState.getPlayer().getPlayerName());
                    action = search.makeDecision(currentState);
                    currentState.setPlayer(actualPlayer);
                    currentState.mark(action);
                    numberOfMovementsSearch++;
                    numberOfMovements++;
                    boardPanel.updateChessPiece(createMovement(action.getOrigin(), action.getDestination()));
                    updateStatistics(search);
                    updateMovement(action);
                    try {
                        boardPanel.checkPromotionedPawn(createMovement(action.getOrigin(), action.getDestination()),
                                allChessPieces,
                                currentState);
                    } catch (IOException ex) {
                    }
                }
            }
        });
        return proposeMove;
    }

    private JPanel createMovementsPanel() {
        JPanel movementsPanel = new JPanel();
        movements = new JTextArea(26, 26);
        movements.setLineWrap(true);
        movements.setWrapStyleWord(true);
        movements.setEditable(false);
        movements.setText("Historial of moves [Row, Column]:\n");
        JScrollPane scrollPane = new JScrollPane(movements);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        movementsPanel.add(scrollPane);
        return movementsPanel;
    }

    private ChessBoardPanel createBoardPanel() {
        boardPanel = new ChessBoardPanel(row, column);
        boardPanel.setLayout(new GridLayout(row, column));
        createCellButton();
        loadImages();
        return boardPanel;
    }

    public void createCellButton() {
        boolean blackFirst = true;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                CellButton cell = new CellButton(null, new Position(i, j));
                paintCell(blackFirst, j, cell);
                boardPanel.getBoard()[i][j] = cell;
                boardPanel.getBoard()[i][j].addActionListener(new ActionListener() {
                    private CellButton secondClicked;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object source = e.getSource();
                        if (source instanceof CellButton) {
                            if (!radikalChessGame.isTerminal(currentState)) {
                                if (buttonPressed) {
                                    secondClicked = (CellButton) e.getSource();
                                    if (!firstClicked.getCell().getPosition().equals(
                                            secondClicked.getCell().getPosition())) {
                                        if (currentState.possibleMove(createMovement(firstClicked.getCell().getPosition(), secondClicked.getCell().getPosition()))) {
                                            boardPanel.updateChessPiece(createMovement(firstClicked.getCell().getPosition(), secondClicked.getCell().getPosition()));
                                            numberOfMovements++;
                                            updateMovement(createMovement(firstClicked.getCell().getPosition(), secondClicked.getCell().getPosition()));
                                        }
                                        try {
                                            boardPanel.checkPromotionedPawn(createMovement(firstClicked.getCell().getPosition(), secondClicked.getCell().getPosition()), allChessPieces,
                                                    currentState);
                                        } catch (IOException ex) {
                                        }
                                    }
                                    buttonPressed = false;
                                } else if (((CellButton) e.getSource()).getCell().getChessPiece() != null) {
                                    buttonPressed = true;
                                    firstClicked = (CellButton) e.getSource();
                                }
                            }
                        }
                    }
                });
                boardPanel.add(cell);
            }
            blackFirst = !blackFirst;
        }
        placePieces();
    }

    private void placePieces() {
        for (ChessPiece chessPiece : whiteChessPieces) {
            boardPanel.getBoard()[chessPiece.getPosition().getRow()][chessPiece.getPosition().getColumn()].getCell().setChessPiece(chessPiece);
        }
        for (ChessPiece chessPiece : blackChessPieces) {
            boardPanel.getBoard()[chessPiece.getPosition().getRow()][chessPiece.getPosition().getColumn()].getCell().setChessPiece(chessPiece);
        }
    }

    private void paintCell(boolean blackFirst, int j, CellButton cell) {
        if (blackFirst) {
            if (j % 2 == 0) {
                cell.setBackground(Color.DARK_GRAY);
            } else {
                cell.setBackground(Color.WHITE);
            }
        } else {
            if (j % 2 == 0) {
                cell.setBackground(Color.WHITE);
            } else {
                cell.setBackground(Color.DARK_GRAY);
            }
        }
    }

    private void loadImages() {
        for (ChessPiece chessPiece : whiteChessPieces) {
            boardPanel.getBoard()[chessPiece.getPosition().getRow()][chessPiece.getPosition().getColumn()].setIcon(
                    convertImageToImageIcon(chessPiece.getImage()));
        }
        for (ChessPiece chessPiece : blackChessPieces) {
            boardPanel.getBoard()[chessPiece.getPosition().getRow()][chessPiece.getPosition().getColumn()].setIcon(
                    convertImageToImageIcon(chessPiece.getImage()));
        }
    }

    private Icon convertImageToImageIcon(Image image) {
        return new ImageIcon(((SwingBitmap) image.getBitmap()).getBufferedImage());
    }

    private void fillBoard() {
        ChessBoard chessBoard = new ChessBoard(row, column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                chessBoard.getCell()[i][j] = new Cell(boardPanel.getBoard()[i][j].getCell().getChessPiece(), new Position(i, j));
            }
        }
        currentState = new RadikalChessState(chessBoard, player);
    }

    private Movement createMovement(Position origin, Position destination) {
        return new Movement(origin, destination);
    }

    private JPanel createResult() {
        JPanel message = new JPanel();
        message.add(createNodesExpandedPanel(), FlowLayout.LEFT);
        message.add(createNodesExaminePanel(), FlowLayout.LEFT);
        message.add(createPathCostPanel(), FlowLayout.RIGHT);
        return message;
    }

    private JPanel createNodesExpandedPanel() {
        JPanel resultNodesExpanded = new JPanel();
        nodesExpanded = new JTextField(5);
        nodesExpanded.setEditable(false);
        resultNodesExpanded.setLayout(new FlowLayout(FlowLayout.LEFT));
        resultNodesExpanded.add(new JLabel("Number of expanded nodes:"));
        resultNodesExpanded.add(nodesExpanded);
        return resultNodesExpanded;
    }

    private JPanel createNodesExaminePanel() {
        JPanel resultNodesExamine = new JPanel();
        time = new JTextField(5);
        time.setEditable(false);
        resultNodesExamine.setLayout(new FlowLayout(FlowLayout.LEFT));
        resultNodesExamine.add(new JLabel("Time:"));
        resultNodesExamine.add(time);
        return resultNodesExamine;
    }

    private JPanel createPathCostPanel() {
        JPanel resultPathCost = new JPanel();
        pathCost = new JTextField(3);
        pathCost.setEditable(false);
        resultPathCost.setLayout(new FlowLayout(FlowLayout.LEFT));
        resultPathCost.add(new JLabel("Number of movements:"));
        resultPathCost.add(pathCost);
        return resultPathCost;
    }

    private void updateStatistics(AdversarialSearch<RadikalChessState, Movement> search) {
        updateNodesExpanded(search);
        updateTime(search);
        updatePathCost();
    }

    private void updateNodesExpanded(AdversarialSearch<RadikalChessState, Movement> search) {
        nodesExpanded.setText(String.valueOf(search.getExpandedNodes()));
    }

    private void updateTime(AdversarialSearch<RadikalChessState, Movement> search) {
        time.setText(String.valueOf(search.getTime() + " ms"));
    }

    private void updatePathCost() {
        pathCost.setText(String.valueOf(numberOfMovementsSearch));
    }

    private void updateMovement(Movement movement) {
        movements.setText(movements.getText() + numberOfMovements + ". " + currentState.getChessBoard().getCell()[movement.getDestination().getRow()]
                [movement.getDestination().getColumn()].getChessPiece().getColour() + " " +
                currentState.getChessBoard().getCell()[movement.getDestination().getRow()]
                [movement.getDestination().getColumn()].getChessPiece().getName() + " from " + "[" 
                + movement.getOrigin().getRow() + "," + movement.getOrigin().getColumn() 
                + "] to " + "[" + movement.getDestination().getRow() + "," 
                + movement.getDestination().getColumn() + "]\n");
    }
}