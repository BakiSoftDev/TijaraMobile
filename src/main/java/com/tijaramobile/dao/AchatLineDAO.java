package com.tijaramobile.dao;

import com.tijaramobile.database.DatabaseConnection;
import com.tijaramobile.model.AchatLine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchatLineDAO {

    public List<AchatLine> getByFacAchat(int idfacAchat) {
        List<AchatLine> list = new ArrayList<>();
        String sql = "SELECT al.*, p.nomP as produit_nom, p.unit as produit_unit FROM achatline al JOIN produit p ON al.idprod = p.idP WHERE al.idfacAchat = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idfacAchat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(AchatLine line) {
        String sql = "INSERT INTO achatline (idfacAchat, idprod, qnt, mode, pAchat, tot) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, line.getIdfacAchat());
            ps.setInt(2, line.getIdprod());
            ps.setBigDecimal(3, line.getQnt());
            ps.setString(4, line.getMode());
            ps.setBigDecimal(5, line.getPAchat());
            ps.setBigDecimal(6, line.getTot());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteByFacAchat(int idfacAchat) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM achatline WHERE idfacAchat=?")) {
            ps.setInt(1, idfacAchat);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private AchatLine map(ResultSet rs) throws SQLException {
        AchatLine l = new AchatLine();
        l.setIdL(rs.getInt("idL"));
        l.setIdfacAchat(rs.getInt("idfacAchat"));
        l.setIdprod(rs.getInt("idprod"));
        l.setQnt(rs.getBigDecimal("qnt"));
        l.setMode(rs.getString("mode"));
        l.setPAchat(rs.getBigDecimal("pAchat"));
        l.setTot(rs.getBigDecimal("tot"));
        try { l.setProduitNom(rs.getString("produit_nom")); } catch (Exception ignored) {}
        try { l.setProduitUnit(rs.getString("produit_unit")); } catch (Exception ignored) {}
        return l;
    }
}
