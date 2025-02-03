public class Node<T> {
    T Node;
    Node<T> next;
    Node<T> prev;
    Node<T> parent;


    public Node(T node){ //in case it has root node (key)
        this.Node = node ;
        next = null;
        prev = null;
        parent = null;
    }
}