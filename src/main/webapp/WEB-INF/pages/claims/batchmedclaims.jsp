<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/batchclaims.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Batch Claims</h2>
        <div class="clearfix"></div>
    </div>
    <form id="search-form" class="form-horizontal">
    <div class="item form-group form-required">
        <div class="col-md-6 col-xs-12">
            <label for="provider-frm" class="label-align col-md-3">
                Service Provider</label>
            <div class="col-md-9 col-xs-12">
                <input type="hidden" name="providerContracts" id="provider-id">
                <input type="hidden" id="provider-contract-no">
                <div id="provider-frm" class="form-control"
                     select2-url="<c:url value="/protected/medical/claims/providercontracts"/>" >

                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">

        </div>

    </div>
        </form>
    <div class="table-responsive">
        <table id="med_clm_enquiry_tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th><input name="select_all" value="1" id="example-select-all" type="checkbox" /></th>
            <th>Claim No</th>
            <th>Claim Revision No</th>
            <th>Event Date</th>
            <th>Member Name</th>
            <th>Notification Date</th>
            <th>Booked By</th>
            <th>Approved Amount</th>
        </tr>
        </thead>
    </table>
        </div>
    <form id="batch-process-form">

    </form>
    <input type="button" class="btn btn-info float-right"
           value="Process" id="btn-process">
</div>

