<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/medicalsetups/sproviders.js"/>"></script>
<script>
    var requestContextPath = '${pageContext.request.contextPath}';
</script>
<div class="x_panel">
    <div class="x_title">
        <h2>Service Provider Types Definition</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form class="form-horizontal">
            <div class="item form-group">
                <label for="brn-id" class="col-md-3 label-align">
                    Service Provider Types</label>

                <div class="col-md-4">

                    <div id="sprovder-types" class="form-control"
                         select2-url="<c:url value="/protected/medical/setups/providertypessel"/>">

                    </div>
                    <input type="hidden" id="ptypes-pk">

                </div>
                <div class="col-md-4">
                    <button type="button" class="btn btn-info" id="btn-add-prov-types">New</button>
                    <button type="button" class="btn btn-info" id="btn-edit-prov-types">Edit</button>
                </div>
            </div>

        </form>

    </div>
</div>
<div class="x_panel">
    <div class="x_title">
        <h2>Service Providers List</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <input type="hidden" id="provd-types-pk">
        <button type="button" class="btn btn-info float-right"
                id="btn-add-providers">New</button>
        <div class="cutom-container">
        <table id="providerTbl" class="table" style="width:100%">
            <thead>
            <tr>

                <th>Name</th>
                <th>Contact Person</th>
                <th>Pin No</th>
                <th>Tel No</th>
                <th>Status?</th>
                <th>Bank Branch</th>
                <th>Account No</th>
                <th>Payment Mode</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
            </div>
        <div class="x_title">
            <h4>Provider Services</h4>
            <input type="hidden" id="service-prvdr-code">
            <button type="button" class="btn btn-info" id="btn-add-prvdr-srvc">New</button>
            <div class="cutom-container">
                <table id="prvdrsrvcList" class="table" style="width:100%">
                    <thead>
                    <tr>

                        <th>Service ID</th>
                        <th>Description</th>
                        <th>CPT Code</th>
                        <th>Wef</th>
                        <th>Wet</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
        <div class="x_title">
            <h4>Service Activities</h4>
            <input type="hidden" id="service-code">
            <button type="button" class="btn btn-info" id="btn-add-srvc-act">New</button>
            <div class="cutom-container">
                <table id="srvcActivityList" class="table" style="width:100%">
                    <thead>
                    <tr>

                        <th>Activity ID</th>
                        <th>Description</th>
                        <th>Affected Benefit</th>
                        <th>Charge</th>
                        <th>Wef</th>
                        <th>Wet</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>


<jsp:include page="providermodals/modals.jsp"></jsp:include>