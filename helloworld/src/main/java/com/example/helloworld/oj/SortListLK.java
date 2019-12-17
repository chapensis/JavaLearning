package com.example.helloworld.oj;

public class SortListLK {
    public static void main(String[] args) {

    }

    public ListNode insertionSortList(ListNode head) {
        ListNode dummy = new ListNode(0), pre;
        dummy.next = head;

        while (head != null && head.next != null) {
            if (head.val <= head.next.val) {
                head = head.next;
                continue;
            }
            pre = dummy;

            while (pre.next.val < head.next.val) {
                pre = pre.next;
            }

            ListNode temp = head.next;
            head.next = temp.next;
            temp.next = pre.next;
            pre.next = temp;
        }
        return dummy.next;
    }
}
