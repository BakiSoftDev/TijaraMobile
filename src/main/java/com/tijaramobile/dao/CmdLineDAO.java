package com.tijaramobile.dao;

import com.tijaramobile.database.DatabaseConnection;
import com.tijaramobile.model.CmdLine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CmdLineDAO {

    public List<CmdLine> getByCmd(int idcmd) {
        List<CmdLine> list = new ArrayList<>();
        String sql = "SELECT cl.*, p.nomP as produit_nom, p.unit as produit_unit FROM cmdline cl JOIN produit p ON cl.idprod = p.idP WHERE cl.idcmd = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idcmd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(CmdLine line) {
        String sql = "INSERT INTO cmdline (idcmd, idprod, qnt, mode, pAchat, pVente) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, line.getIdcmd());
            ps.setInt(2, line.getIdprod());
            ps.setBigDecimal(3, line.getQnt());
            ps.setString(4, line.getMode());
            ps.setBigDecimal(5, line.getPAchat());
            ps.setBigDecimal(6, line.getPVente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteByCmd(int idcmd) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cmdline WHERE idcmd=?")) {
            ps.setInt(1, idcmd);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private CmdLine map(ResultSet rs) throws SQLException {
        CmdLine l = new CmdLine();
        l.setIdL(rs.getInt("idL"));
        l.setIdcmd(rs.getInt("idcmd"));
        l.setIdprod(rs.getInt("idprod"));
        l.setQnt(rs.getBigDecimal("qnt"));
        l.setMode(rs.getString("mode"));
        l.setPAchat(rs.getBigDecimal("pAchat"));
        l.setPVente(rs.getBigDecimal("pVente"));
        try { l.setProduitNom(rs.getString("produit_nom")); } catch (Exception ignored) {}
        try { l.setProduitUnit(rs.getString("produit_unit")); } catch (Exception ignored) {}
        return l;
    }
}
