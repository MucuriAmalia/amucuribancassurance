<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel" id="acct_model">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Final Report Formats</h2>
        <div class="clearfix"></div>
    </div>
    <form class="form-horizontal">
        <div class="item form-group">
            <div class="col-md-6">
                <label for="report-format" class="label-align col-md-3">
                    Final Report </label>
                <div class="col-md-6">
                    <select class="form-control" id="report-format">
                        <option value="">Select Report</option>
                        <option value="BL">Balance Sheet</option>
                        <option value="ME">Management Expenses</option>
                        <option value="CF">Cash Flow Statement</option>
                        <option value="PL">Profit and Loss</option>
                    </select>
                </div>
            </div>
            <div class="col-md-6">

            </div>
        </div>
    </form>

    <table id="reportFormatTbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>
            <th>Row No</th>
            <th>Description</th>
            <th>Detail Format</th>
            <th>Summary Format</th>
            <th>Type</th>
            <th>Order</th>
            <th>Value From</th>
            <th>Asset/Liability</th>
            <th>Sign</th>
            <th>% Allocation</th>
        </tr>
        </thead>
    </table>
</div>
<div class="x_panel">
    <div class="" role="tabpanel" data-example-id="togglable-tabs">
        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Mapped Accounts</a>
            </li>
            <li role="presentation" class=""><a href="#tab_content2"
                                                role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Group Accounts</a>
            </li>
            <li role="presentation" class=""><a href="#tab_content3"
                                                role="tab" id="totals-tab" data-toggle="tab" aria-expanded="false">Totals Mapping</a>
            </li>
        </ul>
    </div>
    <div id="myTabContent" class="tab-content">
        <div role="tabpanel" class="tab-pane active"
             id="tab_content1" aria-labelledby="home-tab">
            <button class="btn btn-success btn btn-info float-left" id="btn-add-account">New</button>
            <div class="cutom-container">
                <table id="mappedAcctsTbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Account No</th>
                        <th>Account Name</th>
                        <th>Sign</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
        <div role="tabpanel" class="tab-pane fade" id="tab_content2"
             aria-labelledby="profile-tab">
            <button class="btn btn-success btn btn-info float-left" id="btn-add-grp-account">New</button>
            <div class="cutom-container">
                <table id="mappedAcctsGrpTbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Group Acc. No</th>
                        <th>Sign</th>
                    </tr>
                    </thead>
                </table>
            </div>
    </div>
        <div role="tabpanel" class="tab-pane fade" id="tab_content3"
             aria-labelledby="profile-tab">
            <button class="btn btn-success btn btn-info float-left" id="btn-add-rpt-total">New</button>
            <div class="cutom-container">
                <table id="reportGropTblTotal" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Column</th>
                        <th>Total</th>
                        <th>Sign</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
</div>
</div>

<div class="modal fade" id="reportFormatAccountsModal" tabindex="-1" role="dialog"
     aria-labelledby="reportFormatAccountsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="reportFormatAccountsModalLabel">
                    Edit/Add Report Format Accounts
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="report-accts-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="rfa-id" name="rfaId">
                    <input type="hidden" class="form-control" id="rf-id" name="rfId">
                    <div class="item form-group">
                        <label for="account-frm" class="col-md-3 label-align">Select Account</label>

                        <div class="col-md-8">
                            <input type="hidden" id="acct-id" name="accountNo"/>
                            <input type="hidden" id="acct-name" name="accountName">
                            <div id="account-frm" class="form-control"
                                 select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group acct-status">
                        <label for="selstatuss" class="label-align col-md-3">Sign<span class="required">*</span></label>
                        <div class="col-md-8">
                            <select class="form-control" id="selstatuss" name="affsign" required>
                                <option value="">Select Sign</option>
                                <option value="P">Positive</option>
                                <option value="N">Negative</option>
                            </select>

                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveReportFmtAcctsBtn"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="grpReportFormatAccountsModal" tabindex="-1" role="dialog"
     aria-labelledby="grpReportFormatAccountsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="grpReportFormatAccountsModalLabel">
                    Edit/Add Report Format Group Accounts
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="grp-report-accts-form" class="form-horizontal">
                    <div class="item form-group">
                        <input type="hidden" class="form-control" id="rfg-id" name="rfId">
                        <label for="account-frm" class="col-md-3 label-align">Group Account No</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="grp-acct-no"
                                   name="accountNo" required>
                        </div>
                    </div>
                    <div class="item form-group acct-status">
                        <label for="selstatuss" class="label-align col-md-3">Sign<span class="required">*</span></label>
                        <div class="col-md-8">
                            <select class="form-control" id="selacctstatuss" name="affsign" required>
                                <option value="">Select Sign</option>
                                <option value="P">Positive</option>
                                <option value="N">Negative</option>
                            </select>

                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveReportFmtGrpAcctsBtn"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<script>

    $(function() {

        $(document).ready(function () {

            $("#report-format").change(function () {
                if($("#report-format").val() != '') {
                    ACCOUNTS.createFinalReportFormats($("#report-format").val());
                    ACCOUNTS.createFinalReportFormatsTotals($("#report-format").val());
                }
                else{
                    ACCOUNTS.createFinalReportFormats($("#report-format").val());
                    ACCOUNTS.createFinalReportFormatsTotals($("#report-format").val());
                }
            });

           ACCOUNTS.saveFinalReportsAccounts();
           ACCOUNTS.saveFinalReportsGroupAccounts();


            $("#btn-add-account").on("click", function(){
                if(ACCOUNTS.reportFormatId===-2000){
                    bootbox.alert("Select Report Format to add Account...")
                    return;
                }
                $('#report-accts-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
                ACCOUNTS.createReportFormatAcctsLov();
                $("#rf-id").val(ACCOUNTS.reportFormatId);
                $('#reportFormatAccountsModal').modal('show');
            });

            $("#btn-add-grp-account").on("click", function(){
                if(ACCOUNTS.reportFormatId===-2000){
                    bootbox.alert("Select Report Format to add Account...")
                    return;
                }
                $('#grp-report-accts-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
                $("#rfg-id").val(ACCOUNTS.reportFormatId);
                $('#grpReportFormatAccountsModal').modal('show');
            });



        });
    });

    var ACCOUNTS = ACCOUNTS || {};
    ACCOUNTS.reportFormatId = -2000;

    ACCOUNTS.saveFinalReportsGroupAccounts = function (){
        var $currForm = $('#grp-report-accts-form');
        var currValidator = $currForm.validate();

        $('#saveReportFmtGrpAcctsBtn').click(function(){
            if (!$currForm.valid()) {
                return;
            }
            var $btn = $(this).text('Saving');
            var data = {};
            $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createReportFormatGroupAccount";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                currValidator.resetForm();
                $('#mappedAcctsGrpTbl').DataTable().ajax.reload();
                $('#grpReportFormatAccountsModal').modal('hide');
            });
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
                $btn.text('Save');
            });

        });
    }

    ACCOUNTS.saveFinalReportsAccounts = function (){
        var $currForm = $('#report-accts-form');
        var currValidator = $currForm.validate();

        $('#saveReportFmtAcctsBtn').click(function(){
            if (!$currForm.valid()) {
                return;
            }
            var $btn = $(this).text('Saving');
            var data = {};
            $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createReportFormatAccount";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                currValidator.resetForm();
                $('#mappedAcctsTbl').DataTable().ajax.reload();
                $('#reportFormatAccountsModal').modal('hide');
            });
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
                $btn.text('Save');
            });

        });
    }

    ACCOUNTS.createReportFormatAcctsLov = function(){
        if($("#account-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "account-frm",
                sort : 'name',
                change: function(e, a, v){
                    $("#acct-id").val(e.added.code);
                    $("#acct-name").val(e.added.name);
                },
                formatResult : function(a)
                {
                    return a.code+"-"+ a.name;
                },
                formatSelection : function(a)
                {
                    return a.code+"-"+ a.name;
                },
                initSelection: function (element, callback) {
                    // var code = $("#acct-code").val();
                    // var name = $("#acct-name").val();
                    // var coid = $("#acct-id").val();
                    // var data = {code:code,name:name,coId:coid};
                    // callback(data);
                },
                id: "coId",
                placeholder:"Select GL Account",
            });
        }
    }


    ACCOUNTS.createFinalReportFormatsTotals= function(type) {
        var url = "finalReportFormatTotals?type=" + type;
        return  $('#reportGropTblTotal').DataTable({
            "processing": true,
            "serverSide": true,
            scrollCollapse: true,
            scrollY: '300px',
            "ajax": {
                'url': url,
            },
            lengthMenu: [[10, 20], [10, 20]],
            pageLength: 10,
            destroy: true,
            "columns": [
                {"data": "column"},
                {"data": "total"},
                { "data": "sign",
                    "render": function ( data, type, full, meta ) {
                        if(full.sign){
                            return "Positive";
                        }
                        else
                            return "Negative";
                    }
                }
            ]
        });
    }

    ACCOUNTS.createFinalReportFormats= function(type){
        var url = "finalReportFormat?type="+type;
        var currTable =  $('#reportFormatTbl').DataTable({
            "processing": true,
            "serverSide": true,
            scrollCollapse: true,
            scrollY: '300px',
            "ajax": {
                'url': url,
            },
            lengthMenu: [[100, 200], [100, 200]],
            pageLength: 100,
            destroy: true,
            "columns": [
                {"data": "rowCode"},
                {"data": "description"},
                {"data": "detailFormat"},
                {"data": "summaryFormat"},
                {"data": "type"},
                {"data": "order"},
                {"data": "pickedFrom"},
                // {"data": "dependsOn"},
                {"data": "assetLiability"},
                {"data": "assetLiabilitySign"},
                { "data": "taxAmount",
                    "render": function ( data, type, full, meta ) {

                        return UTILITIES.currencyFormat(full.allocation);
                    }
                },
            ]
        });

        $('#reportFormatTbl tbody').on( 'click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null){

            }
            else{
                ACCOUNTS.createFinalReportFormatAccounts(aData[0].rfId);
                ACCOUNTS.createFinalReportFormatGroupAccounts(aData[0].rfId);
                ACCOUNTS.reportFormatId = aData[0].rfId;
            }
        } );

        return currTable;
    }

    ACCOUNTS.createFinalReportFormatGroupAccounts= function(type){
        var url = "finalReportFormatGroupAccounts?formatId="+type;
        var currTable =  $('#mappedAcctsGrpTbl').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
            },
            lengthMenu: [[5, 10,20], [5, 10,20]],
            pageLength: 5,
            destroy: true,
            "columns": [
                {"data": "accountNo"},
                { "data": "sign",
                    "render": function ( data, type, full, meta ) {
                        if(full.sign){
                            return "Positive";
                        }
                        else
                            return "Negative";
                    }
                },
            ]
        });

        return currTable;
    }

    ACCOUNTS.createFinalReportFormatAccounts= function(type){
        var url = "finalReportFormatAccounts?formatId="+type;
        var currTable =  $('#mappedAcctsTbl').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
            },
            lengthMenu: [[5, 10,20], [5, 10,20]],
            pageLength: 5,
            destroy: true,
            "columns": [
                {"data": "accountNo"},
                {"data": "accountName"},
                { "data": "sign",
                    "render": function ( data, type, full, meta ) {
                        if(full.sign){
                            return "Positive";
                        }
                        else
                            return "Negative";
                    }
                },
            ]
        });

        return currTable;
    }

</script>