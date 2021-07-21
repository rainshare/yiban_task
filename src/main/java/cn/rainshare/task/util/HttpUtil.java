package cn.rainshare.task.util;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

public class HttpUtil {
    /**
     * doGet
     * @param URL
     * @2021-03-19 00:05:35 663
     */

    public static HashMap<String,String> doGet(String URL, HashMap<String,String> header,
               CookieStore cookieStore , RequestConfig requestConfig) {
        //响应体存放map
        HashMap<String,String> response_map = new HashMap<>();
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = null;
        if (cookieStore != null){
            httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        } else {
            httpClient = HttpClientBuilder.create().build();
        }

        // 创建Get请求
        HttpGet httpGet = new HttpGet(URL);


        // 响应模型
        CloseableHttpResponse response = null;
        HttpEntity responseEntity = null;
        try {
            //设置请求头
            for (String key : header.keySet()){
                httpGet.addHeader(key,header.get(key));
            }
            //设置cookiestore
            //if (cookieStore != null) {
            //    httpGet.addHeader(new BasicHeader("Cookie",cookieStore.toString()));
            //System.out.println("cookieStore="+cookieStore.toString());
            //}
                //httpGet.addHeader();
            //new cookieStore();
            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(requestConfig);
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            //获取响应头
            Header[] headers = response.getAllHeaders();
            //获取响应头key value
            for (Header key:headers){
                HeaderElement [] headerElementArray = key.getElements();
                for(HeaderElement headerElement : headerElementArray) {
                    try {
                        if (headerElement.getName() != null | headerElement.getValue() != null) {
                            //装入map
                            response_map.put(headerElement.getName(),headerElement.getValue());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            if (responseEntity != null) {
                //System.out.println(EntityUtils.toString(responseEntity));
                //System.out.println(responseEntity.getContentType());
                response_map.put("json",EntityUtils.toString(responseEntity));


            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response_map;
    }


    /**
     * POST
     *
     * @2021-03-19 00:05:44 529
     */
    public static HashMap<String,String> doPost(String URL, HashMap<String,String> header,
                       CookieStore cookieStore , RequestConfig requestConfig,String data) {
        //响应体存放map
        HashMap<String,String> response_map = new HashMap<>();
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = null;
        if (cookieStore != null){
            httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        } else {
            httpClient = HttpClientBuilder.create().build();
        }

        // 创建Post请求
        HttpPost httpPost = new HttpPost(URL);
        //设置请求参数
        httpPost.setConfig(requestConfig);
        //把字符串转换成响应体对象
        StringEntity entity = new StringEntity(data, "UTF-8");
        //设置实体数据类型
        entity.setContentType("application/x-www-form-urlencoded");
        entity.setContentEncoding("UTF-8");

        System.out.println(entity.toString());
        //设置请求头
        for (String key : header.keySet()){
            httpPost.addHeader(key,header.get(key));
        }

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);


        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            //获取响应头
            Header[] headers = response.getAllHeaders();
            //获取响应头key value
            for (Header key:headers){
                HeaderElement [] headerElementArray = key.getElements();
                for(HeaderElement headerElement : headerElementArray) {
                    try {
                        if (headerElement.getName() != null | headerElement.getValue() != null) {
                            //装入map
                            response_map.put(headerElement.getName(),headerElement.getValue());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            if (responseEntity != null) {
                //System.out.println(EntityUtils.toString(responseEntity));
                //System.out.println(responseEntity.getContentType());
                response_map.put("json",EntityUtils.toString(responseEntity));

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response_map;
    }
}
