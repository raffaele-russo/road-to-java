/**
 * Reverse a singly linked list — iteratively and recursively.
 * Pattern: three-pointer walk (prev/curr/next); the classic warm-up question
 * that also tests whether you're comfortable with Java's null / reference model.
 * Run:  java 10-coding-problems/ReverseLinkedList.java
 */
public class ReverseLinkedList {

    static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    // Iterative: O(n) time, O(1) space.
    static Node reverseIterative(Node head) {
        Node prev = null, curr = head;
        while (curr != null) {
            Node next = curr.next;   // save before overwriting
            curr.next = prev;        // reverse the link
            prev = curr;
            curr = next;
        }
        return prev;                 // prev is the new head
    }

    // Recursive: O(n) time, O(n) space (call stack).
    static Node reverseRecursive(Node head) {
        if (head == null || head.next == null) return head;   // base case: 0 or 1 node
        Node newHead = reverseRecursive(head.next);
        head.next.next = head;       // point the next node back at this one
        head.next = null;            // this node becomes the new tail
        return newHead;
    }

    static Node of(int... vals) {
        Node dummy = new Node(0), tail = dummy;
        for (int v : vals) { tail.next = new Node(v); tail = tail.next; }
        return dummy.next;
    }

    static String toStr(Node head) {
        StringBuilder sb = new StringBuilder();
        for (Node n = head; n != null; n = n.next) sb.append(n.val).append(n.next != null ? "->" : "");
        return sb.toString();
    }

    public static void main(String[] args) {
        Node list1 = of(1, 2, 3, 4, 5);
        System.out.println("original       : " + toStr(list1));
        Node rev1 = reverseIterative(list1);
        System.out.println("iterative rev  : " + toStr(rev1));
        assert toStr(rev1).equals("5->4->3->2->1");

        Node list2 = of(1, 2, 3, 4, 5);
        Node rev2 = reverseRecursive(list2);
        System.out.println("recursive rev  : " + toStr(rev2));
        assert toStr(rev2).equals("5->4->3->2->1");

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
