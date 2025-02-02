import java.net.PortUnreachableException;

public class StockManager {

    //sentinel to know if leaf
    final static int RIGHT_SEN = 1;
    final static int LEFT_SEN = -1;
    final static int INTERNAL_SEN = 0;
    //used for implementation purpose

    InternalNode root;

    public StockManager() {

    }

    // 1. Initialize the system
    //init- create empty 2-3 tree
    public void initStocks() {
        InternalNode nRoot = new InternalNode();
        nRoot.setKey("");// check for problems

        nRoot.left.setLeafCheck(LEFT_SEN);
        root.middle.setLeafCheck(RIGHT_SEN);
        //should we fill stockId?
        root.setLeafCheck(INTERNAL_SEN);

        root.left.setParent(root);
        root.middle.setParent(root);

        this.root = nRoot;
    }

    public InternalNode search(InternalNode x, String k){
        if (x.IsLeaf()){
            if(x.stockId.equals(k)){
                return x;
            }
            else return null;
        }

        if(k.compareTo(x.left.stockId) <= 0){
            return search(x.left ,k);
        }

        else if(k.compareTo(x.middle.stockId) <= 0) {
            return search(x.middle, k);
        }
        return search(x.right ,k);
    }

    public InternalNode Min(){
        InternalNode x = this.root;
        while (!x.IsLeaf()){
            x = x.left;
        }
        x = x.parent.middle;
        if(x.leafCheck != LEFT_SEN || x.leafCheck != RIGHT_SEN){
            return x;
        }
        return null;
    }

    public InternalNode Successor(InternalNode x){
       InternalNode z = x.parent;
       InternalNode y;
       while(x == z.right || (z.right == null && x==z.middle)){
           x=z;
           z=z.parent;
       }
       if(x == z.left){
           y = z.middle;
       }
       else{
           y =z.right;
       }
       while(!y.IsLeaf()){
           y=y.left;
       }
       if(y.leafCheck == INTERNAL_SEN){
           return y;
       }
       return null;
    }

    public void updateKey(InternalNode x){

        x.stockId = x.left.stockId;
        if(x.middle.stockId != null){
            x.stockId = x.middle.stockId;
        }
        if(x.right != null){
            x.stockId = x.right.stockId;
        }
    }

    public void setChildren(InternalNode x, InternalNode l, InternalNode m, InternalNode r){
        x.left = l;
        x.middle = m;
        x.right = r;
        l.parent = x;
        if(m.stockId != null){
            m.parent = x;
        }
        if(r.stockId != null){
            r.parent = x;
        }
        updateKey(x);
    }

    public InternalNode Insert_And_Split(InternalNode x,InternalNode z) {
        InternalNode l = x.left;
        InternalNode m = x.middle;
        InternalNode r = x.right;

        if (r.stockId == null) {
            if (z.stockId.compareTo(l.stockId) < 0) {
                setChildren(x, z, l, m);
            } else if (z.stockId.compareTo(m.stockId) < 0) {
                setChildren(x, l, z, m);
            } else {
                setChildren(x, l, m, z);
            }
            return null;
        }

        InternalNode y = new InternalNode();
        if (z.stockId.compareTo(l.stockId) < 0) {
            setChildren(x, z, l, null);
            setChildren(y, m, r, null);
        } else if (z.stockId.compareTo(m.stockId) < 0) {
            setChildren(x, l, z, null);
            setChildren(y, m, r, null);
        } else if (z.stockId.compareTo(r.stockId) < 0) {
            setChildren(x, l, m, null);
            setChildren(y, z, r, null);
        }
        else {
            setChildren(x, l, m, null);
            setChildren(y, r, z, null);
        }
        return y;

    }   

    public void insert(InternalNode z){
        InternalNode y = this.root;
        while(!y.isLeaf) {
            if (z.stockId.compareTo(y.left.stockId) <= 0) {
                y = y.left;
            } else if (z.stockId.compareTo(y.middle.stockId) <= 0) {
                y = y.middle;
            } else {
                y = y.right;
            }
        }
        InternalNode x = y.parent;
        z = Insert_And_Split(x, z);
        while(x != this.root){
            x = x.parent;
            if(z != null){
                z = Insert_And_Split(x, z);
            }
            else{
                updateKey(x);
            }
        }
        if(z != null){
            InternalNode w = new InternalNode();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }
    
    public InternalNode borrowOrMerge(InternalNode y){
        InternalNode z = y.parent;
        InternalNode x = new InternalNode();
        if(y == z.left) {
            x = z.middle;

            if (x.right != null) {
                setChildren(y, y.left, x.left, null);
                setChildren(x, x.middle, x.right, null);
            } else {
                setChildren(x, y.left, x.left, x.middle);
                // delete(y); // not sure if needed can might be better to del this line and let java handle
                // check the use for dlete in the lectures.
                setChildren(z, x, x.right, null);
            }
            return z;
        }
        if (y == z.middle){
            x = z.left;
            if (x.right != null){
                setChildren(y ,x.right, y.left, null);
                setChildren(x, x.left, x.middle, null);
            }
            else{
                setChildren(x, x.left, x.middle, y.left);
                //delete(y);// check the use for dlete in the lectures.
                setChildren(z, x, z.right,null);
            }
            return z;
        }
        x = z.middle;
        if(x.right != null){
            setChildren(y,x.right,y.left,null);
            setChildren(x,x.left,x.middle,null);
        }
        else {
            setChildren(x,x.left,x.middle,y.left);
            //delete(y);// check the use for dlete in the lectures.
            setChildren(z,z.left,x,null);
        }
        return z;
    }

    // check the use for dlete in the lectures.
    public void delete(InternalNode x){
        InternalNode y = x.parent;
        if(x == y.left){
            setChildren(y,y.middle,y.right,null);
        }
        else if(x == y.middle){
            setChildren(y,y.left,y.right,null);
        }
        else{
            setChildren(y,y.left,y.middle,null);
        }
        while (y != null) {
            if(y.middle != null){
                updateKey(y);
                y= y.parent;
            }
            else{
                if(y != this.root){
                    y = borrowOrMerge(y);
                }
                else{
                    this.root = y.left;
                    y.left.parent = null;
                    return;
                }

            }
        }
    }
    

    // 2. Add a new stock
    public void addStock(String stockId, long timestamp, Float price) {
         if(search(this.root, stockId) != null){
             throw new IllegalArgumentException("stock already exists");
         }
         Stock stock = new Stock(stockId,timestamp,price);//update curr price
         stock.initPrices();
         this.insert(stock);

    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
    // add code here
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
    // add code here    
    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
    // add code here
    }

    // 6. Remove a specific timestamp from a stock's history
    public void removeStockTimestamp(String stockId, long timestamp) {
    // add code here
    }

    // 7. Get the amount of stocks in a given price range
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
    // add code here
    }

    // 8. Get a list of stock IDs within a given price range
    public String[] getStocksInPriceRange(Float price1, Float price2) {
    // add code here
    }


}


