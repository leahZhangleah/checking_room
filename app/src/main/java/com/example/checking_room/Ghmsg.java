package com.example.checking_room;

import java.util.Objects;

public class Ghmsg {

    private String fjmc;
    private String brxm;
    private String pdhm;

    public Ghmsg(String fjmc, String brxm, String pdhm) {
        this.fjmc = fjmc;
        this.brxm = brxm;
        this.pdhm = pdhm;
    }

    public void setFjmc(String fjmc) {
        this.fjmc = fjmc;
    }
    public String getFjmc() {
        return fjmc;
    }

    public void setBrxm(String brxm) {
        this.brxm = brxm;
    }
    public String getBrxm() {
        return brxm;
    }

    public void setPdhm(String pdhm) {
        this.pdhm = pdhm;
    }
    public String getPdhm() {
        return pdhm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ghmsg)) return false;
        Ghmsg ghmsg = (Ghmsg) o;
        return Objects.equals(getFjmc(), ghmsg.getFjmc()) &&
                Objects.equals(getBrxm(), ghmsg.getBrxm()) &&
                Objects.equals(getPdhm(), ghmsg.getPdhm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFjmc(), getBrxm(), getPdhm());
    }
}