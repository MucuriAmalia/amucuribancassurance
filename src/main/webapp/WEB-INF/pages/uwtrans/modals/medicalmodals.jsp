<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal fade" id="categoryModal" tabindex="-1" role="dialog"
     aria-labelledby="categoryModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="categoryModalLabel">
                    Edit/Add Category
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="med-category-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="category-id" name="id">
                    <input type="hidden" id="category-pol-id" name="policy">


                    <div class="item form-group categoryLov">
                        <label for="unit-id" class="col-md-3 label-align">Category</label>

                        <div class="col-md-8">
                            <input type="hidden" id="binder-det-pk" name="binderDetails">
                            <div id="binderdet-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/binderDetails"/>" >

                            </div>
                        </div>
                    </div>

                        <div class="item form-group categoryDisplay" >
                            <label for="unit-id" class="col-md-3 label-align">Category</label>
                            <div class="col-md-8">
                                <p class="form-control-static" id="binderDetails-Display"> </p>
                            </div>
                        </div>
                    <div class="item form-group categoryDetails" >
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Sht Desc</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="sht-desc"
                                       name="shtDesc"  required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Description</label>

                            <div class="col-md-8">
                                <textarea class="form-control" rows="3" id="cat-desc" name="desc"></textarea>

                            </div>
                        </div>
                    </div>
                    <div class="item form-group bedtypedetails" >
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bed Type</label>

                        <div class="col-md-8">
                            <input type="hidden" id="bedtype-pk" name="bedTypes">
                            <input type="hidden" id="bedtype-disp">
                            <div id="bedtypes-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/selBedtypes"/>" >

                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Override Bed Cost</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="bed-cost"
                                   name="bedCost"  required>
                        </div>
                    </div>
            </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCategory"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button data-loading-text="Saving..." id="saveCategoryBedType"
                        type="button" class="btn btn-success">
                    Save Bed
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editTaxModal" tabindex="-1" role="dialog"
     aria-labelledby="taxModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="taxModalLabel">
                    Edit Tax
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="med-tax-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="tax-id" name="polTaxId">
                    <input type="hidden" id="tax-policy-id" name="policy">

                    <div class="item form-group" >
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Trans Code</label>
                            <div class="col-md-8">
                                <p class="form-control-static" id="Trans-Code"> </p>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Rate</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control" id="tax-rate" name="taxRate" required>

                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Div factor</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control"  id="tax-rate-divfact" name="divFactor" required>

                            </div>
                        </div>

                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savePolTax"
                        type="button" class="btn btn-success">
                    Save
                </button>

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade  " id="editPolicyModal" tabindex="-1" role="dialog"
     aria-labelledby="editPolicyModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editPolicyModalLabel">
                    Edit/Add Policy Details
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="med-polcy-form" class="form-horizontal">
                    <input type="hidden" id="pol-id" name="policy">
                    <input type="hidden" id="pol-createdate" name="polCreateddt">
                    <input type="hidden" id="edit-pol-pk" name="policyId">
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="unit-id" class="label-align col-md-5">
                                Period<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="pol-busstype" name="businessType"
                                >
                                    <option value="">Business Type</option>
                                    <option value="N">Annual</option>
                                    <option value="S">Short Period</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="pol-bintype" class="label-align col-md-5">
                                Scheme Type<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="pol-bintype" name="binder.binType"
                                >
                                    <option value="">Select Scheme Type</option>
                                    <option value="B">Insured Binder</option>
                                    <option value="BN">Insured Non-Binder</option>
                                    <option value="S">Self Fund</option>
                                </select>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="client-frm" class="label-align col-md-5">
                                Scheme/Client<span class="required">*</span></label>
                            <div class="col-md-6 col-xs-12">
                                <div class="edit-client">
                                    <input type="hidden" id="clientid" name="clientId"/>
                                    <input type="hidden" id="client-fname">
                                    <input type="hidden" id="client-othername">
                                    <div id="client-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                                    </div>

                                </div>
                                <div id="display-client">
                                    <p class="form-control-static" id="clientinfo"> </p>
                                </div>
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="binder-frm" class="label-align col-md-5">
                                Binder/Mask<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-binder">
                                    <input type="hidden" id="binderid" name="bindCode"/>
                                    <input type="hidden" id="productid" name="prodId"/>
                                    <input type="hidden"  id="pol-agentid" name="agentId"/>
                                    <input type="hidden" id="bindname" name="bindName"/>
                                    <input type="hidden" name="productName" id="bind-proname"/>
                                    <input type="hidden" name="insuranceCompany" id="bind-insname"/>
                                    <div id="binder-frm" class="form-control"
                                         select2-url="<c:url value="/protected/medical/policies/uwBinders"/>" >

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="pol-inscomp" class="label-align col-md-5">
                                Intermediary</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="pol-inscomp"></p>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="pol-prodname" class="label-align col-md-5">
                                Product</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="pol-prodname"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="brn-id" class="col-md-5 label-align">Date
                                From</label>

                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="wef-date">
                                    <input  class="form-control float-right" name="wefDate"
                                                 id="fromdate" />
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Date
                                To</label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="cover-to-date">
                                    <input type='text' class="form-control float-right" name="wetDate"
                                                id="wetdate"/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>

                        </div>


                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="pol-interfacetype" class="label-align col-md-5">
                                Interface Type<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="pol-interfacetype" name="interfaceType"
                                >
                                    <option value="">Select Interface Type</option>
                                    <option value="A">Accrual</option>
                                    <option value="C">Cash</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="pm-mode-frm" class="label-align col-md-5">
                                Payment Mode<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-payment-mode">
                                    <input type="hidden" id="pmid" name="paymentId"/>
                                    <input type="hidden"  id="pmname" name="paymentMode.pmDesc"/>
                                    <div id="pm-mode-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="polfrequency" class="label-align col-md-5">
                                Payment Frequency<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="polfrequency" name="frequency"
                                >
                                    <option value="">Select Payment Frequency</option>
<%--                                    <option value="D">Daily</option>--%>
<%--                                    <option value="W">Weekly</option>--%>
                                    <option value="M">Monthly</option>
                                    <option value="Q">Quartely</option>
                                    <option value="S">Semi-Annually</option>
                                    <option value="A">Annually</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="brn-frm" class="label-align col-md-5">
                                Branch<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-branch">
                                    <input type="hidden" id="brnid" name="branchId"/>
                                    <input type="hidden"  id="brnname" name="branch.obName"/>
                                    <div id="brn-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwbranches"/>" >

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="client-polno" class="label-align col-md-5">
                                Insurer Policy number<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input name="clientPolNo" id="client-polno" class="form-control"
                                            placeholder="Client Policy Number"/>
                            </div>
                        </div>

                        <div class="col-md-6 col-xs-12">
                            <label for="medicalCoverTypinfo" class="label-align col-md-5">
                                Type</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="polmedicalCoverType" name="medicalCoverType"/>
                                <div id="display-medicalCoverTyp">
                                    <p class="form-control-static" id="medicalCoverTypinfo"> </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="curr-frm" class="label-align col-md-5">
                                Currency<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-currency">
                                    <input type="hidden" id="curid" name="currencyId"/>
                                    <input type="hidden" id="curname" name="transCurrency.curName"/>
                                    <div id="curr-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >

                                    </div>
                                </div>
                            </div>
                        </div>


                        <div class="item form-group policy-card-type">
                            <div class="col-md-6 col-xs-12" >
                                <label for="card-frm" class="label-align col-md-5">
                                    Card Type</label>

                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="pol-cardid" name="cardId"/>
                                    <input type="hidden" id="card-name">
                                    <div id="card-frm" class="form-control"
                                         select2-url="<c:url value="/protected/medical/policies/binderCardTypes"/>" >

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savePolicy"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="selfFundModal" tabindex="-1" role="dialog"
     aria-labelledby="selfFundModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="selfFundModalLabel">
                    Add/Edit Self Fund
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="self-fund-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="self-fund-pk" name="sfpId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-4 label-align">Applicable Level</label>

                        <div class="col-md-8">
                            <select class="form-control" id="self-appli-level" name="applicableLevel" required>
                                <option value="">Select Applicable Level</option>
                                <option value="FFS">Fixed Fee Per Scheme</option>
                                <option value="FFP">Fixed Fee Per Person</option>
                                <option value="FFF">Fixed Fee Per Family</option>
                                <option value="FFC">Fixed Fee Per Claim</option>
                                <option value="PPC">Percentage Per Claim</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-4 label-align">Applicable Value</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control formatcurrency" id="self-appli-value"
                                   name="applicableValue"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Pay When Fund Exhausted?</label>
                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" name="payWhenFundExhausted" id="pay-fund-exhausted">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Pay When Benefit Exhausted?</label>
                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" name="payWhenBenefitExhausted" id="pay-benefit-exhausted">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Carry Forward Fund Balance?</label>
                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" name="carryForwardBalances" id="carry-forward-fund">
                            </label>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Minimum Deposit</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control formatcurrency" id="self-min-deposit"
                                   name="minDeposit"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Fund Reset Amount</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control formatcurrency" id="fund-reset-deposit"
                                   name="fundResetAmount"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Fund Deposit Amount</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control formatcurrency" id="fund-deposit-amount"
                                   name="fundDepositAmount"  required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-4 label-align">Billing Frequency</label>

                        <div class="col-md-8">
                            <select class="form-control" id="self-billing-freq" name="billingFrequency" required>
                                <option value="">Select Billing Frequency</option>
                                <option value="M">Monthly</option>
                                <option value="Q">Quarterly</option>
                                <option value="S">Semi-Annually</option>
                                <option value="A">Annually</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-4 label-align">Deduct Admin Fee From Fund?</label>
                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" name="deductAdminFeeFromFund" id="deduct-admin-from-fund">
                            </label>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSelfFund"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="memberModal" tabindex="-1" role="dialog"
     aria-labelledby="memberModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="memberModalLabel">
                    Edit/Add Category Member
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <input type="hidden" id="member-date-from"/>
                <input type="hidden" id="member-date-to"/>
                <form id="member-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="member-pk" name="sectId">
                    <input type="hidden" id="member-cat-id" name="category">

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Member</label>

                        <div class="col-md-8">
                            <input type="hidden" id="member-client-pk" name="client">
                            <input type="hidden" id="member-fname">
                            <input type="hidden" id="member-oname">
                            <div id="member-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                            </div>
                        </div>
                        <div class="col-md-1">
                            <a
                                    href="<c:url value="/protected/clients/setups/clientsform?type=medmember"/>" id="btn-add-client"  class="btn-success btn-info">New</a>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Dependent Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="dependent-type" name="dependentTypes" required>
                                <option value="">Select Dependent Type</option>
                                <option value="P">Principal</option>
                                <option value="S">Spouse</option>
                                <option value="C">Dependant</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group member-principal">
                        <label for="unit-id" class="col-md-3 label-align">Principal</label>

                        <div class="col-md-8">
                            <input type="hidden" id="principal-client-pk" name="mainClient">
                            <input type="hidden" id="principal-fname">
                            <input type="hidden" id="principal-oname">
                            <div id="princi-member-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/principals"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group membercardDetails">
                        <div class="item form-group memberhascardDetails">
                            <label for="unit-id" class="col-md-3 label-align">Has Card</label>

                            <div class="col-md-8">
                                <select class="form-control" id="memberhas-card" name="memberHasCard" required>
                                    <option value="">Does Member Has a Card?</option>
                                    <option value="Y">Yes</option>
                                    <option value="N">No</option>
                                </select>
                            </div>
                        </div>
                        <div class="item form-group generateCardNoDetails" >
                            <div class="item form-group">
                                <label for="unit-id" class="col-md-3 label-align">Generate Card</label>

                                <div class="col-md-8">
                                    <select class="form-control" id="generate-card" name="autoGenerateCard" required>
                                        <option value="">Generate Card?</option>
                                        <option value="Y">Yes</option>
                                        <option value="N">No</option>
                                    </select>
                                </div>
                            </div>
                            </div>
                            <div class="item form-group cardNoDetails" >
                                <div class="item form-group">
                                    <label for="unit-id" class="col-md-3 label-align">Card No</label>

                                    <div class="col-md-8">
                                        <input type="text" class="form-control" id="card-no"
                                               name="cardNo"  required>
                                    </div>
                                </div>
                            </div>
                    </div>
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-3 label-align">Date
                                From</label>
                            <div class="col-md-8 col-xs-12">
                                <div class='input-group date datepicker-input' id="risk-cover-from">
                                    <input type='text' class="form-control float-right" name="wefDate"
                                           id="risk-wef-date" required />
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="noOfUnits" class="label-align col-md-3">Date
                                To</label>
                            <div class="col-md-8 col-xs-12">
                                <div class='input-group date datepicker-input' id="risk-cover-to">
                                    <input type='text' class="form-control float-right" name="wetDate"
                                           id="risk-wet-date" required />
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    <div class="item form-group child-type">
                        <label for="unit-id" class="col-md-3 label-align">Select Dependant</label>

                        <div class="col-md-8">
                            <select class="form-control" id="child-type" name="childType" required>
                                <option value="">Select</option>
                                <option value="D">Daughter</option>
                                <option value="S">Son</option>
                                <option value="B">Brother</option>
                                <option value="SS">Sister</option>
                                <option value="N">Nephew</option>
                                <option value="C">Cousin</option>
                            </select>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCategoryMember"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="catRulesModal" tabindex="-1" role="dialog"
     aria-labelledby="categoryModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="catRulesModalLabel">
                    Edit Category Rule
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="cat-rule-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="cat-rule-pk" name="sectId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Rule Description</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="rule-desc-disp"> </p>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Rule Value</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="rule-value"
                                   name="value"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCategoryRule"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="editLoadingsModal" tabindex="-1" role="dialog"
     aria-labelledby="loadingsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="loadingsModalLabel">
                    Edit A Loading
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="cat-loading-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="cat-loading-pk" name="clId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Loading</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="loading-desc-disp"> </p>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="load-rate-type" class="col-md-3 label-align">Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="load-rate-type" name="rateType" required>
                                <option value="">Select Rate Type</option>
                                <option value="P">Percentage</option>
                                <option value="M">Per Mille</option>
                                <option value="A">Amount</option>

                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="loading-rate"
                                   name="rate"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="createCatLoadings"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="reportsModal" tabindex="-1" role="dialog"
     aria-labelledby="reportsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="reportsModalLabel">Reports</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6">

                        <ul style="list-style-type: none;" class="reports-links">
                            <li><a
                                    href="<c:url value='/protected/medical/policies/rpt_risk_note'/> "
                                    target="_blank">Risk Note</a></li>
                            <li><a
                                    href="<c:url value='/protected/uw/policies/rpt_debit_note'/> "
                                    target="_blank">Debit Note</a></li>
                            <li><a
                                    href="<c:url value='/protected/uw/policies/rpt_prem_working'/> "
                                    target="_blank">Premium Working</a></li>
                            <li class="endorse-disp"><a
                                    href="<c:url value='/protected/uw/policies/rpt_endorse'/> "
                                    target="_blank">Endorsement Report</a></li>
                            <li class="motor-disp"><a
                                    href="<c:url value='/protected/uw/policies/rpt_renewal_notice_motor'/> "
                                    target="_blank">Renewal Notice</a></li>
                            <li ><a href="javascript:void(0);"  onclick="UTILITIES.sendEmail();">Send Email</a></li>
                        </ul>
                    </div>
                    <div class="col-md-6">

                        <ul style="list-style-type: none;">
                            <!--<li><a href="#">Invoice Note</a></li> -->
                        </ul>

                    </div>
                </div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="emailModal" tabindex="-1" role="dialog"
     aria-labelledby="emailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="emailModalLabel">Send Email</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="email-form"  class="form-horizontal">
                    <div class="item form-group">
                        <label for="email-to" class="col-md-2 label-align">Email To</label>

                        <div class="col-md-10">
                            <select class="form-control" id="email-to" name="receiverType">
                                <option value="">Select Email To</option>
                                <option value="C">Client</option>
                                <option value="A">Intermediary</option>
                                <option value="IN">Inhouse</option>
                                <option value="B">Both</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-send-to" class="col-md-2 label-align">Send To</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-send-to" name="sendTo"
                                   readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-cc" class="col-md-2 label-align">CC</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-cc" name="sendCC"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-bcc" class="col-md-2 label-align">BCC</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-bcc" name="sendBcc"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Reports</label>

                        <div class="col-md-10">
                            <label class="checkbox-inline"><input type="checkbox" value="RN">Risk Note</label>
                            <label class="checkbox-inline"><input type="checkbox" value="DN">Debit Note</label>
                            <label class="checkbox-inline"><input type="checkbox" value="PW">Premium Working</label>
                            <label class="checkbox-inline"><input type="checkbox" value="RT">Renewal Notice</label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-subject" class="col-md-2 label-align">Subject</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-subject" name="subject"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-2 label-align">Email Template</label>

                        <div class="col-md-10">
                            <textarea class="form-control" rows="10" cols="20" id="email-template" name="message"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="sendEmailForm"
                        type="button" class="btn btn-success">Send</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="benefitLimitModal" tabindex="-1" role="dialog"
     aria-labelledby="benefitLimitModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="benefitLimitModalLabel">
                    Edit Benefit Limit
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="benefit-limit-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="limit-benefit-id" name="sectId">

                    <div class="item form-group cover-limit-det" >
                        <label for="unit-id" class="col-md-3 label-align">Limit</label>

                        <div class="col-md-8">
                            <input type="hidden" id="cover-limit-det-pk" name="limit">
                            <input type="hidden" id="cover-limit-value">
                            <div id="cover-limit-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/selCoverLimits"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group fund-limit-value">
                        <label for="unit-id" class="col-md-3 label-align">Limit</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control formatcurrency" id="fund-limit-value"
                                   name="fundLimit"  required>
                        </div>
                    </div>
                    <div class="item form-group applicablelevel" >
                        <label for="unit-id" class="col-md-3 label-align">Applicable At</label>
                        <div class="col-md-8">
                            <select class="form-control" id="benfit-applicable-at" name="applicableAt" required>
                                <option value="">Select Applicable Level</option>
                                <option value="F">Per Family</option>
                                <option value="M">Per Member</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group wait-period">
                        <label for="unit-id" class="col-md-3 label-align">Waiting Period</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control" id="waiting-period"
                                   name="waitPeriod"  required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="updateBenefitLimit"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>





<div class="modal fade" id="binderRulesModal" tabindex="-1" role="dialog"
     aria-labelledby="binderRulesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="binderRulesModalLabel">
                    Add New Rules
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="binderRulesTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="10%">Rule Description</th>
                            <th width="3%">Rule Value</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="category-rule-form">
                    <input type="hidden" id="rule-category-code" name="catCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveNewRulesBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="memberFamilyModal" tabindex="-1" role="dialog"
     aria-labelledby="memberFamilyModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="memberFamilyModalLabel">
                    Family Tree
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="x_title">
                    <h4>Principal Details</h4>

                    <ul class="nav navbar-right panel_toolbox">
                        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                        </li>
                    </ul>
                </div>
                    <table id="principalList" class="table" style="width:100%">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Card No</th>
                            <th>Type</th>
                            <th>Gender</th>
                            <th>Age</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                <div class="x_title">
                    <h4>Dependent Details</h4>

                    <ul class="nav navbar-right panel_toolbox">
                        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                        </li>
                    </ul>
                </div>
                <table id="dependentList" class="table" style="width:100%">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Card No</th>
                        <th>Type</th>
                        <th>Gender</th>
                        <th>Age</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>



            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    OK
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="binderCoversModal" tabindex="-1" role="dialog"
     aria-labelledby="binderCoversModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="binderCoversModalLabel">
                    Add New Benefits
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="binderCoversTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="3%">Benefit Id</th>
                            <th width="10%">Benefit</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="category-benefit-form">
                    <input type="hidden" id="category-ben-code" name="catCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveNewBenefitBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="taxesModal" tabindex="-1" role="dialog"
     aria-labelledby="taxesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                 <h4 class="modal-title" id="taxesModalLabel">
                    Add New Taxes
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="taxesTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="10%">Trans Code</th>
                            <th width="3%">Rate Type</th>
                            <th width="5%">Tax Level</th>
                            <th width="3%">Rate</th>
                            <th width="3%">Div Factor</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="tax-form">
                    <input type="hidden" id="tax-policy-code" name="taxPolicyCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveNewTaxBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="catProvidersModal" tabindex="-1" role="dialog"
     aria-labelledby="catProvidersModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="catProvidersModalLabel">
                    Add New Provider Panel
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="providerPanelTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="10%">Name</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="category-provider-form">
                    <input type="hidden" id="category-provider-code" name="catCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveNewProvidersBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="newclausesModal" tabindex="-1" role="dialog"
     aria-labelledby="newclausesModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="newclausesModalLabel">Add A Clause</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div style="height: 300px !important; overflow: scroll;">
                    <table id="new_clause_tbl" class="table" style="width:100%">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th>Clause Header</th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <form id="new-clause-form">

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savenewClause"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="clauseModal" tabindex="-1" role="dialog"
     aria-labelledby="clauseModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clauseModalLabel">
                    Edit A Clause
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="pol-clause-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="pol-clause-code" name="polClauseId">
                    <input type="hidden" class="form-control" id="sub-clause-code" name="clause">
                    <input type="hidden" class="form-control" id="pol-new-clause" name="newClause">
                    <input type="hidden" class="form-control" id="clause-pol-code" name="policy">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Clause ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sub-clau-id"
                                   name="clauShtDesc"  readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="sub-clause-name" class="col-md-3 label-align">Clause Heading</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sub-clause-name" name="clauHeading"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Editable</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="editable" id="chk-cl-editable" onclick="return false;">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="sub-cla-wording" class="col-md-3 label-align">Clause Wording</label>

                        <div class="col-md-8">
                            <textarea rows="7" cols="20" class="form-control" id="sub-cla-wording" name="clauWording"></textarea>
                        </div>
                    </div>




                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savepolClauseBtn"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="assignModal" tabindex="-1" role="dialog"
     aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="assignModalLabel">Assign Ticket to User</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="assign-user-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Select User</label>

                        <div class="col-md-8">
                            <input type="hidden" id="assign-user-name" name="username"/>
                            <div id="user-assignee-div" class="form-control"
                                 select2-url="<c:url value="/protected/organization/managers"/>" >

                            </div>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveTicket"
                        type="button" class="btn btn-success">
                    OK
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="viewDocModal" tabindex="-1" role="dialog"
     aria-labelledby="viewDocModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
           <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="viewDocModalLabel">View Member Documents</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>

                <div class="modal-body">
                    <button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
                    <input type="hidden" class="form-control" id="risk-code-pk">
                    <table id="risk_docs_tbl" class="table" style="width:100%">
                        <thead>
                        <tr>
                            <th>Document ID</th>
                            <th>Document Desc</th>
                            <th>File Name</th>
                            <th>File Verifier</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close</button>
                </div>
            </div>
    </div>
</div>



<div class="modal fade" id="riskReqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="riskReqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="riskReqDocsModalLabel">Select Required Docs</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Document Name</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="doc-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button  id="searchDocuments"
                                     type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="risksReqDocsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Document Id</th>
                            <th width="12%">Document Name</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="risk-docs-form">
                    <input type="hidden" id="req-risk-code" name="subCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRiskDocsBtn"
                        type="button" class="btn btn-success">Save</button>
                <<button type="button" class="btn btn-default" data-dismiss="modal">
                Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="riskdocModal" tabindex="-1" role="dialog"
     aria-labelledby="riskdocModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="risk-doc-form" class="form-horizontal" enctype="multipart/form-data">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="riskdocModalLabel">Upload Risk Document</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="risk-doc-id" name="docId"/>
                    <div class="item form-group">
                        <label for="risk-doc-name" class="col-md-3 label-align">Document Type</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="risk-doc-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="risk-upload-name" class="col-md-3 label-align">Uploaded File Name</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="risk-upload-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Document</label>

                        <div class="col-md-8">
                            <div class="input-group col-xs-12">
                                <input name="file" type="file" id="avatar" required>
                            </div>
                        </div>
                    </div>



                </div>
                <div class="modal-footer">
                    <input  value="Upload"
                            type="submit" class="btn btn-success"
                            onclick="this.disabled=true;this.value='Sending, please wait...';this.form.submit();">

                    </input>
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close</button>
                </div>
            </div>
        </form>
    </div>
</div>