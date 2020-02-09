package com.example.helloworld.oj;

import java.util.HashSet;

/**
 * 给定一个链表，返回链表开始入环的第一个节点。 如果链表无环，则返回 null。
 * 为了表示给定链表中的环，我们使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。
 * 说明：不允许修改给定的链表。
 * 来源：力扣（LeetCode） 142 中等
 * 链接：https://leetcode-cn.com/problems/linked-list-cycle-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class LinkedListCycle2 {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        LinkedListCycle2 linkedListCycle2 = new LinkedListCycle2();
        ListNode listNode1 = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);

        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;
        listNode5.next = listNode3;
        System.out.println(linkedListCycle2.detectCycle(listNode1).val);
    }

    /**
     * 检测是否有环
     * 核心思想：利用hashset记录遍历过的节点
     *
     * @param head 头结点
     * @return 有：第一个环节点，无：null
     */
    public ListNode detectCycle(ListNode head) {
        HashSet<ListNode> hashSet = new HashSet<>();
        while (head != null) {
            if (hashSet.contains(head)) {
                return head;
            } else {
                hashSet.add(head);
                head = head.next;
            }
        }
        return null;
    }


    /**
     * 检测是否有环
     * 核心思想：利用快慢指针计算环的大小，然后在利用新的快慢指针计算第一个环节点
     *
     * @param head 头结点
     * @return 有：第一个环节点，无：null
     */
    public ListNode detectCycle2(ListNode head) {
        if (head == null) {
            return null;
        }
        // 首先通过快慢指针，找到是否有环，同时还能找到环的相遇节点
        ListNode fastNode = head;
        ListNode slowNode = head;
        do {
            slowNode = slowNode.next;
            fastNode = fastNode.next;
            if (fastNode == null) {
                return null;
            }
            fastNode = fastNode.next;
            if (slowNode == fastNode) {
                break;
            }
        } while (fastNode != null);
        if (fastNode == null) {
            return null;
        }
        // 新的快指针和新的慢指针同时再次出发，总会在第一个环结点处相遇
        ListNode newFastNode = fastNode;
        ListNode newSlowNode = head;
        while (newFastNode != newSlowNode) {
            newFastNode = newFastNode.next;
            newSlowNode = newSlowNode.next;
        }
        return newSlowNode;
    }
}
