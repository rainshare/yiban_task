package cn.rainshare.task.dao;

import cn.rainshare.task.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetUsersDao {

    public static List<HashMap<String,String>> getUsers(){
        //用户集合
        List<HashMap<String,String>> users = new ArrayList<>();
        //用户详细
        HashMap<String,String> user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            //查询GPS信息
            String sql = "SELECT ACCOUNT,NAME,PASSWD FROM USERS";
            ps = conn.prepareStatement(sql);
            //ps.setString(1, account);//防止sql注入
            //ps.setString(1, list.get(1));//防止sql注入
            rs = ps.executeQuery();
            while(rs.next()){
                //用户hashmap
                user = new HashMap();
                //信息装入map
                user.put("ACCOUNT",rs.getString("ACCOUNT"));
                user.put("NAME",rs.getString("NAME"));
                user.put("PASSWD",rs.getString("PASSWD"));
                //map放入list
                users.add(user);

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
            return users;
        }

    }
}
