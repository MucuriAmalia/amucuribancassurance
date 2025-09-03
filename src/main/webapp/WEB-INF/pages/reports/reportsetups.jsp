<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/reports/setups.js"/>"></script>
<script>
    var requestContextPath = '${pageContext.request.contextPath}';
</script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Report Headers</h2>
        <div class="clearfix"></div>
    </div>

    <button type="button" class="btn btn-success btn btn-info float-right" id="addRptHeader">New</button>
    <table id="rptHeadersTbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>
            <th width="30%">Module Name</th>
            <th width="30%">Header Name</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
    <div class="x_title">
        <h4>System Reports</h4>
    </div>

    <button type="button" class="btn btn-success btn btn-info float-right" id="btn-add-rpt-def">New</button>
    <input type="hidden" id="rpt-header-pk">
    <table id="rptDefTbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>
            <th width="30%">Report Name</th>
            <th width="30%">Report Description</th>
            <th width="30%">Template Name</th>
            <th width="30%">Active</th>
            <th width="30%">Password Protect?</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
    <div class="x_title">
        <h4>Report Parameters</h4>
    </div>

    <button type="button" class="btn btn-success btn btn-info float-right" id="btn-add-rpt-param">New</button>
     <input type="hidden" id="rpt-definition-pk">
    <table id="rptParamsTbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>

            <th width="20%">Display Name</th>
            <th width="20%">Parameter Name</th>
            <th width="20%">Parameter Type</th>
            <th width="10%">Lov Name</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>

<jsp:include page="modals/modals.jsp"></jsp:include>