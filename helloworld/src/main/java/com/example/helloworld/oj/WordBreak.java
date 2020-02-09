package com.example.helloworld.oj;

import java.util.Arrays;
import java.util.HashMap;
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
public class WordBreak {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        WordBreak wordBreak = new WordBreak();
        String str = "leetcode";
        List<String> wordDict = Arrays.asList("leet", "code", "sand", "and", "cat");
        System.out.println(wordBreak.wordBreak(str, wordDict));
    }

    HashMap<String, Boolean> hashMap = new HashMap<>();

    /**
     * 是否可以拆分单词
     *
     * @param s        单词
     * @param wordDict 单词字典
     * @return true: 可以 false: 不可以
     */
    public boolean wordBreak(String s, List<String> wordDict) {
        return wordBreak(s, wordDict, 0);
    }

    /**
     * 是否可以拆分单词
     *
     * @param s          单词
     * @param wordDict   单词字典
     * @param startIndex 起始位置
     * @return true: 可以 false: 不可以
     */
    public boolean wordBreak(String s, List<String> wordDict, int startIndex) {
        if (s == null || s.length() == 0) {
            return true;
        }
        if (startIndex >= wordDict.size()) {
            return false;
        }
        // 利用hashmap保存中间状态
        String key = s + startIndex;
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        if (s.startsWith(wordDict.get(startIndex))) {
            String newStr = s.substring(wordDict.get(startIndex).length());
            boolean result = wordBreak(newStr, wordDict, 0) || wordBreak(s, wordDict, startIndex + 1);
            hashMap.put(key, result);
            return result;
        } else {
            return wordBreak(s, wordDict, startIndex + 1);
        }
    }
}
