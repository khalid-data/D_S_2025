public class priceDef{
    Float priceChange;
    Long timeStamp;
    final static Long MAX = Long.MAX_VALUE;
    final static Long MIN = Long.MIN_VALUE;
    priceDef left, mid, right, p;

    public priceDef(Float priceChange, Long timeStamp){}


    public priceDef() {
        this.timeStamp = null;
    }

    public priceDef(Long timeStamp, Float priceChange) {
        this.timeStamp = timeStamp;
        this.priceChange = priceChange;

        this.left = new priceDef();
        this.mid = new priceDef();
        this.left.timeStamp = MIN;
        this.mid.timeStamp = MAX;
    }

    int leafCheck;// infinity if the node is a leaf, or 1 if internal node
    boolean isLeaf;


    public void setKey(Long key) {
        this.timeStamp = key;
    }

    public void setParent(priceDef parent) {
        this.p = p;
    }

    public boolean IsLeaf(){
        return this.left.leafCheck == MIN && this.mid.leafCheck == MAX;
    }

    public void setLeafCheck(int value) {
        this.leafCheck = value;
    }


}
