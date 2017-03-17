package com.yaiba.keep;

import java.util.Random;
/**
 * Created by benyaiba on 2017/03/16.
 */

public class PassWordCreate {

    /**
     * 获得密码
     * @param len 密码长度
     * @return
     */

    public static String getRandomString(int len,Boolean isNumChecked,Boolean isUseLowCaseChecked,Boolean isUseUpCaseChecked) { //length表示生成字符串的长度

        String base = "";
        String numBase = "0123456789";
        String lowCaseWordBase = "qwertyuiopasdfghjklzxcvbnm";
        String upCaseWordBase = "QWERTYUIOPASDFGHJKLZXCVBNM";

        if(isNumChecked){
            base = base +  numBase;
        }

        if(isUseLowCaseChecked){
            base = base +  lowCaseWordBase;
        }

        if(isUseUpCaseChecked){
            base = base +  upCaseWordBase;
        }

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
