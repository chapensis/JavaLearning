package com.example.helloworld.oj;

/**
 * 给定一个链表，判断链表中是否有环。
 * 为了表示给定链表中的环，我们使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。
 * 来源：力扣（LeetCode） 141 简单
 * 链接：https://leetcode-cn.com/problems/linked-list-cycle
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class LinkedListCycle {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        LinkedListCycle linkedListCycle = new LinkedListCycle();
        System.out.println(linkedListCycle.hasCycle(null));
    }

    /**
     * 利用快慢指针判断是否有环
     *
     * @param head 头结点
     * @return 是否有环
     */
    public boolean hasCycle(ListNode head) {
        if (head == null) {
            return false;
        }
        ListNode fastNode = head;
        ListNode slowNode = head;
        do {
            slowNode = slowNode.next;
            fastNode = fastNode.next;
            if (fastNode == null) {
                return false;
            }
            fastNode = fastNode.next;
            if (slowNode == fastNode) {
                return true;
            }
        } while (fastNode != null);
        return false;
    }
}
