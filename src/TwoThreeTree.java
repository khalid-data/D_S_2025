public class TwoThreeTree<Stock, K> {
    final static int RIGHT_SEN = 1;
    final static int LEFT_SEN = -1;
    final static int INTERNAL_SEN = 0;
    final static Float MAXFLOAT = Float.MAX_VALUE;
    final static Float MINFLOAT = Float.MIN_VALUE;

    // may be a stocks tree or a prices tree
    //if a stock tree we use the stocksTree pointers
    //if prices tree use pricesTree pointers
    Node<Stock, K> root;

    // initiates a twoThreeTree
    //if check is 0 means we need to build a stocks tree
    //if check is 1 we build a prices tree
    public TwoThreeTree(int check) {

        // must add sentinels to the left mid and root

        Node<Stock, K> x = new Node<>(null, null);

        if (check == 0) {
            x.leftStock = new Node<>(null, null);
            x.leftStock.isLeaf = true;
            x.midStock = new Node<>(null, null);
            x.midStock.isLeaf = true;

            x.leftStock.parentStock = x;
            x.midStock.parentStock = x;

            this.root = x;
        }
        else if (check == 1) {//should we init the list?
            x.leftPrice = new Node<>(null, null);
            x.leftPrice.isLeaf = true;
            x.leftPrice.key = (K) MINFLOAT;
            x.midPrice = new Node<>(null, null);
            x.midPrice.isLeaf = true;
            x.midPrice.key = (K) MAXFLOAT;
            x.key = (K) MAXFLOAT;

            x.leftPrice.parentPrice = x;
            x.midPrice.parentPrice = x;

            this.root = x;
        }
    }

    //find successor for node x in prices type tree
    public Node<Stock, K> Successor(Node<Stock, K> x) {
        Node<Stock, K> z = x.parentPrice;
        while (x==z.parentPrice || (z.rightPrice == null && x==z.midPrice)) {
            x = z;
            z = z.parentPrice;
        }
        Node<Stock, K> y;
        if (x ==z.leftPrice){ y = z.midPrice;}
        else { y = z.rightPrice;}
        while (!y.isLeaf()){y = y.leftPrice;}
        if ((Float) y.key < MAXFLOAT){return y;}// must update nodes and tree builder
        else {return null;}
    }
    //searches for a node in the tree that has the same key
    //key could be price or stockId
    public Node<Stock, K> Search(Node<Stock, K> x, K key) {

        if (x.isLeaf()) {
            if (x.key.equals(key)) {
                return x;
            } else return null;
        }

        //if the tree is a stocks tree
        if (key instanceof String) {
            if (x.leftStock != null && ((String) key).compareTo((String) x.leftStock.key) <= 0) {
                return Search(x.leftStock, key);
            } else if (x.midPrice != null && ((String) key).compareTo((String) x.midStock.key) <= 0) {
                return Search(x.midStock, key);
            }
            if (x.rightStock != null && ((String) key).compareTo((String) x.rightStock.key) <= 0) {
                return Search(x.rightStock, key);
            }
            return null;
        }
        else if (key instanceof Float) {
            if (((Float) key) < ((Float) x.leftPrice.key)) {
                return Search(x.leftPrice, key);
            } else if (((Float) key) < ((Float) x.midPrice.key)) {
                return Search(x.midPrice, key);
            }
            return Search(x.rightPrice, key);
        }
        return null;//maybe run an Exception instead?
    }

    // UPDATE the Node to have the maximum node in its subtree
    //used in inserting new nodes
    //only mid and right pointers can be null
    // we change the node to be the node in its subtree with the biggest key
    //by the way the tree is built its right > mid> left
    public void updateKey(Node<Stock, K> x) {
        if (x.key instanceof String) {
            if (x.rightStock != null) {
                x = x.rightStock;
            } else if (x.midStock != null) {
                x = x.midStock;
            } else {
                x = x.leftStock;
            }
        }

        if (x.key instanceof Float) {
            if (x.rightPrice != null) {
                x = x.rightPrice;
            } else if (x.midPrice != null) {
                x = x.midPrice;
            } else {
                x = x.leftPrice;
            }
        }
    }

    //Set l, m, and r to be the left, middle, and right children, respectively, of x; 
    //(only) m and r may be NIL 
    public void setChildren(Node<Stock, K> x, Node<Stock, K> l, Node<Stock, K> m, Node<Stock, K> r) {
        if (x.key instanceof String) {// for stocks tree
            x.leftStock = l;
            x.midStock = m;
            x.rightStock = r;
    
            l.parentStock = x;
            if (m != null) {
                m.parentStock = x;
            }
            if (r != null) {
                r.parentStock = x;
            }
    
            // Update isLeaf property
            x.isLeaf = x.leftStock == null && x.midStock == null && x.rightStock == null;
    
        } else if (x.key instanceof Float) {//for prices tree
            x.leftPrice = l;
            x.midPrice = m;
            x.rightPrice = r;
    
            l.parentPrice = x;
            if (m != null) {
                m.parentPrice = x;
            }
            if (r != null) {
                r.parentPrice = x;
            }
    
            // Update isLeaf property
            x.isLeaf = x.leftPrice == null && x.midPrice == null && x.rightPrice == null;
        }
    }
    //Insert node z as a child of node x;
    //split x if necessary and return the new node
    //knows to do it for stocks and prices tree by checking the key insance of the node

    public Node<Stock, K> Insert_And_Split(Node<Stock, K> x, Node<Stock, K> z) {

        if(x.key instanceof String && z.key instanceof String){
            Node<Stock, K> l = x.leftStock;
            Node<Stock, K> m = x.midStock;
            Node<Stock, K> r = x.rightStock;

            // If x has only two children:
            // Check where it should be added
            if (r == null) { // Error may occur
                if (((String) z.key).compareTo((String) l.key) < 0) {
                    setChildren(x, z, l, m);
                } else if (((String) z.key).compareTo((String) m.key) < 0) {
                    setChildren(x, l, z, m);
                } else {
                    setChildren(x, l, m, z);
                }
                return null;
            }

            // X has 3 children
            // Must split it and return the new internal node y
            Node<Stock, K> y = new Node<>(null, null);

            if (((String) z.key).compareTo((String) l.key) < 0) {
                setChildren(x, z, l, null);
                setChildren(y, m, r, null);
            } else if (((String) z.key).compareTo((String) m.key) < 0) {
                setChildren(x, l, z, null);
                setChildren(y, m, r, null);
            } else if (((String) z.key).compareTo((String) r.key) < 0) {
                setChildren(x, l, m, null);
                setChildren(y, z, r, null);
            } else {
                setChildren(x, l, m, null);
                setChildren(y, r, z, null);
            }
            return y;
        }


        else if(x.key instanceof Float && z.key instanceof Float){
            Node<Stock, K> l = x.leftPrice;
            Node<Stock, K> m = x.midPrice;
            Node<Stock, K> r = x.rightPrice;

            if(r == null){
                if(((Float) z.key) < ((Float) l.key)){
                    setChildren(x, z, l, m);
                }
                else if(((Float) z.key) < ((Float) m.key)){
                    setChildren(x, l, z, m);
                }
                else{
                    setChildren(x, l, m, z);
                }
                return null;
            }

            Node<Stock, K> y = new Node<>(null, null);

            if(((Float) z.key) < ((Float) l.key)){
                setChildren(x, z, l, null);
            }
            else if(((Float) z.key) < ((Float) m.key)){
                setChildren(x, l, z, null);
            }
            else if(((Float) z.key) < ((Float) r.key)){
                setChildren(x, l, m, null);
            }
            else{
                setChildren(x, l, m, null);
            }
            return y;
        }
        return null;
    }

    public void insertNode(Node<Stock,K> z) {
        Node<Stock, K> y = this.root;
        //look for where y should be inserted
        while (!y.isLeaf) {
            if (z.key instanceof String) {
                if(y == this.root && y.key == null){// means tree is empty
                    this.root = z;
                    this.root.parentStock = null;
                    this.root.midStock = z;
                    return;
                }
                else if (y.leftStock == null && y.midStock != null){// means we have only one stock in the tree
                    if (((String) z.key).compareTo((String) y.midStock.key) < 0){
                        y.leftStock = z;
                    }
                    else {
                        y.leftStock = y.midStock;
                        y.midStock = z;
                        y.leftStock.parentStock = z;
                        y.midStock.parentStock = z;
                        this.root = z;
                        return;
                    }
                }
                else if ( y.leftStock != null &&((String) z.key).compareTo((String) y.leftStock.key) < 0) {
                    y = y.leftStock;
                }
                else if (y.midStock != null && ((String) z.key).compareTo((String) y.midStock.key) < 0) {
                    y = y.midStock;
                }
                else if (y.rightStock != null && ((String) z.key).compareTo((String) y.rightStock.key) < 0) {
                    y = y.rightStock;
                }
                else return;
            }
            else if (z.key instanceof Float) {
                if (((Float) z.key) < ((Float) y.leftPrice.key)) {
                    y = y.leftPrice;
                }
                else if (((Float) z.key) < ((Float) y.midPrice.key)) {
                    y = y.midPrice;
                }
                else {
                    y = y.rightPrice;
                }
            }
        }
        //z should be inserted as child of x:
        if (z.key instanceof String ) {
            Node<Stock, K> x = y.parentStock;
            z = Insert_And_Split(x, z);
            while (x != this.root) {
                x = x.parentStock;
                if (z != null) {
                    z = Insert_And_Split(x, z);
                } else {
                    updateKey(x);
                }
            }
            // make new root (internal node)
            if (z != null) {
                Node<Stock, K> w = new Node<Stock, K>(null, null);
                setChildren(w, x, z, null);
                this.root = w;
            }
        }
        else if (z.key instanceof Float) {
            Node<Stock, K> x = y.parentPrice;
            z = Insert_And_Split(x, z);
            while (x != this.root) {// x != null &&
                x = x.parentPrice;
                if (z != null) {
                    z = Insert_And_Split(x, z);
                } else {
                    updateKey(x);
                }
            }
            if (z != null) {
                Node<Stock, K> w = new Node<Stock, K>(null, null);
                setChildren(w, x, z, null);
                this.root = w;
            }
        }

    }

    public Node<Stock, K> borrowOrMerge(Node<Stock, K> y){
        // y has one child y.left
        //borrow a child from sibling x or merge y with x
        if(y.key instanceof String){
            Node<Stock, K> z = y.parentStock;
            Node<Stock, K> x;
            if(y == z.leftStock) {
                x = z.midStock;

                if (x.rightStock != null) {
                    setChildren(y, y.leftStock, x.leftStock, null);
                    setChildren(x, x.midStock, x.rightStock, null);
                } else {
                    setChildren(x, y.leftStock, x.leftStock, x.midStock);
                    // delete(y); // not sure if needed can might be better to del this line and let java handle
                    // check the use for dlete in the lectures.
                    setChildren(z, x, x.rightStock, null);
                }
                return z;
            }
            if (y == z.midStock){
                x = z.leftStock;
                if (x.rightStock != null){
                    setChildren(y ,x.rightStock, y.leftStock, null);
                    setChildren(x, x.leftStock, x.midStock, null);
                }
                else{
                    setChildren(x, x.leftStock, x.midStock, y.leftStock);
                    //delete(y);// check the use for delete in the lectures.
                    setChildren(z, x, z.rightStock,null);
                }
                return z;
            }
            x = z.midStock;
            if(x.rightStock != null){
                setChildren(y,x.rightStock,y.leftStock,null);
                setChildren(x,x.leftStock,x.midStock,null);
            }
            else {
                setChildren(x,x.leftStock,x.midStock,y.leftStock);
                setChildren(z,z.leftStock,x,null);
            }
            return z;


        }
        else if (y.key instanceof Float) {
            Node<Stock, K> z = y.parentPrice;
            Node<Stock, K> x;
            if (y == z.leftPrice) {
                x = z.midPrice;
                if (x.rightPrice != null) {
                    setChildren(y, y.leftPrice, x.leftPrice, null);
                    setChildren(x, x.midPrice, x.rightPrice, null);
                } else {
                    setChildren(x, y.leftPrice, x.leftPrice, x.midPrice);
                    setChildren(z, x, x.rightPrice, null);
                }
                return z;
            }
            if (y == z.midPrice) {
                x = z.leftPrice;
                if (x.rightPrice != null) {
                    setChildren(y, x.rightPrice, y.leftPrice, null);
                    setChildren(x, x.leftPrice, x.midPrice, null);
                }
                else {
                    setChildren(x, x.leftPrice, x.midPrice, y.leftPrice);
                    setChildren(z, x, z.rightPrice, null);
                }
                return z;
            }
            x = z.midPrice;
            if (x.rightPrice != null) {
                setChildren(y, x.rightPrice, y.leftPrice, null);
                setChildren(x, x.leftPrice, x.midPrice, null);
            }
            else {
                setChildren(x, x.leftPrice, x.midPrice, y.leftPrice);
                setChildren(z, z.leftPrice, x, null);
            }
            return z;
        }
        return null;
    }

    public void delete(Node<Stock,K> x){
        if(x.key instanceof String){
            Node<Stock, K> y = x.parentStock;
            if(x == y.leftStock){
                setChildren(y,y.midStock,y.rightStock,null);
            }
            else if(x == y.midStock){
                setChildren(y,y.leftStock,y.rightStock,null);
            }
            else{
                setChildren(y,y.leftStock,y.midStock,null);
            }
            while (y != null) {
                if(y.midStock != null){
                    updateKey(y);
                    y= y.parentStock;
                }
                else{
                    if(y != this.root){
                        y = borrowOrMerge(y);
                    }
                    else{
                        this.root = y.leftStock;
                        y.leftStock.parentStock = null;
                        return;
                    }
                }
            }
        }
       else if(x.key instanceof Float){
           Node<Stock, K> y = x.parentPrice;
           if(x == y.leftPrice){
               setChildren(y,y.midPrice,y.rightPrice,null);
           }
           else if(x == y.midPrice){
               setChildren(y,y.leftPrice,y.rightPrice,null);
           }
           else{
               setChildren(y,y.leftPrice,y.midPrice,null);
           }
           while (y != null) {
               if(y.midPrice != null){
                   updateKey(y);
                   y= y.parentPrice;
               }
               else{
                   if(y != this.root){
                       y = borrowOrMerge(y);
                   }
                   else{
                       this.root = y.leftPrice;
                       y.leftPrice.parentPrice = null;
                       return;
                   }
               }
           }
        }
    }
    


}

