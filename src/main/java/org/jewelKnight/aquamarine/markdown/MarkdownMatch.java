package com.impactcn.aquamarine.markdown;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author impactCn
 * @date 2023/11/18 10:08
 */
public class MarkdownMatch {

    /**
     * 有序列表
     */
    private static final String order = "^(\\s*[0-9]+\\.+\\x20).*";

    /**
     * 无序列表
     */
    private static final String disorder = "^(\\s*[-*+]\\x20)+(.*)";

    /**
     * 代码块
     */
    private static final String code = "```([\\s\\S]*?)```";

    /**
     * 标题
     */
    private static final String head = "(^(?<HEADKEY>#{1,6})(?<HEADVAL> .*))";

    /**
     * 横线
     */
    private static final String br = "^(-+)\\s*$";

    /**
     * 引用
     */
    private static final String quote = "^>+( .*)";

    /**
     * 链接
     */
    private static final String img_link = "!\\[[^]](.*)]\\((.*)[^)]\\)";

    /**
     * 有序列表匹配里面的 key
     */
    private static final String order_key = "^(?<ORDER>\\s*[0-9]+\\.+\\x20).*";

    private static final String disorder_key = "^(?<DISORDER>\\s*[-*+]\\x20).*";

    private static final String bold = "(?<BOLD>\\*\\*|__)\\S.*\\S\\1";


    public static final Pattern PATTERN = Pattern.compile(
            bold
                    + "|" + head
                    + "|(?<BR>" + br + ")"
                    + "|(?<QUOTE>" + quote + ")"
                    + "|(?<LINK>" + img_link + ")"
                    + "|(?<CODE>" + code + ")"
                    + "|" + order_key
                    + "|" + disorder_key,
            Pattern.MULTILINE
    );

    /**
     * 匹配有序 & 无序列表
     * @param text
     * @return
     */
    public static String matchList(String text) {


        CompletableFuture<String> orderFuture = CompletableFuture.supplyAsync(() -> {
            Pattern pattern = Pattern.compile(order);
            return find(pattern, text);
        });

        CompletableFuture<String> disorderFuture = CompletableFuture.supplyAsync(() -> {
            Pattern pattern = Pattern.compile(disorder);
            return find(pattern, text);
        });

        CompletableFuture<String> res = orderFuture.thenCombineAsync(disorderFuture, (orderRes, disorderRes) -> {
            if (orderRes != null) {
                return increment(orderRes);
            }
            return disorderRes;

        });

        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static String find(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 递增数字
     * @param text
     * @return
     */
    public static String increment(String text){

        StringBuilder strBuilder = new StringBuilder();
        StringBuilder numberBuild = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                numberBuild.append(text.charAt(i));
            } else {
                if (numberBuild.isEmpty()) {
                    strBuilder.append(text.charAt(i));
                } else {
                    int newNumber = Integer.parseInt(numberBuild.toString()) + 1;
                    strBuilder.append(newNumber);
                    strBuilder.append(text.charAt(i));
                    numberBuild.setLength(0);
                }
            }
        }

        return strBuilder.toString();
    }


}
