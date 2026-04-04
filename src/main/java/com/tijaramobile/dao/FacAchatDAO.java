package com.tijaramobile.dao;

import com.tijaramobile.database.DatabaseConnection;
import com.tijaramobile.model.FacAchat;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FacAchatDAO {

    public List<FacAchat> getAll() {
        List<FacAchat> list = new ArrayList<>();
        String sql = "SELECT fa.*, f.name as fournisseur_name FROM facachat fa LEFT JOIN fournisseur f ON fa.idfour = f.id ORDER BY fa.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public FacAchat getById(int id) {
        String sql = "SELECT fa.*, f.name as fournisseur_name FROM facachat fa LEFT JOIN fournisseur f ON fa.idfour = f.id WHERE fa.idfa=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public BigDecimal getMonthTotal() {
        String sql = "SELECT COALESCE(SUM(tot_ttc), 0) FROM facachat WHERE strftime('%Y-%m', created_at) = strftime('%Y-%m', 'now')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public int insert(FacAchat fa) {
        String sql = "INSERT INTO facachat (idfour, num, code, dt, tot_ht, tot_tva, tot_ttc, tva, init_paye, paye, created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, fa.getIdfour());
            ps.setString(2, fa.getNum());
            ps.setString(3, fa.getCode());
            ps.setString(4, fa.getDt() != null ? fa.getDt().toString() : LocalDate.now().toString());
            ps.setBigDecimal(5, fa.getTotHt());
            ps.setBigDecimal(6, fa.getTotTva());
            ps.setBigDecimal(7, fa.getTotTtc());
            ps.setInt(8, fa.isTva() ? 1 : 0);
            ps.setBigDecimal(9, fa.getInitPaye());
            ps.setBigDecimal(10, fa.getPaye());
            ps.setObject(11, fa.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public boolean update(FacAchat fa) {
        String sql = "UPDATE facachat SET idfour=?, num=?, code=?, dt=?, tot_ht=?, tot_tva=?, tot_ttc=?, tva=?, init_paye=?, paye=? WHERE idfa=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, fa.getIdfour());
            ps.setString(2, fa.getNum());
            ps.setString(3, fa.getCode());
            ps.setString(4, fa.getDt() != null ? fa.getDt().toString() : null);
            ps.setBigDecimal(5, fa.getTotHt());
            ps.setBigDecimal(6, fa.getTotTva());
            ps.setBigDecimal(7, fa.getTotTtc());
            ps.setInt(8, fa.isTva() ? 1 : 0);
            ps.setBigDecimal(9, fa.getInitPaye());
            ps.setBigDecimal(10, fa.getPaye());
            ps.setInt(11, fa.getIdfa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM facachat WHERE idfa=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private FacAchat map(ResultSet rs) throws SQLException {
        FacAchat fa = new FacAchat();
        fa.setIdfa(rs.getInt("idfa"));
        fa.setIdfour((Integer) rs.getObject("idfour"));
        fa.setNum(rs.getString("num"));
        fa.setCode(rs.getString("code"));
        String dt = rs.getString("dt");
        if (dt != null) { try { fa.setDt(LocalDate.parse(dt.substring(0, 10))); } catch (Exception ignored) {} }
        fa.setTotHt(rs.getBigDecimal("tot_ht"));
        fa.setTotTva(rs.getBigDecimal("tot_tva"));
        fa.setTotTtc(rs.getBigDecimal("tot_ttc"));
        fa.setTva(rs.getInt("tva") == 1);
        fa.setInitPaye(rs.getBigDecimal("init_paye"));
        fa.setPaye(rs.getBigDecimal("paye"));
        fa.setCreatedBy((Integer) rs.getObject("created_by"));
        String ca = rs.getString("created_at");
        if (ca != null) { try { fa.setCreatedAt(LocalDateTime.parse(ca.replace(" ", "T"))); } catch (Exception ignored) {} }
        try { fa.setFournisseurName(rs.getString("fournisseur_name")); } catch (Exception ignored) {}
        return fa;
    }
}
