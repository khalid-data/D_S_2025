import java.net.PortUnreachableException;


public class StockManager {

    //sentinel to know if leaf
    final static int RIGHT_SEN = 1;
    final static int LEFT_SEN = -1;
    final static int INTERNAL_SEN = 0;
    final static Float MAXFLOAT = Float.MAX_VALUE;
    final static Float MINFLOAT = Float.MIN_VALUE;
    //used for implementation purpose

    Stock root;
    StockPriceNode priceRoot; // Root of the tree sorted by price


    public StockManager() {
        // what should be here?
    }

    // 1. Initialize the system
    //init- create empty 2-3 tree
    public void initStocks() {

        //make new internal node
        Stock x = new Stock();

        // make left and mid stock IDS ""
        x.left = new Stock();
        x.mid = new Stock();

        x.left.setLeafCheck(LEFT_SEN);
        x.mid.setLeafCheck(RIGHT_SEN);
        x.left.subTreeSize = 0;
        x.mid.subTreeSize = 0;
        //should we fill stockId?

        x.left.setParent(x);
        x.mid.setParent(x);

        this.root = x;
        this.priceRoot = null;
        this.initStockPrices();

    }

    public Stock search(Stock x, String k){
        if (x.IsLeaf()){
            if(x.stockId.equals(k)){
                return x;
            }
            else return null;
        }

        if(k.compareTo(x.left.stockId) <= 0){
            return search(x.left ,k);
        }

        else if(k.compareTo(x.mid.stockId) <= 0) {
            return search(x.mid, k);
        }
        return search(x.right ,k);
    }

    public Stock Min(){
        Stock x = this.root;
        while (!x.IsLeaf()){
            x = x.left;
        }
        x = x.parent.mid;
        if(x.leafCheck != LEFT_SEN || x.leafCheck != RIGHT_SEN){// need to check only if x.leafCheck is 1
            return x;
        }
        return null;
    }

    public Stock Successor(Stock x){
       Stock z = x.parent;
       Stock y;
       while(x == z.right || (z.right == null && x==z.mid)){
           x=z;
           z=z.parent;
       }
       if(x == z.left){
           y = z.mid;
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

    public void updateKey(Stock x){

        x.stockId = x.left.stockId;
        if(x.mid.stockId != null){
            x.stockId = x.mid.stockId;
        }
        if(x.right != null){
            x.stockId = x.right.stockId;
        }
    }

    public void setChildren(Stock x, Stock l, Stock m, Stock r){
        x.left = l;
        x.mid = m;
        x.right = r;
        l.parent = x;
        x.subTreeSize = x.left.subTreeSize;
        if(m.stockId != null){
            m.parent = x;
        }
        if(r.stockId != null){
            r.parent = x;
        }
        updateKey(x);
    }

    public Stock Insert_And_Split(Stock x,Stock z) {
        Stock l = x.left;
        Stock m = x.mid;
        Stock r = x.right;

        //if x has only two children:
        //check were it should be added
        if (r == null) {// error may occur
            if (z.stockId.compareTo(l.stockId) < 0) {
                setChildren(x, z, l, m);
            } else if (z.stockId.compareTo(m.stockId) < 0) {
                setChildren(x, l, z, m);
            } else {
                setChildren(x, l, m, z);
            }
            return null;
        }

        //X has 3 children
        //must split it and return the new internal node y
        Stock y = new Stock();

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

    public void insert(Stock z){
        Stock y = this.root;
        //look for where y should be inserted
        while(!y.isLeaf) {
            if (z.stockId.compareTo(y.left.stockId) <= 0) {
                y = y.left;
            } else if (z.stockId.compareTo(y.mid.stockId) <= 0) {
                y = y.mid;
            } else {
                y = y.right;
            }
        }
        //z should be inserted as child of x:
        Stock x = y.parent;
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
        // make new root (internal node)
        if(z != null){
            Stock w = new Stock();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }
    
    public Stock borrowOrMerge(Stock y){
        // y has one child y.left
        //borrow a child from sibling x or merge y with x
        Stock z = y.parent;
        Stock x;
        if(y == z.left) {
            x = z.mid;

            if (x.right != null) {
                setChildren(y, y.left, x.left, null);
                setChildren(x, x.mid, x.right, null);
            } else {
                setChildren(x, y.left, x.left, x.mid);
                // delete(y); // not sure if needed can might be better to del this line and let java handle
                // check the use for dlete in the lectures.
                setChildren(z, x, x.right, null);
            }
            return z;
        }
        if (y == z.mid){
            x = z.left;
            if (x.right != null){
                setChildren(y ,x.right, y.left, null);
                setChildren(x, x.left, x.mid, null);
            }
            else{
                setChildren(x, x.left, x.mid, y.left);
                //delete(y);// check the use for delete in the lectures.
                setChildren(z, x, z.right,null);
            }
            return z;
        }
        x = z.mid;
        if(x.right != null){
            setChildren(y,x.right,y.left,null);
            setChildren(x,x.left,x.mid,null);
        }
        else {
            setChildren(x,x.left,x.mid,y.left);
            setChildren(z,z.left,x,null);
        }
        return z;
    }

    // check the use for dlete in the lectures.
    public void delete(Stock x){
        Stock y = x.parent;
        if(x == y.left){
            setChildren(y,y.mid,y.right,null);
        }
        else if(x == y.mid){
            setChildren(y,y.left,y.right,null);
        }
        else{
            setChildren(y,y.left,y.mid,null);
        }
        while (y != null) {
            if(y.mid != null){
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

        // in case the stock already exists --> THROW EXCEPTION
         if(search(this.root, stockId) != null){
             throw new IllegalArgumentException("stock already exists");
         }

         //make new object stock and insert in stock manager
         Stock stock = new Stock(stockId,timestamp,price);//update curr price
        StockPriceNode priceNode = new StockPriceNode(price, stockId);
         stock.initPrices();
         this.insert(stock);
         this.insert(priceNode);
    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
        if(search(this.root, stockId) == null){
            throw new IllegalArgumentException("stock doesnt exist");
        }
        Stock x = search(root,stockId );
        delete(x);

        float currPrice = x.currPrice;
        StockPriceNode y = search(priceRoot ,currPrice);
        delete(y);
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
    // add code here    
    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        if(search(this.root, stockId) == null){
            throw new IllegalArgumentException("stock doesnt exist");
        }
        return search(this.root, stockId).currPrice;
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


