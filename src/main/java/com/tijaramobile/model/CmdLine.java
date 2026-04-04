package com.tijaramobile.model;

import java.math.BigDecimal;

public class CmdLine {
    private int idL;
    private int idcmd;
    private int idprod;
    private BigDecimal qnt;
    private String mode; // U, C, D, B, DC
    private BigDecimal pAchat;
    private BigDecimal pVente;

    // Joined
    private String produitNom;
    private String produitUnit;

    public CmdLine() {
        this.qnt = BigDecimal.ONE;
        this.mode = "U";
        this.pAchat = BigDecimal.ZERO;
        this.pVente = BigDecimal.ZERO;
    }

    public int getIdL() { return idL; }
    public void setIdL(int idL) { this.idL = idL; }

    public int getIdcmd() { return idcmd; }
    public void setIdcmd(int idcmd) { this.idcmd = idcmd; }

    public int getIdprod() { return idprod; }
    public void setIdprod(int idprod) { this.idprod = idprod; }

    public BigDecimal getQnt() { return qnt; }
    public void setQnt(BigDecimal qnt) { this.qnt = qnt; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public BigDecimal getPAchat() { return pAchat; }
    public void setPAchat(BigDecimal pAchat) { this.pAchat = pAchat; }

    public BigDecimal getPVente() { return pVente; }
    public void setPVente(BigDecimal pVente) { this.pVente = pVente; }

    public BigDecimal getTotal() { return pVente != null ? pVente.multiply(qnt != null ? qnt : BigDecimal.ONE) : BigDecimal.ZERO; }

    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }

    public String getProduitUnit() { return produitUnit; }
    public void setProduitUnit(String produitUnit) { this.produitUnit = produitUnit; }
}
