package com.planb.util;


import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidateUtil {

    /**
     * 检查字符串长度
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 检查结果
     */
    public static boolean checkLength(final String str, final int minLength, final int maxLength) {
        if (maxLength < minLength) {
            return false;
        }

        if (ValidateUtil.isBlank(str)) {
            if (minLength == 0) {
                return true;
            }
            return false;
        }
        final int length = str.length();
        if (length >= minLength && length <= maxLength) {
            return true;
        }
        return false;
    }

    /**
     * 检查参数是否有空
     *
     * @param objects 待验证对象，可以是多个
     * @return 验证结果
     */
    public static boolean hasBlank(final Object... objects) {
        for (final Object obj : objects) {
            if (isBlank(obj)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断对象是否在集合里
     *
     * @param searchObj 对象
     * @param list      集合
     * @param b
     * @return 判断结果
     */
    public static boolean inCollection(final Object searchObj, final Collection<Object> list, boolean b) {
        return inCollection(searchObj, list, false);
    }


    /**
     * 判断是否是Base64字符串
     *
     * @param str Base64字符串
     * @return 判断结果
     */
    public static boolean isBase64(final String str) {
        return isMatch(str, "[A-Za-z0-9\\+\\/\\=]");
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 要判断的对象
     * @return 判断结果
     */
    public static boolean isBlank(final Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return "".equals(((String) obj).trim());
        }

        if (obj instanceof Collection) {
            final Collection collection = (Collection) obj;
            return collection.isEmpty();
        }

        return false;
    }

    /**
     * 判断数是否为空
     *
     * @param obj 要判断的对象
     * @return 判断结果
     */
    public static boolean isNull(final Integer obj) {
        boolean blank = isBlank(obj);
        if (blank) {
            return true;
        }
        return obj == 0;
    }

    /**
     * 判断数是否不为空
     *
     * @param obj 要判断的对象
     * @return 判断结果
     */
    public static boolean isNotNull(final Integer obj) {
        return !isNull(obj);
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 对象
     * @return 验证结果
     */
    public static boolean isNotBlank(final Object obj) {
        return !isBlank(obj);
    }

    /**
     * 判断是否是Email地址字符串
     *
     * @param strEmail URL地址字符串
     * @return 判断结果
     */
    public static boolean isEmail(final String strEmail) {
        return isMatch(strEmail, "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 验证手机号码正确性
     *
     * @param strMobilePhone 手机号码
     * @return 验证结果
     */
    public static boolean isMobilePhoneNo(final String strMobilePhone) {
        return isMatch(strMobilePhone, "^0?(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$");
    }

    /**
     * 判断是否为正整数字符串
     *
     * @param intStr 数字字符串
     * @return 判断结果
     */
    public static boolean isInt(final String intStr) {
        return isMatch(intStr, "^[0-9]*$");
    }

    /**
     * 判断是否为IP字符串
     *
     * @param ipStr ip字符串
     * @return 判断结果
     */
    public static boolean isIP(final String ipStr) {
        return isMatch(ipStr, "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");
    }

    /**
     * 判断字符串是否和正则表达式相匹配,大小写敏感
     *
     * @param str   字符串
     * @param regEx 正则表达式
     * @return 判断结果
     */
    public static boolean isMatch(final String str, final String regEx) {
        return isMatch(str, regEx, false);
    }

    /**
     * 判断字符串是否和正则表达式相匹配
     *
     * @param str             字符串
     * @param regEx           正则表达式
     * @param caseInsensetive 是否不区分大小写, true为不区分, false为区分
     * @return 判断结果
     */
    public static boolean isMatch(final String str, final String regEx, final boolean caseInsensetive) {
        if (!hasBlank(str, regEx)) {
            Pattern pattern;
            if (caseInsensetive) {
                pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(regEx);
            }
            final Matcher matcher = pattern.matcher(str);
            return matcher.find();
        }
        return false;
    }

    /**
     * 判断是否是数字
     *
     * @param strNumber 数字字符串
     * @return 判断结果
     */
    public static boolean isNumber(final String strNumber) {
        return isMatch(strNumber, "^\\d+$");
    }

    /**
     * 判断是否是小数
     *
     * @param strDecimal 小数字符串
     * @return 判断结果
     */
    public static boolean isDecimal(final String strDecimal) {
        return isMatch(strDecimal, "^(\\-|\\+)?\\d+(\\.\\d+)?$");
    }

    /**
     * 判断是否是Sql危险字符
     *
     * @param sqlStr sql字符串
     * @return 判断结果
     */
    public static boolean isSafeSqlString(final String sqlStr) {
        return isMatch(sqlStr, "[-|;|,|\\/|\\(|\\)|\\[|\\]|\\}|\\{|%|@|\\*|!|\\']");
    }

    /**
     * 判断是否是URL地址字符串
     *
     * @param strUrl URL地址字符串
     * @return 判断结果
     */
    public static boolean isUrl(final String strUrl) {
        return isMatch(strUrl,
                "^(http|https)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&%\\$\\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|localhost|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{1,10}))(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\?\\'\\\\\\+&%\\$#\\=~_\\-]+))*$");
    }

    /**
     * 检查对象类型是不是Number类型
     *
     * @param type 类类型
     * @return 验证结果
     */
    public static boolean isNumber(Class<?> type) {
        return type.equals(Byte.class) || type.equals(Byte.TYPE) || type.equals(Short.class) || type.equals(Short.TYPE) || type.equals(Integer.class) || type.equals(Integer.TYPE) || type.equals(Long.class) || type.equals(Long.TYPE) || type.equals(Float.class) || type.equals(Float.TYPE) || type.equals(Double.class) || type.equals(Double.TYPE);
    }

    /**
     * 身份证15位编码规则：dddddd yymmdd xx p
     * dddddd：6位地区编码
     * yymmdd: 出生年(两位年)月日，如：910215
     * xx: 顺序编码，系统产生，无法确定
     * p: 性别，奇数为男，偶数为女
     * <p>
     * 身份证18位编码规则：dddddd yyyymmdd xxx y
     * dddddd：6位地区编码
     * yyyymmdd: 出生年(四位年)月日，如：19910215
     * xxx：顺序编码，系统产生，无法确定，奇数为男，偶数为女
     * y: 校验码，该位数值可通过前17位计算获得
     * <p>
     * 前17位号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ]
     * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]
     * 如果验证码恰好是10，为了保证身份证是十八位，那么第十八位将用X来代替
     * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )
     * i为身份证号码1...17 位; Y_P为校验码Y所在校验码数组位置
     */
    public static boolean isIdCardNo(final String idCardNo) {

        String regIdCard = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";

        boolean match = isMatch(idCardNo, regIdCard);

        if (!match) {
            return false;
        }

        if (idCardNo.length() == 18) {

            int[] idCardWi = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2}; //将前17位加权因子保存在数组里

            int[] idCardY = new int[]{1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2}; //这是除以11后，可能产生的11位余数、验证码，也保存

            int idCardWiSum = 0; //用来保存前17位各自乖以加权因子后的总和

            for (int i = 0; i < idCardWi.length; i++) {
                idCardWiSum += Integer.parseInt(idCardNo.substring(i, i + 1)) * idCardWi[i];
            }

            int idCardMod = idCardWiSum % 11;//计算出校验码所在数组的位置

            String idCardLast = idCardNo.substring(17);//得到最后一位身份证号码


            if (idCardMod == 2) {
                if ("X".equalsIgnoreCase(idCardLast)) {
                    match = true;
                } else {
                    match = false;
                }
            } else {
                //用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                match = String.valueOf(idCardY[idCardMod]).equals(idCardLast);
            }
        }

        return match;
    }


    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }
}
