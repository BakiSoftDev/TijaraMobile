package com.tijaramobile.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cmd {
    private int idcmd;
    private int idclient;
    private String num;
    private String code;
    private BigDecimal tot;
    private BigDecimal initPaye;
    private BigDecimal paye;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private Long idSession;

    // Joined field
    private String clientName;

    public Cmd() {
        this.tot = BigDecimal.ZERO;
        this.initPaye = BigDecimal.ZERO;
        this.paye = BigDecimal.ZERO;
    }

    public int getIdcmd() { return idcmd; }
    public void setIdcmd(int idcmd) { this.idcmd = idcmd; }

    public int getIdclient() { return idclient; }
    public void setIdclient(int idclient) { this.idclient = idclient; }

    public String getNum() { return num; }
    public void setNum(String num) { this.num = num; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getTot() { return tot != null ? tot : BigDecimal.ZERO; }
    public void setTot(BigDecimal tot) { this.tot = tot; }

    public BigDecimal getInitPaye() { return initPaye != null ? initPaye : BigDecimal.ZERO; }
    public void setInitPaye(BigDecimal initPaye) { this.initPaye = initPaye; }

    public BigDecimal getPaye() { return paye != null ? paye : BigDecimal.ZERO; }
    public void setPaye(BigDecimal paye) { this.paye = paye; }

    public BigDecimal getReste() { return getTot().subtract(getPaye()); }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getIdSession() { return idSession; }
    public void setIdSession(Long idSession) { this.idSession = idSession; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
}
