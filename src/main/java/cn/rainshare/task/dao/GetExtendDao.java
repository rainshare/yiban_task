package cn.rainshare.task.dao;

import cn.rainshare.task.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class GetExtendDao {

    public static HashMap<String,String> getExtend(String account){
        HashMap<String,String> extend = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            //查询GPS信息
            String sql = "SELECT ACCOUNT,RENT,ACCSTATUS FROM EXTEND WHERE ACCOUNT = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);//防止sql注入
            //ps.setString(1, list.get(1));//防止sql注入
            rs = ps.executeQuery();
            while(rs.next()){
                extend = new HashMap();
                extend.put("ACCOUNT",rs.getString("ACCOUNT"));
                extend.put("RENT",rs.getString("RENT"));
                extend.put("ACCSTATUS",rs.getString("ACCSTATUS"));

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
            return extend;
        }

    }


}
