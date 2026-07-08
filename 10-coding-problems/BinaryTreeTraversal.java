import java.util.*;

/**
 * Binary tree traversals: recursive DFS (in/pre/post-order) and iterative BFS
 * (level order). Know both the recursive form AND how to do it iteratively
 * with an explicit Deque/Queue — interviewers often ask for both.
 * Run:  java 10-coding-problems/BinaryTreeTraversal.java
 */
public class BinaryTreeTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode l, TreeNode r) { this.val = val; left = l; right = r; }
    }

    // ---- recursive DFS ----
    static void inorder(TreeNode n, List<Integer> out) {       // left, node, right
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.val);
        inorder(n.right, out);
    }
    static void preorder(TreeNode n, List<Integer> out) {      // node, left, right
        if (n == null) return;
        out.add(n.val);
        preorder(n.left, out);
        preorder(n.right, out);
    }
    static void postorder(TreeNode n, List<Integer> out) {     // left, right, node
        if (n == null) return;
        postorder(n.left, out);
        postorder(n.right, out);
        out.add(n.val);
    }

    // ---- iterative in-order using an explicit stack (no recursion) ----
    static List<Integer> inorderIterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) { stack.push(curr); curr = curr.left; }  // go all the way left
            curr = stack.pop();
            out.add(curr.val);
            curr = curr.right;
        }
        return out;
    }

    // ---- iterative BFS (level order) using a queue ----
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> levels = new ArrayList<>();
        if (root == null) return levels;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();                            // snapshot: nodes in this level
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                level.add(n.val);
                if (n.left != null) queue.offer(n.left);
                if (n.right != null) queue.offer(n.right);
            }
            levels.add(level);
        }
        return levels;
    }

    public static void main(String[] args) {
        //         4
        //       /   \
        //      2     6
        //     / \   / \
        //    1   3 5   7
        TreeNode root = new TreeNode(4,
            new TreeNode(2, new TreeNode(1), new TreeNode(3)),
            new TreeNode(6, new TreeNode(5), new TreeNode(7)));

        List<Integer> in = new ArrayList<>(); inorder(root, in);
        List<Integer> pre = new ArrayList<>(); preorder(root, pre);
        List<Integer> post = new ArrayList<>(); postorder(root, post);

        System.out.println("inorder   (recursive): " + in);
        System.out.println("preorder  (recursive): " + pre);
        System.out.println("postorder (recursive): " + post);
        System.out.println("inorder   (iterative): " + inorderIterative(root));
        System.out.println("level order (BFS)    : " + levelOrder(root));

        assert in.equals(List.of(1, 2, 3, 4, 5, 6, 7));
        assert pre.equals(List.of(4, 2, 1, 3, 6, 5, 7));
        assert post.equals(List.of(1, 3, 2, 5, 7, 6, 4));
        assert inorderIterative(root).equals(in);
        assert levelOrder(root).equals(List.of(List.of(4), List.of(2, 6), List.of(1, 3, 5, 7)));

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
