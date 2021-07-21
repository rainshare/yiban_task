package cn.rainshare.task.dao;

import cn.rainshare.task.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class WriteTaskLogDao {

    public static boolean writeLog(HashMap<String,String> info){
        HashMap<String,String> extend = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int value = 0;
        try {
            //获取连接
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            //查询GPS信息
            String sql = "INSERT INTO TASKLOG(ACCOUNT,NAME,LOCATION,INFO,TEMPERATURE,REDATE) VALUES(?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, info.get("ACCOUNT"));//防止sql注入
            ps.setString(2, info.get("NAME"));//防止sql注入
            ps.setString(3, info.get("LOCATION"));//防止sql注入
            ps.setString(4, info.get("INFO"));//防止sql注入
            ps.setString(5, info.get("TEMPERATURE"));//防止sql注入
            ps.setString(6, info.get("REDATE"));//防止sql注入
            //ps.setString(1, list.get(1));//防止sql注入
            value = ps.executeUpdate();

            if (value == 1){
                //提交事务
                conn.commit();
                return true;
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

        }
        return false;
    }

}
