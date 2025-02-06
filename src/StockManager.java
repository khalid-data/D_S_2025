
public class StockManager {

    //used for implementation purpose
    final static int RIGHT_SEN = 1;
    final static int LEFT_SEN = -1;
    final static int INTERNAL_SEN = 0;
    final static Float MAXFLOAT = Float.MAX_VALUE;
    final static Float MINFLOAT = Float.MIN_VALUE;


    TwoThreeTree <Stock, String> Stocks;
    TwoThreeTree<Stock, Float> Prices;


    public StockManager() {
        // what should be here?
        this.initStocks();
    }

    // 1. Initialize the system
    //init- create empty 2-3 tree
    public void initStocks() {
        Stocks = new TwoThreeTree<Stock, String>(0);
        Prices = new TwoThreeTree<Stock, Float>(1);

    }

    //find Node with stock with the stockid given starts from the stocks tree root
    public Node<Stock,String> search( String k){//should take a node
        if(this.Stocks.root.key == null){return null;}
        if(this.Stocks.root.key.equals(k)){return this.Stocks.root;}
        return this.Stocks.Search(this.Stocks.root, k);
    }



    // 2. Add a new stock
    //check f suck stock exists -> exception
    //make new stock
    //make a node x for the stocks tree and adds it
    // search for node y with same price in price tree:
    // if exists such y add x  to the list of y
    public void addStock(String stockId, long timestamp, Float price) {

        // in case the stock already exists --> THROW EXCEPTION
        if( this.Stocks.root.key != null) {
            if (this.search(stockId) != null) {
                throw new IllegalArgumentException("stock already exists");
            }
        }

        //make new object stock and insert in stock manager
        Stock stock = new Stock(stockId,timestamp,price);
        stock.initPrices();
        Node<Stock,String> x = new Node<>(stock,stockId);
        this.Stocks.insertNode(x);
        Node<Stock,Float> y = this.Prices.Search(this.Prices.root, price);
        if(y == null){
            y = new Node<>(stock,price);
            y.isLeaf = true;
            this.Prices.insertNode(y);
        }
        else {
            y.list.addNode(new Node<>(stock,price));
        }
    }

    // 3. Remove a stock
    // if no suck stock exists throw exception
    //if exists search for NODE x that has it in the stocks tree
    // remove it from stocks tree
    //search for Node y in prices tree with the same price:
    //if it has the same stock: 1. its list is empty -> remove it
    //2. the list is not empty -> pop the list head z, save its stock in y.stock
    public void removeStock(String stockId) {
        Node<Stock,String> x = this.search(stockId);
        if(x == null){
            throw new IllegalArgumentException("stock doesnt exist");
        }
        else {
            this.Stocks.delete(x);
            //find NODE with same price
            Node<Stock,Float> y = this.Prices.Search(this.Prices.root, x.data.currPrice);
            //if it has only the head in the list u can delete the node
            if (y.list.head.next == null) {
                this.Prices.delete(y);
            }
            else {//delete x from the list
                x.next.prev = x.prev;
                x.prev.next = x.next;
            }
        }
    }

    // 4. Update a stock price
    //if stock doesn't exist throw exception
    //find node x in stocks tree, save the currprice in temp and update the price (add price in pricetree of the stock and change currprice)
    //make new node z for prices tree to be added later (its price is curr + pricedef)
    //find Node y in prices tree with price == temp,
    // case 1: y list has only the head -> delete y
    //case 2: delete node x from y's list
    //add node z to prices tree
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        if(search(stockId) == null){
            throw new IllegalArgumentException("stock doesnt exist");
        }
        Node<Stock, String> x = this.Stocks.Search(this.Stocks.root, stockId);
        Float temp = x.data.currPrice;
        priceDef update = new priceDef(timestamp, priceDifference);
        x.data.insert(update);
        Node<Stock, Float> y = this.Prices.Search(this.Prices.root, temp);
        if(y!= null){
            if(y.list.head.next == null){
                this.Prices.delete(y);
            }
            else {
                x.next.prev = x.prev;
                x.prev.next = x.next;
            }
        }
        Node<Stock, Float> z = new Node<>(x.data, temp+priceDifference);
    }


    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        if(search(stockId) == null){
            throw new IllegalArgumentException("stock doesnt exist");
        }
        return search(stockId).data.currPrice;
    }


    // 6. Remove a specific timestamp from a stock's history
    //search for it in the stocks tree
    //delete it (in delete method in stock the curr price changes)
    public void removeStockTimestamp(String stockId, long timestamp) {
        Stock x =search(stockId).data;
        if(x == null ){
            throw new IllegalArgumentException("stock doesnt exist");
        }
        if(x.search(x.root,timestamp) == null ){
            throw new IllegalArgumentException("timestamp doesnt exist");
        }
        x.delete(x.search(x.root,timestamp));
    }

    // 7. Get the amount of stocks in a given price range
    //find if exists node with price 2, if not find the biggest node with the price smaller than price2 (node t)
    //find if exists node with price 1, if not find the  smallest node with price bigger than price1 (Node x\p)
    //travel from x to y nd count the stocks
    //do an inorder count (add the size of the list for each node) from x to y in prices tree
    public int getAmountStocksInPriceRange(Float price1, Float price2) {


        Node<Stock, Float> t1;
        Node<Stock, Float> p2;
        Node<Stock, Float> y = this.Prices.root;

        //find node x where price 1 would be inserted
        //case 1: successor is child of x
        // case 2: successor is x
        //look for where a node with price2 should be inserted
        while (!y.isLeaf) {
            if (price1 <  y.leftPrice.key) {
                y = y.leftPrice;
            }
            else if (price2 < y.midPrice.key) {
                y = y.midPrice;
            }
            else {
                y = y.rightPrice;
            }
        }

        Node<Stock, Float> x = y.parentPrice;
        //case 1
        if(price2 >= x.leftPrice.key ){ t1= x.leftPrice;}
        else if(price2 >= x.midPrice.key){ t1= x.leftPrice;}
        else if(price2 >= x.rightPrice.key){ t1= x.leftPrice;}
        else t1=x;
        t1 = this.Prices.Search(this.Prices.root, t1.key);
        return count(t1, price2);
        //z should be inserted as child of x:
    }

    public int count(Node<Stock, Float> x, Float price2){

        int count = 0;
        if (x.isLeaf() && x.key <= price2 && x.visited == 0) {
            count += x.list.size;
            x.visited = 1;
            if(x==x.parentPrice.leftPrice){// left child
                if (x.parentPrice.rightPrice== null && x.parentPrice.midPrice != null){count += count(x.parentPrice.midPrice, price2);}
                else if (x.parentPrice.midPrice != null){count += count(x.parentPrice.midPrice, price2) +count(x.parentPrice.rightPrice, price2);}
            }
        }
        else if (x.isLeaf() && x.visited == 1  ) {x.visited = 0;}


        else {// not a leaf
            if (x.leftPrice != null && x.visited == 0) {
                count += count(x.leftPrice, price2);
            }
            else if(x.leftStock != null && x.visited == 1){x.visited = 0;}

            if (x.midPrice != null && x.visited == 0) {
            }
            else if(x.midStock != null && x.visited == 1){x.visited = 0;}

            if (x.rightPrice != null && x.visited == 0) {
                count += count(x.rightPrice, price2);
            }
            else if(x.rightStock != null && x.visited == 1){x.visited = 0;}
        }
        return count;
    }

    // 8. Get a list of stock IDs within a given price range
    // get amount stock in the range
    // make new array
    //find if exists node with price 1, if not find the  smallest node with price bigger than price1 (Node x)
    //do an inorder traversal  (add the size of the list for each node) from x as long as lower than price 2
    //
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        int size = getAmountStocksInPriceRange(price1, price2);
        String[] stocks = new String[size];
        return fill(this.Prices.root, price2, stocks, size, 0);

    }

    public String[] fill(Node<Stock, Float> x, Float price2, String[] Stocks, int count, int currStock){

        if (x.isLeaf() && x.key <= price2 && x.visited == 0) {
            int temp = currStock;
            for (int j = temp; x.list.size > 0; x.list.size--, j++, temp++) {
                Stocks[j] = x.list.head.data.stockId;
                x.list.head.next.prev = null;
                x.list.head = x.list.head.next;
            }
            x.visited = 1;
            currStock= temp;
            if(x==x.parentPrice.leftPrice){// left child
                if (x.parentPrice.rightPrice== null && x.parentPrice.midPrice != null){fill(x.parentPrice.midPrice, price2, Stocks,count,currStock);}
                else if (x.parentPrice.midPrice != null){
                    fill(x.parentPrice.midPrice, price2, Stocks,count,currStock);
                    fill(x.parentPrice.rightPrice, price2,Stocks,count,currStock);
                }
            }
        }
        //if leaf was visited
        else if(x.isLeaf() && x.visited == 1){x.visited = 0;}

        else {// if not leaf
            if (x.leftPrice != null && x.visited == 0) {
                fill(x.leftPrice, price2, Stocks,count,currStock);
            }
            else if(x.leftStock != null && x.visited == 1){x.visited = 0;}

            if (x.midPrice != null && x.visited == 0) {
                fill(x.midPrice, price2, Stocks,count,currStock);
            }
            else if(x.midStock != null && x.visited == 1){x.visited = 0;}

            if (x.rightPrice != null && x.visited == 0) {
                fill(x.rightPrice, price2, Stocks,count,currStock);
            }
            else if(x.rightStock != null && x.visited == 1){x.visited = 0;}
        }
        return Stocks;
    }

}


