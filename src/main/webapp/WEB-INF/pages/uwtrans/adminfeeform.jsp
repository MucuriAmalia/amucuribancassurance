<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/adminfeeuw.js"/>"></script>
<script type="text/javascript">
    var adminFeeId = ${adminFeeCode};
</script>

<div class="x_panel">
    <a     class="btn btn-info float-right"
            href="<c:url value='/protected/trans/adminfee/rpt_admin_fee'/> "
            target="_blank">Admin Fee Report</a></li>
    <input type="button" class="btn btn-info float-right"
           value="Authorise" id="btn-auth-admin">

    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Admin Fee Transactions</h2>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>

    <div class="x_content">
        <form id="admin-fee-form" class="form-horizontal form-label-left">
            <input type="hidden" id="med-pol-bin-code">
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-client-id" class="label-align col-md-5">
                        Client</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-client-no"> </p>
                        <input type="hidden" id="txt-client-id">
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="txt-ref-no" class="label-align col-md-5">
                        Ref No</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-ref-no"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-branch" class="label-align col-md-5">
                        Branch</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-branch"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="txt-currency" class="label-align col-md-5">
                       Currency</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-currency"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-trans-date" class="label-align col-md-5">
                        Trans Date</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-trans-date"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="txt-gross-amt" class="label-align col-md-5">
                        Gross Amount</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-gross-amt"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-vat-amt" class="label-align col-md-5">
                       VAT Amount</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-vat-amt"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="txt-excise-duty" class="label-align col-md-5">
                        Excise Duty</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-excise-duty"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-vat-excise-duty" class="label-align col-md-5">
                        VAT on Excise Duty</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-vat-excise-duty"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="txt-net-amt" class="label-align col-md-5">
                        Net Amount</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-net-amt"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-authorised" class="label-align col-md-5">
                        Authorised</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-authorised"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="txt-authorised-by" class="label-align col-md-5">
                        Authorised By</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-authorised-by"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="txt-remarks" class="label-align col-md-5">
                        Remarks</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="txt-remarks"> </p>
                    </div>
                </div>
            </div>
            </form>
    </div>
    <div class="x_title">
        <h4>Admin Fee Policies</h4>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>

    <div class="x_content">
        <button class="btn btn-success btn btn-info float-left" id="btn-add-policies">New</button>
        <div class="table-responsive">
        <table id="admin_fee_policies" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Policy No</th>
                <th>Endorsement Number</th>
                <th>Product</th>
                <th>Cover From</th>
                <th>Cover To</th>
                <th>Dr/Cr. Number</th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
            </div>
        </div>
</div>



<div class="modal fade" id="adminFeePolModal" tabindex="-1" role="dialog"
     aria-labelledby="adminFeePolModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="adminFeePolModalLabel">
                    Add New Policies
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="adminPoliciesTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="5%">Policy No</th>
                            <th width="5%">Endors. No</th>
                            <th width="5%">Dr. No</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="admin-pol-form">
                    <input type="hidden" id="admin-fee-code" name="adminFeeId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveAdminFeePolBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

