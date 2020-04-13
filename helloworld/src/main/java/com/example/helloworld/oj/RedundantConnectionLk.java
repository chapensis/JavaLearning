package com.example.helloworld.oj;

import java.util.Arrays;

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
public class RedundantConnectionLk {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        RedundantConnectionLk redundantConnection2 = new RedundantConnectionLk();
        int[][] edge = {{3, 4}, {4, 1}, {1, 2}, {5, 1}, {2, 3}};
        System.out.println(Arrays.toString(redundantConnection2.findRedundantDirectedConnection(edge)));
    }

    /**
     * 入度为2代表这个点有两个父节点那么肯定要去点一个，那就都尝试一次从后往前看看一旦去除仍可连通就return
     * 如果没有入度为2.那就从后往前考虑入度为1的点去掉。意思让这个点为根(根没有父节点入度为0)
     * 作者：jin-mu-yan-3
     * 链接：https://leetcode-cn.com/problems/redundant-connection-ii/solution/fen-bie-tao-lun-ru-du-wei-2he-ru-du-wei-1de-qing-k/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     *
     * @param edges
     * @return
     */
    public int[] findRedundantDirectedConnection(int[][] edges) {
        // 表示每个节点的入度
        int[] degree = new int[edges.length + 1];
        for (int[] edge : edges) {
            degree[edge[1]]++;
        }
        for (int i = edges.length - 1; i >= 0; i--) {
            // 入度为2，则需要去除一个
            if (degree[edges[i][1]] == 2) {
                if (helper(edges, i)) {
                    return edges[i];
                }
            }
        }
        // 没有入度为2，则需要从后往前判断，去除一个合法的就行
        // 即找出最后一个导致环路的边
        for (int i = edges.length - 1; i >= 0; i--) {
            if (helper(edges, i)) {
                return edges[i];
            }
        }
        return new int[0];
    }

    /**
     * 判断是否是合法的“有根树”
     * @param edges 有向图边矩阵
     * @param i 要忽略的节点
     * @return 是否合法
     */
    public boolean helper(int[][] edges, int i) {
        UnionFind u = new UnionFind(edges.length + 1);
        u.setCount(edges.length);
        for (int j = 0; j < edges.length; j++) {
            if (i != j) {
                u.connect(edges[j][0], edges[j][1]);
            }
        }
        return u.getCount() == 1;
    }

    class UnionFind {
        private int[] father;
        // count应该就是大哥的数量
        private int count;

        public UnionFind(int n) {
            father = new int[n];
            for (int i = 0; i < n; i++) {
                father[i] = i;
            }
        }

        int find(int x) {
            if (father[x] == x) {
                return x;
            }
            return father[x] = find(father[x]);
        }

        public void connect(int x, int y) {
            int rootx = find(x);
            int rooty = find(y);
            if (rootx != rooty) {
                father[rootx] = rooty;
                count--;
            }
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
