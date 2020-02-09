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
public class HouseRobber3 {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        HouseRobber3 houseRobber3 = new HouseRobber3();
        System.out.println(houseRobber3.rob(null));
    }

    /**
     * 计算能够盗取的最高金额
     *
     * @param root 根节点
     * @return 最大价值
     */
    public int rob(TreeNode root) {
        return rob(root, false);
    }

    /**
     * 计算能够盗取的最高金额
     * 核心思想：递归，判断打劫还是不打劫当前节点
     *
     * @param root  根节点
     * @param isRob 父节点是否被打劫
     * @return 最高金额
     */
    public int rob(TreeNode root, boolean isRob) {
        if (root == null) {
            return 0;
        }
        if (isRob) {
            // 如果父节点被打劫，那么当前节点就不能被打劫，但是子节点可以选择是否打劫
            return rob(root.left, false) + rob(root.right, false);
        }
        // 如果父节点没有被打劫，则当前节点可以打劫，也可以不打劫，选择最大值即可
        return Math.max(root.val + rob(root.left, true) + rob(root.right, true),
                rob(root.left, false) + rob(root.right, false));
    }
}
