package com.tijaramobile.model;

import java.math.BigDecimal;

public class AchatLine {
    private int idL;
    private int idfacAchat;
    private int idprod;
    private BigDecimal qnt;
    private String mode; // U or C
    private BigDecimal pAchat;
    private BigDecimal tot;

    // Joined
    private String produitNom;
    private String produitUnit;

    public AchatLine() {
        this.qnt = BigDecimal.ONE;
        this.mode = "U";
        this.pAchat = BigDecimal.ZERO;
        this.tot = BigDecimal.ZERO;
    }

    public int getIdL() { return idL; }
    public void setIdL(int idL) { this.idL = idL; }

    public int getIdfacAchat() { return idfacAchat; }
    public void setIdfacAchat(int idfacAchat) { this.idfacAchat = idfacAchat; }

    public int getIdprod() { return idprod; }
    public void setIdprod(int idprod) { this.idprod = idprod; }

    public BigDecimal getQnt() { return qnt; }
    public void setQnt(BigDecimal qnt) { this.qnt = qnt; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public BigDecimal getPAchat() { return pAchat; }
    public void setPAchat(BigDecimal pAchat) { this.pAchat = pAchat; }

    public BigDecimal getTot() { return tot; }
    public void setTot(BigDecimal tot) { this.tot = tot; }

    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }

    public String getProduitUnit() { return produitUnit; }
    public void setProduitUnit(String produitUnit) { this.produitUnit = produitUnit; }
}
