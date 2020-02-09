package com.example.helloworld.oj;

import java.util.Arrays;
import java.util.List;

/**
 * 给定一个非空字符串 s 和一个包含非空单词列表的字典 wordDict，判定 s 是否可以被空格拆分为一个或多个在字典中出现的单词。
 * 说明：
 * 拆分时可以重复使用字典中的单词。
 * 你可以假设字典中没有重复的单词。
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/word-break
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author yangchang
 */
public class WordBreakLk {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        WordBreakLk wordBreakLk = new WordBreakLk();
        String str = "leetcode";
        List<String> wordDict = Arrays.asList("leet", "code");
        System.out.println(wordBreakLk.wordBreak(str, wordDict));
    }

    /**
     * 是否可以拆分单词
     *
     * @param s        单词
     * @param wordDict 单词字典
     * @return true: 可以 false: 不可以
     */
    public boolean wordBreak(String s, List<String> wordDict) {
        // dp[i]表示s的后i位是否可以用wordDictwordDict中的单词表示。
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i < dp.length; i++) {
            String subStr = s.substring(s.length() - i);
            for (int j = 0; j < wordDict.size(); j++) {
                String dictWord = wordDict.get(j);
                if (subStr.startsWith(dictWord)) {
                    if (dp[i - dictWord.length()]) {
                        dp[i] = true;
                        // 表示后i位是可以用wordDict表示的，就可以跳出循环了
                        break;
                    }
                }
            }
        }
        return dp[s.length()];
    }
}
