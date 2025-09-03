package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brokersystems.brokerapp.setup.model.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrganizationRepository extends JpaRepository<Organization, Long> {



    @Query(value = "SELECT org_code, org_address, add_email_addr, phy_address, \n" +
            " org_cert_number, \n" +
            "org_date_incorp, org_desc, org_fax, org_mobile, org_name, org_phone, org_sht_desc, org_website, org_cou_code, \n" +
            "org_cur_code, sbc.cur_name,sbc2.cou_name,org_logo,org_logo_content_type,org_pin_number \n" +
            "FROM sys_brk_organization join sys_brk_currencies sbc on org_cur_code = sbc.cur_code \n" +
            "left join sys_brk_countries sbc2 on sbc2.cou_code =org_cou_code",nativeQuery = true)
    List<Object[]> findAllOrganizations();

    @Query(value = "select org_logo,org_logo_content_type from sys_brk_organization", nativeQuery = true)
    List<Object[]> getOganizationLog();

}
