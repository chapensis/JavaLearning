package com.example.helloworld.oj;

public class SortList {
    public ListNode insertionSortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode h = new ListNode(-1);
        h.next = head;
        ListNode pre = h;
        ListNode tail = head;

        // 总是尾指针的下一个和已有的排序队列进行比较
        while (tail.next != null) {
            // 只有 tail.next 比 tail 小才需要向前寻找插入点, 并且此时tail是不会变化的
            if (tail.next.val < tail.val) {
                // 寻找插入点，从 pre 开始遍历 （每次都是头节点 h 开始向后遍历，因为单向链表是无法从后往前遍）
                // 如果当前节点的值小于要插入排序的值
                while (pre.next.val < tail.next.val) {
                    // 继续向后移动
                    pre = pre.next;
                }
                ListNode tmp = tail.next;
                tail.next = tmp.next;
                tmp.next = pre.next;
                pre.next = tmp;

                // 由于每次都是从前往后找插入位置，但是单向链表是无法从后往前遍历，所以需要每次插入完成后要让 pre 复位
                pre = h;
            } else {
                // 到这直接把 tail 指针指向到下一个
                tail = tail.next;
            }
        }
        return h.next;
    }
}
