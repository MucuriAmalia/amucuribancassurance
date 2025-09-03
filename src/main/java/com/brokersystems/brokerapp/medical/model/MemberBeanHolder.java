package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.server.utils.MemberUploadUtils;

import java.util.List;

public class MemberBeanHolder {

    private List<MemberUploadUtils.MemberBean> memberBeans;
    private String excelFile;

    public List<MemberUploadUtils.MemberBean> getMemberBeans() {
        return memberBeans;
    }

    public void setMemberBeans(List<MemberUploadUtils.MemberBean> memberBeans) {
        this.memberBeans = memberBeans;
    }

    public String getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(String excelFile) {
        this.excelFile = excelFile;
    }

    @Override
    public String toString() {
        return "MemberBeanHolder{" +
                "memberBeans=" + memberBeans +
                ", excelFile='" + excelFile + '\'' +
                '}';
    }
}
