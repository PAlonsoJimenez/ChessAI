package chessai.Swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Pablo Alonso
 */
public class Board extends JPanel {

    private final SwingInterface wholeInteface;
    private boolean mainViewWhites;
    private boolean originSquareSelected;
    private Color originSquareOriginalColor;
    private int OriginSquarePosition;
    private int DestinationSquarePosition;
    private final Square[] squares;
    private final ArrayList<int[]> squareMemoryShots;
    private boolean blocked;
    private boolean permanentlyBlocked;

    public Board(SwingInterface wholeInterface) {
        this.wholeInteface = wholeInterface;
        mainViewWhites = true;
        originSquareSelected = false;
        originSquareOriginalColor = new Color(0, 0, 0);
        OriginSquarePosition = -1;
        DestinationSquarePosition = -1;
        squares = new Square[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int position = i*8+j;
                Square currentSquare = new Square(position);
                editSquareUI(position, currentSquare);
                currentSquare.addActionListener(squareAction());
                squares[position] = currentSquare;
                this.add(currentSquare);
            }
        }
        squareMemoryShots = new ArrayList<>();
        capture();
        blocked = false;
        permanentlyBlocked = false;
        setPreferredSize(new Dimension(700, 700));
    }
    
    public void permanentlyBlocked(){
        permanentlyBlocked = true;
    }

    public void rotateView() {
        mainViewWhites = !mainViewWhites;
        this.removeAll();
        
        if(mainViewWhites){
            for (int i = 0; i < 64; i++) {
                this.add(squares[i]);
            }
        }else{
            for (int i = 63; i >= 0; i--) {
                this.add(squares[i]);
            }
        }
        revalidate();
    }

    public void movePiece(int OriginSquarePosition, int DestinationSquarePosition, boolean capture) {
        squares[DestinationSquarePosition].setIcon(squares[OriginSquarePosition].getIcon());
        squares[DestinationSquarePosition].setValue(squares[OriginSquarePosition].getValue());
        squares[OriginSquarePosition].setIcon(null);
        squares[OriginSquarePosition].setValue(0);
        if(capture){
            capture();
        }else{
            capture();
            squareMemoryShots.set(squareMemoryShots.size()-2, squareMemoryShots.get(squareMemoryShots.size()-1));
            squareMemoryShots.remove(squareMemoryShots.size()-1);
        }
    }
    
    public void changeSquareImageIcon(int squarePosition, String newImageName, boolean color){
        if(newImageName.equals("None")){
            squares[squarePosition].setIcon(null);
            squares[squarePosition].setValue(0);
        }else{
            if(color){
                squares[squarePosition].setIcon(new ImageIcon(newImageName + " W 64.png"));
            }else{
                squares[squarePosition].setIcon(new ImageIcon(newImageName + " B 64.png"));
            }
            int newValue = 0;
            switch(newImageName){
                case "Knight":
                    newValue = color ? 3 : -3;
                    break;
                case "Bishop":
                    newValue = color ? 4 : -4;
                    break;
                case "Rook":
                    newValue = color ? 5 : -5;
                    break;
                case "Queen":
                    newValue = color ? 9 : -9;
                    break;
            }
            squares[squarePosition].setValue(newValue);
        }
        capture();
        squareMemoryShots.set(squareMemoryShots.size()-2, squareMemoryShots.get(squareMemoryShots.size()-1));
        squareMemoryShots.remove(squareMemoryShots.size()-1);
    }
    
    public void changeDisplayedMovement(int moveIndex){
        blocked = moveIndex != squareMemoryShots.size()-1;
        for(int i = 0; i < 64; i++){
            switch(squareMemoryShots.get(moveIndex)[i]){
                case 1:
                    squares[i].setIcon(new ImageIcon("Pawn W 64.png"));
                    break;
                case 3:
                    squares[i].setIcon(new ImageIcon("Knight W 64.png"));
                    break;
                case 4:
                    squares[i].setIcon(new ImageIcon("Bishop W 64.png"));
                    break;
                case 5:
                    squares[i].setIcon(new ImageIcon("Rook W 64.png"));
                    break;
                case 9:
                    squares[i].setIcon(new ImageIcon("Queen W 64.png"));
                    break;
                case 10:
                    squares[i].setIcon(new ImageIcon("King W 64.png"));
                    break;
                case -1:
                    squares[i].setIcon(new ImageIcon("Pawn B 64.png"));
                    break;
                case -3:
                    squares[i].setIcon(new ImageIcon("Knight B 64.png"));
                    break;
                case -4:
                    squares[i].setIcon(new ImageIcon("Bishop B 64.png"));
                    break;
                case -5:
                    squares[i].setIcon(new ImageIcon("Rook B 64.png"));
                    break;
                case -9:
                    squares[i].setIcon(new ImageIcon("Queen B 64.png"));
                    break;
                case -10:
                    squares[i].setIcon(new ImageIcon("King B 64.png"));
                    break;
                case 0:
                    squares[i].setIcon(null);
                    break;
            }
        }
    }

    private void editSquareUI(int position, Square currentSquare) {
        currentSquare.setPreferredSize(new Dimension(75,75));
        int row = position/8;
        int column = position%8;
        if ((row + column) % 2 == 0) {
            currentSquare.setBackground(Color.yellow);
        } else {
            currentSquare.setBackground(new Color(156, 93, 82));//Brown
        }
        switch (row) {
            case 1:
                currentSquare.setIcon(new ImageIcon("Pawn B 64.png"));
                currentSquare.setValue(-1);
                break;
            case 6:
                currentSquare.setIcon(new ImageIcon("Pawn W 64.png"));
                currentSquare.setValue(1);
                break;
            case 0:
                switch (column) {
                    case 0:
                        currentSquare.setIcon(new ImageIcon("Rook B 64.png"));
                        currentSquare.setValue(-5);
                        break;
                    case 1:
                        currentSquare.setIcon(new ImageIcon("Knight B 64.png"));
                        currentSquare.setValue(-3);
                        break;
                    case 2:
                        currentSquare.setIcon(new ImageIcon("Bishop B 64.png"));
                        currentSquare.setValue(-4);
                        break;
                    case 3:
                        currentSquare.setIcon(new ImageIcon("Queen B 64.png"));
                        currentSquare.setValue(-9);
                        break;
                    case 4:
                        currentSquare.setIcon(new ImageIcon("King B 64.png"));
                        currentSquare.setValue(-10);
                        break;
                    case 5:
                        currentSquare.setIcon(new ImageIcon("Bishop B 64.png"));
                        currentSquare.setValue(-4);
                        break;
                    case 6:
                        currentSquare.setIcon(new ImageIcon("Knight B 64.png"));
                        currentSquare.setValue(-3);
                        break;
                    case 7:
                        currentSquare.setIcon(new ImageIcon("Rook B 64.png"));
                        currentSquare.setValue(-5);
                        break;
                }
                break;
            case 7:
                switch (column) {
                    case 0:
                        currentSquare.setIcon(new ImageIcon("Rook W 64.png"));
                        currentSquare.setValue(5);
                        break;
                    case 1:
                        currentSquare.setIcon(new ImageIcon("Knight W 64.png"));
                        currentSquare.setValue(3);
                        break;
                    case 2:
                        currentSquare.setIcon(new ImageIcon("Bishop W 64.png"));
                        currentSquare.setValue(4);
                        break;
                    case 3:
                        currentSquare.setIcon(new ImageIcon("Queen W 64.png"));
                        currentSquare.setValue(9);
                        break;
                    case 4:
                        currentSquare.setIcon(new ImageIcon("King W 64.png"));
                        currentSquare.setValue(10);
                        break;
                    case 5:
                        currentSquare.setIcon(new ImageIcon("Bishop W 64.png"));
                        currentSquare.setValue(4);
                        break;
                    case 6:
                        currentSquare.setIcon(new ImageIcon("Knight W 64.png"));
                        currentSquare.setValue(3);
                        break;
                    case 7:
                        currentSquare.setIcon(new ImageIcon("Rook W 64.png"));
                        currentSquare.setValue(5);
                        break;
                }
                break;
        }
    }

    private ActionListener squareAction() {
        return new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!blocked && !permanentlyBlocked) {
                    if (originSquareSelected) {
                        squares[OriginSquarePosition].setBackground(originSquareOriginalColor);
                        DestinationSquarePosition = ((Square) ae.getSource()).getPosition();
                        wholeInteface.tryingToMove(OriginSquarePosition, DestinationSquarePosition);
                    } else {
                        originSquareOriginalColor = ((Square) ae.getSource()).getBackground();
                        ((Square) ae.getSource()).setBackground(Color.red);
                        OriginSquarePosition = ((Square) ae.getSource()).getPosition();
                    }
                    originSquareSelected = !originSquareSelected;
                }
            }
            
        };
    }

    private void capture() {
        int[] newShot = new int[64];
        for (int i = 0; i < newShot.length; i++) {
            newShot[i] = squares[i].getValue();
        }
        squareMemoryShots.add(newShot);
    }

}
