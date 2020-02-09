package com.example.helloworld.oj;

/**
 * 你是一个专业的小偷，计划偷窃沿街的房屋，每间房内都藏有一定的现金。
 * 这个地方所有的房屋都围成一圈，这意味着第一个房屋和最后一个房屋是紧挨着的。
 * 同时，相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
 * 给定一个代表每个房屋存放金额的非负整数数组，计算你在不触动警报装置的情况下，能够偷窃到的最高金额。
 * 来源：力扣（LeetCode） 213 中等
 * 链接：https://leetcode-cn.com/problems/house-robber-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class HouseRobber2 {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        HouseRobber2 houseRobber2 = new HouseRobber2();
        int[] nums = {2};
        System.out.println(houseRobber2.rob(nums));
    }

    /**
     * 计算能够偷窃到的最高金额
     *
     * @param nums 金额数组
     * @return 最高金额
     */
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }
        int[][] dp = new int[nums.length][2];
        // 1、第一种情况，第一家偷
        dp[0][1] = nums[0];
        dp[0][0] = nums[0];
        dp[1][1] = nums[0];
        dp[1][0] = nums[0];
        for (int i = 2; i < nums.length - 1; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1]);
            dp[i][1] = dp[i - 1][0] + nums[i];
        }
        int max = Math.max(dp[nums.length - 2][0], dp[nums.length - 2][1]);

        // 1、第二种情况，第一家不偷
        dp[0][1] = 0;
        dp[0][0] = 0;
        for (int i = 1; i < nums.length; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1]);
            dp[i][1] = dp[i - 1][0] + nums[i];
        }
        return Math.max(max, Math.max(dp[nums.length - 1][0], dp[nums.length - 1][1]));
    }
}
