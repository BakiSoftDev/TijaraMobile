package com.tijaramobile.dao;

import com.tijaramobile.database.DatabaseConnection;
import com.tijaramobile.model.Cmd;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CmdDAO {

    public List<Cmd> getAll() {
        List<Cmd> list = new ArrayList<>();
        String sql = "SELECT c.*, cl.name as client_name FROM cmd c LEFT JOIN client cl ON c.idclient = cl.id ORDER BY c.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Cmd> getTodayAll() {
        List<Cmd> list = new ArrayList<>();
        String sql = "SELECT c.*, cl.name as client_name FROM cmd c LEFT JOIN client cl ON c.idclient = cl.id WHERE date(c.created_at) = date('now') ORDER BY c.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public BigDecimal getTodayTotal() {
        String sql = "SELECT COALESCE(SUM(tot), 0) FROM cmd WHERE date(created_at) = date('now')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTodayPaid() {
        String sql = "SELECT COALESCE(SUM(paye), 0) FROM cmd WHERE date(created_at) = date('now')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTodayUnpaid() {
        String sql = "SELECT COALESCE(SUM(tot - paye), 0) FROM cmd WHERE date(created_at) = date('now')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public int getTodayCount() {
        String sql = "SELECT COUNT(*) FROM cmd WHERE date(created_at) = date('now')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int insert(Cmd cmd) {
        String sql = "INSERT INTO cmd (idclient, num, code, tot, init_paye, paye, created_by) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cmd.getIdclient());
            ps.setString(2, cmd.getNum() != null ? cmd.getNum() : "0");
            ps.setString(3, cmd.getCode() != null ? cmd.getCode() : "0");
            ps.setBigDecimal(4, cmd.getTot());
            ps.setBigDecimal(5, cmd.getInitPaye());
            ps.setBigDecimal(6, cmd.getPaye());
            ps.setObject(7, cmd.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public boolean update(Cmd cmd) {
        String sql = "UPDATE cmd SET idclient=?, num=?, code=?, tot=?, init_paye=?, paye=? WHERE idcmd=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cmd.getIdclient());
            ps.setString(2, cmd.getNum());
            ps.setString(3, cmd.getCode());
            ps.setBigDecimal(4, cmd.getTot());
            ps.setBigDecimal(5, cmd.getInitPaye());
            ps.setBigDecimal(6, cmd.getPaye());
            ps.setInt(7, cmd.getIdcmd());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cmd WHERE idcmd=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Cmd map(ResultSet rs) throws SQLException {
        Cmd c = new Cmd();
        c.setIdcmd(rs.getInt("idcmd"));
        c.setIdclient(rs.getInt("idclient"));
        c.setNum(rs.getString("num"));
        c.setCode(rs.getString("code"));
        c.setTot(rs.getBigDecimal("tot"));
        c.setInitPaye(rs.getBigDecimal("init_paye"));
        c.setPaye(rs.getBigDecimal("paye"));
        c.setCreatedBy((Integer) rs.getObject("created_by"));
        String ca = rs.getString("created_at");
        if (ca != null) {
            try { c.setCreatedAt(LocalDateTime.parse(ca.replace(" ", "T"))); } catch (Exception ignored) {}
        }
        try { c.setClientName(rs.getString("client_name")); } catch (Exception ignored) {}
        return c;
    }
}
