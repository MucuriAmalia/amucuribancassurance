<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript">
    function toggleSelectAll(selectAllCheckbox) {
        let isChecked = selectAllCheckbox.checked;
        $('.checker-checkbox').prop('checked', isChecked);
    }
    const mck_id_no = ${mckIdNo};
    console.log(mck_id_no);
</script>


<div class="container">
    <div class="x_panel">
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Receipt Details</h2>
            <div class="clearfix"></div>
        </div>
    </div>

    <div class="x_panel">
        <form id="receipt-form" class="form-horizontal">
            <div class="item form-group" style="display: none">
                <input type="hidden" id="receipt-type" name="receiptType" class="form-control">
                <input type="hidden" id="receipt-user" name="userId" class="form-control">
                <input type="hidden" id="receipt-no" name="receiptNo" class="form-control">
                <input type="hidden" id="receipt-amount-words" class="form-control">
                <input type="hidden" id="cancel-comment" class="form-control">
                <input type="hidden" id="cancelled" class="form-control">
                <input type="hidden" id="cancelled-by" class="form-control">
                <input type="hidden" id="cancelled-date" class="form-control">
                <input type="hidden" id="cancelled-ref" class="form-control">
                <input type="hidden" id="cleared" class="form-control">
                <input type="hidden" id="client" class="form-control">
                <input type="hidden" id="comm-posted" class="form-control">
                <input type="hidden" id="counter" class="form-control">
                <input type="hidden" id="direct-rcpt" class="form-control">
                <input type="hidden" id="fund-rcpt" class="form-control">
                <input type="hidden" id="printed" class="form-control">
                <input type="hidden" id="receipt-id" class="form-control">
                <input type="hidden" id="receipt-trans-date" class="form-control">


            </div>

            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="rec-type" class="label-align col-md-3 col-xs-12"> Receipt Type: <span
                        class="required">*</span></label>
                <div class="col-md-6 col-xs-12">
                    <select class="form-control" id="rec-type" name="receiptType" required>
                        <option value="">Select Receipt Type</option>
                        <option value="N">General Insurance</option>
                        <option value="L">Life Insurance</option>
                        <option value="COM">Commissions</option>
                    </select>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="coll-id" class="col-md-3 col-xs-12  label-align">Collection Account<span
                        class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <div id="edit-collect-acct">
                        <input type="hidden" id="coll-id" name="payId"/>
                        <input type="hidden" id="coll-name" />
                        <div id="coll-div" class="form-control"
                             select2-url="<c:url value="/protected/uw/receipts/collectAccts"/>">
                        </div>
                    </div>
                    <div id="display-collect-acct">
                        <p class="form-control-static" id="collect-acct-info"></p>
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 insurance-co" style="display: none">
                <label for="insurance-div" class="col-md-3 col-xs-12 label-align">Insurance Company<span
                        class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <div id="edit-insurance">
                        <input type="hidden" id="insurance-id" name="insuranceId"/>
                        <input type="hidden" id="insurance-name"/>
                        <div id="insurance-div" class="form-control"
                             select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">
                        </div>
                    </div>
                    <div id="display-insurance">
                        <p class="form-control-static" id="insurance-info"></p>
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="pymt-mode" class="label-align col-md-3 col-xs-12">Payment Mode</label>
                <div class="col-md-6 col-xs-12">
                    <p class="form-control-static" id="pymt-mode"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="bank-desc" class="label-align col-md-3 col-xs-12">Currency
                </label>
                <div class="col-md-6 col-xs-12">
                    <p class="form-control-static" id="currency-desc"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="bank-desc" class="label-align col-md-3 col-xs-12">Bank Info<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <p readonly class="form-control-static" id="bank-desc"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="bank-desc" class="label-align col-md-3 col-xs-12">Branch
                </label>
                <div class="col-md-6 col-xs-12">
                    <div id="edit-branch">
                        <input type="hidden" id="brn-id" name="brnCode"/>
                        <input type="hidden" id="branch-name"/>
                        <div id="brn-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">
                        </div>
                    </div>
                    <div id="display-branch">
                        <p class="form-control-static" id="branch-info"></p>
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="rct-amount" class="label-align col-md-3 col-xs-12">Amount<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="rct-amount"
                           name="receiptAmount" required>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="paid-by" class="label-align col-md-3 col-xs-12">Paid By<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="paid-by"
                           name="paidBy" required>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="narration" class="label-align col-md-3 col-xs-12">Narration<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <textarea rows="2" cols=30 class="form-control" name="receiptDesc" id="narration"
                              REQUIRED></textarea>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="receipt-date" class="label-align col-md-3 col-xs-12">Receipt Date<span
                        class="required">*</span></label>
                <div class="col-md-6 col-xs-12">
                    <div class='input-group date datepicker-input'>
                        <input type='text' class="form-control" name="receiptDate" id="receipt-date"/>
                        <span class="input-group-addon">
                        <span class="fa fa-calendar"></span>
                    </span>
                    </div>
                    <div class="col-md-3">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="payment-ref" class="label-align col-md-3 col-xs-12">Reference</label>

                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="payment-ref"
                           name="paymentRef">
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="doc-date" class="label-align col-md-3 col-xs-12">Document Date</label>

                <div class="col-md-6 col-xs-12">
                    <div class='input-group date datepicker-input'>
                        <input type='text' class="form-control" name="documentDate" id="doc-date"/>
                        <span class="input-group-addon">
                        <span class="fa fa-calendar"></span>
                    </span>
                    </div>
                    <div class="col-md-3">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="manual-ref" class="label-align col-md-3 col-xs-12">Manual Ref </label>
                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="manual-ref" name="manualRef">
                </div>
                <div class="col-md-3">
                </div>
            </div>
        </form>
    </div>
    <div class="x_panel">
        <div id="rates-details-div">
            <input type="button" id="add-det-btn"
                   class="btn btn-primary float-left" style="margin-right: 10px;"
                   value="Add">
        </div>
        <div class="box-body">
            <div class="table-responsive">
                <table id="rct-detail-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>

                        <th>Trans No</th>
                        <th>Policy Number</th>
                        <th>Date</th>
                        <th>Client</th>
                        <th>Balance</th>
                        <th>Allocated Amount</th>
                        <th></th>
                    </tr>
                    </thead>

                </table>
            </div>
        </div>
    </div>
</div>

<div id="receipt-panel">
<%--    <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">--%>
<%--        <input type="button" class="btn btn-primary float-right"--%>
<%--               value="Submit" id="btn-submit-task">--%>
<%--    </sec:authorize>--%>

    <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
        <input type="button" class="btn btn-primary float-right"
               value="Assign" id="btn-assign-task">
    </sec:authorize>

</div>

<div class="modal fade" id="assignCheckerModal" tabindex="-1" role="dialog"
     aria-labelledby="assignCheckerModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="assignCheckerModalLabel">Assign Ticket To User</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="text" id="searchChecker" class="form-control" placeholder="Search Checkers...">
                <table class="table table-striped table-hover table-bordered" id="checkersList">
                    <thead>
                    <tr>
                        <th width="5%"><input type="checkbox" id="selectAll"></th><%--                        <th width="4%">User Name</th>--%>
                        <th width="12%">Checker Name</th>
                        <th width="12%">AB No.</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
            <div class="modal-footer" style="position: sticky; bottom: 0; background-color: #FFFFFF; z-index: 1000;">
                <button data-loading-text="Saving..." id="assign-checker-submit"
                        type="button" class="btn btn-success">Submit
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
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
        $("#rct-amount").number(true, 2);

        // Make sure mck_id_no is set
        var url = SERVLET_CONTEXT + "/protected/viewMakerTask";

        agentLov();
        collectionAcctsLov();
        populateUserBranches();
        // resubmitTask();


        $.ajax({
            url: url,
            method: 'GET',
            data: {
                taskId: mck_id_no,
                // taskType: taskType
            },
            success: function (receipt) {
                console.log(receipt);
                if ($.fn.DataTable.isDataTable('#rct-detail-tbl')) {
                    $('#rct-detail-tbl').DataTable().clear().destroy();
                }

                // addReceiptRecord();
                // Populate receipt details
                $("#receipt-user").val(receipt.userId);
                $("#receipt-no").val(receipt.receiptNo);
                // $("#receipt-amount-words").val(receipt.amountWords);
                // $("#cancel-comment").val(receipt.cancelComment);
                // $('#cancelled').val(receipt.cancelled);
                // $('#cancelled-by').val(receipt.cancelledBy);
                // $('#cancelled-date').val(receipt.cancelledDate);
                // $('#cancelled-ref').val();
                // $('#cleared').val();
                // $('#client').val();
                // $('#comm-posted').val();
                $('#counter').val(receipt.counter);
                $('#direct-rcpt').val(receipt.directReceipt);
                $('#fund-rcpt').val(receipt.fundReceipt);
                // $('#printed').val();
                // $('#receipt-id').val();
                $("#receipt-trans-date").val(moment(receipt.receiptTransDate).format("DD/MM/YYYY"));
                $('#edit-branch').show();
                $('#display-branch').show();
                $("#rec-type").val(receipt.receiptType);
                $("#receipt-type").val(receipt.receiptType);
                $("#rct-amount").val(receipt.receiptAmount);
                $("#narration").val(receipt.receiptDesc.trim());
                $("#receipt-date").val(receipt.receiptDate);
                $("#payment-ref").val(receipt.paymentRef.trim());
                $("#doc-date").val(receipt.documentDate);
                $("#manual-ref").val(receipt.manualRef.trim());
                $("#paid-by").val(receipt.paidBy);

                $('#insurance-info').val(receipt.insuranceName);
                $('#insurance-id').val(receipt.insuranceId);
                $('#insurance-name').val(receipt.insuranceName);
                agentLov();
                $("#branch-info").val(receipt.branchName);
                $("#branch-name").val(receipt.branchName);
                $('#brn-id').val(receipt.branch.obId);
                populateUserBranches();
                if(receipt.collectionAccount) {
                    $('#coll-id').val(receipt.payId)
                    $("#coll-name").val(receipt.collectionAccount.name);
                    $("#collect-acct-info").val(receipt.collectionAccount.name);
                    $("#pymt-mode").text(receipt.collectionAccount.paymentModes.pmDesc);
                    $("#currency-desc").text(receipt.collectionAccount.currencies.curName);
                    $("#bank-desc").text(
                        receipt.collectionAccount.bankBranches.branchName + " - " +
                        receipt.collectionAccount.bankBranches.bank.bankName
                    );
                    collectionAcctsLov();
                }
                if (receipt.receiptType === "N" || receipt.receiptType === "L") {
                    $('.collect-acct').css('display','none')
                    $('.insurance-co').css('display','block')
                    $('#edit-collect-acct').hide();
                    $('#display-collect-acct').hide();
                    $('#edit-insurance').show();
                    $('#display-insurance').show();
                    $('#coll-id').val(null);

                } else {
                    $(".collect-acct").css('display', 'block');
                    $(".insurance-co").css('display', 'none');
                    $('#edit-collect-acct').show();
                    $('#display-collect-acct').show();
                    $('#edit-insurance').hide();
                    $('#display-insurance').hide();
                }
                addReceiptRecord();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: 'Failed to load receipt details: ' + jqXHR.responseText,
                    icon: 'error'
                });
            }

        });

        $('#selectAll').on('click', function () {
            var isChecked = $(this).prop('checked');
            $('.checker-checkbox').prop('checked', isChecked);
        });

        $('#searchChecker').on('keyup', function () {
            loadEligibleCheckers($(this).val());
        });

        $("#btn-assign-task").click(function () {
                loadEligibleCheckers('');
        });

        $('#assign-checker-submit').click(function () {
            resubmitTask()
        });

        $("#add-det-btn").on('click', function () {
            if ($("#rec-type").val()) {
                addReceiptRecord();
            } else {
                Swal.fire({
                    title: 'Error',
                    text: 'Select the Receipt Type to Proceed!!',
                    icon: 'error'
                });
                $("#rec-type").val('');
                $("#rct-detail-tbl").clear().draw();
            }
        });

        $("#rec-type").change(function () {
            var _val = $(this).val();
            $("#receipt-type").val(_val);
            if (_val === 'N' || _val === 'L') {
                $(".insurance-co").css('display', 'block');
                $(".collect-acct").css('display', 'none');
                agentLov();
            } else if (_val === 'COM') {
                $(".collect-acct").css('display', 'block');
                $(".insurance-co").css('display', 'none');
            }
        });

        $("#rct-detail-tbl").on('click', '.hyperlink-btn', function () {
            $(this).closest('tr').remove();
            var row_index = $('#rct-detail-tbl tr').length;
            if (row_index === 1) {
                $("#rec-type").attr('disabled', false);
            }
        });

        function resubmitTask() {
            var $currForm = $('#receipt-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var receiptData = {};
            $currForm.serializeArray().map(function (x) {
                receiptData[x.name] = x.value;
            });

            receiptData.details = createAllocation();

            // Get selected checkers from modal checkboxes
            var selectedCheckers = [];
            $('#checkersList tbody input.checker-checkbox:checked').each(function () {
                selectedCheckers.push($(this).val());
            });

            if (selectedCheckers.length === 0) {
                Swal.fire({
                    title: 'Warning',
                    text: 'Please select at least one checker to assign the task.',
                    icon: 'warning'
                });
                return;
            }

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/home/resubmitTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    taskId: mck_id_no,
                    taskType: "RC",
                    updatedData: receiptData,
                    checkerIds: selectedCheckers
                }),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Task assigned and submitted successfully!',
                        icon: 'success'
                    });
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: 'Failed to assign and submit the task: ' + jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        }


        function loadEligibleCheckers(searchParam) {
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/home/getEligibleCheckers', // Endpoint for getting eligible checkers
                type: 'GET',
                data: {permissionName: 'CREATE_RECEIPT', searchParam: searchParam },
                success: function (response) {
                    // Populate checkboxes with eligible checkers
                    $("#checkersList tbody").each(function() {
                        $(this).remove();
                    });
                    for(var res in response) {
                        var absaNo = response[res].absaNo !== null ? response[res].absaNo : '';
                        var markup = "<tr>" +
                            // "<td><input type='hidden' id='checker-id'></td>" +
                            "<td><input type='checkbox'  class='checker-checkbox' value='"+ response[res].id +"' id='" + response[res].id + "'></td><td>" + response[res].username + "</td><td>" + absaNo + "</td>"+
                            "</tr>";
                        $('#checkersList').append(markup)

                    }
                    $('#assignCheckerModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        }


        function agentLov() {
            if ($("#insurance-div").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "insurance-div",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#insurance-id").val(e.added.acctId);
                        $('#insurance-name').val(e.added.name);
                        $("#rct-detail-tbl > tbody").empty();
                    },
                    formatResult: function (a) {
                        return a.name
                    },
                    formatSelection: function (a) {
                        return a.name
                    },
                    initSelection: function (element, callback) {
                        var code = $('#insurance-id').val();
                        var name = $('#insurance-name').val();
                        var data = {name:name,acctId:code};
                        callback(data);
                    },
                    id: "acctId",
                    placeholder: "Select Insurance Company",
                });
            }
        }

        function collectionAcctsLov() {
            if ($("#coll-div").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "coll-div",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#coll-id").val(e.added.caId);
                        $("#coll-name").val(e.added.name);
                        if (e.added.bankBranches)
                            $("#bank-desc").text(e.added.bankBranches.branchName + " - " + e.added.bankBranches.bank.bankName);
                        else $("#bank-desc").text("No Bank Account");
                        $("#currency-desc").text(e.added.currencies.curName);
                        $("#pymt-mode").text(e.added.paymentModes.pmDesc);
                    },
                    formatResult: function (a) {
                        return a.name
                    },
                    formatSelection: function (a) {
                        return a.name
                    },
                    initSelection: function (element, callback) {
                        var code = $('#coll-id').val();
                        var name = $('#coll-name').val();
                        var data = {name:name,caId:code};
                        callback(data);
                    },
                    id: "caId",
                    placeholder: "Select Collection Account",
                    width: "220px"
                });
            }
        }

        function populateUserBranches() {
            if ($("#brn-frm").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "brn-frm",
                    sort: 'obName',
                    change: function (e, a, v) {
                        $("#brn-id").val(e.added.obId);
                        $('#branch-name').val(e.added.obName);
                    },
                    formatResult: function (a) {
                        return a.obName;
                    },
                    formatSelection: function (a) {
                        return a.obName;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#brn-id').val();
                        var name = $('#branch-name').val();
                        var data = {obName:name,obId:code};
                        callback(data);

                    },
                    id: "obId",
                    width: "250px",
                    placeholder: "Select Branch"

                });
            }
        }

        function createAllocation() {
            var arr = [];
            $('#rct-detail-tbl > tbody  > tr').each(
                function () {
                    var data = {};
                    $(this).find(":input[type='text'],:input[type='hidden']").serializeArray()
                        .map(function (x) {
                            if (x.value !== null && x.value.trim() !=='') {
                                data[x.name] = x.value;
                            }
                        });
                    arr.push(data);
                });
            return arr
        }

        function isNullOrUndefined(value) {
            if (value === undefined || value === null || value === '') {
                return -2000;
            } else return value;
        }

        function addReceiptRecord() {
            // Use a selector that will select all the rows and take the length.
            // Note: this approach also counts all trs of every nested table!
            var row_index = $('#rct-detail-tbl tr').length;
            if (row_index > 0) {
                $("#rec-type").attr('disabled', true);
            }

            var transType = $("#rec-type").val();
            if (transType === 'N') {
                var trans = '';
                trans = '<input type="hidden" id="cr-debit-code' + row_index + '" name="transNo">' +
                    '<input type="hidden" id="cr-temp-code' + row_index + '" name="transTempNo">' +
                    '<input type="hidden" id="cr-ref' + row_index + '" >' +
                    '<input type="hidden" id="cr-control-acc' + row_index + '" >' +
                    '<input type="hidden" id="cr-client-name' + row_index + '" >' +
                    ' <div id="debit-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/uw/receipts/selclienttrans" </div> ';
                var markup = "<tr><td>" + trans + "</td><td><p class='form-control-static' id='rec-policy-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-date-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-client-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-balance-" + row_index + "'> </p></td>" +
                    "<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='rctAmount'/></td><td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
                $("#rct-detail-tbl").append(markup);
                $('[id^=rctamt-]').number(true, 2);
                Select2Builder.initAjaxSelect2({
                    containerId: "debit-frm" + row_index,
                    sort: 'refNo',
                    counter: row_index,
                    change: function (e, a, v) {
                        $.each($(this), function () {
                            var transId = (e.added.transno) && e.added.transno !== 0 ? e.added.transno : null;
                            var tempId = (e.added.transTempNo) ? e.added.transTempNo : null;
                            var key = Object.keys(this)[2];
                            var value = this[key];
                            drId = "#cr-debit-code" + value;
                            drIdTemp = "#cr-temp-code" + value;
                            controlAcct = "#cr-control-acc" + value;
                            client = "#cr-v"
                            $(drId).val(transId);
                            $(drIdTemp).val(tempId);
                            $("#rec-policy-" + value).text(e.added.polNo);
                            $("#trans-date-" + value).text(moment(e.added.transDate).format('DD/MM/YYYY'));
                            $("#trans-client-" + value).text(e.added.client);
                            $("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.balance));
                            $("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.balance));
                            refNo = e.added.refNo;
                            controlAcct = e.added.controlAcc;
                            client = e.added.client;
                        });
                    },
                    formatResult: function (a) {
                        return a.refNo + ' - ' + a.controlAcc + ' - ' + a.client;
                    },
                    formatSelection: function (a) {
                        return a.refNo + ' - ' + a.controlAcc + ' - ' + a.client;
                    },
                    initSelection: function (element, callback) {
                        //var row_index = $(element).data('counter');
                        var data = {
                            transno: $("#cr-debit-code" + row_index).val(),
                            refNo: $("#cr-ref" + row_index).val(),
                            controlAcc: $("#cr-control-acc" + row_index).val(),
                            client:$("#cr-client-name" + row_index).val()
                        };
                        callback(data);
                    },
                    id: "transno",
                    width: "300px",
                    params: {acctId: isNullOrUndefined($("#insurance-id").val())},
                    placeholder: "Select Transaction"

                });

            } else if (transType === 'COM') {
                trans = '<input type="hidden" id="cr-debit-code' + row_index + '" name="transNo">' +
                    '  <div id="debit-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/uw/receipts/creditorcommtrans" </div> ';
                var markup = "<tr><td>" + trans + "</td><td><p class='form-control-static' id='rec-policy-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-date-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-client-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-balance-" + row_index + "'> </p></td>" +
                    "<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='rctAmount'/></td><td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
                $("#rct-detail-tbl").append(markup);
                $('[id^=rctamt-]').number(true, 2);
                Select2Builder.initAjaxSelect2({
                    containerId: "debit-frm" + row_index,
                    sort: 'refNo',
                    counter: row_index,
                    change: function (e, a, v) {
                        $.each($(this), function () {
                            var key = Object.keys(this)[2];
                            var value = this[key];
                            var drId = "#cr-debit-code" + value;
                            $(drId).val(e.added.transno);
                            $("#rec-policy-" + value).text(e.added.narrations);
                            $("#trans-date-" + value).text(moment(e.added.transDate).format('DD/MM/YYYY'));
                            $("#trans-client-" + value).text(e.added.payeeName);
                            $("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.balance));
                            $("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.balance));
                        });
                    },
                    formatResult: function (a) {
                        return a.refNo;
                    },
                    formatSelection: function (a) {
                        return a.refNo;
                    },
                    initSelection: function (element, callback) {

                    },
                    id: "transno",
                    width: "300px",
                    placeholder: "Select Transaction",
                    params: {acctId: $("#insurance-id").val()},

                });
            } else {
                trans = '<input type="hidden" id="cr-debit-code' + row_index + '" name="transNo">' +
                    '  <div id="debit-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/uw/receipts/lifepolicies" </div> ';
                var markup = "<tr><td>" + trans + "</td><td><p class='form-control-static' id='rec-policy-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-date-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-client-" + row_index + "'> </p></td>" +
                    "<td><p class='form-control-static' id='trans-balance-" + row_index + "'> </p></td>" +
                    "<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='rctAmount'/></td><td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
                $("#rct-detail-tbl").append(markup);
                $('[id^=rctamt-]').number(true, 2);
                Select2Builder.initAjaxSelect2({
                    containerId: "debit-frm" + row_index,
                    sort: 'refNo',
                    counter: row_index,
                    change: function (e, a, v) {
                        $.each($(this), function () {
                            var key = Object.keys(this)[2];
                            var value = this[key];
                            var drId = "#cr-debit-code" + value;
                            $(drId).val(e.added.policyId);
                            //$("#rec-policy-" + value).text(e.added.polNo);
                            if (e.added.polNo) {
                                $("#rec-policy-" + value).text(e.added.polNo);
                            } else {
                                $("#rec-policy-" + value).text(e.added.proposalNo);
                            }
                            if (e.added.authDate) {
                                $("#trans-date-" + value).text(moment(e.added.authDate).format('DD/MM/YYYY'));
                            } else {
                                $("#trans-date-" + value).text(moment(e.added.polCreateddt).format('DD/MM/YYYY'));
                            }
                            //$("#trans-date-" + value).text(moment(e.added.authDate).format('DD/MM/YYYY'));
                            $("#trans-client-" + value).text(e.added.client.fname + " " + e.added.client.otherNames);
                            if (e.added.negotiatedPremium){
                            $("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.negotiatedPremium));
                            $("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.negotiatedPremium));
                            }else{
                            $("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.netPrem));
                            $("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.netPrem));
                            }
                        });
                    },
                    formatResult: function (a) {
                        if (a.polNo)
                            return a.polNo;
                        else return a.proposalNo;
                    },
                    formatSelection: function (a) {
                        if (a.polNo)
                            return a.polNo;
                        else return a.proposalNo;
                    },
                    initSelection: function (element, callback) {

                    },
                    id: "policyId",
                    width: "300px",
                    placeholder: "Select Transaction"

                });
            }

        }
    });
</script>