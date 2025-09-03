<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Accounting Periods</h2>
        <div class="clearfix"></div>
    </div>
    <form class="form-horizontal">
        <div class="item form-group">
            <label for="brn-id" class="col-md-3 label-align">Select
                Branch</label>
            <div class="col-md-4">
                <div id="branches-frm" class="form-control"
                     select2-url="<c:url value="/protected/uw/policies/allbranches"/>" >

                </div>
            </div>
        </div>

    </form>
    <div class="x_content">
        <div class="table-responsive">
            <button class="btn btn-success btn btn-info float-left" id="btn-add-account">Populate Accounting Period</button>
            <table id="accountYearTbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Year</th>
                    <th>Year Start</th>
                    <th>Year End</th>
                    <th>No of Periods</th>
                    <th>Status</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
<div class="x_panel">
    <div class="x_title">
        <h2>
            Periods
        </h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="table-responsive">
            <table id="accountYearPrdsTbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Branch</th>
                    <th>Period</th>
                    <th>Period Start</th>
                    <th>Period End</th>
                    <th>Status</th>
                    <th>Closed By</th>
                    <th>Closed Date</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="accountingYearModal" tabindex="-1" role="dialog"
     aria-labelledby="accountingYearModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="accountingYearModalLabel">
                    Populate Accounting Year
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="acct-year-form" class="form-horizontal">
                    <div class="item form-group edit-hide">
                        <label for="acct-number" class="col-md-3 label-align">Accounting Year</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="acct-year"
                                   name="year"  required readonly>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveAcctYear"
                        type="button" class="btn btn-success">
                    Continue
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
            ACCOUNTS.createBranches();
            ACCOUNTS.createAccountingYears(-2000);
            ACCOUNTS.populateAccountingYear();

            $("#btn-add-account").on("click", function(){
                $.ajax({
                    type: 'GET',
                    url:  'currentAccountingYear',
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        $("#acct-year").val(result.currentYear);
                        $('#accountingYearModal').modal('show');
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                    }
                });

            });
        });

    });
    var ACCOUNTS = ACCOUNTS || {};

    ACCOUNTS.populateAccountingYear = function (){
        var $currForm = $('#acct-year-form');
        var currValidator = $currForm.validate();

        $('#saveAcctYear').click(function(){
            if (!$currForm.valid()) {
                return;
            }
            var $btn = $(this).text('Processing...');
            var data = {};
            $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createAccountingYear";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                    title: 'Success',
                    text: 'Accounting Year Populated successfully',
                    icon: 'success'
                });
                currValidator.resetForm();
                $('#accountYearTbl').DataTable().ajax.reload();
                $('#accountingYearModal').modal('hide');
            });
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
                $btn.text('Continue');
            });

        });
    }
    ACCOUNTS.createBranches = function (){
        if($("#branches-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "branches-frm",
                sort : 'obName',
                change: function(e, a, v){
                    ACCOUNTS.createAccountingYears(e.added.obId);
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
                placeholder:"Select Branch",
            });
        }
    }

    ACCOUNTS.createAccountingYearPeriods = function (yearId){
        var url = "accountingYearPeriods";
        var currTable = $('#accountYearPrdsTbl').DataTable( {
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data':{
                    'yearId': yearId
                },
            },
            lengthMenu: [ [12], [12] ],
            pageLength: 12,
            destroy: true,
            "columns": [
                { "data": "branch" },
                { "data": "periodName" },
                { "data": "periodId",
                    "render": function ( data, type, full, meta ) {
                        if (full.wef)
                            return moment(full.wef).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "periodId",
                    "render": function ( data, type, full, meta ) {
                        if (full.wet)
                            return moment(full.wet).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "status" },
                { "data": "userClosed" },
                { "data": "periodId",
                    "render": function ( data, type, full, meta ) {
                        if (full.closedDate)
                            return moment(full.closedDate).format('DD/MM/YYYY');
                        else return "";
                    }
                },

            ]
        } );
        return currTable;
    }

    ACCOUNTS.createAccountingYears = function (branchId,year){
        var url = "accountingYears";
        var currTable = $('#accountYearTbl').DataTable( {
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data':{
                    'obId': branchId,
                    'year': year
                },
            },
            lengthMenu: [ [10, 15], [10, 15] ],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "year" },
                { "data": "yearId",
                    "render": function ( data, type, full, meta ) {
                        if (full.wef)
                            return moment(full.wef).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "wet",
                    "render": function ( data, type, full, meta ) {
                        if (full.wet)
                            return moment(full.wet).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "noofMonths" },
                { "data": "status" },
            ]
        } );
        $('#accountYearTbl tbody').on( 'click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null){

            }
            else{
                ACCOUNTS.createAccountingYearPeriods(aData[0].yearId);
            }
        } );
        return currTable;
    }
</script>