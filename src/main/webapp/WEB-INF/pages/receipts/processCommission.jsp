<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<%--<script type="text/javascript" src="<c:url value="/js/modules/receipts/processreceipts.js"/>"></script>--%>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/processCommission.js"/>"></script>
<script>
    var requestContextPath = '${pageContext.request.contextPath}';
</script>

<div class="x_panel">
    <div class="x_title">
        <h4>Insurance Receipts Processing</h4>
    </div>
    <form class="form-horizontal">
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="agent-search-number" class="col-md-3 label-align">Intermediary</label>
                <div class="col-md-9 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="agent-search-number" name="accountCode"/>
                    <div id="acc-frm" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="underwriter-def" class="col-md-3 label-align">Commission Receipt</label>
                <div class="col-md-9 col-xs-12">
                    <input type="hidden" id="trans-id"/>
                    <div id="underwriter-def" class="form-control"
                         select2-url="<c:url value="/protected/accounts/insurancereceipts"/>">

                    </div>
                </div>
            </div>
            <div class="col-md-6">

            </div>
        </div>

    </form>
</div>
    <div class="x_panel">
        <div class="x_title">
            <h4>Uploaded Commission Policies</h4>
        </div>
        <form id="upload-form" class="form-horizontal" enctype="multipart/form-data">
            <%--<input type="hidden" class="form-control" id="sub-limit-cov-id" name="coverLimit">--%>
            <input type="hidden" class="form-control" id="insurance-id" name="insurance">
            <input type="hidden" class="form-control" id="receipt-id" name="receipt">
            <div class="col-md-12 col-xs-12 form-required">

                <div class="col-md-5 col-xs-12">
                    <%--<div class="input-group col-xs-12">--%>
                    <%--<input name="file" type="file" id="avatar"> --%>
                    <%--</div>--%>
                    <div style="position:relative;">
                        <a class='btn btn-primary' href='javascript:;'>
                            Choose Commission Upload File...
                            <input type="file" id="avatar"
                                   style='position:absolute;z-index:2;top:0;left:0;filter: alpha(opacity=0);-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";opacity:0;background-color:transparent;color:transparent;'
                                   name="file" size="40" onchange='$("#upload-file-info").html($(this).val());'>
                        </a>
                        &nbsp;
                        <span class='label label-info' id="upload-file-info"></span>
                    </div>
                </div>
                <div class="col-md-3 col-xs-12">
                    <input type="submit" class="btn btn-success float-left" style="margin-right: 10px;"
                           value="Upload Commission">
                </div>
                <div class="btn-group col-md-4 col-xs-12">
                    <a id="downloadFile"  class="btn btn-primary active float-right">
                        <i class="glyphicon glyphicon-download-alt" aria-hidden="true"></i> Download Commission Template
                    </a>
                </div>
            </div>
        </form>
        <div class="table-responsive">
            <table id="commission_tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>

                    <th>Debit No</th>
                    <th>Policy No</th>
                    <th>Insurance Policy</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Select</th>
                </tr>
                </thead>
            </table>
        </div>
        <form class="form-horizontal" id="comm-form">
            <div class="col-md-4 col-xs-12">
                <label for="receipt-amt" class="col-md-6 label-align">Receipt Amount:</label>
                <span class="float-left"> <input id="receipt-amt" type="text" class="form-control total" disabled></span>
                <input type="hidden" id="total-rec" value="0">
            </div>
            <div class="col-md-4 col-xs-12">
                <label for="bal-amt" class="col-md-6 label-align">Running Balance:</label>
                <span class="float-left"> <input id="bal-amt" type="text" class="form-control total" disabled></span>
                <input type="hidden" id="runningl-bal" value-amt="0">
            </div>
            <div class="col-md-4 col-xs-12">
                <label for="total-val" class="col-md-6 label-align">Total Amount:</label>
                <span class="float-left"> <input id="total-amt" type="text" class="form-control total" disabled></span>
                <input type="hidden" id="total-val" value="0">
            </div>
        </form>
        <input type="button" value="Process" class="btn btn-info" id="btn-process">
</div>

<div class="x_panel">
    <div class="x_panel">
        <div class="x_title">
            <h4>Processed Commissions</h4>
        </div>
        <div class="table-responsive">
            <table id="process_commission_tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Receipt No</th>
                    <th>Date</th>
                    <th>Intermediary</th>
                    <th>Receipt Amount</th>
                    <th>Commission Amount</th>
                    <th>New Balance</th>
                    <th>Status</th>
                    <th></th>
                    <th>Select</th>
                </tr>
                </thead>
            </table>
        </div>
        <form class="form-horizontal" id="process-comm-form">


        </form>
        <input type="button" value="Confirm" class="btn btn-success" id="confirm-comm">

    </div>
</div>