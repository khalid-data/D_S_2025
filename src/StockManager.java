import java.net.PortUnreachableException;

public class StockManager {

    //sentinel to know if leaf
    final static int RIGHT_SEN = 1;
    final static int LEFT_SEN = -1;
    final static int INTERNAL_SEN = 0;
    //used for implementation purpose

    Stock root;

    public StockManager() {
        // what should be here?
    }

    // 1. Initialize the system
    //init- create empty 2-3 tree
    public void initStocks() {
        Stock nRoot = new Stock();

        nRoot.left.setLeafCheck(LEFT_SEN);
        root.middle.setLeafCheck(RIGHT_SEN);
        //should we fill stockId?
        root.setLeafCheck(INTERNAL_SEN);//in lecture we did +infinity

        root.left.setParent(root);
        root.middle.setParent(root);

        this.root = nRoot;
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
        if(x.leafCheck != LEFT_SEN || x.leafCheck != RIGHT_SEN){
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
        while(!y.isLeaf) {
            if (z.stockId.compareTo(y.left.stockId) <= 0) {
                y = y.left;
            } else if (z.stockId.compareTo(y.mid.stockId) <= 0) {
                y = y.mid;
            } else {
                y = y.right;
            }
        }
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
        if(z != null){
            Stock w = new Stock();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }
    
    public Stock borrowOrMerge(Stock y){
        Stock z = y.parent;
        Stock x = new Stock();
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
                //delete(y);// check the use for dlete in the lectures.
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
            //delete(y);// check the use for dlete in the lectures.
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


