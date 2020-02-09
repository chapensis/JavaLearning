package com.example.helloworld.oj;

/**
 * 给定一个整数数组 nums ，找出一个序列中乘积最大的连续子序列（该序列至少包含一个数）。
 * leetcode 152 中等
 *
 * @author yangchang
 */
public class MaximumProductSubarray {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        MaximumProductSubarray maximumProductSubarray = new MaximumProductSubarray();
        int[] nums = {-2, 3, -2, 4};
        System.out.println(maximumProductSubarray.maxProduct(nums));
        System.out.println(maximumProductSubarray.maxProduct2(nums));
        System.out.println(maximumProductSubarray.maxProductLk(nums));
    }

    /**
     * 找出一个序列中乘积最大的连续子序列
     * 暴力遍历所有答案
     *
     * @param nums
     * @return
     */
    public int maxProduct(int[] nums) {
        int max = Integer.MIN_VALUE;
        int[] maxBefore = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            int result = nums[i];
            if (result > max) {
                max = result;
            }
            if (result == 0) {
                continue;
            }
            if (maxBefore[i] > 0) {
                continue;
            }
            for (int j = i + 1; j < nums.length; j++) {
                if (result > 0) {
                    maxBefore[i] = result;
                }
                result = result * nums[j];
                if (result > max) {
                    max = result;
                }
                if (result == 0) {
                    break;
                }
            }
        }
        return max;
    }

    /**
     * 找出一个序列中乘积最大的连续子序列
     *
     * @param nums
     * @return
     */
    public int maxProduct2(int[] nums) {
        // 保存一个到某个节点为止，正数最大值和负数最大值
        int maxPositive = Integer.MIN_VALUE;
        int maxNegative = Integer.MAX_VALUE;
        int max = nums[0];
        for (int i = 0; i < nums.length; i++) {
            // 如果当前节点大于0，则正数存在，则越来越大，不存在则是当前节点。负数也越来越大，不存在还是不存在
            if (nums[i] > 0) {
                if (maxPositive > 0) {
                    maxPositive = maxPositive * nums[i];
                } else {
                    maxPositive = nums[i];
                }

                if (maxNegative < 0) {
                    maxNegative = maxNegative * nums[i];
                } else {
                    maxNegative = Integer.MAX_VALUE;
                }
            } else if (nums[i] < 0) {
                // 如果节点小于0，则正数是负数*当前节点（负数不存在，则正数也不存在）。负数是正数*当前节点，正数不存在，则负数是当前节点
                int tempMaxPositive = maxPositive;
                if (maxNegative < 0) {
                    maxPositive = maxNegative * nums[i];
                } else {
                    maxPositive = Integer.MIN_VALUE;
                }

                if (tempMaxPositive > 0) {
                    maxNegative = tempMaxPositive * nums[i];
                } else {
                    maxNegative = nums[i];
                }
            } else {
                // 等于0，则正数负数都可以算不存在，但是要比较最大值
                if (nums[i] > max) {
                    max = nums[i];
                }
                maxPositive = Integer.MIN_VALUE;
                maxNegative = Integer.MAX_VALUE;
            }
            if (maxPositive > max) {
                max = maxPositive;
            }
        }
        return max;
    }

    /**
     * 标签：动态规划
     * 遍历数组时计算当前最大值，不断更新
     * 令imax为当前最大值，则当前最大值为 imax = max(imax * nums[i], nums[i])
     * 由于存在负数，那么会导致最大的变最小的，最小的变最大的。因此还需要维护当前最小值imin，imin = min(imin * nums[i], nums[i])
     * 当负数出现时则imax与imin进行交换再进行下一步计算
     * 时间复杂度：O(n)O(n)
     * 作者：guanpengchn
     * 链接：https://leetcode-cn.com/problems/maximum-product-subarray/solution/hua-jie-suan-fa-152-cheng-ji-zui-da-zi-xu-lie-by-g/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     *
     * @param nums 数组
     * @return 最大乘积
     */
    public int maxProductLk(int[] nums) {
        // imax表示以当前节点为终结节点的最大连续子序列乘积, imin表示以当前节点为终结节点的最小连续子序列乘积
        // imax和imin都不考虑正负
        int max = Integer.MIN_VALUE, imax = 1, imin = 1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < 0) {
                int tmp = imax;
                imax = imin;
                imin = tmp;
            }
            imax = Math.max(imax * nums[i], nums[i]);
            imin = Math.min(imin * nums[i], nums[i]);

            max = Math.max(max, imax);
        }
        return max;
    }
}
