<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/uwtrans/endorsebulk.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Endorsements Transactions</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <c:if test="${error != null}">
            <div class="alert alert-error alert-dismissible">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    ${error}
            </div>
        </c:if>


        <form:form class="form-horizontal" action="reviseTransaction"
                   modelAttribute="revisionForm">
            <spring:hasBindErrors name="revisionForm">
                <div class="alert alert-error alert-dismissible">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    <form:errors path="effectiveDate"/>
                </div>
            </spring:hasBindErrors>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Type of Revision</label>

                    <div class="col-md-6 col-xs-12">
                        <form:select cssClass="form-control" id="rev-type"
                                     path="revisionType" required="required">
                            <form:option value="">Select Revision Type</form:option>
                            <form:option value="EN">Revision of Terms</form:option>
                            <form:option value="EX">Extension of Cover</form:option>
                            <form:option value="CN">Cancellation</form:option>
                            <%--							<form:option value="EN">Declaration</form:option>--%>
                            <form:option value="RS">Sections Reinstatement</form:option>
                        </form:select>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>


            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Select a Policy</label>
                    <div class="col-md-6 col-xs-12">
                        <div class='input-group'>
                            <form:hidden id="rev-pol-id" path="policyId"/>
                            <form:input cssClass="form-control float-right" id="pol-number"
                                        path="policyNumber" required="required" readonly="true"/>
                            <div class="input-group-addon">
								<span class="glyphicon glyphicon-chevron-down"
                                      id="btn-show-search-pol" style="cursor: pointer"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>


            </div>

            <div id="rein-amt" class="item form-group form-required" hidden>
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-6 col-xs-12" for="rein">Reinstatement Amount: </label>
                    <div class="col-md-6 col-xs-12">
                        <form:input type="number" class="form-control" id="rein"
                                    placeholder="Enter Reinstatement Amount" path="amount"></form:input>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>
            </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Effective
                        Date</label>

                    <div class="col-md-6 col-xs-12">
                        <div class='input-group date datepicker-input' id="eff-date-from">
                            <form:input cssClass="form-control float-right"
                                        path="effectiveDate" id="eff-date" required="required"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>


            </div>
            <div class="item form-group form-required" id="eff-date-to" style="display: none">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Date
                        To</label>

                    <div class="col-md-6 col-xs-12">
                        <div class='input-group date datepicker-input'>
                            <form:input cssClass="form-control float-right"
                                        path="effToDate" id="eff-to-date"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>


            </div>

            <div class="box-footer">
                <input type="submit" class="btn btn-info float-left"
                       style="margin-right: 10px;" value="Next">
            </div>

        </form:form>
        <div class="spacer"></div>
        <div id="existing-trans" style="display: none">
            <h3>Existing unfinished transactions</h3>
            <hr>
            <div class="table-responsive">
                <table id="poltrans" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th width="10%">Policy. No.</th>
                        <th width="10%">Trans. Date</th>
                        <th width="10%">WEF</th>
                        <th width="10%">WET</th>
                        <th width="10%">Initiated By</th>
                        <th width="10%">Status</th>
                        <th width="2%"></th>
                        <th width="2%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>


<jsp:include page="modals/endorsemodals.jsp"></jsp:include>