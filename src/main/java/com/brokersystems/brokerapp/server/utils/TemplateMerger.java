package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.claims.model.ClaimActivities;
import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.model.SendEmailPublisherBean;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.medical.model.MedicalParTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.ProductsDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.repository.ProductsRepo;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.model.PasswordResetToken;
import com.brokersystems.brokerapp.users.model.UserDto;
import com.brokersystems.brokerapp.users.repository.PasswordResetTokenRepo;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.RiskIntPartiesRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TemplateMerger {
	
	@Autowired
	private VelocityEngine engine;

	@Autowired
	private OrgBranchRepository branchRepository;

	@Autowired
	private ProductsRepo productsRepo;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private DateUtilities dateUtilities;
	
	@Autowired
	private RiskIntPartiesRepo riskIntPartiesRepo;

	@Autowired
	private RiskTransRepo riskTransRepo;

	@Autowired
	private MailTemplateService mailTemplateService;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordResetTokenRepo passwordResetTokenRepo;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	Environment env;

	@Autowired
	private SendEmailPublisherBean sendEmailPublisherBean;

	
	public String mergePolicyDetails(PolicyTrans policy,String rawText){
		Iterable<RiskTrans> riskTranses = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
		 List<BigDecimal>suminsured = Streamable.streamOf(riskTranses).map(RiskTrans::getSumInsured).collect(Collectors.toList());
		 //System.out.println("suminsured .."+suminsured);
		 BigDecimal total = BigDecimal.ZERO;
		 for(BigDecimal val:suminsured) {
			 total  = total.add(val==null?BigDecimal.ZERO:val);
		 }
		 StringBuilder intparty = new StringBuilder();
		 if(policy.getPreviousTrans()!=policy) {
			    Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
			  	 Streamable.streamOf(riskTrans).forEach(a -> {
				 Iterable<RiskInterestedParties> intParties = riskIntPartiesRepo.findAll(QRiskInterestedParties.riskInterestedParties.risk.riskId.eq(a.getRiskId()));
				 Set<RiskInterestedParties> parties = Streamable.streamOf(intParties).collect(Collectors.toSet());
					 Iterable<RiskTrans> prevRiskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPreviousTrans().getPolicyId()));
				 Streamable.streamOf(prevRiskTrans).forEach(x -> {
					 Iterable<RiskInterestedParties> prevIntParties = riskIntPartiesRepo.findAll(QRiskInterestedParties.riskInterestedParties.risk.riskId.eq(x.getRiskId()));
					 Set<RiskInterestedParties> prevparties = Streamable.streamOf(prevIntParties).collect(Collectors.toSet());
					 
					 parties.removeAll(prevparties);
					 
					   for (RiskInterestedParties party:parties) {
						   intparty.append(party.getInterestedParties().getPartName());
					   }
				 });
			 });
		 
		 }
		VelocityContext context = new VelocityContext();
		context.put("policy", policy.getPolNo());
		context.put("polno", policy.getPolNo());
		context.put("client", policy.getClient().getFname()+" "+policy.getClient().getOtherNames());
		context.put("agent", policy.getAgent().getName());
		context.put("premium", policy.getBasicPrem());
		context.put("fap", policy.getFuturePrem());
		context.put("endosno", policy.getPolRevNo());
		context.put("endorsno", policy.getPolRevNo());
		context.put("orgname",organizationService.getOrganizationDetails().getOrgName());
		context.put("wef",dateUtilities.formatDate(policy.getWefDate()));
		context.put("wet",dateUtilities.formatDate(policy.getWetDate()));
		if (policy.getRenewalDate() != null) {
			context.put("rendate", dateUtilities.formatDate(policy.getRenewalDate()));
		}
		context.put("refno",policy.getRefNo());
		context.put("totalSI",total);
		context.put("polmonths",dateUtilities.getNumberOfMonths(policy.getWefDate(),policy.getWetDate()));
		context.put("intparty",intparty.toString());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "policyMerge", rawText);
		return writer.toString();
	}

	public String mergeQuoteDetails(QuoteTrans quoteTrans, String rawText){
		VelocityContext context = new VelocityContext();
		context.put("quote", quoteTrans.getQuotNo());
		if("C".equalsIgnoreCase(quoteTrans.getClientType())) {
			context.put("client", quoteTrans.getClient().getFname());
		}
		else if("P".equalsIgnoreCase(quoteTrans.getClientType())) {
			context.put("client", quoteTrans.getProspect().getFname());
		}
		context.put("premium", quoteTrans.getPremium());
		context.put("wef",quoteTrans.getWefDate());
		context.put("orgname",organizationService.getOrganizationDetails().getOrgName());
		context.put("wef",dateUtilities.formatDate(quoteTrans.getWefDate()));
		context.put("wet",dateUtilities.formatDate(quoteTrans.getWetDate()));

		System.out.println("Premium..."+context.get("premium"));
		System.out.println("Premium..."+quoteTrans.getPremium());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "quoteMerge", rawText);
		return writer.toString();
	}

	public String mergeMedClaims(MedicalParTrans parTrans,String rawText){
		VelocityContext context = new VelocityContext();
		context.put("parNo", parTrans.getParNo());
		context.put("status", parTrans.getParStatus());
		context.put("parRevNo", parTrans.getParRevisionNo());
		context.put("memberNo",parTrans.getCategoryMember().getMemberShipNo());
		context.put("client", parTrans.getCategoryMember().getClient().getFname()+" "+parTrans.getCategoryMember().getClient().getOtherNames());
		context.put("orgname",organizationService.getOrganizationDetails().getOrgName());
		context.put("eventDate",dateUtilities.formatDate(parTrans.getEventDate()));
		context.put("notDate",dateUtilities.formatDate(parTrans.getNotDate()));
		context.put("servProvider",parTrans.getProviderContracts().getServiceProviders().getName());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "medClaimsMerge", rawText);
		return writer.toString();
	}

	public String mergeClaims(ClaimBookings claimBookings, String rawText){
		VelocityContext context = new VelocityContext();
//		context.put("parNo", parTrans.getParNo());
//		context.put("status", parTrans.getParStatus());
//		context.put("parRevNo", parTrans.getParRevisionNo());
//		context.put("memberNo",parTrans.getCategoryMember().getMemberShipNo());
		context.put("client",claimBookings.getRisk().getPolicy().getClient().getFname()+" "+claimBookings.getRisk().getPolicy().getClient().getOtherNames());
//		context.put("orgname",organizationService.getOrganizationDetails().getOrgName());
//		context.put("eventDate",dateUtilities.formatDate(parTrans.getEventDate()));
//		context.put("notDate",dateUtilities.formatDate(parTrans.getNotDate()));
//		context.put("servProvider",parTrans.getProviderContracts().getServiceProviders().getName());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "medClaimsMerge", rawText);
		return writer.toString();
	}


	public String generateFormat(String rawText,Long branchCode,Long prodCode,Date wefDate,String counter,RiskTrans riskTrans) throws BadRequestException{
		VelocityContext context = new VelocityContext();
		if(branchCode!=null){
			OrgBranch branch = branchRepository.findOne(branchCode);
			context.put("BRANCH", branch.getObShtDesc());
		}
		if(prodCode!=null){
			ProductsDef product = productsRepo.findOne(prodCode);
			context.put("PRODUCT",(product.getProPolPrefix()==null || StringUtils.isBlank(product.getProPolPrefix()))?product.getProShtDesc():product.getProPolPrefix()+"/"+product.getProShtDesc());
		}
		if(wefDate!=null){
			context.put("MON", new SimpleDateFormat("MMM").format(wefDate));
			context.put("M", new SimpleDateFormat("MM").format(wefDate));
			context.put("YYYY", new SimpleDateFormat("yyyy").format(wefDate));
			context.put("YY", new SimpleDateFormat("yy").format(wefDate));
		}
		if (riskTrans!=null){
			context.put("INSURANCECOMPANY",(riskTrans.getBinder()==null)?riskTrans.getBinder().getAccount().getShtDesc():riskTrans.getPolicy().getBinder().getAccount().getShtDesc() );
		}
		if(StringUtils.isBlank(StringUtils.trim(counter))){
			throw new BadRequestException("Error getting Policy Number Generation Sequence...");
		}
		else{
			context.put("COUNTER", counter);
		}
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "policyNumberFormat", rawText);
		return writer.toString();
	}

	public void sendPasswordResetEmail(String username,String password, HttpServletRequest request) throws BadRequestException {
		String rawText = mailTemplateService.getMailTemplate(MailTemplates.EMAIL_RESET_TEMPLATE,request);
		UserDTO user = userService.findByUserName(username);
		if(user==null){
			throw new BadRequestException("Invalid Username....");
		}
		if(user.getEmail()==null){
			throw new BadRequestException("The User has no email to continue the process of password reset...");
		}
		VelocityContext context = new VelocityContext();
		context.put("newPass", password);
		context.put("name", user.getName());
		context.put("org",organizationService.getOrganizationDetails().getOrgName());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "mailPassMerge", rawText);
		final MailMessageBean messageBean = new MailMessageBean();
		messageBean.setMessage(writer.toString());
		messageBean.setSendTo(user.getEmail());
		messageBean.setSubject("Password Reset Email");
		sendEmailPublisherBean.sendEmail(messageBean);
	}

	public void sendClaimReminders(ClaimActivities claimActivities) throws BadRequestException {
		String rawText = mailTemplateService.getClaimReminderTemplate();
		if(claimActivities.getReviewUser()==null||claimActivities.getReviewUser().getUsername()==null){
			  throw new BadRequestException("Invalid Username....");
		}
		UserDTO user =  userService.findByUserName(claimActivities.getReviewUser().getUsername());
		VelocityContext context = new VelocityContext();
		context.put("name", user.getName());
		context.put("org",organizationService.getOrganizationDetails().getOrgName());
		context.put("claim",claimActivities.getClaimBookings().getClaimNo());
		context.put("activity",claimActivities.getActivity().getActivityDesc());
		context.put("date",new SimpleDateFormat("dd-MMM-yyyy").format(claimActivities.getRemDate()));
        context.put("tense",(new Date().before(claimActivities.getRemDate()))?"was":"is");
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "mailPassMerge", rawText);
		MailMessageBean messageHelper = new MailMessageBean();
		//messageHelper.setSendTo(user.getEmail());
		//todo remove this line below after completion of testing
		// added for testing by absa bank
		messageHelper.setSendTo("Kennedy.Wangui@absa.africa");
		messageHelper.setSubject("Claim Reminder");
		messageHelper.setMessage(writer.toString());
		sendEmailPublisherBean.sendEmail(messageHelper);

	}

	public String sendCertAllocEmail(Long userId,Long certFrom,Long certTo, String insurance, String certtype,String alloc,HttpServletRequest request) throws BadRequestException {
		String rawText = mailTemplateService.getMailTemplate(MailTemplates.CERT_ALLOC_TEMPLATE, request);
		try {
			User user = userService.findById(userId);
			if (user == null) {
				return "No user to send email to....";
			}
			VelocityContext context = new VelocityContext();
			if ("N".equalsIgnoreCase(alloc)) {
				context.put("activity", "de-allocated from you");
				context.put("requiredaction","");
			}else {
				context.put("activity", "allocated to you");
				context.put("requiredaction", "Please acknowledge in the system once received for the certificates to be available for printing.");
			}
			context.put("certfrom", certFrom);
			context.put("certto", certTo);
			context.put("name", user.getName());
			context.put("allocatinguser", userUtils.getCurrentUser().getName());
			context.put("insurer", insurance);
			context.put("certtype", certtype);
			context.put("org", organizationService.getOrganizationDetails().getOrgName());
			StringWriter writer = new StringWriter();
			engine.evaluate(context, writer, "mailPassMerge", rawText);
			final String subject =  ("N".equalsIgnoreCase(alloc))?"Certificate Deallocation":"Certificate Allocation";
			MailMessageBean messageHelper = new MailMessageBean();
			messageHelper.setSendTo(user.getEmail());
			messageHelper.setSubject(subject);
			messageHelper.setMessage(writer.toString());
			sendEmailPublisherBean.sendEmail(messageHelper);
		}catch (Exception ex){
			return ex.toString();
		}
	return	"sent";
	}

	public void sendEmail(UserDto userDto, HttpServletRequest request) throws BadRequestException {
		String rawText = mailTemplateService.getMailTemplate(MailTemplates.EMAIL_TEMPLATE,request);
		UserDTO user = userService.findByUserName(userDto.getJ_username());
		if(user==null){
			throw new BadRequestException("Invalid Username....");
		}
		PasswordResetToken token = new PasswordResetToken();
		token.setToken(UUID.randomUUID().toString());
		token.setUser(userService.findById(user.getId()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, 24);
		java.util.Date expirationDate = cal.getTime();
		token.setExpiryDate(expirationDate);
		passwordResetTokenRepo.save(token);
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+""+request.getContextPath();
		VelocityContext context = new VelocityContext();
		context.put("resetUrl", url + "/reset-password?token=" + token.getToken());
		context.put("name", user.getName());
		context.put("org",organizationService.getOrganizationDetails().getOrgName());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "mailMerge", rawText);
		System.out.println("Sending forgot password email...");
		MailMessageBean messageHelper = new MailMessageBean();
		messageHelper.setSendTo(user.getEmail());
		messageHelper.setSubject("Password Reset Request");
		messageHelper.setMessage(writer.toString());
		sendEmailPublisherBean.sendEmail(messageHelper);
	}

}