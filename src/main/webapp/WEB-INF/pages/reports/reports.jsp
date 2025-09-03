<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/reports/report.js"/>"></script>
<script>
    var requestContextPath = '${pageContext.request.contextPath}';

    function updateFormAction(reportType, reportName) {
        var form = document.getElementById('report-form');
        if (reportType === 'custom') {
            form.action = 'printCustomReport';
        } else { 
            form.action = reportName;
        }
    }
</script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> ${reportName}</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <c:forEach var="module" items="${modules}">
            <c:set var="search" value="'" />
            <div class="col-md-6">
                <ul style="list-style-type: none;" class="reports-links">
                    <sec:authorize access="hasAnyAuthority('${module.permissionsDef.permName}')">
                        <li>
                            <a data-report="${module.rptId}"
                               data-template="${module.rptTemplateName}"
                               data-url="<c:url value='/protected/reports/report/${module.rptTemplateName}'/>"
                               onclick="reportModule.callReportModal(this); updateFormAction('standard', '${module.rptTemplateName}');"
                               href="#">
                                    ${module.rptName}
                            </a>

                        </li>
                    </sec:authorize>
                </ul>
            </div>
        </c:forEach>
    </div>
</div>


<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Customised ${reportName}</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <c:forEach var="stretchyModule" items="${stretchyModules}">
            <c:set var="search" value="'" />
            <div class="col-md-6">
                <ul style="list-style-type: none;" class="reports-links">
                    <li>
                        <a data-report="${stretchyModule.strId}" data-template="${stretchyModule.strTemplateName}"
                           data-url="<c:url value='/protected/reports/report/${stretchyModule.strTemplateName}'/>"
                           onclick="reportModule.callStretchyReportModal(this); updateFormAction('custom');" href="#">
                                ${stretchyModule.strRptName}
                        </a>
                    </li>
                </ul>
            </div>
        </c:forEach>
    </div>
</div>

<div class="modal fade" id="printReportModal" tabindex="-1" role="dialog" aria-labelledby="printReportModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form:form id="report-form" target="_blank" class="form-horizontal" modelAttribute="reportData" action="printReport" method="post">
                <div class="modal-header">
                    <h4 class="modal-title" id="printReportModalLabel">Print Report</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                       <div class="data-inf" style="overflow-y: auto; max-height: 400px; max-width: 100%; -webkit-overflow-scrolling: touch;"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="row">
                        <input type="submit" value="Print" class="btn btn-success">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>
