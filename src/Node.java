public class Node<Stock, K> {
    K key;//stockId or Price
    Stock data;
    boolean isLeaf;
    int visited;


    // for stocks tree
    Node<Stock, K> leftStock;
    Node<Stock, K> midStock;
    Node<Stock, K> rightStock;
    Node<Stock, K> parentStock;

    //for prices tree
    Node<Stock, K> midPrice;
    Node<Stock, K> leftPrice;
    Node<Stock, K> rightPrice;
    Node<Stock, K> parentPrice;

    //price tree nodes have list of stock tree nodes 
    LinkedList<Stock, K> list;

    //for the list in prices tree nodes
    Node<Stock, K> next;
    Node<Stock, K> prev;


    public Node(Stock node, K key) { //in case it has root node (key)
        this.data = node;
        this.key = key;

    }

    public boolean isLeaf() {
        return isLeaf;
    }
}


