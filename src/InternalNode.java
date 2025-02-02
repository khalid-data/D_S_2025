public class InternalNode {

    final static int RIGHT_SEN = 1;
    final static int LEFT_SEN = -1;

    String stockId;
    InternalNode left, middle, right, parent;


    int leafCheck;// infinity if the node is a leaf, or 1 if internal node
    boolean isLeaf;

    public InternalNode() {
        //initiate treenode
        this.stockId = null;
    }

    public void setKey(String key) {
        this.stockId = key;
    }

    public void setParent(InternalNode parent) {
        this.parent = parent;
    }

    public boolean IsLeaf(){
        return this.left.leafCheck == LEFT_SEN && this.middle.leafCheck == RIGHT_SEN;
    }

    public void setLeafCheck(int value) {
        this.leafCheck = value;
    }

}

