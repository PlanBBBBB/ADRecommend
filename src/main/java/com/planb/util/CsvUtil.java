package com.planb.util;

import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;

import java.io.StringWriter;
import java.util.List;

/**
 * @author PalnB
 * CSV工具类
 */
public class CsvUtil {

    /**
     * 将用户行为数据转换为 CSV 格式
     *
     * @param behaviors 用户行为数据
     * @return csv 格式的字符串
     */
    public static String behaviorsToCsv(List<UserBehavior> behaviors) {
        StringWriter stringWriter = new StringWriter();

        // 写入 CSV 文件的表头
        stringWriter.append("behaviorId|userId|adId|action|created\n");

        // 遍历查询结果并写入 CSV 文件
        for (UserBehavior behavior : behaviors) {
            stringWriter.append(behavior.getId());
            stringWriter.append("|");
            stringWriter.append(behavior.getUserId());
            stringWriter.append("|");
            stringWriter.append(behavior.getAdId());
            stringWriter.append("|");
            stringWriter.append(behavior.getAction());
            stringWriter.append("|");
            stringWriter.append(behavior.getCreated());
            stringWriter.append("\n");
        }
        return stringWriter.toString();
    }

    /**
     * 将用户数据转换为 CSV 格式
     * @param users 用户数据
     * @return csv 格式的字符串
     */
    public static String usersToCsv(List<User> users) {
        StringWriter stringWriter = new StringWriter();

        // 写入 CSV 文件的表头
        stringWriter.append("userId|interest\n");

        // 遍历查询结果并写入 CSV 文件
        for (User user : users) {
            stringWriter.append(user.getId());
            stringWriter.append("|");
            stringWriter.append(user.getInterest());
            stringWriter.append("\n");
        }
        return stringWriter.toString();
    }

    public static String adToCsv(List<Ad> ads) {
        StringWriter stringWriter = new StringWriter();

        // 写入 CSV 文件的表头
        stringWriter.append("adId|keyWords|position|startTime|endTime|type\n");

        // 遍历查询结果并写入 CSV 文件
        for (Ad ad : ads) {
            stringWriter.append(ad.getId());
            stringWriter.append("|");
            stringWriter.append(ad.getKeyWords());
            stringWriter.append("|");
            stringWriter.append(ad.getPosition());
            stringWriter.append("|");
            stringWriter.append(ad.getStartTime());
            stringWriter.append("|");
            stringWriter.append(ad.getEndTime());
            stringWriter.append("|");
            stringWriter.append(ad.getType());
            stringWriter.append("\n");
        }
        return stringWriter.toString();
    }
}
