package com.example.helloworld.oj;

/**
 * 在 O(n log n) 时间复杂度和常数级空间复杂度下，对链表进行排序。
 * leetcode 148 中等
 * https://leetcode-cn.com/problems/sort-list/
 *
 * @author yangchang
 */
public class SortList {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(4);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(1);
        ListNode listNode4 = new ListNode(3);
//        ListNode listNode5 = new ListNode(0);

        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
//        listNode4.next = listNode5;
//        listNode5.next = listNode3;

        SortList sortList = new SortList();
        ListNode sortResult = sortList.sortList(listNode1);
        sortList.printListNode(sortResult);
    }

    public void printListNode(ListNode head) {
        while (head != null) {
            System.out.println(head.val);
            head = head.next;
        }
    }

    /**
     * 对链表进行排序
     * 核心思想：1、快慢指针寻找中间节点。 2、链表进行归并排序
     *
     * @param head 头结点
     * @return 排序后的头结点
     */
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        // 快慢指针寻找中间节点
        ListNode slowNode = head;
        ListNode fastNode = head;
        while (true) {
            fastNode = fastNode.next;
            if (fastNode == null) {
                break;
            }
            fastNode = fastNode.next;
            if (fastNode == null) {
                break;
            }
            slowNode = slowNode.next;
        }
        ListNode sortNode1 = sortList(slowNode.next);
        slowNode.next = null;
        ListNode sortNode2 = sortList(head);

        ListNode sortedNode = mergeListNode(sortNode1, sortNode2);
        return sortedNode;
    }

    /**
     * 合并两个链表
     *
     * @param sortNode1 链表1
     * @param sortNode2 链表2
     * @return 合并后的结果
     */
    public ListNode mergeListNode(ListNode sortNode1, ListNode sortNode2) {
        ListNode newHead = null;
        if (sortNode1.val < sortNode2.val) {
            newHead = sortNode1;
            sortNode1 = sortNode1.next;
        } else {
            newHead = sortNode2;
            sortNode2 = sortNode2.next;
        }

        ListNode move = newHead;
        while (sortNode1 != null && sortNode2 != null) {
            if (sortNode1.val < sortNode2.val) {
                move.next = sortNode1;
                move = move.next;
                sortNode1 = sortNode1.next;
            } else {
                move.next = sortNode2;
                move = move.next;
                sortNode2 = sortNode2.next;
            }
        }
        if (sortNode1 != null) {
            move.next = sortNode1;
            return newHead;
        }

        if (sortNode2 != null) {
            move.next = sortNode2;
            return newHead;
        }
        return newHead;
    }
}
