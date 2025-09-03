<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Create Report parameter</h2>
        <div class="clearfix"></div>
    </div>
    <div class="" role="tabpanel" data-example-id="togglable-tabs">

        <form id="report-form" class="form-horizontal"
              action="<c:url value='/protected/stretchyreports/createStretchyReport'/>" method="post">
            <div class="x_panel">
                <div class="form-group">
                    <c:if test="${empty param.type}">
                        <a href="<c:url value='/protected/stretchyreports/rpts'/>" class="btn btn-primary pull-right">Back</a>
                    </c:if>
                    <button data-loading-text="Saving..." id="saveStretchyParameterBtn"
                            type="button" class="btn btn-success pull-right">
                        Save
                    </button>
                </div>
            </div>
            <div class="x_panel">
                <!-- Start of adding fields -->
                <div class="form-group form-required">
                    <div class="col-md-4 col-sm-12">
                        <div class="form-group row">
                            <label for="report-name">Report Name*</label>
                            <input type="text" name="strRptName" id="report-name" class="form-control"
                                   placeholder="Report Name" required>
                        </div>
                    </div>
                    <div class="col-md-4 col-sm-12">
                        <div class="form-group row">
                            <label>Report Template*</label>
                            <input type="text" name="strTemplateName" id="report-template" class="form-control"
                                   placeholder="Report Template" required>
                        </div>
                    </div>
                    <div class="col-md-4 col-sm-12">
                        <label>Report Category*</label>
                        <select name="reportCategory" class="form-control" id="report-category" name="moduleCode"
                                required>
                            <option value="">Select Category</option>
                            <option value="U">Underwriting</option>
                            <option value="A">Accounts</option>
                            <option value="C">Claims</option>
<%--                            <option value="M">Medical</option>--%>
                        </select>
                    </div>
                </div>
                <div class="form-group form-required">
                    <div class="form-group form-required col-md-12 col-sm-12">
                        <div class="form-group row">
                            <label>Report SQL*</label>
                            <textarea class="form-control" name="reportSql" id="report-sql"
                                      placeholder="Report SQL"></textarea>
                        </div>
                    </div>
                    <div class="col-md-12 col-sm-12">
                        <div class="form-group row">
                            <label>Description</label>
                            <textarea class="resizable_textarea form-control" name="description" id="description"
                                      placeholder="Give a brief description..."></textarea>
                        </div>
                    </div>
                </div>
                <div class="x_panel">
                    <div id="parameter-details-div">
                        <input type="button" id="add-param-btn"
                               class="btn btn-primary pull-left" style="margin-right: 10px;"
                               value="Add">
                    </div>
                    <div class="box-body">
                        <div class="cutom-container">
                            <table id="param-detail-tbl" class="table table-hover table-bordered">
                                <thead>
                                <tr>
                                    <th>Add Parameters</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Dynamic parameter rows will be added here -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<jsp:include page="../modals/editstretchyreportmodal.jsp"></jsp:include>

<script type="text/javascript" src="<c:url value="/js/modules/developermodule/stretchyreport.js"/>"></script>

<script>
    const reportTypeSelect = document.getElementById('report-type');
    const reportSubTypeContainer = document.getElementById('report-sub-type-container');

    reportTypeSelect.addEventListener('change', function () {
        if (this.value === 'C') {
            reportSubTypeContainer.style.display = 'block';
        } else {
            reportSubTypeContainer.style.display = 'none';
        }
    });
</script>

<script type="text/javascript" src="<c:url value="/js/modules/developermodule/stretchyreport.js"/>"></script>

<script type="text/javascript">

    $(function () {
        $(document).ready(function () {
            STRETCHY_REPORTS_UTILITIES.createStretchyReport();
        });
        $(document).ready(function () {
            STRETCHY_REPORTS_UTILITIES.saveReportDefs();
        });
    });
</script>
<script>
    var redirectUrl = "<c:url value='/protected/stretchyreports/rpts'/>";
</script>