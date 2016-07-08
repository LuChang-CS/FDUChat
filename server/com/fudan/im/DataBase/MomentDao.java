/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fudan.im.DataBase;

import com.fudan.im.bean.Moment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 畅
 */
public class MomentDao {
    
    private MomentDao() {
        
    }
    
    public static List<Moment> getMomentsByUserId(int userId) {
        String sql0 = "use myqq";
        String sql1 = "select * from moments where moments.userid = ? or (moments.userid in (select friendid from friendlist where friendlist.master = ?)) order by moments.time DESC;";
        List<Moment> result = new ArrayList<>();
        Connection con = DBPool.getConnection();
        try {
            con.setAutoCommit(false);
            PreparedStatement ps;
            ps = con.prepareStatement(sql0);
            ps.execute();

            ps = con.prepareStatement(sql1);
            ps.setInt(1, userId);            
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Moment moment = new Moment();
                moment.setId(rs.getInt("sendid"));
                moment.setUserid(rs.getInt("userid"));
                moment.setText(rs.getString("text"));
                moment.setPhoto(rs.getBytes("photo"));
                moment.setSendDate(rs.getString("time"));
                result.add(moment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBPool.close(con);
        return result;
    }
    
    public static int insertMoment(Moment moment) {
        String sql0 = "use myqq";
        String sql1 = "insert into moments (userid, text, photo, time) values(?, ?, ?, ?)";
        Connection con = DBPool.getConnection();
        try {
            con.setAutoCommit(false);
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql0);
            ps.execute();
            ps = con.prepareStatement(sql1);
            ps.setInt(1, moment.getUserid());
            ps.setString(2, moment.getText());
            ps.setBytes(3, moment.getPhoto());
            ps.setString(4, moment.getSendDate());
            ps.executeUpdate();
            con.commit();
        } catch (Exception e) {
            try {
                System.out.println("插入数据库异常，正在进行回滚..");
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return -1;
        }
        return getLastID(con);
    }
    
    public static int getLastID(Connection con) {
        String sql0 = "use myqq";
        String sql1 = "select MAX(sendid) as ID from moments";// 注意:使用MAX(ID) 必须加上 as
                                                                                                        // id 翻译
        PreparedStatement ps;
        ResultSet rs;
        int id = -1;
        try {
            ps = con.prepareStatement(sql0);
            ps.execute();
            ps = con.prepareStatement(sql1);
            rs = ps.executeQuery();
            if (rs.first())
                id = rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBPool.close(con);
        return id;
    }
    
}
