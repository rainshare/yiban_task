package cn.rainshare.task.yiban;


import cn.rainshare.task.dao.GetGpsDao;
import cn.rainshare.task.utils.EncryptUtil;
import cn.rainshare.task.utils.GetDateUtil;
import cn.rainshare.task.utils.HttpUtil;
import com.alibaba.fastjson.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;


public class YibanTask {
    //易班密码公钥
    public static final String PUBLIC_KEY =
                    "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxbzZk3gEsbSe7A95iCIk" +
                    "59Kvhs1fHKE6zRUfOUyTaKd6Rzgh9TB/jAK2ZN7rHzZExMM9kw6lVwmlV0VabSmO" +
                    "YL9OOHDCiEjOlsfinlZpMZ4VHg8gkFoOeO4GvaBs7+YjG51Am6DKuJWMG9l1pAge" +
                    "96Uhx8xWuDQweIkjWFADcGLpDJJtjTcrh4fy8toE0/0zJMmg8S4RM/ub0q59+VhM" +
                    "zBYAfPmCr6YnEZf0QervDcjItr5pTNlkLK9E09HdKI4ynHy7D9lgLTeVmrITdq++" +
                    "mCbgsF/z5Rgzpa/tCgIQTFD+EPPB4oXlaOg6yFceu0XUQEaU0DvAlJ7Zn+VwPkkq" +
                    "JEoGudklNePHcK+eLRLHcjd9MPgU6NP31dEi/QSCA7lbcU91F3gyoBpSsp5m7bf5" +
                    "//OBadjWJDvl2KML7NMQZUr7YXqUQW9AvoNFrH4edn8d5jY5WAxWsCPQlOqNdybM" +
                    "vKF2jhjIE1fTWOzK+AvvFyNhxer5bWGU4S5LTr7QNXnvbngXCdkQfrcSn/ydQXP0" +
                    "vXfjf3NhpluFXqWe5qUFKXvjY6+PdrE/lvTmX4DdvUIu9NDa2JU9mhwAPPR1yjjp" +
                    "4IhgYOTQL69ZQcvy0Ssa6S25Xi3xx2XXbdx8svYcQfHDBF1daK9vca+YRX/DzXxl" +
                    "1S4uGt+FUWSwuFdZ122ZCZ0CAwEAAQ==";

    //2021.7.10 version
    public static final String WFId = "d05209e535938ecdcffca081f67f2a4d";

    //2021.3.14 version post提交表单
    public static final String JSON_DATA =
            "{\n" +
            "    \"data\":{\n" +
            "        \"a441d48886b2e011abb5685ea3ea4999\":{\n" +
            "            \"time\":\"2021-03-14 17:59\",\n" +
            "            \"longitude\":116.410344,\n" +
            "            \"latitude\":39.916295,\n" +
            "            \"address\":\"\"\n" +
            "        },\n" +
            "        \"9cd65a003f4a2c30a4d949cad83eda0d\":\"37.3度以下\",\n" +
            "        \"65ff68aeda65f345fef50b8b314184a7\":[\n" +
            "            \"健康\"\n" +
            "        ],\n" +
            "        \"b36100fc06308abbd5f50127d661f41e\":[\n" +
            "            \"正常\"\n" +
            "        ],\n" +
            "        \"c693ed0f20e629ab321514111f3ac2cb\":[\n" +
            "            \"否\"\n" +
            "        ],\n" +
            "        \"0d0bb7b021aafc67cf8053462539eba5\":\"无外出\",\n" +
            "        \"6c4ed7eacfeb41a2e67d7019d7c0faff\": \"无\"\n" +
            "    },\n" +
            "    \"extend\":{\n" +
            "        \"TaskId\":\"1dcf397e280d92a598bf7c58caaf608d\",\n" +
            "        \"title\":\"任务信息\",\n" +
            "        \"content\":[\n" +
            "            {\n" +
            "                \"label\":\"任务名称\",\n" +
            "                \"value\":\"学生每日健康打卡(2021-07-10）\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"label\":\"发布机构\",\n" +
            "                \"value\":\"学工部\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";



    //正则匹配字符串
    public static final String REG_TASKID = "\"TaskId\":\"([\\S\\s]{0,32})\"";
    public static final String REG_VALUE = "\"value\":\"([\\S\\s]{20})\"";
    public static final String REG_TIME = "\"time\":\"([\\S\\s]{16})\"";
    public static final String REG_LONGITUDE = "\"longitude\":\\d{3}.\\d{6}";
    public static final String REG_LATITUDE = "\"latitude\":\\d{2}.\\d{6}";
    public static final String REG_ADDRESS = "\"address\":\"([\\S\\s]{0})\"";

    /**
     *登录
     * @return token的HashMap
     * @2021-03-19 00:28:41 047
     */
    public static boolean doLogin(HashMap<String,String> token) {
        HashMap<String,String> response_map = null;
        String encrypt_text = null;
        try {
            encrypt_text = URLEncoder.encode(EncryptUtil.encrypt(token.get("PASSWD"),PUBLIC_KEY));
            String url = "https://mobile.yiban.cn/api/v2/passport/login?account="+ token.get("ACCOUNT") +"&passwd="+ encrypt_text +"&ct=2&app=1&v=4.9.1&apn=wifi&identify=865166029774844&sig=9c7c67569143c835&token=&device=OPPO%3APCLM10&sversion=22";
            //设置响应头
            HashMap<String,String> header = new HashMap<>();
            header.put("User-Agent","Mozilla/5.0 (Linux; Android) AppleWebKit/530.17(KHTML,like Gecko) Version/4.0 Mobile Safari/530.17");
            //配置重定向等信息
            RequestConfig requestConfig = RequestConfig.custom()
            // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)
            // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)
            // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)
            // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();

            response_map = HttpUtil.doGet(url,header,null,requestConfig);

            try {
                JSONObject jsonObject = JSON.parseObject(response_map.get("json"));
                JSONObject jsonObject_data = (JSONObject)JSON.toJSON(jsonObject.get("data"));
                token.put("token",jsonObject_data.get("token").toString());
                token.put("access_token",jsonObject_data.get("access_token").toString());

            } catch (Exception e) {
                System.out.println("账号或密码错误");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @2021-07-10 14:51:37 275
     * @param token
     * @param cookieStore
     * @return
     */
    public static boolean doLocalization(HashMap<String,String> token,CookieStore cookieStore) {
        HashMap<String,String> response_map = null;
        String[] arr = null;

        try {

            //设置响应头
            HashMap<String,String> header = new HashMap<>();
            header.put("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 yiban_iOS/4.9.10");
            header.put("authorization","Bearer "+ token.get("access_token"));
            header.put("Upgrade-Insecure-Requests","1");
            header.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            header.put("Accept-Encoding","gzip, deflate");
            header.put("loginToken",token.get("access_token"));
            header.put("AppVersion","4.9.10");


            //设置cookie
            BasicClientCookie loginToken = new BasicClientCookie("loginToken",token.get("access_token"));
            loginToken.setDomain("f.yiban.cn");
            loginToken.setPath("/");
            cookieStore.addCookie(loginToken);

            BasicClientCookie client = new BasicClientCookie("client","IOS");
            client.setDomain("f.yiban.cn");
            client.setPath("/");
            cookieStore.addCookie(client);

            //配置重定向等信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(false).build();

            String url = "http://f.yiban.cn/iapp82725";
            response_map = HttpUtil.doGet(url, header,cookieStore,requestConfig);

            url = "https://f.yiban.cn/iapp/index?act=iapp82725";
            //设置cookie
            BasicClientCookie waf_cookie = new BasicClientCookie("waf_cookie",response_map.get("waf_cookie"));
            waf_cookie.setDomain("f.yiban.cn");
            waf_cookie.setPath("/");
            cookieStore.addCookie(waf_cookie);

            header.remove("authorization");

            response_map = HttpUtil.doGet(url, header,cookieStore,requestConfig);
            /*for(String key : response_map.keySet()) {
                System.out.println(key +"="+ response_map.get(key));
            }*/
            String verify_request = response_map.get("http://yiban.fjsjyt.cn/?verify_request");
            //System.out.println("===============第一次============");
            try {
                //分割verify_request
                arr = verify_request.split("[&]");

            } catch (Exception e) {

                System.out.println("第一次授权失败");
                return false;
            }

            //设置cookie
            BasicClientCookie _C = new BasicClientCookie("_C",response_map.get("_C"));
            _C.setDomain("f.yiban.cn");
            _C.setPath("/");
            cookieStore.addCookie(_C);

            //设置cookie
            BasicClientCookie _X = new BasicClientCookie("_X",response_map.get("_X"));
            _X.setDomain("f.yiban.cn");
            _X.setPath("/");
            cookieStore.addCookie(_X);

            //设置cookie
            BasicClientCookie _Y = new BasicClientCookie("_Y",response_map.get("_Y"));
            _Y.setDomain("f.yiban.cn");
            _Y.setPath("/");
            cookieStore.addCookie(_Y);
            //设置cookie
            BasicClientCookie _Z = new BasicClientCookie("_Z",response_map.get("_Z"));
            _Z.setDomain("f.yiban.cn");
            _Z.setPath("/");
            cookieStore.addCookie(_Z);
            //设置cookie
            BasicClientCookie _YB_OPEN_V2_0 = new BasicClientCookie("_YB_OPEN_V2_0",response_map.get("_YB_OPEN_V2_0"));
            _YB_OPEN_V2_0.setDomain("f.yiban.cn");
            _YB_OPEN_V2_0.setPath("/");
            cookieStore.addCookie(_YB_OPEN_V2_0);

            //设置cookie
            BasicClientCookie yibanM_user_token = new BasicClientCookie("yibanM_user_token",token.get("access_token"));
            yibanM_user_token.setDomain("f.yiban.cn");
            yibanM_user_token.setPath("/");
            cookieStore.addCookie(yibanM_user_token);

            header.put("authorization","Bearer "+ token.get("access_token"));
            url  = "http://f.yiban.cn/iapp7463?access_token="+token.get("access_token")+"&v_time=" + Long.toString(System.currentTimeMillis()/1000L) + "0000";
            response_map = HttpUtil.doGet(url,header,cookieStore,requestConfig);
            arr = null;
            /*for(String key : response_map.keySet()) {
                System.out.println(key +"="+ response_map.get(key));
            }*/
            //System.out.println("===============第二次_1============");
            url = "https://f.yiban.cn/iapp/index?act=iapp7463";
            response_map = HttpUtil.doGet(url,header,cookieStore,requestConfig);
            //System.out.println(response_map);
            //System.out.println("===============第二次============");
            verify_request = response_map.get("https://c.uyiban.com/#/?verify_request");
            try {
                //System.out.println(verify_request);
                //分割verify_request
                arr = verify_request.split("[&]");
                //System.out.println(arr[0]);
                token.put("verify_request",arr[0]);
                return true;
            } catch (Exception e) {

                System.out.println("第二次授权失败");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 认证
     * @param token 
     * @return
     * @2021-03-19 02:38:37 604
     */
    public static CookieStore doAuth(HashMap<String,String> token, CookieStore cookieStore) {
        HashMap<String,String> response_map = null;

        try {
            //auth url
            String url = "https://api.uyiban.com/base/c/auth/yiban?verifyRequest="+
                    token.get("verify_request") +
                    "&CSRF=" + token.get("CSRF");
            //设置响应头
            HashMap<String,String> header = new HashMap<>();
            header.put("Accept","*/*");
            header.put("Accept-Encoding","gzip, deflate");
            header.put("Accept-Language","zh-CN,en-US;q=0.8");
            header.put("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; PCLM10 Build/LYZ28N; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.100 Safari/537.36 yiban_android");
            header.put("X-Requested-With","com.yiban.app");
            header.put("Origin","https://c.uyiban.com");


            //设置cookie

            BasicClientCookie csrf_token = new BasicClientCookie("csrf_token",token.get("CSRF"));
            csrf_token.setDomain("api.uyiban.com");
            csrf_token.setPath("/");
            cookieStore.addCookie(csrf_token);
            //配置重定向等信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            response_map = HttpUtil.doGet(url,header,cookieStore,requestConfig);


            //cookie装入map
            token.put("PHPSESSID",response_map.get("PHPSESSID"));
            token.put("cpi",response_map.get("cpi"));
            token.put("csrf_token",token.get("CSRF"));
            //cookie装入cookiestore
            BasicClientCookie cpi = new BasicClientCookie("cpi",token.get("cpi"));
            cpi.setDomain("api.uyiban.com");
            cpi.setPath("/");
            cookieStore.addCookie(cpi);
            BasicClientCookie PHPSESSID = new BasicClientCookie("PHPSESSID",token.get("PHPSESSID"));
            PHPSESSID.setDomain("api.uyiban.com");
            PHPSESSID.setPath("/");
            cookieStore.addCookie(PHPSESSID);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return cookieStore;
    }

    /**
     * 生成32位CSRF字符串
     * @return CSRF字符串
     * @2021-03-19 02:41:20 598
     */
    public static StringBuffer create_CSRF(){
        StringBuffer csrf = new StringBuffer();
        Random random = new Random();
        int i = 1;
        while(i <= 32) {
            if(i % 2 == 0){
                csrf.appendCodePoint(random.nextInt(57-48+1) + 48);
            } else {
                csrf.appendCodePoint(random.nextInt(102-97+1) + 97);
            }
            i++;
        }
        return csrf;
    }

    /**
     * 获取未完成列表
     * @param token 通行证
     * @param cookieStore cookie集合
     * @2021-03-21 01:01:48 020
     */
    public static boolean getUncompletedList(HashMap<String,String> token,CookieStore cookieStore){

        HashMap<String,String> response_map = null;
        try {
            //获取未完成列表
            String url =
                    "https://api.uyiban.com/officeTask/client/index/uncompletedList?CSRF=" +
                    token.get("CSRF");

            //设置响应头
            HashMap<String,String> header = new HashMap<>();
            header.put("Accept","*/*");
            header.put("Accept-Encoding","gzip, deflate");
            header.put("Accept-Language","zh-CN,en-US;q=0.8");
            header.put("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; PCLM10 Build/LYZ28N; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.100 Safari/537.36 yiban_android");
            header.put("X-Requested-With","com.yiban.app");
            header.put("Origin","https://c.uyiban.com");


            //配置重定向等信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(20000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(20000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(20000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();


            try {
                response_map = HttpUtil.doGet(url,header,cookieStore,requestConfig);

                JSONObject jsonObject = JSON.parseObject(response_map.get("json"));
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                jsonObject = (JSONObject)jsonArray.get(0);


                token.put("TaskId",jsonObject.get("TaskId").toString());
                token.put("Title",jsonObject.get("Title").toString());
                token.put("StartTime",jsonObject.get("StartTime").toString());
                token.put("EndTime",jsonObject.get("EndTime").toString());

            } catch (Exception e) {
                System.out.println("获取未执行任务！");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * //根据TaskId获取详细信息
     * @param token
     * @param cookieStore
     * @2021-03-21 02:27:58 178
     */
    public static void getDetail(HashMap<String,String> token,CookieStore cookieStore) {
        HashMap<String,String> response_map = null;
        try {
            //根据TaskId获取详细信息
            String url ="https://api.uyiban.com/officeTask/client/index/detail?TaskId=" +
                    token.get("TaskId") +
                    "&CSRF=" + token.get("CSRF");

            //设置响应头
            HashMap<String,String> header = new HashMap<>();
            header.put("Accept","*/*");
            header.put("Accept-Encoding","gzip, deflate");
            header.put("Accept-Language","zh-CN,en-US;q=0.8");
            header.put("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; PCLM10 Build/LYZ28N; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.100 Safari/537.36 yiban_android");
            header.put("X-Requested-With","com.yiban.app");
            header.put("Origin","https://c.uyiban.com");


            //配置重定向等信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            response_map = HttpUtil.doGet(url,header,cookieStore,requestConfig);

            System.out.println(response_map);
            try {

                //获取返回json的WFId
                JSONObject jsonObject = JSON.parseObject(response_map.get("json"));
                JSONObject jsonObject_data = (JSONObject)JSON.toJSON(jsonObject.get("data"));
                token.put("WFId",jsonObject_data.get("WFId").toString());


            } catch (Exception e) {
                System.out.println("获取详细任务失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断WFId是否改变
     * @param token
     * @return 判断结果
     * @2021-03-21 02:55:10 559
     */
    public static boolean isUpdated(HashMap<String,String> token) {
        //判断WFId是否改变，WFId改变代表已经打卡或者易班打卡更新
        return !(WFId.equals(token.get("WFId")));
    }

    /**
     *
     * @param token
     * @return 封装的类json数据
     * @2021-03-21 15:22:14 974
     */
    public static String dataPackage(HashMap<String,String> token) {
        String reg = null;
        String package_data = null;
        try {

            reg = JSON_DATA;
            //时间类型格式化
            //获取当前时间
            String time = GetDateUtil.getDate("yyyy-MM-dd HH:mm");
            //获取定位信息
            HashMap<String,String> gps = GetGpsDao.getGps(token.get("ACCOUNT"));
            //获取替换的value信息

            String TaskId = token.get("TaskId");
            String Title = token.get("Title");
            String longitude = gps.get("LONGITUDE");
            String latitude = gps.get("LATITUDE");
            String address = gps.get("ADDRESS");

            //正则字符串替换value
            reg = reg.replaceAll(REG_TASKID,"\"TaskId\":\""+ TaskId +"\"");
            reg = reg.replaceAll(REG_VALUE,"\"value\":\""+ Title +"\"");
            reg = reg.replaceAll(REG_TIME,"\"time\":\""+ time +"\"");
            reg = reg.replaceAll(REG_LONGITUDE,"\"longitude\":"+longitude);
            reg = reg.replaceAll(REG_LATITUDE,"\"latitude\":"+latitude);

            try {
                reg = reg.replaceAll(REG_ADDRESS,"\"address\":\""+address+"\"");
            } catch (Exception e){
                e.printStackTrace();
            }


            //封装打卡数据
            JSONObject jsonObject = JSON.parseObject(reg);
            String data = jsonObject.get("data").toString();
            String extend = jsonObject.get("extend").toString();
            package_data = "data=" + data +"&"+"extend=" + extend;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return package_data;
    }

    /**
     * 提交打卡
     * @param token
     * @param cookieStore 
     * @param data 提交表单
     * @2021-03-21 16:53:58 174
     */
    public static boolean submitData(HashMap<String,String> token,CookieStore cookieStore,String data) {

        String url = "https://api.uyiban.com/workFlow/c/my/apply/" +
                token.get("WFId") + "?CSRF=" + token.get("CSRF");
        //设置响应头
        HashMap<String,String> header = new HashMap<>();
        header.put("Accept","*/*");
        header.put("Accept-Encoding","gzip, deflate");
        header.put("Accept-Language","zh-CN,en-US;q=0.8");
        header.put("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; PCLM10 Build/LYZ28N; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.100 Safari/537.36 yiban_android");
        header.put("X-Requested-With","com.yiban.app");
        header.put("Origin","https://c.uyiban.com");
        header.put("Content-Type", "application/x-www-form-urlencoded");

        //配置重定向等信息
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();

        HashMap<String,String> response_map = null;
        response_map = HttpUtil.doPost(url,header,cookieStore,requestConfig,data);
        try {
            System.out.println(response_map);
            JSONObject jsonObject = JSON.parseObject(response_map.get("json"));
            String judge = jsonObject.get("data").toString();
            if ("".equals(judge) == true | judge == null | "null".equals(judge) == true){
                System.out.println("打卡失败");
                return false;
            } else {
                System.out.println("打卡失败");
                return true;
            }

        } catch (Exception e){
            System.out.println("打卡失败");
            return false;

        }

    }

    /**
     *
     * @return 随机温度字符串(36-36.7)之间
     * @2021-03-23 00:20:32 440
     */
    public static String getRandTemperature(){

        return "36." + new Random().nextInt(8);

    }

}

