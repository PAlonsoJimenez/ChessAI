package chessai.Swing;

import chessai.BoardObserver;
import chessai.GameGUIInterface;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Pablo Alonso
 */
public class SwingInterface extends JFrame implements GameGUIInterface{
    
    private final Board board;
    private final MovementRecord record;
    private final JLabel whiteTimerLabel, blackTimerLabel;
    private final Timer whiteTimer, blackTimer;
    private long whiteTime, blackTime;
    private long timeLapse;
    private boolean whitePlayer;
    private boolean blackStarted;
    private final LinkedList<BoardObserver> observers;
    
    public SwingInterface(boolean mainWhites, long time){
        board = new Board(this);
        record = new MovementRecord();
        if(!mainWhites) board.rotateView();
        whiteTimerLabel = new JLabel();
        blackTimerLabel = new JLabel();
        whiteTimer = new Timer(100, whiteTimerActionListener());
        blackTimer = new Timer(100, blackTimerActionListener());
        whiteTime = time;
        blackTime = time;
        whitePlayer = true;
        blackStarted = false;
        observers = new LinkedList<>();
        createUI(time, mainWhites);
        setTitle("Chess Game");
        setIconImage(new ImageIcon("Chess Icon.png").getImage());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void startWhiteThread(){
        timeLapse = System.currentTimeMillis();
        whiteTimer.start();
    }

    public void tryingToMove(int OriginSquarePosition, int DestinationSquarePosition) {
        for (BoardObserver obs : observers) {
            obs.tryingToMove(OriginSquarePosition, DestinationSquarePosition);
        }
    }

    @Override
    public void addObserver(BoardObserver observer) {
        observers.add(observer);
    }

    @Override
    public void moveApproved(int OriginSquarePosition, int DestinationSquarePosition, boolean capture) {
        board.movePiece(OriginSquarePosition, DestinationSquarePosition, capture);
        timeLapse = System.currentTimeMillis();
        if(!blackStarted){
            blackStarted = true;
            blackTimer.start();
        }
        if(whitePlayer){
            whiteTimer.stop();
            blackTimer.restart();
        }else{
            blackTimer.stop();
            whiteTimer.restart();
        }
        whitePlayer = !whitePlayer;
    }

    @Override
    public void changeSquareImageIcon(int squarePosition, String newImageName, boolean color) {
        board.changeSquareImageIcon(squarePosition, newImageName, color);
    }

    @Override
    public void promotePawn(int pawnOldAbsolutePosition, int pawnNewAbsolutePosition, boolean color, boolean AIpromoting) {
        if(AIpromoting){
            changeSquareImageIcon(pawnNewAbsolutePosition, "Queen", color);
            for (BoardObserver observer : observers) {
                observer.pawnPromotedTo(pawnOldAbsolutePosition, pawnNewAbsolutePosition, "Queen", color);
            }
        }else{
            PromotionSelector selector = new PromotionSelector(color);
            changeSquareImageIcon(pawnNewAbsolutePosition, selector.getPieceSelectedName(), color);
            for (BoardObserver observer : observers) {
                observer.pawnPromotedTo(pawnOldAbsolutePosition, pawnNewAbsolutePosition, selector.getPieceSelectedName(), color);
            }
        }
    }
    
    @Override
    public void writeMoveNotation(String notation){
        record.addMovement(notation);
    }
    
    @Override
    public void gameEnded(int result, String message){
        board.permanentlyBlocked();
        
        whiteTimer.stop();
        blackTimer.stop();
        
        new WinnerPopup(result, message);
    }

    private void createUI(long time, boolean mainWhites) {
        createCentralPanel();
        createEastPanel(time, mainWhites);
    }

    private void createCentralPanel() {
        //JPanel FlowLayout by default
        JPanel centerPanel = new JPanel();
        centerPanel.add(board);
        
        this.add(centerPanel);
    }

    private void createEastPanel(long time, boolean mainWhites) {
        whiteTimerLabel.setFont(new Font("Dialog Bold", Font.BOLD, 18));
        blackTimerLabel.setFont(new Font("Dialog Bold", Font.BOLD, 18));
        whiteTimerLabel.setText(""+ time/60 + ": " + time%60);
        blackTimerLabel.setText(""+ time/60 + ": " + time%60);
        
        JButton changeView = new JButton();
        changeView.setIcon(new ImageIcon("Change Side View 16.png"));
        changeView.addActionListener(changeViewButtonActionListener());
        
        JPanel timerPanel = new JPanel(new GridLayout(3, 1, 0, 275));
        
        if(mainWhites){
            timerPanel.add(blackTimerLabel);
            timerPanel.add(changeView);
            timerPanel.add(whiteTimerLabel);
        }else{
            timerPanel.add(whiteTimerLabel);
            timerPanel.add(changeView);
            timerPanel.add(blackTimerLabel);
        }
        
        JButton previous = new JButton("Prev");
        previous.addActionListener(previousButtonActionListener());        
        JButton present = new JButton("O");
        present.addActionListener(presentButtonActionListener());
        JButton next = new JButton("Next");
        next.addActionListener(nextButtonActionListener());
        
        JPanel buttons = new JPanel();
        buttons.add(previous);
        buttons.add(present);
        buttons.add(next);
        
        GridLayout layout = new GridLayout(2, 1, 0, 0);
        JPanel recordPanel = new JPanel(layout);
        recordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(), "Record"));
        recordPanel.add(record);
        recordPanel.add(buttons);
        
        JPanel eastPanel = new JPanel();
        eastPanel.add(timerPanel);
        eastPanel.add(recordPanel);
        
        this.add(eastPanel, BorderLayout.EAST);
    }

    private ActionListener previousButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                record.previousMovement();
                board.changeDisplayedMovement(record.getCurrentMovement()+1);
            }

        };
    }

    private ActionListener presentButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                record.backToPresent();
                board.changeDisplayedMovement(record.getCurrentMovement()+1);
            }
            
        };
    }

    private ActionListener nextButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                record.nextMovement();
                board.changeDisplayedMovement(record.getCurrentMovement()+1);
            }
            
        };
    }
    
    private ActionListener whiteTimerActionListener(){
        
        return new ActionListener(){
            private long millisecondCounter = 0;
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                millisecondCounter += System.currentTimeMillis() - timeLapse;
                timeLapse = System.currentTimeMillis();
                if(millisecondCounter > 1000){
                    millisecondCounter -= 1000;
                    whiteTime--;
                    whiteTimerLabel.setText(""+ whiteTime/60 + ": " + whiteTime%60);
                    if(whiteTime <= 0){
                        for (BoardObserver observer : observers) {
                            observer.timeOver(true);
                        }
                    }
                }
            }
            
        };
    }

    private ActionListener blackTimerActionListener() {
        
        return new ActionListener(){
            private long millisecondCounter = 0;
            
            @Override
            public  void actionPerformed(ActionEvent ae) {
                millisecondCounter += System.currentTimeMillis() - timeLapse;
                timeLapse = System.currentTimeMillis();
                if(millisecondCounter > 1000){
                    millisecondCounter -= 1000;
                    blackTime--;
                    blackTimerLabel.setText(""+ blackTime/60 + ": " + blackTime%60);
                    if(blackTime <= 0){
                        for (BoardObserver observer : observers) {
                            observer.timeOver(false);
                        }
                    }
                }
            }
            
        };
    }

    private ActionListener changeViewButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                board.rotateView();
            }
            
        };
    }

}
