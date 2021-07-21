package cn.rainshare.task.dao;

import cn.rainshare.task.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class GetTaskLog {

    public static String getTaskLog(String startTime,String endTime){
        StringBuilder info = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            //查询GPS信息
            String sql = "SELECT ID,NAME,ACCOUNT,LOCATION,INFO,TEMPERATURE,REDATE FROM TASKLOG WHERE REDATE > ? AND REDATE < ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, startTime);//防止sql注入
            ps.setString(2, endTime);//防止sql注入
            rs = ps.executeQuery();
            info = new StringBuilder();
            while(rs.next()){
                //用户hashmap

                //信息装入
                info.append("[ID="+rs.getString("ID")+",");
                info.append("NAME="+rs.getString("NAME")+",");
                info.append("ACCOUNT="+rs.getString("ACCOUNT")+",");
                info.append("INFO="+rs.getString("INFO")+",");
                info.append("TEMPERATURE="+rs.getString("TEMPERATURE")+",");
                info.append("REDATE="+rs.getString("REDATE") + "]</br>");


            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
            }

        } finally {
            DBUtil.close(conn, ps, rs);
            return info.toString();
        }

    }
}
