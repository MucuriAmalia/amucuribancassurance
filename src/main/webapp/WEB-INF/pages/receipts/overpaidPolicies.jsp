<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i>Overpaid Policies</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form id="search-form" class="form-horizontal">
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="from-date" class="col-md-5 label-align">Date
                        From</label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="wef-date">
                            <input type='text' class="form-control float-right"
                                   id="from-date" required/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="cover-to-date" class="col-md-5 label-align">Date To
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="cover-to-date">
                            <input type='text' class="form-control float-right"
                                   id="wet-date" required/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="acc-frm" class="col-md-5 label-align">Intermediary
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="agent-search-number" name="accountCode"/>
                        <div id="acc-frm" class="form-control"
                             select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <input type="button" class="btn btn-primary float-right"
                       style="margin-right: 10px;" value="Search"
                       id="btn-search-policies">
            </div>
        </form>
        <div class="clearfix"></div>
    </div>
    <button class="btn btn-primary float-right" id="btn-print-report">Print Report</button>

    <div class="x_content">
        <div class="card-box table-responsive">

            <table id="overpaid-pol-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Policy No.</th>
                    <th>Policy Type</th>
                    <th>Maturity Date</th>
                    <th>Client</th>
                    <th>Total Premium</th>
                    <th>Paid Premium</th>
                    <th>Refund Amount</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });
        });

        createAccountsForSel();
        $('#btn-search-policies').on('click', function () {
            if (!$("#from-date").val()) {
                bootbox.alert("Select Date From");
                return;
            }
            if (!$("#wet-date").val()) {
                bootbox.alert("Select Date To");
                return;
            }
            if (!$("#agent-search-number").val()) {
                bootbox.alert("Select Intermediary");
                return;
            }
            var polTable = getOverpaidPolicies($("#agent-search-number").val(), $("#from-date").val(), $("#wet-date").val());

            polTable.on('draw', function () {
                var data = polTable.rows().data().length;
                $('#btn-print-report').prop('disabled', data === 0);
            })
        });
    });

    function getOverpaidPolicies(accountCode, dateFrom, dateTo) {
        if ($.fn.DataTable.isDataTable('#overpaid-pol-tbl')) {
            $('#overpaid-pol-tbl').DataTable().clear().destroy();
        }
        var accountCode = accountCode;  // You can dynamically set this based on your page logic
        var dateFrom = dateFrom; // Replace with dynamic dateFrom, you can get from a date input field or another source
        var dateTo = dateTo;   // Replace with dynamic dateTo
        var table = $('#overpaid-pol-tbl').DataTable({
            processing: true,
            serverSide: true,
            searching: true,
            lengthMenu: [5, 10, 25, 50],
            pageLength: 10,
            "ajax": {
                "url": '<c:url value="/protected/uw/policies/overpaidPolicies"/>',
                "data": {
                    dateFrom: dateFrom,
                    dateTo: dateTo,
                    accountCode: accountCode
                }
            },
            "columns": [
                {"data": "policyNo"},
                {"data": "policyType"},
                {"data": "maturityDate"},
                {"data": "client"},
                {"data": "totalPremium"},
                {"data": "paidPremium"},
                {"data": "refundPremium"}
            ],
            order: [[1, 'asc']]
        });
        document.getElementById('btn-print-report').addEventListener('click', function() {
            $.ajax({
                url: '<c:url value="/protected/uw/policies/rpt_overpaidpremiums"/>',
                type: 'POST',
                data: {
                    accountCode: accountCode,
                    dateFrom: dateFrom,
                    dateTo: dateTo
                },
                xhrFields: {
                    responseType: 'blob' // Important for binary data
                },
                success: function(blob) {
                    // Create a URL for the blob
                    const url = window.URL.createObjectURL(blob);

                    // Open the blob URL in a new browser tab
                    window.open(url, '_blank');
                },
                error: function(xhr, status, error) {
                    console.error("Failed to generate report: ", error);
                }
            });
        });
        return table;

    }

    function createAccountsForSel() {
        if ($("#acc-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "acc-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#agent-search-number").val(e.added.acctId);
                },
                formatResult: function (a) {
                    return a.name
                },
                formatSelection: function (a) {
                    return a.name
                },
                initSelection: function (element, callback) {

                },
                id: "acctId",
                placeholder: "Select Intermediary",
            });
        }

        $("#acc-frm").on("select2-removed", function (e) {
            $("#agent-search-number").val('');
        })
    }


</script>