<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fom" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/claims.js"/>"></script>


<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Enter Claim Payment Details</h2>
        <div class="clearfix"></div>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>

    <div class="x_content">
        <c:if test="${error != null}">
            <div class="alert alert-error alert-dismissible">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    ${error}
            </div>
        </c:if>
        <form:form class="form-horizontal form-label-left" method="post" action="newclaim" modelAttribute="claimForm">

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="loss-date" class="label-align col-md-5">
                        Loss Date<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="los-date">
                            <form:input path="lossDate" class="form-control float-right"  id="loss-date" required="true"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="not-date" class="label-align col-md-5">
                        Notification Date<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="not-date">
                            <form:input path="notificationDate" class="form-control float-right"  id="notification-date" required="true"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="risk-frm" class="label-align col-md-5">
                        Select A Risk<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:hidden path="riskId"  id="risk-id"/>
                        <form:hidden path="riskDesc"  id="risk-desc"/>
                        <form:hidden path="riskShtDesc"  id="risk-sht-desc"/>
                        <input type="hidden" id="insured-name">
                        <input type="hidden" id="binder-code">
                        <div id="risk-frm" class="form-control"
                             select2-url="<c:url value="/protected/claims/selLossRisks"/>" >

                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="loss-desc" class="label-align col-md-5">
                        Loss Description<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                    <form:textarea class="form-control" rows="2" id="loss-desc" path="lossDesc"></form:textarea>
                        </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-ins-balance" class="label-align col-md-5">
                        Client Balance</label>
                    <div class="col-md-7 col-xs-12">

                        <p class="form-control-static" id="pol-ins-balance"></p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="net-review-date" class="label-align col-md-5">
                        Next Review Date<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="next-rev-date">
                            <form:input  class="form-control float-right" path="nextReviewDate"
                                         id="net-review-date" required="true" />
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>


            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="chk-active" class="col-md-5 label-align">Liability Admission</label>
                    <div class="col-md-7 checkbox">
                        <label>
                            <form:checkbox path="liabilityAdmission" id="chk-active"/>
                        </label>

                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="party-to-blame" class="col-md-5 label-align">Party to Blame</label>

                    <div class="col-md-7">
                        <form:select class="form-control" id="party-to-blame" path="partyToBlame">
                            <form:option value="">Select Party to Blame</form:option>
                            <form:option value="N">Non</form:option>
                            <form:option value="I">Insured</form:option>
                            <form:option value="T">Third party</form:option>
                        </form:select>
                    </div>

                </div>
                <div>
                    <form:hidden id="expire-section-id" path="expireSectionId"/>
                    <form:hidden id="expire-section" path="expireSection"/>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="activity-code" class="label-align col-md-5">
                        Select Causation<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:hidden  id="activity-code" path="activityId"/>
                        <form:hidden id="activity-desc" path="activityDesc"/>
                        <div id="clm-activity" class="form-control"
                             select2-url="<c:url value="/protected/claims/selclmActivity"/>" >

                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">

                </div>


            </div>
            <div class="x_title">
                <h4>Payment Details</h4>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pay-type" class="label-align col-md-5">
                        Payment Type<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:select class="form-control" id="pay-type" path="paymentType">
                            <form:option value="">Select Payment</form:option>
                            <form:option value="CP">Claimant Payment</form:option>
                            <form:option value="SP">Service Provider Payment</form:option>
                        </form:select>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <div class="item form-group">
                        <label for="cur-def" class="col-md-5 label-align">Currency</label>

                        <div class="col-md-7">
                            <form:hidden path="currencyId" id="cur-id"/>
                            <div id="cur-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/activeCurrencies"/>" >

                            </div>
                        </div>
                    </div>
                </div>

            </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12">
                <label for="self-as-clmnt" class="col-md-5 label-align">Pay Self as Claimant</label>
                <div class="col-md-7 checkbox">
                    <label>
                        <form:checkbox path="selfAsClaimant" id="self-as-clmnt"/>
                    </label>
                </div>
            </div>
            <div class="col-md-6 col-xs-12 tpd" style="display: none">
                <label for="brn-id" class="col-md-5 label-align">Third Party Claimant</label>
                <div class="col-md-7">
                    <div class="col-md-11">
                        <form:hidden path="claimantCode" id="clmt-code"/>
                        <div id="tpd-clmant-def" class="form-control"
                             select2-url="<c:url value="/protected/claims/selClaimants"/>" >

                        </div>
                    </div>
                    <div class="col-md-1">
                        <input type="button" class="btn-sm btn-success btn-info" id="btn-add-claimant" value="New">
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12 spr" style="display: none">
                <label for="brn-id" class="col-md-5 label-align">Service Provider</label>
                <div class="col-md-7">
                    <fom:hidden path="sprCode" id="spr-code"/>
                    <div id="spr-clmant-def" class="form-control"
                         select2-url="<c:url value="/protected/claims/selproviders"/>" >

                    </div>
                </div>
            </div>
        </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pm-mode-frm" class="label-align col-md-5">
                        Payment Mode<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:hidden path="paymentModeId" id="pm-id"/>
                        <div id="pm-mode-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <div class="item form-group">
                        <label for="pay-comments" class="col-md-5 label-align">Comments</label>

                        <div class="col-md-7">
                            <form:textarea cssClass="form-control" rows="1" id="pay-comments" path="comments"></form:textarea>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="invoiceno" class="label-align col-md-5">Invoice
                            No</label>
                        <div class="col-md-7 col-xs-12">
                            <form:input path="invoiceNo"  id="invoiceno" cssClass="form-control"
                                   placeholder="Enter Invoice No"/>
                        </div>


                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="invoicedate" class="label-align col-md-5">Invoice
                            Date</label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="invoicedate">
                                <form:input  class="form-control float-right" path="invoiceDate"
                                             id="invoice-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="invoiceno" class="label-align col-md-5">Invoice
                        Amount</label>
                    <div class="col-md-7 col-xs-12">
                        <form:input path="invoiceAmount" id="invoiceamount" cssClass="form-control"
                                    placeholder="Enter Invoice Amount"/>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="bank-branch-lov" class="col-md-5 label-align">Bank Branch</label>
                    <div class="col-md-7">
                        <form:hidden path="bankBranchId" id="bank-branch-id"/>
                        <div id="bank-branch-lov" class="form-control" select2-url="<c:url value="/protected/accounts/selBankBranches"/>" >

                        </div>
                    </div>
                </div>
            </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12">
                <label for="acct-frm" class="label-align col-md-5">
                    Select Bank Account<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="acct-id" name="bankActCode"/>
                    <div id="acct-frm" class="form-control"
                         select2-url="<c:url value="/protected/accounts/payments/selBankAccounts"/>" >

                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="invoiceno" class="label-align col-md-5">Payee Account
                    No</label>
                <div class="col-md-7 col-xs-12">
                    <form:input path="accountNo"  id="accountNo" cssClass="form-control"
                                placeholder="Enter Account No"/>
                </div>
            </div>
        </div>



            <div class="x_title">
                    <h4>Payment Perils</h4>

                </div>

            <input type="button" id="add-peril-btn"
                   class="btn btn-info float-left" style="margin-right: 10px;"
                   value="Add">

            <table id="peril-table-id" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">
                    <th>Peril</th>
                    <th>Payment Amount</th>
                    <th></th>
                </tr>
                </thead>

            </table>
            <input type="submit" value="Next" class="btn btn-success" />
    </form:form>
    </div>
</div>




<div class="modal fade" id="perilTransModal" tabindex="-1" role="dialog"
     aria-labelledby="perilTransModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="perilTransModalLabel">
                    Add A new Peril
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="new-peril-form" class="form-horizontal">
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-4 label-align">Peril</label>

                            <div class="col-md-8">
                                <input type="hidden" id="peril-code"/>
                                <input type="hidden" id="peril-name"/>
                                <div id="peril-def" class="form-control"
                                     select2-url="<c:url value="/protected/claims/selSubclassPerils"/>" >

                                </div>
                            </div>
                    </div>
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-4 label-align">Payment Amount</label>

                            <div class="col-md-8">
                                <input type='text' class="form-control float-right"
                                       id="clm-estimate" />
                            </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="btn-add-peril-selected"
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

<div class="modal fade" id="claimantsModal" tabindex="-1" role="dialog"
     aria-labelledby="claimantsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="claimantsModalLabel">
                    Edit/Add Claimant
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">
                <form id="claimants-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="clmnt-id" name="claimantId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Surname</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-surname"
                                   name="surname"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Other Names</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-othernames"
                                   name="otherNames"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Occupation</label>

                        <div class="col-md-8">
                            <input type="hidden" id="clmt-occupt" name="occupation"/>
                            <input type="hidden" id="clmt-occupt-names"/>
                            <div id="occup-def" class="form-control"
                                 select2-url="<c:url value="/protected/claims/selOccupations"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">ID Number</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-idnumber"
                                   name="idNumber"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                                            <label for="unit-id" class="col-md-3 label-align">Email Address</label>

                                            <div class="col-md-8">
                                                <textarea class="form-control" rows="2" id="clmnt-email" name="email"></textarea>
                                            </div>
                                        </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Address</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="2" id="clmnt-address" name="address"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Mobile Number</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-mobnumber"
                                   name="mobileNo"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClaimantDef"
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
