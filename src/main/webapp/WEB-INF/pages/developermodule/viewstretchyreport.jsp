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
</div>
<div class="x_panel">
    <div class="form-group">
        <input action="action" type="button" onclick="history.go(-1);" class="btn btn-primary pull-right" value="Back"
               id="renbtn" style="display:none"/>
    </div>
</div>
    <form id="stretchy-report-form" class="form-horizontal form-label-left">

        <input type="hidden" id="report-id" name="strId"/>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group form-required">
                    <label for="div-endos-no" class="label-align col-md-5">
                        Report Name</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="div-endos-no" name="polRevNo"/>
                        <p class="form-control-static"name="reportName" id="report-name"></p>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group form-required">
                    <label for="report-type" class="label-align col-md-5">
                        Report Type</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="report-type" name="reportType"></p>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group form-required">
                    <label for="report-category" class="label-align col-md-5">
                        Report Category</label>
                    <div class="col-md-7 col-xs-12">

                        <p class="form-control-static" class="form-control" id="report-category" name="reportCategory"></p>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group form-required">
                    <label for="report-sql" class="label-align col-md-5">
                        Report Sql</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" name="reportSql" id="report-sql"></p>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group form-required">
                    <label for="description" class="label-align col-md-5">
                        Description</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" name="description" id="description"></p>
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
                    <th>Parameter Name</th>
                    <th>Parameter Type</th>
                    <th>Display Type</th>
                    <th>Parameter SQL</th>
                    <th width="5%"></th>
                    <th width="5%"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<script type="text/javascript">
    console.log("report Id: "+SreportId);

    $(function () {
        $(document).ready(function () {
            STRETCHY_REPORTS_UTILITIES.getStretchyReportDetails();
        });
    });
</script>
