package com.example.helloworld.oj;

/**
 * 在上次打劫完一条街道之后和一圈房屋后，小偷又发现了一个新的可行窃的地区。
 * 这个地区只有一个入口，我们称之为“根”。 除了“根”之外，每栋房子有且只有一个“父“房子与之相连。
 * 一番侦察之后，聪明的小偷意识到“这个地方的所有房屋的排列类似于一棵二叉树”。
 * 如果两个直接相连的房子在同一天晚上被打劫，房屋将自动报警。
 * 计算在不触动警报的情况下，小偷一晚能够盗取的最高金额。
 * 来源：力扣（LeetCode） 337 简单
 * 链接：https://leetcode-cn.com/problems/house-robber-iii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class HouseRobber3Lk {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        HouseRobber3Lk houseRobber3 = new HouseRobber3Lk();
        System.out.println(houseRobber3.rob(null));
    }

    /**
     * 计算能够盗取的最高金额
     * 核心思想：递归，判断打劫还是不打劫当前节点
     *
     * @param root 根节点
     * @return 最高金额
     */
    public int rob(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int[] result = robResult(root);
        return Math.max(result[0], result[1]);
    }

    /**
     * 计算能够盗取的最高金额
     * 核心思想：递归，当前节点打劫和不打劫的最优解都返回
     *
     * @param root 根节点
     * @return 最高金额
     */
    public int[] robResult(TreeNode root) {
        if (root == null) {
            return new int[2];
        }
        // 当前节点打劫和不打劫的最优解都返回
        int[] result = new int[2];
        int[] left = robResult(root.left);
        int[] right = robResult(root.right);
        // 当前节点不打劫，则说明子节点可以打劫
        result[0] = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        // 当前节点打劫，则说明子节点不可以打劫
        result[1] = root.val + left[0] + right[0];
        return result;
    }
}
