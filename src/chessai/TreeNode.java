package chessai;

import java.util.ArrayList;

/**
 *
 * @author Pablo Alonso
 */
public class TreeNode {
    
    private final TreeNode father;
    private final Game currentGameState;
    private final boolean color;
    private final int originSquarePosition;
    private final int destinationSquarePosition;
    private final ArrayList<TreeNode> childs;
    private int heuristicValue;
    
    public TreeNode(TreeNode father, Game currentGameState, boolean color, int originSquarePosition, int destinationSquarePosition){
        this.father = father;
        this.currentGameState = currentGameState;
        this.color = color;
        this.originSquarePosition = originSquarePosition;
        this.destinationSquarePosition = destinationSquarePosition;
        childs = new ArrayList<>();
        heuristicValue = 0;
    }

    public TreeNode getFather() {
        return father;
    }

    public Game getCurrentGameState() {
        return currentGameState;
    }

    public boolean isColor() {
        return color;
    }

    public int getOriginSquarePosition() {
        return originSquarePosition;
    }

    public int getDestinationSquarePosition() {
        return destinationSquarePosition;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }
    
    public void addChild(TreeNode child){
        childs.add(child);
    }

    public ArrayList<TreeNode> getChilds() {
        return childs;
    }
    
}
