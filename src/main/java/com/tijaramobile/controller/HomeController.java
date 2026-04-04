package com.tijaramobile.controller;

import com.tijaramobile.dao.ClientDAO;
import com.tijaramobile.dao.CmdDAO;
import com.tijaramobile.dao.FournisseurDAO;
import com.tijaramobile.dao.ProduitDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.math.BigDecimal;

public class HomeController {

    @FXML private Label lblTotalCmds;
    @FXML private Label lblTotalRevenu;
    @FXML private Label lblTotalPaye;
    @FXML private Label lblTotalImpaye;
    @FXML private Label lblTotalClients;
    @FXML private Label lblTotalFournisseurs;
    @FXML private Label lblTotalProduits;

    private final CmdDAO cmdDAO = new CmdDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        int todayCount = cmdDAO.getTodayCount();
        BigDecimal todayTotal = cmdDAO.getTodayTotal();
        BigDecimal todayPaid = cmdDAO.getTodayPaid();
        BigDecimal todayUnpaid = cmdDAO.getTodayUnpaid();

        lblTotalCmds.setText(String.valueOf(todayCount));
        lblTotalRevenu.setText(String.format("%.2f", todayTotal));
        lblTotalPaye.setText(String.format("%.2f", todayPaid));
        lblTotalImpaye.setText(String.format("%.2f", todayUnpaid));
        lblTotalClients.setText(String.valueOf(clientDAO.count()));
        lblTotalFournisseurs.setText(String.valueOf(fournisseurDAO.count()));
        lblTotalProduits.setText(String.valueOf(produitDAO.getAll().size()));
    }
}
