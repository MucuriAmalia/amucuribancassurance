<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- accounts -->
<div class="x_panel" id="streport_model">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Custom Reports</h2>
        <div class="clearfix"></div>
    </div>
    <a href="<c:url value='/protected/stretchyreports/stretchyReportsForm'/> " class="btn btn-info pull-right">New</a>
    <!-- add new report url-->
    <div class="cutom-container">
        <table id="streportbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Report Id</th>
                <th>Report Name</th>
                <th>Report Category</th>
                <th>Description</th>
                <th width="5%"></th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<jsp:include page="../modals/editstretchyreportmodal.jsp"></jsp:include>
<script type="text/javascript" src="<c:url value="/js/modules/developermodule/stretchyreport.js"/>"></script>

<script type="text/javascript">

    $(document).ready(function () {
        STRETCHY_REPORTS_UTILITIES.createStretchyReportsListing();
        STRETCHY_REPORTS_UTILITIES.saveReportDefs();
    });

</script>

