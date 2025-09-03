<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_title">
    <h2 id="wkflow-task-name"><i class="fa fa-bars"></i> New Requisition</h2>

    <ul class="nav navbar-right panel_toolbox">
        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
        </li>
    </ul>
    <div class="clearfix"></div>
</div>
<div class="container">
    <div class="x_panel">
            <input type="button" class="btn btn-info float-right"
                   style="margin-right: 10px;" value="Save" id="btn-add-req">

    </div>

    <div class="x_panel">
    <form id="requistion-form" class="form-horizontal form-label-left">
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12">
                <label for="rq-type" class="label-align col-md-5">
                    Select Requisition Type<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="rq-type" name="paymentType" required>
                        <option value="">Select Requisition Type</option>
                        <option value="GL">GL</option>
<%--                        <option value="PC">Petty Cash</option>--%>
                    </select>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="pm-mode-frm" class="label-align col-md-5">
                    Payment Mode<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="pm-id" name="paymentModeId"/>
                        <input type="hidden" id="pm-name">
                        <div id="pm-mode-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >
                        </div>
                </div>
            </div>

        </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12">
                <label for="payee-frm" class="label-align col-md-5">
                    Select Payee<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="pay-id" name="payee"/>
                        <input type="hidden" id="pay-name">
                        <div id="payee-frm" class="form-control"
                             select2-url="<c:url value="/protected/accounts/payments/selPayees"/>" >

                        </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="acct-frm" class="label-align col-md-5">
                    Select Bank Account<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="acct-id" name="bankActCode"/>
                    <input type="hidden" id="acct-name">
                    <div id="acct-frm" class="form-control"
                         select2-url="<c:url value="/protected/accounts/payments/selBankAccounts"/>" >

                    </div>
                </div>
            </div>

        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="invoiceno" class="label-align col-md-5">Invoice
                    No<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" name="invoiceNo" id="invoiceno" class="form-control"
                           placeholder="Enter Invoice No" required>
                </div>


            </div>
            <div class="col-md-6 col-xs-12">
                <label for="invoicedate" class="label-align col-md-5">Invoice
                    Date<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="invoicedate">
                        <input type='text' class="form-control float-right" name="invoiceDate"
                               id="wet-date" required />
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12">
                <label for="curr-frm" class="label-align col-md-5">
                    Currency<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="cur-id" name="curId"/>
                        <input type="hidden" id="cur-name">
                        <div id="curr-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >

                        </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="req-amount" class="label-align col-md-5">
                    Requisition Amount<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" name="amount" id="req-amount" class="form-control amount"
                           placeholder="Requisition Amount">
                </div>
            </div>
        </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12">
                <label for="curr-frm" class="label-align col-md-5">
                    Narration<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <textarea class="form-control" rows="2" name="narration"></textarea>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group form-required">
                    <label for="edit-branch" class="label-align col-md-5">
                       Requisition Branch<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-branch">
                            <input type="hidden" id="brn-id" name="branchCode"/>
                            <div id="brn-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div id="gl-trans" class="x_panel" style="display: none">
        <div class="x_title">
            <h2>GL Transactions</h2>
            <div class="clearfix"></div>
        </div>
            <input type="button" id="add-det-btn"
                   class="btn btn-info float-left" style="margin-right: 10px;"
                   value="Add">
            <div class="box-body">
                <div class="table-responsive">
                    <table id="gl-transactions-tbl" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>

                            <th>Narration</th>
                            <th>Amount</th>
                            <th>GL</th>
                            <th>Transaction Type</th>
                            <th>Branch</th>
                            <th></th>
                        </tr>
                        </thead>

                    </table>
                </div>
            </div>
    </div>
<%--    <div id="petty-cash-trans" class="x_panel" style="display: none">--%>
<%--        <div class="x_title">--%>
<%--            <h2>Petty Cash Transactions</h2>--%>
<%--            <div class="clearfix"></div>--%>
<%--        </div>--%>
<%--        <input type="button" id="add-deta-btn"--%>
<%--               class="btn btn-info float-left" style="margin-right: 10px;"--%>
<%--               value="Add">--%>
<%--        <div class="box-body">--%>
<%--            <div class="cutom-container">--%>
<%--                <table id="petty-transactions-tbl" class="table" style="width:100%">--%>
<%--                    <thead>--%>
<%--                    <tr>--%>

<%--                        <th>Narration</th>--%>
<%--                        <th>Amount</th>--%>
<%--                        <th>Bank Account</th>--%>
<%--                        <th>Transaction Type</th>--%>
<%--                        <th></th>--%>
<%--                    </tr>--%>
<%--                    </thead>--%>

<%--                </table>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
</div>
</div>


<script>
    $(function() {


        $(document).ready(function () {
            $('.amount').number( true, 2 );
            $(".datepicker-input").each(function() {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY',
                    maxDate: 'now'
                });

            });
            $("#rq-type").on('change', function(){
                if(this.value==='GL'){
                  $("#gl-trans").css("display","block");
                  $("#petty-cash-trans").css("display","none");
                }
                else  if(this.value==='PC'){
                    $("#gl-trans").css("display","none");
                    $("#petty-cash-trans").css("display","block");
                }
            });
            REQUISTION.paymentmodeLov();
            REQUISTION.payeeLov();
            REQUISTION.bankAccountsLov();
            REQUISTION.currencyLov();
            REQUISTION.populateUserBranches();
            $("#add-det-btn").on('click', function () {
                REQUISTION.addGlTrans();
            });

            $("#btn-add-req").on('click', function() {
                REQUISTION.createRequistion();

            });

            $("#gl-transactions-tbl").on('click','.hyperlink-btn',function() {
                $(this).closest('tr').remove();
                var row_index = $('#gl-transactions-tbl tr').length;
                if(row_index ===1){
                    $("#rq-type").attr('disabled',false);
                }
            });


        });
    });

    var REQUISTION = REQUISTION || {};
    REQUISTION.paymentmodeLov = function(){
        if($("#pm-mode-frm").filter("div").html() !== undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "pm-mode-frm",
                sort : 'pmDesc',
                change: function(e, a, v){
                    $("#pm-id").val(e.added.pmId);
                },
                formatResult : function(a)
                {
                    return a.pmDesc;
                },
                formatSelection : function(a)
                {
                    return a.pmDesc;
                },
                initSelection: function (element, callback) {

                },
                id: "pmId",
                width:"250px",
                placeholder:"Select Payment Mode"

            });
        }
    }

    REQUISTION.payeeLov = function(){
        if($("#payee-frm").filter("div").html() !== undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "payee-frm",
                sort : 'fullName',
                change: function(e, a, v){
                    $("#pay-id").val(e.added.payId);
                },
                formatResult : function(a)
                {
                    return a.fullName;
                },
                formatSelection : function(a)
                {
                    return a.fullName;
                },
                initSelection: function (element, callback) {

                },
                id: "payId",
                width:"250px",
                placeholder:"Select Payee"

            });
        }
    }
    REQUISTION.bankAccountsLov = function(){
        if($("#acct-frm").filter("div").html() !== undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "acct-frm",
                sort : 'bankAcctName',
                change: function(e, a, v){
                    $("#acct-id").val(e.added.baId);
                },
                formatResult : function(a)
                {
                    return a.bankAcctName;
                },
                formatSelection : function(a)
                {
                    return a.bankAcctName;
                },
                initSelection: function (element, callback) {

                },
                id: "baId",
                width:"250px",
                placeholder:"Select Bank Account"

            });
        }
    }
    REQUISTION.populateUserBranches = function (){
        if($("#brn-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "brn-frm",
                sort : 'obName',
                change: function(e, a, v){
                    $("#brn-id").val(e.added.obId);
                },
                formatResult : function(a)
                {
                    return a.obName;
                },
                formatSelection : function(a)
                {
                    return a.obName;
                },
                initSelection: function (element, callback) {

                },
                id: "obId",
                width:"250px",
                placeholder:"All Branches"

            });
        }
        $("#brn-frm").on("select2-removed", function(e) {
            $("#brn-id").val('');
        });
    }

    REQUISTION.currencyLov = function (){
        if($("#curr-frm").filter("div").html() !== undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "curr-frm",
                sort : 'curName',
                change: function(e, a, v){
                    $("#cur-id").val(e.added.curCode);

                },
                formatResult : function(a)
                {
                    return a.curName;
                },
                formatSelection : function(a)
                {
                    return a.curName;
                },
                initSelection: function (element, callback) {

                },
                id: "curCode",
                width:"250px",
                placeholder:"Select Currency"

            });
        }
    }

    REQUISTION.addGlTrans = function (){
        var row_index = $('#gl-transactions-tbl tr').length;
        if(row_index > 0){
            $("#rq-type").attr('readonly',true);
        }

        var trans = '<td><input type="hidden" id="glId-code' + row_index + '" name="glId">' +
            ' <div id="gl-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/setups/revitems/selGlAccount" </div></td> ';

        var branch = '<td><input type="hidden" id="brn-id' + row_index + '" name="branchCode"/>' +
            '<div id="brn-frm' + row_index + '" class="form-control" select2-url="' + SERVLET_CONTEXT + '/protected/uw/policies/uwbranches" </div></td>';

        var markup = "<tr><td><textarea class='form-control' rows='1' cols='25' id='rctnarr-" + row_index + "'  name='narrative'/></td>" +
            "<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='transAmount'/></td>" +
            trans +
            "<td><select class='form-control' id='rctdrcr-" + row_index + "' name='drcr'><option value='D'>Debit</option><option value='C'>Credit</option> </select></td>" +
            branch +
            "<td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
        $("#gl-transactions-tbl").append(markup);
        $('[id^=rctamt-]').number(true, 2);
        Select2Builder.initAjaxSelect2({
            containerId: "gl-frm" + row_index,
            sort: 'coId',
            counter: row_index,
            change: function (e, a, v) {
                $.each($(this), function () {
                    var transId = (e.added.coId)&& e.added.coId!==0?e.added.coId:null;
                    var key = Object.keys(this)[2];
                    var value = this[key];
                    var drId = "#glId-code" + value;
                    $(drId).val(transId);
                });
            },
            formatResult: function (a) {
                return a.code+' - '+a.name;
            },
            formatSelection: function (a) {
                return a.code+' - '+a.name;
            },
            initSelection: function (element, callback) {

            },
            id: "coId",
            width: "300px",
            placeholder: "Select GL Account"

        });

        Select2Builder.initAjaxSelect2({
            containerId : "brn-frm"+ row_index,
            sort : 'obName',
            counter: row_index,
            change: function(e, a, v){
                var transId = (e.added.obId)&& e.added.obId!==0?e.added.obId:null;
                var key = Object.keys(this)[2];
                console.log(key);
                var value = this[key];
                var drId = "#brn-id" + value;
                $(drId).val(transId);
            },
            formatResult : function(a)
            {
                return a.obName
            },
            formatSelection : function(a)
            {
                return a.obName
            },
            initSelection: function (element, callback) {

            },
            id: "obId",
            width:"200px",
            placeholder:"Select Branch"
        });

    }

    REQUISTION.createRequistion = function (){
        var $currForm = $('#requistion-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "saveRequisition";
        var arr = REQUISTION.createAllocation();
        data.glTrans = arr;
        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
        console.log(JSON.stringify(data));
        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                $('#requistion-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
                REQUISTION.paymentmodeLov();
                REQUISTION.payeeLov();
                REQUISTION.bankAccountsLov();
                REQUISTION.currencyLov();
                $("#rq-type").attr('readonly',false);
                arr = {};
                $('#gl-transactions-tbl tbody').remove();
            },
            error : function(jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            },
            dataType : "json",
            contentType : "application/json"
        });
    }

    REQUISTION.createAllocation = function() {
        var arr = [];
        $('#gl-transactions-tbl > tbody  > tr').each(
            function() {
                var data = {};
                $(this).find(":input[type='text'],:input[type='hidden'],textarea,select").serializeArray()
                    .map(function(x) {
                        data[x.name] = x.value;
                    });
                arr.push(data);
            });
        return arr;
    }

</script>