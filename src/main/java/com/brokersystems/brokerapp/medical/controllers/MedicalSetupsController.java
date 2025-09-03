package com.brokersystems.brokerapp.medical.controllers;

import com.brokersystems.brokerapp.accounts.model.BankBranches;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.service.MedicalSetupsService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 4/21/2017.
 */
@Controller
@RequestMapping({ "/protected/medical/setups" })
public class MedicalSetupsController {

    @Autowired
    private MedicalSetupsService medicalSetupsService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "bedtypes",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String bedTypes(Model model, HttpServletRequest request)
    {
        String message="Accessed Bed Types Screen";
        String resource = "Bed Types";
        auditTrailLogger.log(message,request, resource);
        return "bedtypes";
    }

    @RequestMapping(value = "cardtypes",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String cardtypes(Model model, HttpServletRequest request)
    {
        String message="Accessed Card Types Screen";
        String resource = "Card Types";
        auditTrailLogger.log(message,request, resource);

        return "cardtypes";
    }

    @RequestMapping(value = "labtests",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String labtests(Model model, HttpServletRequest request)
    {
        String message="Accessed Services/Lab Test Tests Screen";
        String resource = "Lab Tests";
        auditTrailLogger.log(message,request, resource);

        return "labtests";
    }

    @RequestMapping(value = "dependtypes",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String dependtypes(Model model, HttpServletRequest request)
    {
        String message="Accessed Dependants Screen";
        String resource = "Depend Types";
        auditTrailLogger.log(message,request, resource);
        return "dependtypes";
    }

    @RequestMapping(value = "bensections",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String bensections(Model model, HttpServletRequest request)
    {
        String message="Accessed Claim Networks Screen";
        String resource = "Ben Sections";
        auditTrailLogger.log(message,request, resource);
        return "bensections";
    }

    @RequestMapping(value = "specfees",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String specfees(Model model, HttpServletRequest request)
    {
        String message="Accessed Specialist Fee Screen";
        String resource = "Spec Fees";
        auditTrailLogger.log(message,request, resource);

        return "specfees";
    }

    @RequestMapping(value = "benefits",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String benefits(Model model, HttpServletRequest request)
    {
        String message="Accessed Benefits Screen";
        String resource = "Benefits";
        auditTrailLogger.log(message,request, resource);

        return "benefits";
    }

    @RequestMapping(value = "familysizes",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String familysizes(Model model, HttpServletRequest request)
    {
        String message="Accessed Family Sizss Screen";
        String resource = "Family Sizes";
        auditTrailLogger.log(message,request, resource);

        return "familysize";
    }

    @RequestMapping(value = "ailment",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String ailments(Model model, HttpServletRequest request)
    {
        String message="Accessed Ailments Screen";
        String resource = "Ailment";
        auditTrailLogger.log(message,request, resource);
        return "ailment";
    }

    @RequestMapping(value = "servproviders",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String providers(Model model, HttpServletRequest request)
    {
        String message="Accessed Service Providers Screen";
        String resource = "Serv providers";
        auditTrailLogger.log(message,request, resource);

        return "providers";
    }

    @RequestMapping(value = "providercontract",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String providercontract(Model model, HttpServletRequest request)
    {
        String message="Accessed Service Providers Contract Screen";
        String resource = "Provider Contract";
        auditTrailLogger.log(message,request, resource);

        return "sproviderscontract";
    }


    @RequestMapping(value = { "bedtypeslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<BedTypes> getBedTypes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllBedTypes(pageable);
    }


    @RequestMapping(value = { "cardtypeslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CardTypes> getCardTypes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllCardTypes(pageable);
    }



    @RequestMapping(value = { "familysizelist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<FamilySizes> getFamilySize(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findFamilySize(pageable);
    }


    @RequestMapping(value = { "createFamilySize" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createFamilySize(FamilySizes familySizes) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineFamilySize(familySizes);
    }

    @RequestMapping(value = { "deleteFamilySize/{famId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFamilySize(@PathVariable Long famId) {
        medicalSetupsService.deleteFamilySize(famId);
    }

    @RequestMapping(value = { "createBedTypes" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createBedTypes(BedTypes bedTypes) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineBedTypes(bedTypes);
    }

    @RequestMapping(value = { "createCardTypes" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createCardTypes(CardTypes cardTypes) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineCardTypes(cardTypes);
    }


    @RequestMapping(value = { "deleteBedTypes/{bedId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBedTypes(@PathVariable Long bedId) {
        medicalSetupsService.deleteBedTypes(bedId);
    }

    @RequestMapping(value = { "deleteCardTypes/{cardId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCardTypes(@PathVariable Long cardId) {
        medicalSetupsService.deleteCardTypes(cardId);
    }

    @RequestMapping(value = { "labtestslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<LabTest> getLabTests(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllLabtests(pageable);
    }

    @RequestMapping(value = { "createLabTest" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createLabTest(LabTest labTest) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineLabTests(labTest);
    }

    @RequestMapping(value = { "deletelabTest/{labId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletelabTest(@PathVariable Long labId) {
        medicalSetupsService.deleteLabTests(labId);
    }

    @RequestMapping(value = { "dependtypeslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<DependentTypes> dependtypeslist(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllDependentTypes(pageable);
    }

    @RequestMapping(value = { "createDependTypes" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createDependTypes(DependentTypes dependentTypes) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineDependentTypes(dependentTypes);
    }

    @RequestMapping(value = { "deleteDependTypes/{depId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDependTypes(@PathVariable Long depId) {
        medicalSetupsService.deleteDependentTypes(depId);
    }


    @RequestMapping(value = { "bensectionslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalNetworks> bensectionslist(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllBenefitSections(pageable);
    }

    @RequestMapping(value = { "createBenSections" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createBenSections(MedicalNetworks medicalNetworks) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineBenefitSections(medicalNetworks);
    }

    @RequestMapping(value = { "deleteBenSections/{sectId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBenSections(@PathVariable Long sectId) {
        medicalSetupsService.deleteBenefitSections(sectId);
    }


    @RequestMapping(value = { "specfeeslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SpecialistFees> specfeeslist(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllSpecialistFees(pageable);
    }

    @RequestMapping(value = { "createSpecialFees" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createSpecialFees(SpecialistFees specialistFees) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineSpecialistFees(specialistFees);
    }

    @RequestMapping(value = { "deleteSpecialistFees/{specId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialistFees(@PathVariable Long specId) {
        medicalSetupsService.deleteSpecialistFees(specId);
    }


    @RequestMapping(value = { "benefitslist/{serviceCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedActivities> benefitslist(@DataTable DataTablesRequest pageable,@PathVariable Long serviceCode)
            throws IllegalAccessException {
        return medicalSetupsService.findAllBenefits(pageable,serviceCode);
    }

    @RequestMapping(value = { "createBenefits" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createbenefits(MedActivities benefits) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineBenefits(benefits);
    }

    @RequestMapping(value = { "deleteBenefits/{benId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBenefits(@PathVariable Long benId) {
        medicalSetupsService.deleteBenefits(benId);
    }


    @RequestMapping(value = { "ailmentslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<Ailments> ailmentList(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAilments(pageable);
    }

    @RequestMapping(value = { "createAilment" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createAilment(Ailments ailments) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineAilments(ailments);
    }

    @RequestMapping(value = { "deleteAilment/{ailId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAilment(@PathVariable Long ailId) {
        medicalSetupsService.deleteAilments(ailId);
    }

    @RequestMapping(value = { "providertypeslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedServiceProviderTypes> providerTypes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findServiceProviderTypes(pageable);
    }

    @RequestMapping(value = { "createProviderTypes" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createProviderTypes(MedServiceProviderTypes providerTypes) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineProviderTypes(providerTypes);
    }

    @RequestMapping(value = { "deleteProviderTypes/{provId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProviderTypes(@PathVariable Long provId) {
        medicalSetupsService.deleteProviderTypes(provId);
    }


    @RequestMapping(value = { "providerlist/{provId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedServiceProviders> providerlist(@DataTable DataTablesRequest pageable,@PathVariable Long provId)
            throws IllegalAccessException {
        return medicalSetupsService.findServiceProviders(pageable,provId);
    }

    @RequestMapping(value = { "createProviders" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createProviders(MedServiceProviders providers) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineProviders(providers);
    }

    @RequestMapping(value = { "createPrvdrServices" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createPrvdrServices(ServiceProviderServices services) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineProviderSerces(services);
    }

    @RequestMapping(value = { "deletePrvdrService/{provServId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrvdrService(@PathVariable Long provServId) {
        medicalSetupsService.deleteProviderService(provServId);
    }



    @RequestMapping(value = { "deleteProviders/{provId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProviders(@PathVariable Long provId) {
        medicalSetupsService.deleteProviders(provId);
    }

    @RequestMapping(value={"providertypessel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<MedServiceProviderTypes> providertypessel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findProviderTypesForSelect(term, pageable);
    }

    @RequestMapping(value={"bankbranchsel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<BankBranches> bankbranchsel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findBankBranchesForSelect(term, pageable);
    }


    @RequestMapping(value = "events",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String events(Model model, HttpServletRequest request)
    {
        String message="Accessed Events Screen";
        String resource = "Events";
        auditTrailLogger.log(message,request, resource);

        return "event";
    }

    @RequestMapping(value = { "eventslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalEvents> eventslist(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return medicalSetupsService.findAllEvents(pageable);
    }

    @RequestMapping(value = { "createEvents" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createEvent(MedicalEvents event) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineEvents(event);
    }

    @RequestMapping(value = { "deleteEvent/{eventId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvents(@PathVariable Long eventId) {
        medicalSetupsService.deleteEvents(eventId);
    }


    @RequestMapping(value={"providersel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<MedServiceProviders> providersel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findProviderForSelect(term, pageable);
    }

    @RequestMapping(value = { "providercontracts/{bindCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ServiceProviderContracts> providercontracts(@DataTable DataTablesRequest pageable, @PathVariable Long bindCode)
            throws IllegalAccessException {
        return medicalSetupsService.findAllProviderContracts(pageable,bindCode);
    }

    @RequestMapping(value = { "providerservices/{provSrvId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ServiceProviderServices> providerservices(@DataTable DataTablesRequest pageable, @PathVariable Long provSrvId)
            throws IllegalAccessException {
        return medicalSetupsService.findProviderServices(pageable,provSrvId);
    }

    @RequestMapping(value = { "servicesActvty/{serviceId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ServiceProviderActivities> getServicesActivities(@DataTable DataTablesRequest pageable, @PathVariable Long serviceId)
            throws IllegalAccessException {
        return medicalSetupsService.findServiceActivities(pageable,serviceId);
    }

    @RequestMapping(value = { "createProviderContract" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createProviderContract(ServiceProviderContracts providerContract) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineProviderContract(providerContract);
    }

    @RequestMapping(value = { "deleteProviderContract/{contractId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProviderContract(@PathVariable Long contractId) {
        medicalSetupsService.deleteProviderContract(contractId);
    }

    @RequestMapping(value = { "diagnosis/{ailId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<Ailments> ailmentsList(@DataTable DataTablesRequest pageable,@PathVariable Long ailId)
            throws IllegalAccessException {
        return medicalSetupsService.findAilmentDiagnosis(pageable,ailId);
    }

    @RequestMapping(value={"medsectionssel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<SectionsDef> medsectionssel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findMedicalSections(term, pageable);
    }

    @RequestMapping(value = { "defineBinderExclusion" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void defineBinderExclusion(BinderExclusions binderExclusion) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineBinderExclusion(binderExclusion);
    }

    @RequestMapping(value = { "defineBinderLoading" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void defineBinderLoading(BinderLoadings binderLoading) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.defineBinderLoadings(binderLoading);
    }

    @RequestMapping(value = { "getUnassignedServices" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<LabTest> getUnassignedServices(@RequestParam(value = "providerId", required = false) Long providerId, @RequestParam(value = "search", required = false) String search )
            throws IllegalAccessException {
        return medicalSetupsService.findUnassignedServices(providerId,  search);
    }

    @RequestMapping(value = { "getUnassignedServiceActvty" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<MedActivities> getUnassignedServiceActvty(@RequestParam(value = "serviceId", required = false) Long serviceId, @RequestParam(value = "search", required = false) String search )
            throws IllegalAccessException {
        return medicalSetupsService.findUnassignedServiceActivities(serviceId,  search);
    }

    @RequestMapping(value = { "createProviderServices" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createProviderServices(@RequestBody providerServicesBean servicesBean) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.createProviderServices(servicesBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "createPrvdrSrvActivity" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createProviderSrvcActvty(@RequestBody providerServicesActivitiesBean activityBean) throws IllegalAccessException, IOException, BadRequestException {
        medicalSetupsService.createProviderServiceActvty(activityBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

}
