package com.example.helloworld.oj;

/**
 * 给定一个二叉树，原地将它展开为链表。
 * 例如，给定二叉树
 * leetcode 114 中等
 * https://leetcode-cn.com/problems/flatten-binary-tree-to-linked-list/
 *
 * @author yangchang
 */
public class FlattenBinaryTreeToLinkedList {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        FlattenBinaryTreeToLinkedList fbt2ll = new FlattenBinaryTreeToLinkedList();
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(2);
        TreeNode treeNode3 = new TreeNode(3);
        TreeNode treeNode4 = new TreeNode(4);
        TreeNode treeNode5 = new TreeNode(5);
        TreeNode treeNode6 = new TreeNode(6);
        treeNode1.left = treeNode2;
        treeNode1.right = treeNode5;

        treeNode2.left = treeNode3;
        treeNode2.right = treeNode4;

        treeNode5.right = treeNode6;

        fbt2ll.prinf(treeNode1);
        fbt2ll.flatten(treeNode1);
        fbt2ll.flattenLk(treeNode1);
        fbt2ll.prinf(treeNode1);
    }

    /**
     * 打印链表
     *
     * @param root 根节点
     */
    public void prinf(TreeNode root) {
        while (root != null) {
            System.out.println(root.val);
            root = root.right;
        }
    }

    /**
     * 获得平整的链表
     *
     * @param root 根节点
     */
    public void flatten(TreeNode root) {
        getflatten(root);
    }

    /**
     * 获得平整的链表
     *
     * @param root 根节点
     * @return 返回根节点
     */
    public TreeNode getflatten(TreeNode root) {
        if (root == null) {
            return root;
        }
        TreeNode move = root;
        TreeNode temp = root.right;
        // 如果左边不为空，则先把左边捋直
        if (root.left != null) {
            TreeNode flattenLeft = getflatten(root.left);
            root.right = flattenLeft;
            while (move.right != null) {
                move = move.right;
            }
        }

        // 如果右边不等空，也把右边捋直，然后接起来
        if (temp != null) {
            TreeNode flattenRight = getflatten(temp);
            move.right = flattenRight;
        }

        root.left = null;
        return root;
    }

    /**
     * 将左子树插入到右子树的地方
     * 将原来的右子树接到左子树的最右边节点
     * 考虑新的右子树的根节点，一直重复上边的过程，直到新的右子树为 null
     *
     * @param root 根节点
     * @return 返回根节点
     */
    public void flattenLk(TreeNode root) {
        while (root != null) {
            //左子树为 null，直接考虑下一个节点
            if (root.left == null) {
                root = root.right;
            } else {
                // 找左子树最右边的节点
                TreeNode pre = root.left;
                while (pre.right != null) {
                    pre = pre.right;
                }
                //将原来的右子树接到左子树的最右边节点
                pre.right = root.right;
                // 将左子树插入到右子树的地方
                root.right = root.left;
                root.left = null;
                // 考虑下一个节点
                root = root.right;
            }
        }
    }
}
