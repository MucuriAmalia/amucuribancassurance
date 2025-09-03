<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/uwtrans/reinstate.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Reinstate Transactions</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <c:if test="${error != null}">
            <div class="alert alert-error alert-dismissible">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    ${error}
            </div>
        </c:if>


        <form:form class="form-horizontal" action="reinstateTransaction"
                   modelAttribute="reinstateForm">
            <spring:hasBindErrors name="reinstateForm">
                <div class="alert alert-error alert-dismissible">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    <form:errors path="effectiveDate"/>
                </div>
            </spring:hasBindErrors>
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


<jsp:include page="modals/reinstatemodals.jsp"></jsp:include>