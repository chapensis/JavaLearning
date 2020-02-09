package com.example.helloworld.oj;

/**
 * 给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数大于 ⌊ n/2 ⌋ 的元素。
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 * 来源：力扣（LeetCode） 169
 * 链接：https://leetcode-cn.com/problems/majority-element
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class MajorityElement {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        MajorityElement majorityElement = new MajorityElement();
        int[] nums = {2, 2, 1, 1, 1, 2, 2};
        System.out.println(majorityElement.majorityElement(nums));
    }

    /**
     * 找到数组中的多数元素
     * 核心思想：用一个数num表示当前出现次数最多的数，以及出现的次数count，
     * 如果次数count是0，且来了新的数，则替换num,并重置count为1
     *
     * @param nums 数组
     * @return 多数元素
     */
    public int majorityElement(int[] nums) {
        int num = nums[0];
        int count = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == num) {
                count++;
            } else {
                if (count > 0) {
                    count--;
                } else {
                    num = nums[i];
                    count = 1;
                }
            }
        }
        return num;
    }
}
