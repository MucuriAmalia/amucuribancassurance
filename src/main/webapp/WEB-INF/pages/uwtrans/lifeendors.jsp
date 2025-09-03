<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/uwtrans/endorse.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
    .auto-selected {
        background-color: rgba(65, 66, 65, 0.13) !important;
        border: 2px solid rgba(94, 96, 94, 0.06) !important;
        cursor: not-allowed !important;
    }
</style>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Life Endorsements Transactions</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <c:if test="${error != null}">
            <div class="alert alert-error alert-dismissible">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    ${error}
            </div>
        </c:if>


        <form:form class="form-horizontal" action="reviseLifeTransaction"
                   modelAttribute="revisionForm">
            <spring:hasBindErrors name="revisionForm">
                <div class="alert alert-error alert-dismissible">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    <form:errors path="effectiveDate"/>
                </div>
            </spring:hasBindErrors>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Type of
                        Revision</label>

                    <div class="col-md-6 col-xs-12" >
                        <form:select cssClass="form-control" id="rev-type"
                                     path="revisionType" required="required">
                            <form:option value="">Select Endorsement Type</form:option>
                            <form:option value="EN">Policy Revisions(Non-Financial)</form:option>
                            <form:option value="CN">Cancellation</form:option>
                        </form:select>
                        <!-- Conditional dropdown for Terms or Cancellation Reason -->
                        <select class="form-control" id="conditional-type" name="remarks" style="display:none; margin-top:10px;">

                        </select>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>


            </div>
            <div class="item form-group form-required cancel-Type">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Cancellation Type</label>

                    <div class="col-md-6 col-xs-12">
                        <div class="radio">
                            <label>
                                    <form:radiobutton path="cancellationType" id="pCancel" value="PR"/> Prorata
                        </div>
                        <div class="radio">
                            <label><form:radiobutton path="cancellationType" id="sCancel" value="SP"/>Short Period
                        </div>
                        <div class="radio">
                            <label><form:radiobutton path="cancellationType" id="nCancel" value="NR"/>Cancellation with no refund
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12"></div>


            </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-6 label-align">Select a
                        Policy</label>

                    <div class="col-md-6 col-xs-12">
                        <div class='input-group'>
                            <form:hidden id="rev-pol-id" path="policyId" />
                            <form:input cssClass="form-control float-right" id="pol-number"
                                        path="policyNumber" required="required" readonly="true"/>
                            <div class="input-group-addon">
								<span class="glyphicon glyphicon-chevron-down"
                                      id="btn-show-search-life" style="cursor: pointer"></span>
                            </div>
                        </div>
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
                    <th width="10%">Proposal. No.</th>
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