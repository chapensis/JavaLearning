package com.example.helloworld.oj;

/**
 * 编写一个程序，找到两个单链表相交的起始节点。
 * leetcode 160 简单
 * https://leetcode-cn.com/problems/intersection-of-two-linked-lists/
 *
 * @author yangchang
 */
public class IntersectionOfTwoLinkedLists {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        IntersectionOfTwoLinkedLists iotll = new IntersectionOfTwoLinkedLists();
        iotll.getIntersectionNode(null, null);
    }

    /**
     * 找到两个单链表相交的起始节点。
     *
     * @param headA 链表1
     * @param headB 链表2
     * @return
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }

        ListNode moveA = headA;
        ListNode moveB = headB;
        boolean changeA = false;
        boolean changeB = false;
        while (moveA != moveB) {
            moveA = moveA.next;
            if (moveA == null) {
                if (changeA) {
                    return null;
                } else {
                    moveA = headB;
                    changeA = true;
                }
            }

            moveB = moveB.next;
            if (moveB == null) {
                if (changeB) {
                    return null;
                } else {
                    moveB = headA;
                    changeB = true;
                }
            }
        }
        return moveA;
    }
}
