public class Stock extends InternalNode{

    final static Long MAX = Long.MAX_VALUE;
    final static Long MIN = Long.MIN_VALUE;

    String stockId;
    Float startingPrice;
    Long startTimeStamp;
    Long timeStamp;
    Float currPrice;

    priceDef root;

    public Stock(String stockId,Long timeStamp,Float startingPrice){
        this.stockId = stockId;
        this.startingPrice = startingPrice;
        this.timeStamp = timeStamp;
    }


    public String getStockId(){
        return this.stockId;
    }
    public Float getStartingPrice(){
        return this.startingPrice;
    }
    public Long getTimeStamp(){
        return this.startTimeStamp;
    }

    public void initPrices(){

        priceDef x = new priceDef();
        priceDef l = new priceDef();
        priceDef m = new priceDef();

        l.timeStamp = MIN;
        m.timeStamp = MAX;

        l.p = m.p = x;

        x.timeStamp = MAX;
        x.left = l;
        x.mid = m;

        this.root = x;
    }

    public priceDef search(priceDef x, long k){
        if (x.IsLeaf()){
            if(x.timeStamp.equals(k)){
                return x;
            }
            else return null;
        }

        if(k <= x.left.timeStamp){
            return search(x.left ,k);
        }

        else if(k<= x.mid.timeStamp) {
            return search(x.mid, k);
        }
        return search(x.right ,k);
    }

    public priceDef min(){
        priceDef x = this.root;
        while (!x.IsLeaf()){
            x = x.left;
        }
        x = x.p.mid;
        if(x.leafCheck != MIN || x.leafCheck != MAX){
            return x;
        }
        return null;
    }

    public priceDef successor(priceDef x){
        priceDef z = x.p;
        priceDef y;
        while(x == z.right || (z.right == null && x == z.mid)){
            x = z;
            z = z.p;
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
        if(y.timeStamp < MAX){
            return y;
        }
        return null;
    }

    public void updateKey(priceDef x){

        x.timeStamp = x.left.timeStamp;
        if(x.mid.timeStamp != null){
            x.timeStamp = x.mid.timeStamp;
        }
        if(x.right != null){
            x.timeStamp = x.right.timeStamp;
        }
    }

    public void setChildren(priceDef x, priceDef l, priceDef m, priceDef r){
        x.left = l;
        x.mid = m;
        x.right = r;
        l.p = x;
        if(m.timeStamp != null){
            m.p = x;
        }
        if(r.timeStamp != null){
            r.p = x;
        }
        updateKey(x);
    }


    public priceDef Insert_And_Split(priceDef x,priceDef z) {
        priceDef l = x.left;
        priceDef m = x.mid;
        priceDef r = x.right;

        if (r.timeStamp == null) {
            if (z.timeStamp.compareTo(l.timeStamp) < 0) {
                setChildren(x, z, l, m);
            } else if (z.timeStamp.compareTo(m.timeStamp) < 0) {
                setChildren(x, l, z, m);
            } else {
                setChildren(x, l, m, z);
            }
            return null;
        }

        priceDef y = new priceDef();
        if (z.timeStamp.compareTo(l.timeStamp) < 0) {
            setChildren(x, z, l, null);
            setChildren(y, m, r, null);
        } else if (z.timeStamp.compareTo(m.timeStamp) < 0) {
            setChildren(x, l, z, null);
            setChildren(y, m, r, null);
        } else if (z.timeStamp.compareTo(r.timeStamp) < 0) {
            setChildren(x, l, m, null);
            setChildren(y, z, r, null);
        }
        else {
            setChildren(x, l, m, null);
            setChildren(y, r, z, null);
        }
        return y;

    }

    public void insert(priceDef z){
        priceDef y = this.root;
        while(!y.isLeaf) {
            if (z.timeStamp.compareTo(y.left.timeStamp) <= 0) {
                y = y.left;
            } else if (z.timeStamp.compareTo(y.mid.timeStamp) <= 0) {
                y = y.mid;
            } else {
                y = y.right;
            }
        }
        priceDef x = y.p;
        z = Insert_And_Split(x, z);
        while(x != this.root){
            x = x.p;
            if(z != null){
                z = Insert_And_Split(x, z);
            }
            else{
                updateKey(x);
            }
        }
        if(z != null){
            priceDef w = new priceDef();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }


    public priceDef borrowOrMerge(priceDef y){
        priceDef z = y.p;
        priceDef x = new priceDef();
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

    public void delete(priceDef x){
        priceDef y = x.p;
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
                y= y.p;
            }
            else{
                if(y != this.root){
                    y = borrowOrMerge(y);
                }
                else{
                    this.root = y.left;
                    y.left.p = null;
                    return;
                }

            }
        }
    }
}

