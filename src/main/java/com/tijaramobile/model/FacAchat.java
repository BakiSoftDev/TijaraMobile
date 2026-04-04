package com.tijaramobile.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacAchat {
    private int idfa;
    private Integer idfour;
    private String num;
    private String code;
    private LocalDate dt;
    private BigDecimal totHt;
    private BigDecimal totTva;
    private BigDecimal totTtc;
    private boolean tva;
    private BigDecimal initPaye;
    private BigDecimal paye;
    private Integer createdBy;
    private LocalDateTime createdAt;

    // Joined
    private String fournisseurName;

    public FacAchat() {
        this.totHt = BigDecimal.ZERO;
        this.totTva = BigDecimal.ZERO;
        this.totTtc = BigDecimal.ZERO;
        this.initPaye = BigDecimal.ZERO;
        this.paye = BigDecimal.ZERO;
        this.tva = true;
        this.dt = LocalDate.now();
    }

    public int getIdfa() { return idfa; }
    public void setIdfa(int idfa) { this.idfa = idfa; }

    public Integer getIdfour() { return idfour; }
    public void setIdfour(Integer idfour) { this.idfour = idfour; }

    public String getNum() { return num; }
    public void setNum(String num) { this.num = num; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDate getDt() { return dt; }
    public void setDt(LocalDate dt) { this.dt = dt; }

    public BigDecimal getTotHt() { return totHt != null ? totHt : BigDecimal.ZERO; }
    public void setTotHt(BigDecimal totHt) { this.totHt = totHt; }

    public BigDecimal getTotTva() { return totTva != null ? totTva : BigDecimal.ZERO; }
    public void setTotTva(BigDecimal totTva) { this.totTva = totTva; }

    public BigDecimal getTotTtc() { return totTtc != null ? totTtc : BigDecimal.ZERO; }
    public void setTotTtc(BigDecimal totTtc) { this.totTtc = totTtc; }

    public boolean isTva() { return tva; }
    public void setTva(boolean tva) { this.tva = tva; }

    public BigDecimal getInitPaye() { return initPaye != null ? initPaye : BigDecimal.ZERO; }
    public void setInitPaye(BigDecimal initPaye) { this.initPaye = initPaye; }

    public BigDecimal getPaye() { return paye != null ? paye : BigDecimal.ZERO; }
    public void setPaye(BigDecimal paye) { this.paye = paye; }

    public BigDecimal getReste() { return getTotTtc().subtract(getPaye()); }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFournisseurName() { return fournisseurName; }
    public void setFournisseurName(String fournisseurName) { this.fournisseurName = fournisseurName; }
}
