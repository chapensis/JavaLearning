package com.example.helloworld.oj;

import java.util.ArrayList;

public class DynamicProgramming {
    public static void main(String[] args) {
        int[] A = {1, 4, 6, 2, 8, 9, 7};
        System.out.println(LIS(A));
        System.out.println(LIS2(A));
    }

    /**
     * 最长递增子序列（Longest Increasing Subsequence）
     * 问题描述。给定长度为N的数组A，计算A的最长单调递增的子序列（不一定连续）。
     * 如给定数组A{5，6，7，1，2，8}，则A的LIS为{5，6，7，8}，长度为4.
     * 思路：因为子序列要求是递增的，所以重点是子序列的起始字符和结尾字符，
     * 因此我们可以利用结尾字符。想到：以A[0]结尾的最长递增子序列有多长？
     * 以A[1]结尾的最长递增子序列有多长？……以A[n-1]结尾的最长递增子序列有多长？
     * 使用动态规划方法的到O(N2)的时间复杂度算法
     *
     * @param A
     * @return
     */
    public static int LIS(int[] A) {
        if (A == null || A.length == 0) {
            return 0;
        }
        // 使用额外空间来保存结束点已知的路径最大值
        int[] B = new int[A.length];
        int maxLength = 1;
        B[0] = 1;
        // 遍历原数组中的每个元素
        for (int i = 0; i < A.length; i++) {
            int subMaxLength = 1;
            for (int j = 0; j < i; j++) {
                // 然后和当前元素的所有前面的元素比较，并设置最大长度
                if (A[i] > A[j] && (B[j] + 1) > subMaxLength) {
                    subMaxLength = B[j] + 1;
                }
            }
            B[i] = subMaxLength;
            if (subMaxLength > maxLength) {
                maxLength = subMaxLength;
            }
        }
        return maxLength;
    }

    /**
     * 1 4 6 2
     * 核心思想：看到了字符“2”，“2”比“1”大，比“4”小，因此将“4”直接替换成“2”，得到“126”
     * O(nlogn)
     *
     * @param A
     * @return
     */
    public static int LIS2(int[] A) {
        if (A == null || A.length == 0) {
            return 0;
        }
        ArrayList<Integer> LISList = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            boolean isRepalce = false;
            for (int j = 0; j < LISList.size(); j++) {
                if (LISList.get(j) > A[i]) {
                    LISList.set(j, A[i]);
                    isRepalce = true;
                    break;
                }
            }
            if (!isRepalce) {
                LISList.add(A[i]);
            }
        }
        return LISList.size();
    }

    /**
     * 问题描述。给定一个m*n的矩阵，每个位置是一个非负整数，从左上角开始放一个机器人，它每次只能朝右和下走，走到右下角，
     * 求机器人的所有路径中，总和最小的那条路径。如下图所示，其中图中所示的彩色方块是已知的某些非负整数值。
     * 思路：考虑一般情况下位于机器人位于某点(x, y)处，那么它是怎么来的呢？只可能来自于左边或者上边。即：
     * dp[x, y] = min(dp[x-1, y], dp[x, y-1]) + a[x, y],其中a[x, y]是棋盘中(x, y)点的权重取值。
     *
     * @param chess
     * @return
     */
    public int minPath(int[][] chess) {
        if (chess == null) {
            return 0;
        }
        int row = chess.length;
        int column = chess[0].length;
        int[][] dp = new int[row][column];
        dp[0][0] = chess[0][0];
        for (int i = 1; i < row; i++) {
            dp[i][0] = dp[i - 1][0] + chess[i][0];
        }
        for (int j = 1; j < column; j++) {
            dp[0][j] = dp[0][j - 1] + chess[0][j];
        }
        for (int i = 1; i < row; i++) {
            for (int j = 1; j < column; j++) {
                if (dp[i][j - 1] > dp[j - 1][i]) {
                    dp[i][j] = dp[j - 1][i] + chess[i][j];
                } else {
                    dp[i][j] = dp[j - 1][i] + chess[i][j];
                }
            }
        }
        return dp[row - 1][column - 1];
    }

    /**
     * 问题描述。给定某不超过100万元的现金总额，兑换成数量不限的1、2、5、10、20、50、100元的纸币组合
     *
     * @param money
     * @param total
     * @return
     */
    public int charge(int[] money, int total) {
        int moneyLength = money.length;
        int[][] dp = new int[moneyLength][total + 1];
        for (int i = 0; i < dp.length; i++) {
            // 表示用任意的钱凑出0元的组合数都为1
            dp[i][0] = 1;
        }
        for (int j = 0; j < dp[0].length; j++) {
            // 表示用1块钱凑出任何金额的组合数都为1
            dp[0][j] = 1;
        }
        for (int i = 1; i < moneyLength; i++) {
            for (int j = 1; j < dp[i].length; j++) {
                if (money[i] < j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - money[i]];
                }
            }
        }
        return dp[money[moneyLength - 1]][total];
    }
}
