<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/medicaltrans.js"/>"></script>
<div class="x_content">
    <div class="x_panel">
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Medical Policy Details</h2>
            <div class="clearfix"></div>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
            </ul>
        </div>
        <div class="x_content">
            <form:form id="policy-form" class="form-horizontal form-label-left" modelAttribute="policyTrans" method="post" action="createPolicy">
                <c:if test="${error != null}">
                    <div class="alert alert-error alert-dismissible">
                        <a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
                            ${error}
                    </div>
                </c:if>

                <hr>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-buss-type" class="label-align col-md-5">Period</label>
                        <div class="col-md-7 col-xs-12">
                            <form:select class="form-control" id="pol-buss-type" path="businessType">
                                <form:option value="">Select Business Type</form:option>
                                <form:option value="N">Annual</form:option>
                                <form:option value="S">Short Period</form:option>
                            </form:select>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-bin-type" class="label-align col-md-5">Scheme Type<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <form:select class="form-control" id="pol-bin-type" path="binder.binType">
                                <form:option value="">Select Scheme Type</form:option>
                                <form:option value="B">Insured Binder</form:option>
                                <form:option value="BN">Insured Non-Binder</form:option>
                                <form:option value="S">Self Fund</form:option>
                            </form:select>
                        </div>
                    </div>
<%--                    <div class="col-md-6 col-xs-12  bind-insurance">--%>
<%--                        <div class="item form-group form-required ">--%>
<%--                            <label for="prd-code" class="label-align col-md-5">--%>
<%--                                Product<span class="required">*</span></label>--%>
<%--                            <div class="col-md-7 col-xs-12">--%>
<%--                                <input type="hidden" id="prd-id" rv-value="product.proCode" name="product"/>--%>
<%--                                <input type="hidden" id="pr-name">--%>
<%--                                <div id="prd-code" class="form-control"--%>
<%--                                     select2-url="<c:url value="/protected/setups/binders/selgenproducts"/>">--%>

<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="client-frm" class="label-align col-md-5">Scheme/Client<span class="required">*</span></label>
                        <div class="col-md-6 col-xs-12">
                            <div class="edit-client">
                                <form:hidden id="client-id" path="client.tenId"/>
                                <input type="hidden" id="client-f-name"/>
                                <input type="hidden" id="client-other-name"/>
                                <div id="client-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >
                                </div>
                            </div>
                            <div id="display-client">
                                <p class="form-control-static" id="client-info"></p>
                            </div>
                        </div>
                        <div class="col-md-1 col-xs-12 edit-client">
                            <a href="<c:url value="/protected/clients/setups/clientsform?type=med"/>" id="btn-add-client" class="btn-sm btn-success btn-info">New</a>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="binder-frm" class="label-align col-md-5">Contract<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <div id="edit-binder">
                                <form:hidden id="binder-id" path="binder.binId"/>
                                <form:hidden id="product-id" path="product.proCode"/>
                                <form:hidden id="pol-agent-id" path="agent.acctId"/>
                                <form:hidden id="bind-name" path="binder.binName"/>
                                <form:hidden path="product.proDesc" id="bind-pro-name"/>
                                <form:hidden path="binder.account.name" id="bind-ins-name"/>
                                <div id="binder-frm" class="form-control"
                                     select2-url="<c:url value="/protected/medical/policies/uwBinders"/>" >
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
<%--                <div class="item form-group form-required">--%>
<%--                    <div class="col-md-6 col-xs-12">--%>
<%--                        <label for="pol-ins-comp" class="label-align col-md-5">Intermediary</label>--%>
<%--                        <div class="col-md-7 col-xs-12">--%>
<%--                            <p class="form-control-static" id="pol-ins-comp"></p>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                    <div class="col-md-6 col-xs-12">--%>
<%--                        <label for="pol-prod-name" class="label-align col-md-5">Product</label>--%>
<%--                        <div class="col-md-7 col-xs-12">--%>
<%--                            <p class="form-control-static" id="pol-prod-name"></p>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
                <div class="item form-group">
                    <div class="col-md-6 col-xs-12 form-required">
                        <label for="wef-date" class="col-md-5 label-align">Date From</label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <form:input class="form-control float-right" path="wefDate" id="from-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="cover-to-date" class="label-align col-md-5">Date To</label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="cover-to-date">
                                <form:input type="text" class="form-control float-right" path="wetDate" id="wet-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-interface-type" class="label-align col-md-5">Interface Type<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <form:select class="form-control" id="pol-interface-type" path="interfaceType">
                                <form:option value="">Select Interface Type</form:option>
                                <form:option value="A">Accrual</form:option>
                                <form:option value="C">Cash</form:option>
                            </form:select>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pm-mode-frm" class="label-align col-md-5">Payment Mode<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <div id="edit-payment-mode">
                                <form:hidden id="pm-id" path="paymentMode.pmId"/>
                                <form:hidden id="pm-name" path="paymentMode.pmDesc"/>
                                <div id="pm-mode-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-frequency" class="label-align col-md-5">Payment Frequency<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <form:select class="form-control" id="pol-frequency" path="frequency">
                                <form:option value="">Select Payment Frequency</form:option>
                                <form:option value="M">Monthly</form:option>
                                <form:option value="Q">Quarterly</form:option>
                                <form:option value="S">Semi-Annually</form:option>
                                <form:option value="A">Annually</form:option>
                            </form:select>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="brn-frm" class="label-align col-md-5">Branch<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <div id="edit-branch">
                                <form:hidden id="brn-id" path="branch.obId"/>
                                <form:hidden id="brn-name" path="branch.obName"/>
                                <div id="brn-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwbranches"/>" >
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="client-pol-no" class="label-align col-md-5">Insurer Policy Number<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <form:input path="clientPolNo" id="client-pol-no" class="form-control" placeholder="Client Policy Number"/>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="medicalCoverTyp-info" class="label-align col-md-5">Type</label>
                        <div class="col-md-7 col-xs-12">
                            <form:hidden id="pol-medicalCoverType" path="medicalCoverType"/>
                            <div id="display-medicalCoverTyp">
                                <p class="form-control-static" id="medicalCoverTyp-info"></p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="curr-frm" class="label-align col-md-5">Currency<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <div id="edit-currency">
                                <form:hidden id="cur-id" path="transCurrency.curCode"/>
                                <form:hidden id="cur-name" path="transCurrency.curName"/>
                                <div id="curr-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="comm-rate" class="label-align col-md-5">Commission Rate<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <form:input type="number" path="commRate" id="comm-rate" class="form-control" placeholder="Comm Rate" required="required"/>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="item form-group policy-card-type">
                        <div class="col-md-6 col-xs-12">
                            <label for="card-frm" class="label-align col-md-5">Card Type</label>
                            <div class="col-md-7 col-xs-12">
                                <form:hidden id="card-id" path="binCardType.id"/>
                                <input type="hidden" id="card-name"/>
                                <div id="card-frm" class="form-control"
                                     select2-url="<c:url value="/protected/medical/policies/binderCardTypes"/>" >
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <hr>
                <div class="item form-group form-required">
                    <input type="submit" class="btn btn-info float-right" value="Save"/>
                </div>
            </form:form>
        </div>
    </div>
</div>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>