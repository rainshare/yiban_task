package cn.rainshare.task.dao;

import cn.rainshare.task.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class GetGpsDao {

    public static HashMap<String,String> getGps(String account){
        HashMap<String,String> gps = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            //查询GPS信息
            String sql = "SELECT ACCOUNT,LONGITUDE,LATITUDE,ADDRESS FROM GPS WHERE ACCOUNT = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);//防止sql注入
            //ps.setString(1, list.get(1));//防止sql注入
            rs = ps.executeQuery();
            while(rs.next()){
                gps = new HashMap();
                gps.put("ACCOUNT",rs.getString("ACCOUNT"));
                gps.put("LONGITUDE",rs.getString("LONGITUDE"));
                gps.put("LATITUDE",rs.getString("LATITUDE"));
                gps.put("ADDRESS",rs.getString("ADDRESS"));

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
            return gps;
        }

    }

}
