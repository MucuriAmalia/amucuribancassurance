
package com.brokersystems.brokerapp.webservices.portalmodel;

import javax.xml.bind.Binder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 6/12/2018.
 */
public class Benefits {

    private BigDecimal id;
    private Cover_Type coverType;
    private String name;
    private String limit;
    private List<Benefits> benefits = new ArrayList<>();
    private List<Binder> binders = new ArrayList<>();
    private List<Ipf> ipfs = new ArrayList<>();

    public void setCoverType(Cover_Type coverType) {
        this.coverType = coverType;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public List<Benefits> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<Benefits> benefits) {
        this.benefits = benefits;
    }

    public Cover_Type getCoverType() {
        return coverType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public List<Binder> getBinders() {
        return binders;
    }

    public void setBinders(List<Binder> binders) {
        this.binders = binders;
    }

    public List<Ipf> getIpfs() {
        return ipfs;
    }

    public void setIpfs(List<Ipf> ipfs) {
        this.ipfs = ipfs;
    }
}