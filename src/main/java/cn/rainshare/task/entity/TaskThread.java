package cn.rainshare.task.entity;
import cn.rainshare.task.TaskStart;

import java.util.HashMap;

public class TaskThread implements Runnable{
    //用户详细信息
    private HashMap<String,String> user = null;

    public TaskThread() {
    }

    public TaskThread(HashMap<String, String> user) {
        this.user = user;
    }

    public HashMap<String, String> getUser() {
        return user;
    }

    public void setUser(HashMap<String, String> user) {
        this.user = user;
    }

    @Override
    public void run() {
        //执行任务
        TaskStart.doTask(user);
    }
}
