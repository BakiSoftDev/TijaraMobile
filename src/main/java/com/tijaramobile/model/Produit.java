package com.tijaramobile.model;

import java.math.BigDecimal;

public class Produit {
    private int idP;
    private String nomP;
    private String ref;
    private String unit;
    private BigDecimal prixA;
    private BigDecimal prixV;
    private BigDecimal qnt;
    private String img;
    private BigDecimal packPrix;
    private Integer packNb;

    public Produit() {}

    public int getIdP() { return idP; }
    public void setIdP(int idP) { this.idP = idP; }

    public String getNomP() { return nomP; }
    public void setNomP(String nomP) { this.nomP = nomP; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPrixA() { return prixA != null ? prixA : BigDecimal.ZERO; }
    public void setPrixA(BigDecimal prixA) { this.prixA = prixA; }

    public BigDecimal getPrixV() { return prixV != null ? prixV : BigDecimal.ZERO; }
    public void setPrixV(BigDecimal prixV) { this.prixV = prixV; }

    public BigDecimal getQnt() { return qnt != null ? qnt : BigDecimal.ZERO; }
    public void setQnt(BigDecimal qnt) { this.qnt = qnt; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public BigDecimal getPackPrix() { return packPrix; }
    public void setPackPrix(BigDecimal packPrix) { this.packPrix = packPrix; }

    public Integer getPackNb() { return packNb; }
    public void setPackNb(Integer packNb) { this.packNb = packNb; }

    @Override
    public String toString() { return nomP != null ? nomP : ""; }
}
