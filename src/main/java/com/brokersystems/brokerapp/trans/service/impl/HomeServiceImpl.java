package com.brokersystems.brokerapp.trans.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

import com.brokersystems.brokerapp.quotes.dto.PendingQuotDTO;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.utils.DatabaseUtilities;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.trans.model.HomeAggregateBean;
import com.brokersystems.brokerapp.trans.model.HomePremiumBean;
import com.brokersystems.brokerapp.trans.service.HomeService;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyEnquiryDTO;
import com.brokersystems.brokerapp.uw.dtos.PortfolioDTO;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 2/22/2017.
 */
@Service
public class HomeServiceImpl implements HomeService {
    private static final Logger log = LoggerFactory.getLogger(HomeServiceImpl.class);

    @Autowired
    private PolicyTransRepo transRepo;

    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private DatabaseUtilities databaseUtilities;


    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserUtils userUtils;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Override
    @Transactional(readOnly = true)
    public HomeAggregateBean getDashBoardDetails() {
        HomeAggregateBean aggregateBean = new HomeAggregateBean();
        aggregateBean.setPoliciesSold(transRepo.countYtpPolicies());
        aggregateBean.setPremiumYTD(transRepo.ytdPremium());
        aggregateBean.setSumAssuredYTD(transRepo.ytdSumAssured());
        return aggregateBean;
    }
    @Override
    @Transactional(readOnly = true)
    public HomeAggregateBean getDashBoardDetailsByUser(Long userId) {
        HomeAggregateBean aggregateBean = new HomeAggregateBean();

        // Get user-specific data
        Long policiesSold = transRepo.countYtdPoliciesByUser(userId);
        BigDecimal premiumYTD = transRepo.ytdPremiumByUser(userId);
        BigDecimal sumAssuredYTD = transRepo.ytdSumAssuredByUser(userId);

        // Handle null values (set to 0 if no data found)
        aggregateBean.setPoliciesSold(policiesSold != null ? policiesSold : 0L);
        aggregateBean.setPremiumYTD(premiumYTD != null ? premiumYTD : BigDecimal.ZERO);
        aggregateBean.setSumAssuredYTD(sumAssuredYTD != null ? sumAssuredYTD : BigDecimal.ZERO);

        return aggregateBean;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomePremiumBean> getPremiumProduction() {
        String databaseType = databaseUtilities.getDbName();
        List<HomePremiumBean> premium = new ArrayList<>();
        if (databaseType != null) {
            if (StringUtils.equalsIgnoreCase(databaseUtilities.getDbName(), "oracle")) {
                List<Object[]> result = transRepo.getOraclePremiumProduction();
                for (Object[] object : result) {
                    premium.add(new HomePremiumBean((String) object[1], ((BigDecimal) object[2]).doubleValue(), (BigDecimal) object[3]));
                }
            } else if (databaseType.equalsIgnoreCase("sqlserver")) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                premium = jdbcTemplate.query(transRepo.sqlServerQuery, new HomePremiumMapper(), new Object[]{userUtils.getCurrentUser().getUsername()});
            } else {
                List<Object[]> result = transRepo.getPostgresPremiumProduction();
                for (Object[] object : result) {
                    double value = 0d;
                    if (object[2] instanceof BigDecimal) {
                        value = ((BigDecimal) object[2]).doubleValue();
                    } else {
                        value = (Double) object[2];
                    }
                    premium.add(new HomePremiumBean((String) object[1], value, (BigDecimal) object[3]));
                }
            }
        }

        return premium;
    }

    private final static class HomePremiumMapper implements RowMapper<HomePremiumBean> {

        @Override
        public HomePremiumBean mapRow(ResultSet rs, int i) throws SQLException {
            return new HomePremiumBean(rs.getString(2), rs.getDouble(3), rs.getBigDecimal(4));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomePremiumBean> getProductPremium() {
        List<HomePremiumBean> premium = new ArrayList<>();
        List<Object[]> result = transRepo.getProductsPremium(userUtils.getCurrentUser().getId());
        int count = 0;
        for (Object[] object : result) {
            if (count == 5) break;
            HomePremiumBean premiumBean = new HomePremiumBean();
            premiumBean.setPremium((BigDecimal) object[0]);
            premiumBean.setProduct((String) object[1]);
            premium.add(premiumBean);
            count++;
        }
        return premium;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomePremiumBean> getBranchPremium() {
        List<HomePremiumBean> premium = new ArrayList<>();
        List<Object[]> result = transRepo.getBranchPremium(userUtils.getCurrentUser().getId());
        int count = 0;
        for (Object[] object : result) {
            if (count == 5) break;
            HomePremiumBean premiumBean = new HomePremiumBean();
            premiumBean.setPremium((BigDecimal) object[0]);
            premiumBean.setBranch((String) object[1]);
            premium.add(premiumBean);
            count++;
        }
        return premium;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PendingQuotDTO> getPendingQuotes(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        List<Object[]> quotes = quotTransRepo.getPendingQuotes(search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        final List<PendingQuotDTO> quotDTOList = new ArrayList<>();
        if (!quotes.isEmpty()) rowCount = ((BigInteger) quotes.get(0)[7]).intValue();
        for (Object[] enquire : quotes) {
            PendingQuotDTO quotDTO = new PendingQuotDTO();
            quotDTO.setQuotNo((String) enquire[0]);
            quotDTO.setPremium((BigDecimal) enquire[1]);
            quotDTO.setExpiryDate((Date) enquire[2]);
            quotDTO.setStatus((String) enquire[3]);
            quotDTO.setQuotDate((Date) enquire[4]);
            quotDTO.setProduct((String) enquire[5]);
            quotDTO.setQuoteId(((BigInteger) enquire[6]).longValue());
            quotDTOList.add(quotDTO);
        }
        Page<PendingQuotDTO> page = new PageImpl<>(quotDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyEnquiryDTO> getExpiredPolicies(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        List<Object[]> policies = transRepo.searchExpiredPolicies(search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0l;
        final List<PolicyEnquiryDTO> enquiryDTOList = new ArrayList<>();
        if (!policies.isEmpty()) rowCount = ((BigInteger) policies.get(0)[7]).intValue();
        for (Object[] enquire : policies) {
            PolicyEnquiryDTO enquiryDTO = new PolicyEnquiryDTO();
            enquiryDTO.setPolNo((String) enquire[0]);
            enquiryDTO.setWef((Date) enquire[1]);
            enquiryDTO.setWet((Date) enquire[2]);
            enquiryDTO.setUser((String) enquire[3]);
            enquiryDTO.setAuthDate((Date) enquire[4]);
            enquiryDTO.setPremium((BigDecimal) enquire[5]);
            enquiryDTO.setTransactionId(((BigInteger) enquire[6]).longValue());
            enquiryDTOList.add(enquiryDTO);
        }
        Page<PolicyEnquiryDTO> page = new PageImpl<>(enquiryDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyEnquiryDTO> getPendingEndorsements(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        List<Object[]> policies = transRepo.searchPendingEndorsements(search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0l;
        final List<PolicyEnquiryDTO> enquiryDTOList = new ArrayList<>();
        if (!policies.isEmpty()) rowCount = ((BigInteger) policies.get(0)[7]).intValue();
        for (Object[] enquire : policies) {
            PolicyEnquiryDTO enquiryDTO = new PolicyEnquiryDTO();
            enquiryDTO.setPolNo((String) enquire[0]);
            enquiryDTO.setWef((Date) enquire[1]);
            enquiryDTO.setWet((Date) enquire[2]);
            enquiryDTO.setUser((String) enquire[3]);
            enquiryDTO.setAuthDate((Date) enquire[4]);
            enquiryDTO.setPremium((BigDecimal) enquire[5]);
            enquiryDTO.setTransactionId(((BigInteger) enquire[6]).longValue());
            enquiryDTOList.add(enquiryDTO);
        }
        Page<PolicyEnquiryDTO> page = new PageImpl<>(enquiryDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyEnquiryDTO> getPendingRenewals(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        List<Object[]> policies = transRepo.searchPendingRenewals(search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0l;
        final List<PolicyEnquiryDTO> enquiryDTOList = new ArrayList<>();
        if (!policies.isEmpty()) rowCount = ((BigInteger) policies.get(0)[7]).intValue();
        for (Object[] enquire : policies) {
            PolicyEnquiryDTO enquiryDTO = new PolicyEnquiryDTO();
            enquiryDTO.setPolNo((String) enquire[0]);
            enquiryDTO.setWef((Date) enquire[1]);
            enquiryDTO.setWet((Date) enquire[2]);
            enquiryDTO.setUser((String) enquire[3]);
            enquiryDTO.setAuthDate((Date) enquire[4]);
            enquiryDTO.setPremium((BigDecimal) enquire[5]);
            enquiryDTO.setTransactionId(((BigInteger) enquire[6]).longValue());
            enquiryDTOList.add(enquiryDTO);
        }
        Page<PolicyEnquiryDTO> page = new PageImpl<>(enquiryDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_USER_PORTFOLIO')")
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PortfolioDTO> findUserPolicyPortfolio(DataTablesRequest request, String status) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        Long userId = userUtils.getCurrentUser().getId();
        boolean hasPermission = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains("VIEW_USER_PORTFOLIO");

        List<Object[]> userPortfolio = transRepo.findUserPolicyPortfolio(
                search.toLowerCase(),
                request.getPageNumber(),
                request.getPageSize(),
                hasPermission,
                userId,
                status
        );

        final List<PortfolioDTO> userPortfolioList = new ArrayList<>();
        long rowCount = 0L;
        if (!userPortfolio.isEmpty()) {
            rowCount = ((BigInteger) userPortfolio.get(0)[17]).longValue();
        }
        for (Object[] policy : userPortfolio) {
            PortfolioDTO portfolioDTO = new PortfolioDTO();
            if (policy[0] != null) portfolioDTO.setPolNo((String) policy[0]);
            portfolioDTO.setCurrentStatus((String) policy[2]);
            portfolioDTO.setPolRevNo((String) policy[3]);
            Long polId = ((BigInteger) policy[4]).longValue();
            portfolioDTO.setPolId(polId);
            MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polId).and((QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")).or((QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")))));
            if (makerChecker != null) {
                portfolioDTO.setStatus(makerChecker.getStatus());
            }
            portfolioDTO.setClientName((String) policy[5]);
            portfolioDTO.setAgentName((String) policy[6]);
            portfolioDTO.setProduct((String) policy[10]);
            portfolioDTO.setWefDate((Date) policy[11]);
            portfolioDTO.setWetDate((Date) policy[12]);
            portfolioDTO.setCurrency((String) policy[13]);
            portfolioDTO.setUsername((String) policy[14]);
            portfolioDTO.setAuthComments((String) policy[16]);
            userPortfolioList.add(portfolioDTO);
        }

        Page<PortfolioDTO> page = new PageImpl<>(userPortfolioList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }
}

