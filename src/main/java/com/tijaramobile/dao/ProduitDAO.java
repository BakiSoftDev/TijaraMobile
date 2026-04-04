package com.tijaramobile.dao;

import com.tijaramobile.database.DatabaseConnection;
import com.tijaramobile.model.Produit;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public List<Produit> getAll() {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit ORDER BY nomP";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Produit> search(String query) {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nomP LIKE ? OR ref LIKE ? ORDER BY nomP";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Produit getById(int id) {
        String sql = "SELECT * FROM produit WHERE idP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Produit p) {
        String sql = "INSERT INTO produit (nomP, ref, unit, prixA, prixV, qnt, img, packPrix, packNb) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomP());
            ps.setString(2, p.getRef());
            ps.setString(3, p.getUnit());
            ps.setBigDecimal(4, p.getPrixA());
            ps.setBigDecimal(5, p.getPrixV());
            ps.setBigDecimal(6, p.getQnt());
            ps.setString(7, p.getImg());
            ps.setBigDecimal(8, p.getPackPrix());
            ps.setObject(9, p.getPackNb());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Produit p) {
        String sql = "UPDATE produit SET nomP=?, ref=?, unit=?, prixA=?, prixV=?, qnt=?, img=?, packPrix=?, packNb=? WHERE idP=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomP());
            ps.setString(2, p.getRef());
            ps.setString(3, p.getUnit());
            ps.setBigDecimal(4, p.getPrixA());
            ps.setBigDecimal(5, p.getPrixV());
            ps.setBigDecimal(6, p.getQnt());
            ps.setString(7, p.getImg());
            ps.setBigDecimal(8, p.getPackPrix());
            ps.setObject(9, p.getPackNb());
            ps.setInt(10, p.getIdP());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM produit WHERE idP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStock(int idP, BigDecimal delta) {
        String sql = "UPDATE produit SET qnt = qnt + ? WHERE idP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, delta);
            ps.setInt(2, idP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Produit map(ResultSet rs) throws SQLException {
        Produit p = new Produit();
        p.setIdP(rs.getInt("idP"));
        p.setNomP(rs.getString("nomP"));
        p.setRef(rs.getString("ref"));
        p.setUnit(rs.getString("unit"));
        p.setPrixA(rs.getBigDecimal("prixA"));
        p.setPrixV(rs.getBigDecimal("prixV"));
        p.setQnt(rs.getBigDecimal("qnt"));
        p.setImg(rs.getString("img"));
        p.setPackPrix(rs.getBigDecimal("packPrix"));
        Object packNb = rs.getObject("packNb");
        p.setPackNb(packNb != null ? ((Number) packNb).intValue() : null);
        return p;
    }
}
