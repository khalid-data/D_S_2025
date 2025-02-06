public class LinkedList<T,K> {

    Node<T,K> head;
    Node<T,K> End;
    int size;

    public LinkedList() {
        this.head = null;
        this.End = null;
    }

    void addInFirst(Node<T,K> nNode) {
        if(this.head == null)
        {
            this.head = nNode;
            this.End = null;
        }
        else{
            nNode.next = this.head;
            this.head.prev = nNode;
            this.head = nNode;
        }

    }

    void addNode(Node<T,K> nNode) {
        //divide to 2 options
        //1) we didnt add nodes yet to the list

        this.size++;
        if(this.head == null)
        {
            nNode.prev = null;
            nNode.next = null;
            this.head = nNode;

        }
        //2)we already have some nodes in
        else
        {
            nNode.next = null;
            nNode.prev = this.End;
            this.End.next = nNode;

        }
        this.End = nNode;
    }


}
