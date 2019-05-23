package com.example.checking_room;

import java.util.List;
import java.util.Objects;

public class CheckingRoomResponse {
    private String ysgh;
    private String ysgy;
    private String fjmc;
    private List<Waitmsg> waitmsg;
    private String xpdz;
    private String ysxm;
    private String brxm;
    private String wc;
    private List<Ghmsg> ghmsg;
    private String pdhm;
    public void setYsgh(String ysgh) {
        this.ysgh = ysgh;
    }
    public String getYsgh() {
        return ysgh;
    }

    public void setYsgy(String ysgy) {
        this.ysgy = ysgy;
    }
    public String getYsgy() {
        return ysgy;
    }

    public void setFjmc(String fjmc) {
        this.fjmc = fjmc;
    }
    public String getFjmc() {
        return fjmc;
    }

    public void setWaitmsg(List<Waitmsg> waitmsg) {
        this.waitmsg = waitmsg;
    }
    public List<Waitmsg> getWaitmsg() {
        return waitmsg;
    }

    public void setXpdz(String xpdz) {
        this.xpdz = xpdz;
    }
    public String getXpdz() {
        return xpdz;
    }

    public void setYsxm(String ysxm) {
        this.ysxm = ysxm;
    }
    public String getYsxm() {
        return ysxm;
    }

    public void setBrxm(String brxm) {
        this.brxm = brxm;
    }
    public String getBrxm() {
        return brxm;
    }

    public void setWc(String wc) {
        this.wc = wc;
    }
    public String getWc() {
        return wc;
    }

    public void setGhmsg(List<Ghmsg> ghmsg) {
        this.ghmsg = ghmsg;
    }
    public List<Ghmsg> getGhmsg() {
        return ghmsg;
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
        if (!(o instanceof CheckingRoomResponse)) return false;
        CheckingRoomResponse that = (CheckingRoomResponse) o;
        return Objects.equals(getYsgh(), that.getYsgh()) &&
                Objects.equals(getYsgy(), that.getYsgy()) &&
                Objects.equals(getFjmc(), that.getFjmc()) &&
                Objects.equals(getWaitmsg(), that.getWaitmsg()) &&
                Objects.equals(getXpdz(), that.getXpdz()) &&
                Objects.equals(getYsxm(), that.getYsxm()) &&
                Objects.equals(getBrxm(), that.getBrxm()) &&
                Objects.equals(getWc(), that.getWc()) &&
                Objects.equals(getGhmsg(), that.getGhmsg()) &&
                Objects.equals(getPdhm(), that.getPdhm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getYsgh(), getYsgy(), getFjmc(), getWaitmsg(), getXpdz(), getYsxm(), getBrxm(), getWc(), getGhmsg(), getPdhm());
    }
}
