package com.example.helloworld.oj;

import java.util.Stack;

/**
 * 设计一个支持 push，pop，top 操作，并能在常数时间内检索到最小元素的栈。
 * push(x) -- 将元素 x 推入栈中。
 * pop() -- 删除栈顶的元素。
 * top() -- 获取栈顶元素。
 * getMin() -- 检索栈中的最小元素。
 * 来源：力扣（LeetCode） 155 简单
 * 链接：https://leetcode-cn.com/problems/min-stack
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class MinStack {
    Stack<Integer> nums;
    Stack<Integer> mins;

    /**
     * initialize your data structure here.
     */
    public MinStack() {
        nums = new Stack<>();
        mins = new Stack<>();
    }

    public void push(int x) {
        nums.push(x);
        if (mins.empty()) {
            mins.push(x);
        } else {
            mins.push(Math.min(mins.peek(), x));
        }
    }

    public void pop() {
        nums.pop();
        mins.pop();
    }

    public int top() {
        return nums.peek();
    }

    public int getMin() {
        return mins.peek();
    }

    /**
     * 主函数
     * 核心思想：就是一个辅助栈，同步压入数据
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.getMin());   // 返回 -3.
        minStack.pop();
        System.out.println(minStack.top());     // 返回 0.
        System.out.println(minStack.getMin());   // 返回 -2.
    }
}
