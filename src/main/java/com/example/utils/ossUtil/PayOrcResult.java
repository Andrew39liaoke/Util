package com.example.utils.ossUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信转账内容解析工具
 */
public class PayOrcResult {
    /**
     * @param content 图片识别解析出来的内容 如："HDn5G HD21l.1 ..l9 ◇*汇42148：02 转账-转给 张三  -80.00 当前状态  " +
     *                "对方已收钱 转账说明 微信转账  转账时间  2023年9月23日21：34：45 收款时间 2023年9月24日09：40：15 \n" +
     *                "支付方式 零钱 转账单号 1000050001202309230810128205677 账单服务 对订单有疑惑 ● 定位到聊天位置 申请转账电子凭证 气 查看往来转账 本服务由财付通提供 ";
     * @return
     * @throws Exception
     */
    public static Expenses parseContent(String content) throws Exception {
        Expenses expenses = new Expenses();
        expenses.setPayDate(getPayDate(content));
        expenses.setPayAmount(getPayAmount(content));
        expenses.setPayee(getPayee(content));
        return expenses;
    }

    //支付时间
    public static Date getPayDate(String content) throws Exception {
        // 定义日期时间格式的正则表达式模式
        String regex = "\\d{4}年\\d{1,2}月\\d{1,2}日\\d{2}：\\d{2}：\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        String dateTime = null;
        Date payDate = null;
        String formattedDateTime = null;
        if (matcher.find()) {
            dateTime = matcher.group();
            formattedDateTime = dateTime.replaceAll("(\\d{4})年(\\d{1,2})月(\\d{1,2})日(\\d{2})：(\\d{2})：(\\d{2})", "$1-$2-$3 $4:$5:$6");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            payDate = simpleDateFormat.parse(formattedDateTime);
        } else {
            System.out.println("No unique date time format found.");
        }
        return payDate;
    }

    //取得转账金额
    public static BigDecimal getPayAmount(String content) {
        String numberRegex = "-\\d+(\\.\\d+)?";
        Pattern numberPattern = Pattern.compile(numberRegex);
        Matcher numberMatcher = numberPattern.matcher(content);
        String endSubstr = null;
        if (numberMatcher.find()) {
            endSubstr = numberMatcher.group().replaceAll("-", "");
        } else {
            System.out.println("No float value found.");
        }
        return new BigDecimal(endSubstr);
    }

    //获取收款人信息
    public static String getPayee(String content) {
        // 多种可能的开始子串，使用正则语法的“或”
        String startSubstrRegex = "(转账-转给|商户全称)";

        // 定义浮点数的正则表达式模式
        String numberRegex = "-\\d+(\\.\\d+)?";
        Pattern numberPattern = Pattern.compile(numberRegex);
        Matcher numberMatcher = numberPattern.matcher(content);

        String endSubstr = null;
        if (numberMatcher.find()) {
            endSubstr = numberMatcher.group();
        } else {
            System.out.println("No float value found.");
            return null;
        }

        // 构建正则表达式，匹配从 startSubstr 到 endSubstr 中间的内容
        String regex = startSubstrRegex + "(.*?)" + Pattern.quote(endSubstr);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        String extractedSubstr = null;
        if (matcher.find()) {
            extractedSubstr = matcher.group(2); // group(1) 是 start 匹配，group(2) 是中间内容
            return extractedSubstr.trim();
        } else {
            System.out.println("Substring not found.");
            return null;
        }
    }
}