package com.example.helloworld.oj;

/**
 * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，
 * 影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
 * 给定一个代表每个房屋存放金额的非负整数数组，计算你在不触动警报装置的情况下，能够偷窃到的最高金额。
 * 来源：力扣（LeetCode） 198 简单
 * 链接：https://leetcode-cn.com/problems/house-robber
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class HouseRobber {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        HouseRobber houseRobber = new HouseRobber();
        int[] nums = {2, 7, 9, 3, 1};
        System.out.println(houseRobber.rob(nums));
    }

    /**
     * 计算能够偷窃到的最高金额
     * 核心思想：动态规划
     *
     * @param nums 金额数组
     * @return 最高金额
     */
    public int rob(int[] nums) {
        int[][] dp = new int[nums.length + 1][2];
        for (int i = 1; i <= nums.length; i++) {
            // 这家不偷的最大收益就是前一家偷或者不偷的最大收益
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1]);
            // 这家偷的最大收益就是前一家不偷的最大收益加上这家偷的收益之和
            dp[i][1] = dp[i - 1][0] + nums[i - 1];
        }
        // 最后看看最后一家偷和不偷的最大收益之和
        return Math.max(dp[nums.length][0], dp[nums.length][1]);
    }
}
