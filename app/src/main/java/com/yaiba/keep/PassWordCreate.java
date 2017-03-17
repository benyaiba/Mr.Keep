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

    public  String getRandomString(
            int len,
            Boolean isNumChecked,
            Boolean isUseLowCaseChecked,
            Boolean isUseUpCaseChecked,
            Boolean isUseSymbolChecked,
            String PasswordContainStr,
            String PasswordNotContainStr,
            String PasswordIndexStr) { //length表示生成字符串的长度

        String base = "";
        String numBase = "0123456789";
        String lowCaseWordBase = "qwertyuiopasdfghjklzxcvbnm";
        String upCaseWordBase = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String symbolBase = "!@#$";

        if(isNumChecked){
            base = base +  numBase;
        }

        if(isUseLowCaseChecked){
            base = base +  lowCaseWordBase;
        }

        if(isUseUpCaseChecked){
            base = base +  upCaseWordBase;
        }

        if(isUseSymbolChecked){
            base = base +  symbolBase;
        }

        if(!PasswordContainStr.isEmpty()){
            base = base +  PasswordContainStr;
        }

        if(!PasswordNotContainStr.isEmpty()){
             for (int x = 0; x < PasswordNotContainStr.length(); x++) {
                  base = base.replaceAll(PasswordNotContainStr.charAt(x)+"", "");
              }
        }

        if(!PasswordIndexStr.isEmpty()){
            int indexlen = PasswordIndexStr.length();
            if(len - indexlen > 0){
                return PasswordIndexStr + CreatePassword(len-indexlen,base);
            }

            if(len - indexlen == 0){
                return PasswordIndexStr;
            }

            if(len - indexlen < 0){
                return PasswordIndexStr.substring(0,len);
            }
        }

        return CreatePassword(len,base);

    }

    public String CreatePassword(int len, String base){

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();

    }

}
