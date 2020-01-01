package com.example.helloworld.oj;

/**
 * 假设按照升序排序的数组在预先未知的某个点上进行了旋转。
 * ( 例如，数组 [0,1,2,4,5,6,7] 可能变为 [4,5,6,7,0,1,2] )。
 * 搜索一个给定的目标值，如果数组中存在这个目标值，则返回它的索引，否则返回 -1 。
 * 你可以假设数组中不存在重复的元素。
 * 你的算法时间复杂度必须是 O(log n) 级别。
 * 来源：力扣（LeetCode）33
 * 链接：https://leetcode-cn.com/problems/search-in-rotated-sorted-array
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 * @since 2019-12-23
 */
public class SearchRotationSortArray {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        SearchRotationSortArray searchRotationSortArray = new SearchRotationSortArray();
        int[] nums = {4, 5, 6, 7, 0, 1, 2};
        System.out.println(searchRotationSortArray.search(nums, 0));
    }

    /**
     * 先寻找分割点，再利用二分查找
     *
     * @param nums   数组
     * @param target 目标数字
     * @return 目标数字的索引下标
     */
    public int search(int[] nums, int target) {
        int start = 0;
        int end = nums.length - 1;
        while (start < end) {
            int middle = (start + end) / 2;
            // 如果中间点大于开始点，则说明断裂点在中间点后面的数组中,(保留开始节点的目的是防止开始节点就是断裂点)
            if (nums[middle] > nums[start]) {
                start = middle;
            } else if (nums[middle] < nums[start]) {
                // 如果中间点小于开始点，则说明断裂点在中间点前面的数组中（包含中间节点）
                end = middle;
            } else if (nums[middle] == nums[start]) {
                // 如果中间点等于开始点，则说明断裂点就是结束节点
                start = end;
            }
        }
        // 从断裂点前用二分查找找目标值
        int result = search(nums, target, 0, start - 1);
        if (result != -1) {
            return result;
        } else {
            // 找不到则从断裂点开始（包括）往后用二分查找目标值
            return search(nums, target, start, nums.length - 1);
        }
    }

    /**
     * 在升序排序的数组中二分查找指定值
     *
     * @param nums   指定数组
     * @param target 目标值
     * @param start  索引开始位置
     * @param end    索引结束位置
     * @return 目标值的索引下标
     */
    public int search(int[] nums, int target, int start, int end) {
        while (start <= end) {
            int middle = (start + end) / 2;
            if (nums[middle] == target) {
                return middle;
            } else if (nums[middle] > target) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return -1;
    }
}
