package cn.rainshare.task;

import cn.rainshare.task.dao.*;
import cn.rainshare.task.entity.TaskThread;
import cn.rainshare.task.entity.YibanTask;
import cn.rainshare.task.util.GetDateUtil;
import cn.rainshare.task.util.SendMailUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskStart {
    //线程池最大线程数量
    public static final int MAX_THREAD  =  20;

    /**
     * 程序入口
     * @param args
     * @2021-03-22 23:51:21 066
     */
    public static void main(String[] args) {



        String startTime = GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss");
        //标题时间
        String title = GetDateUtil.getDate("yyyy-MM-dd");

        //获取所有users信息
        int i = 0;
        List<HashMap<String,String>> users = GetUsersDao.getUsers();
        ExecutorService executorService = null;
        for( HashMap<String,String> user : users) {
            //创建线程池
            executorService = Executors.newFixedThreadPool(MAX_THREAD);
            //executorService = Executors.newSingleThreadExecutor();
            i++;

            //new Thread(new TaskThread(user)).start(); //弃用
            //doTask(user);单线程
            //开启多线程执行任务
            executorService.execute(new TaskThread(user));

        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("Log_JAVA",true);
            String info = "["+GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss SSS")+"][INFO][打卡开始][任务数量]["+i+"]\n";
            fileOutputStream.write(info.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (executorService != null) {
            //关闭线程池
            executorService.shutdown();
            //判断线程任务是否结束，返回boolean
            //executorService.isTerminated();
        }

        try {
            //主线程休眠60秒后退出JVM
            Thread.sleep(1000 * 60);

            String endTime = GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss");
            //文本信息
            String content = GetTaskLog.getTaskLog(startTime,endTime);
            String value = "[TASK_VALUE=" + i + "]</br>";
            //发邮件
            SendMailUtil.sendEmail("rainpeaks@163.com","学生每日健康打卡("+title + ")",value+content);
            System.out.println("退出JVM");
            System.exit(0);
        } catch (Exception e) {

            e.printStackTrace();

        }
    }
    /**
     * 开启易班任务
     * @2021-03-19 01:02:46 226
     */
    public static void doTask(HashMap<String,String> user){
        HashMap<String,String> token = null;
        //设置cookie集合
        CookieStore cookieStore = new BasicCookieStore();
        try {
            token = new HashMap<>();
            try {
                token.put("ACCOUNT",user.get("ACCOUNT"));
                token.put("NAME",user.get("NAME"));
                token.put("PASSWD",user.get("PASSWD"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                //判断是否自动打卡，不自动退出
                HashMap<String,String> extend = GetExtendDao.getExtend(token.get("ACCOUNT"));
                if ("否".equals(extend.get("ACCSTATUS"))) {
                    System.out.println(token.get("ACCOUNT")+"未设置自动打卡");
                    //插入数据库信息
                    HashMap<String,String> info = new HashMap<String,String>();
                    info.put("ACCOUNT",token.get("ACCOUNT"));
                    info.put("NAME",token.get("NAME"));
                    info.put("LOCATION","[ERROR]");
                    info.put("INFO","未设置自动打卡");
                    info.put("TEMPERATURE",YibanTask.getRandTemperature());
                    info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                    if (WriteTaskLogDao.writeLog(info)){
                        System.out.println("插入成功");
                    }

                    return;
                }
            } catch (Exception e) {
                System.out.println(token.get("ACCOUNT")+"未设置自动打卡");
                //插入数据库信息
                HashMap<String,String> info = new HashMap<String,String>();
                info.put("ACCOUNT",token.get("ACCOUNT"));
                info.put("NAME",token.get("NAME"));
                info.put("LOCATION","[ERROR]");
                info.put("INFO","未设置自动打卡");
                info.put("TEMPERATURE",YibanTask.getRandTemperature());
                info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                if (WriteTaskLogDao.writeLog(info)){
                    System.out.println("插入成功");
                }
                return;
            }


            try {
                //判断是有定位信息
                HashMap<String,String> gps = GetGpsDao.getGps(token.get("ACCOUNT"));
                if (gps == null) {
                    System.out.println("未设置gps");
                    HashMap<String,String> info = new HashMap<String,String>();
                    info.put("ACCOUNT",token.get("ACCOUNT"));
                    info.put("NAME",token.get("NAME"));
                    info.put("LOCATION","[ERROR]");
                    info.put("INFO","未设置GPS定位信息");
                    info.put("TEMPERATURE",YibanTask.getRandTemperature());
                    info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                    if (WriteTaskLogDao.writeLog(info)){
                        System.out.println("插入成功");
                    }
                    return;
                }
                token.put("LONGITUDE",gps.get("LONGITUDE"));
                token.put("LATITUDE",gps.get("LATITUDE"));
                token.put("ADDRESS",gps.get("ADDRESS"));

            } catch (Exception e) {
                System.out.println("未设置gps");
                HashMap<String,String> info = new HashMap<String,String>();
                info.put("ACCOUNT",token.get("ACCOUNT"));
                info.put("NAME",token.get("NAME"));
                info.put("LOCATION","[ERROR]");
                info.put("INFO","未设置GPS定位信息");
                info.put("TEMPERATURE",YibanTask.getRandTemperature());
                info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                if (WriteTaskLogDao.writeLog(info)){
                    System.out.println("插入成功");
                }
                return;
            }



            //登录失败退出
            if (YibanTask.doLogin(token) == false) {
                System.out.println(token.get("ACCOUNT")+"账号或者密码错误");

                HashMap<String,String> info = new HashMap<String,String>();
                info.put("ACCOUNT",token.get("ACCOUNT"));
                info.put("NAME",token.get("NAME"));
                String gps = "("+token.get("LONGITUDE")+","+token.get("LATITUDE")+")";
                info.put("LOCATION", token.get("ADDRESS")+gps);
                info.put("INFO","账号或者密码错误");
                info.put("TEMPERATURE",YibanTask.getRandTemperature());
                info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                if (WriteTaskLogDao.writeLog(info)){
                    System.out.println("插入成功");
                }

                return;
            }
            /*for (String key : token.keySet()) {
                System.out.println(key + "="  + token.get(key));
            }*/
            //verify_request存放token
            if (!YibanTask.doLocalization(token,cookieStore)){
                return;
            }
            //生成32为CSRF码放入token
            token.put("CSRF",YibanTask.create_CSRF().toString());


            cookieStore = YibanTask.doAuth(token,cookieStore);
            //获取任务id放入token map中



            if (YibanTask.getUncompletedList(token,cookieStore) == false) {
               /* for ( String key : token.keySet()) {

                    System.out.println(key + "="+ token.get(key));

                }*/
                return;
            }
            //根据TaskId获取详细信息
            YibanTask.getDetail(token,cookieStore);

            //判断是否已经打卡或者易班打卡更新
            if (YibanTask.isUpdated(token) != true){
                //封装数据
                String data = YibanTask.dataPackage(token);
                //System.out.println(data);


                //提交数据打卡
                if (YibanTask.submitData(token,cookieStore,data) == true) {
                    HashMap<String,String> info = new HashMap<String,String>();
                    info.put("ACCOUNT",token.get("ACCOUNT"));
                    info.put("NAME",token.get("NAME"));
                    String gps = "("+token.get("LONGITUDE")+","+token.get("LATITUDE")+")";
                    info.put("LOCATION", token.get("ADDRESS")+gps);
                    info.put("INFO","打卡成功");
                    info.put("TEMPERATURE",YibanTask.getRandTemperature());
                    info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                    if (WriteTaskLogDao.writeLog(info)){
                        System.out.println("插入成功");
                    }

                } else {
                    HashMap<String,String> info = new HashMap<String,String>();
                    info.put("ACCOUNT",token.get("ACCOUNT"));
                    info.put("NAME",token.get("NAME"));
                    String gps = "("+token.get("LONGITUDE")+","+token.get("LATITUDE")+")";
                    info.put("LOCATION", token.get("ADDRESS")+gps);
                    info.put("INFO","打卡失败");
                    info.put("TEMPERATURE",YibanTask.getRandTemperature());
                    info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                    if (WriteTaskLogDao.writeLog(info)){
                        System.out.println("插入成功");
                    }

                }

            } else {
                /*for ( String key : token.keySet()) {

                    System.out.println(key + "="+ token.get(key));

                }*/
                System.out.println("已经打卡或者程序已更新");

                HashMap<String,String> info = new HashMap<String,String>();
                info.put("ACCOUNT",token.get("ACCOUNT"));
                info.put("NAME",token.get("NAME"));
                String gps = "("+token.get("LONGITUDE")+","+token.get("LATITUDE")+")";
                info.put("LOCATION", token.get("ADDRESS")+gps);
                info.put("INFO","已经打卡或者程序已更新");
                info.put("TEMPERATURE",YibanTask.getRandTemperature());
                info.put("REDATE",GetDateUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                if (WriteTaskLogDao.writeLog(info)){
                    System.out.println("插入成功");
                }

            }
        } catch (Exception e){

        } finally {
            if (cookieStore != null) {
                cookieStore.clear();
            }
        }

    }

}

