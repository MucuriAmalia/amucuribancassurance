<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/bulktransactions/bulkPolCreation.js"/>"></script>
<script>
    window.ctx = '${pageContext.request.contextPath}';
</script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Bulk Policy Creation</h2>
        <button type="button" class="btn btn-primary float-right"
                id="btn-trans-processing-reports" data-toggle="modal" data-target="#reportsModal">
            <i class="fa fa-print"></i>
        </button>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <button class="btn btn-primary btn btn-primary" id="btn-import-data">Upload Document</button>
        <a href="bulkpoltemplate" class="btn btn-success">
            <i class="fa fa-file-excel-o"></i> Download Template
        </a>
        <button id="download-error-btn" class="btn btn-danger mt-2" style="display: none;">
            Download Error File
        </button>
        <div class="x_title">
            <h2>Unprocessed policy Listing</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="card-box table-responsive">
            <table id="uploadedTransTable" class="table table-striped" style="width: 100%">
                <thead>
                <tr>
                    <th><input type="checkbox" id="selectAll"></th>
                    <th>Cover Type</th>
                    <th>Client First Name</th>
                    <th>Client Other Names</th>
                    <th>Inured First Name</th>
                    <th>Inured Other Names</th>
                    <th>Cover From</th>
                    <th>Sum Insured</th>
                    <th>Uploaded Date</th>
<%--                    <th></th>--%>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div style="margin-top: 20px;">
            <button class="btn btn-danger float-right" id="bulk-delete-trans">Bulk Delete </button>
            <button class="btn btn-primary float-right" id="bulk-process-trans">Bulk Process</button>
        </div>
    </div>
</div>

<jsp:include page="modals/bulkpolicymodal.jsp"></jsp:include>