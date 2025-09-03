<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/developermodule/stretchyreport.js"/>"></script>
<script type="text/javascript">
    var SreportId = ${reportId};
</script>
<script type="text/javascript">
    var requestContextPath = '${pageContext.request.contextPath}';
</script>
<div class="x_panel">
    <div class="x_title">
        <h2> Report Details</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_panel">
        <div class="form-group">
    <input action="action" type="button" onclick="history.go(-1);" class="btn btn-primary pull-right" value="Back"
           id="renbtn" style="display:none"/>
            <button data-loading-text="Saving..." id="saveStretchyParameterBtn"
                    type="button" class="btn btn-success pull-right">
                Save
            </button>
        </div>
    </div>
    <form id="report-form" class="form-horizontal form-label-left">

        <input type="hidden" id="report-id" name="strId"/>
        <div class="col-md-6 col-xs-12">
            <div class="item form-group form-required">
                <label for="report-name" class="label-align col-md-5">
                    Report Name</label>
                <div class="col-md-7 col-xs-12">
<%--                    <input type="hidden" id="div-endos-no" name="polRevNo"/>--%>
                    <input type="text" name="strRptName" id="report-name" class="form-control"
                           placeholder="Report Name" required>
<%--                    <p class="form-control-static"name="reportName" id="report-name"></p>--%>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <div class="item form-group form-required">
                <label for="report-type" class="label-align col-md-5">
                    Report Type</label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="report-type" name="reportType" name="moduleCode" required>
                        <option value="">Report Type</option>
                        <option value="T">Table</option>
                        <option value="C">Charts</option>
                    </select>
<%--                    <p class="form-control-static" id="report-type" name="reportType"></p>--%>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <div class="item form-group form-required">
                <label for="report-category" class="label-align col-md-5">
                    Report Category</label>
                <div class="col-md-7 col-xs-12">
                    <select name="reportCategory" class="form-control" id="report-category" name="moduleCode" required>
                        <option value="">Report Category</option>
                        <option value="U">Underwriting</option>
                        <option value="A">Accounts</option>
                        <option value="C">Claims</option>
                        <option value="M">Medical</option>
                    </select>


<%--                    <p class="form-control-static" class="form-control" id="report-category"></p>--%>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <div class="item form-group form-required">
                <label for="report-sql" class="label-align col-md-5">
                    Report Sql</label>
                <div class="col-md-7 col-xs-12">
                     <textarea class="form-control" name="reportSql" id="report-sql"
                               placeholder="Report SQL"></textarea>

<%--                    <p class="form-control-static" name="reportSql" id="report-sql"></p>--%>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <div class="item form-group form-required">
                <label for="description" class="label-align col-md-5">
                    Description</label>
                <div class="col-md-7 col-xs-12">
                      <textarea class="resizable_textarea form-control" name="description" id="description"
                                placeholder="Give a brief description..."></textarea>
<%--                    <p class="form-control-static" name="description" id="description"></p>--%>
                </div>
            </div>
        </div>
    </form>
</div>
<div class="x_panel multi-product-uw">
    <div class="x_title">
        <h4>Report Parameters</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">
        <div class="card-box table-responsive">
            <input type="hidden" id="pol-binder-pk">
            <table id="stretchy-parameters-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Parameter ID</th>
                    <th>Parameter Name</th>
                    <th>Parameter Label</th>
                    <th>Format Type</th>
                    <th width="5%"></th>
                    <th width="5%"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div id="edit-parameter-modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="editParameterLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editParameterLabel">Edit Parameter</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div id="edit-param-select" class="form-control" select2-url="stretchyparams"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" id="save-edited-parameter" class="btn btn-primary">Save changes</button>
            </div>
        </div>
    </div>
</div>



<script type="text/javascript">
    console.log("report Id: "+SreportId);

    $(function () {
        $(document).ready(function () {
            STRETCHY_REPORTS_UTILITIES.getEditStretchyReportDetails();
        });
        $(document).ready(function () {
            STRETCHY_REPORTS_UTILITIES.saveReportDefs();
        });
    });
</script>
<script>
    var redirectUrl = "<c:url value='/protected/stretchyreports/rpts'/>";
</script>
