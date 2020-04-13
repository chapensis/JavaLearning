package com.example.helloworld.oj;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 在本问题中，有根树指满足以下条件的有向图。该树只有一个根节点，所有其他节点都是该根节点的后继。
 * 每一个节点只有一个父节点，除了根节点没有父节点。
 * 输入一个有向图，该图由一个有着N个节点 (节点值不重复1, 2, ..., N) 的树及一条附加的边构成。
 * 附加的边的两个顶点包含在1到N中间，这条附加的边不属于树中已存在的边。
 * 结果图是一个以边组成的二维数组。 每一个边 的元素是一对 [u, v]，用以表示有向图中连接顶点 u and v和顶点的边，
 * 其中父节点u是子节点v的一个父节点。
 * 返回一条能删除的边，使得剩下的图是有N个节点的有根树。若有多个答案，返回最后出现在给定二维数组的答案。
 * 来源：力扣（LeetCode） 685
 * 链接：https://leetcode-cn.com/problems/redundant-connection-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class RedundantConnection2 {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        RedundantConnection2 redundantConnection2 = new RedundantConnection2();
        int[][] edge = {{3, 4}, {4, 1}, {1, 2}, {5, 1}, {2, 3}};
        System.out.println(Arrays.toString(redundantConnection2.findRedundantDirectedConnection(edge)));
    }

    /**
     * 返回一条能删除的边，使得剩下的图是有N个节点的有根树
     *
     * @param edges 有向图矩阵
     * @return 能删除的边
     */
    public int[] findRedundantDirectedConnection(int[][] edges) {
        int[] bosses = new int[edges.length + 1];
        for (int i = 0; i < bosses.length; i++) {
            bosses[i] = i;
        }
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < edges.length; i++) {
            int first = findBoss(bosses, edges[i][0]);
            int second = findBoss(bosses, edges[i][1]);
            if (first != second) {
                // 自己是自己的大哥，随便跟别人
                if (second == edges[i][1]) {
                    bosses[second] = first;
                } else {
                    // 如果两个节点大哥不一样，且第二个节点自己也不是大哥
                    // 1、不认新大哥，看试试行不行。期望后面新大哥跟我认同一个大哥
                    int[] newBosses1 = new int[bosses.length];
                    System.arraycopy(bosses, 0, newBosses1, 0, bosses.length);
                    if (findRedundantDirectedConnection(edges, newBosses1, i + 1)) {
                        return new int[]{edges[i][0], edges[i][1]};
                    }
                    // 2、那就是之前大哥认错了，现在不认之前的大哥就可以了
                    return new int[]{hashMap.get(edges[i][1]), edges[i][1]};
                }
            } else {
                // 自己是自己的大哥，随便跟别人
                if (second == edges[i][1]) {
                    // 然后从现在开始一直往前都不认大哥试试
                    for (int ignoreIndex = i; ignoreIndex >= 0; ignoreIndex--) {
                        int[] newBosses = new int[bosses.length];
                        if (findRedundantDirectedConnection2(edges, newBosses, ignoreIndex)) {
                            return new int[]{edges[ignoreIndex][0], edges[ignoreIndex][1]};
                        }
                    }
                } else {
                    return new int[]{edges[i][0], edges[i][1]};
                }
            }
            hashMap.put(edges[i][1], edges[i][0]);
        }
        return null;
    }

    /**
     * 查看后续情况
     *
     * @param edges
     * @param bosses
     * @param index
     * @return
     */
    public boolean findRedundantDirectedConnection(int[][] edges, int[] bosses, int index) {
        for (int i = index; i < edges.length; i++) {
            int first = findBoss(bosses, edges[i][0]);
            int second = findBoss(bosses, edges[i][1]);
            if (second == edges[i][1]) {
                bosses[second] = first;
            } else {
                return false;
            }
        }
        int bossNum = 0;
        for (int i = 1; i <= edges.length; i++) {
            if (bosses[i] == i) {
                bossNum++;
            }
        }
        if (bossNum != 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 查看后续情况
     *
     * @param edges
     * @param bosses
     * @param ignoreIndex
     * @return
     */
    public boolean findRedundantDirectedConnection2(int[][] edges, int[] bosses, int ignoreIndex) {
        for (int i = 0; i < bosses.length; i++) {
            bosses[i] = i;
        }
        for (int i = 0; i < edges.length; i++) {
            if (i == ignoreIndex) {
                continue;
            }
            int first = findBoss(bosses, edges[i][0]);
            int second = findBoss(bosses, edges[i][1]);
            if (second == edges[i][1]) {
                bosses[second] = first;
            } else {
                return false;
            }
        }
        int bossNum = 0;
        for (int i = 1; i <= edges.length; i++) {
            if (bosses[i] == i) {
                bossNum++;
            }
        }
        if (bossNum != 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 寻找大哥
     *
     * @param bosses 大哥列表
     * @param index  小弟索引
     * @return 大哥索引下标
     */
    public int findBoss(int[] bosses, int index) {
        while (bosses[index] != index) {
            index = bosses[index];
        }
        return index;
    }
}
