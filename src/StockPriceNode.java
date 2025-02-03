import java.util.Objects;

public class StockPriceNode {

    final static Float MAXFLOAT = Float.MAX_VALUE;
    final static Float MINFLOAT = Float.MIN_VALUE;

    Float price;
    LinkedList<String> StockIds;
    int arraysSize;
    StockPriceNode left,mid, right, parent;
    int subTreeSize;



    public StockPriceNode(Float price, String stockId) {
        this.price = price;
        this.StockIds = new LinkedList<>();
        this.StockIds.addNode(new Node<>(stockId));
        this.subTreeSize = 1;

        this.left = new StockPriceNode();
        this.mid = new StockPriceNode();
        this.left.price = MINFLOAT;
        this.mid.price = MAXFLOAT;
    }

    //add stockId to length

    public StockPriceNode() {

    }

    public boolean IsLeaf(){
        return Objects.equals(this.left.price, MINFLOAT) && Objects.equals(this.mid.price, MAXFLOAT);
    }


}

public