<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="x_panel" id="acct_model">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Intermediaries List</h2>
        <div class="clearfix"></div>
    </div>

    <div class="x_content">
        <form id="acct-type-form" class="form-horizontal">
            <div class="item form-group form-required">
                <div class="col-md-6">
                    <label for="acc-id" class="col-md-5 label-align">
                        Account Type</label>

                    <div class="col-md-7">
                        <input type="hidden" id="acc-id" name="acctTypeId"/>
                        <input type="hidden" id="acc-name">
                        <input type="hidden" id="acc-type-desc">
                        <div id="accounttypes" class="form-control"
                             select2-url="<c:url value="/protected/setups/selAcctTypes"/>">

                        </div>

                    </div>
                </div>

            </div>
        </form>
    </div>

    <%--	 <a href="<c:url value='/protected/setups/acctsform'/> " class="btn btn-info float-right">New</a>--%>
    <div class="x_panel">
        <div class="x_title">
            <h2>Intermediaries</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                </li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <button class="btn btn-primary btn btn-primary" id="btn-import-data">Import SubAgent/Marketer Data</button>
            <a href="<c:url value='/protected/setups/acctsform'/> " class="btn btn-info float-right">New</a>
            <div class="spacer"></div>
            <div class="table-responsive">
                <table id="acctbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Intermediary Name</th>
                        <th>Phone</th>
                        <th>Pin No</th>
                        <th>Status</th>
                        <th>Modified By</th>
                        <th>Intermediary Type</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="importDataModal" tabindex="-1" role="dialog"
     aria-labelledby="importDataModalLabel" aria-hidden="true">
    <form id="data-upload-form" class="form-horizontal" enctype="multipart/form-data">
        <div class="modal-dialog modal-md">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="importRisksModalLabel">Import Data</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <div class="item form-group">
                        <label for="file-avatar" class="label-align col-md-5">
                            Excel File<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input name="file" type="file" id="file-avatar" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-success" style="margin-right: 10px;" value="Upload">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>

    $(function () {

        $(document).ready(function () {
            AccountDef.init();
        });

    });

    var AccountDef = (function ($) {
        'use strict';

        var confirmAccountDel = function (button) {
            var account = JSON.parse(decodeURI($(button).data("account")));
            bootbox.confirm("Are you sure want to delete " + account["name"] + "?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'GET',
                        url: 'deleteAccount/' + account["acctId"],
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            Swal.fire({
                                title: 'Success',
                                text:"Record deleted Successfully",
                                icon: 'success',
                            });
                            $('#acctbl').DataTable().ajax.reload();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            bootbox.alert(jqXHR.responseText);
                        }
                    });
                }

            });
        };

        var importData = function () {
            // Bind the click event to the Import button
            $("#btn-import-data").on('click', function () {
                // Hide the account form
                $("#account-form").hide();

                // Show the import modal
                $('#importDataModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            });

            // Restore the account form when the modal is closed
            $('#importDataModal').on('hidden.bs.modal', function () {
                // Show the account form when the modal is closed
                $("#account-form").show();
            });

            // Validate and handle form submission
            var $form = $("#data-upload-form");
            var validator = $form.validate();

            $('form#data-upload-form').submit(function (e) {
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }
                $("#btn-import-data").prop('disabled', true);

                var data = new FormData(this);
                data.append('file', $('#file-avatar')[0].files[0]);

                $.ajax({
                    url: 'uploadSubAgent',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        $('#importDataModal').modal('hide'); // Hide the modal on success
                        Swal.fire({
                            title: 'Success',
                            text: 'Data Uploaded Successfully',
                            icon: 'success',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.href = SERVLET_CONTEXT + '/protected/setups/accts';
                            }
                        });

                        // Enable the import button
                        $("#btn-import-data").prop('disabled', false);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                        $("#btn-import-data").prop('disabled', false);
                    }
                });
            });
        };


        var createAccounts = function () {
            var url = "allaccounts/" + $('#acc-id').val();
            var baseOptions = UTILITIES.extendsOpts({
                "ajaxUrl": url,
                "columns": [
                    {"data": "shtDesc"},
                    {"data": "name"},
                    {"data": "phoneNo"},
                    {"data": "pinNo"},
                    {
                        "data": "status",
                        "render": function (data, type, full, meta) {
                            if (!full.status || full.status === "I") {
                                return "Inactive";
                            } else if (full.status === "A")
                                return "Active";
                            else if (full.status === "DA")
                                return "Deactivated";
                            else if (full.status === "D")
                                return "Draft";
                        }
                    },
                    {"data": "updatedBy"},
                    {"data": "accountTypeName"},
                    {
                        "data": "acctId",
                        "render": function (data, type, full, meta) {
                            return '<form action="viewAcctForm" method="post"><input type="hidden" name="id" value=' + full.acctId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        }
                    },
                    {
                        "data": "acctId",
                        "render": function (data, type, full, meta) {
                            return '<input type="button" class="btn btn-danger btn btn-info btn-sm" data-account=' + encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="AccountDef.confirmAccountDel(this);"/>';
                        }
                    },
                ]
            });

            baseOptions.ajax = {
                'url': url,
                'data': function(d) {
                    console.log("DataTables request data:", d);
                    if (d.search && d.search.value) {
                        d.search.value = d.search.value.toUpperCase();
                    }
                    return d;
                },
                'dataSrc': function(json) {
                    console.log("Server response:", json);
                    return json.data;
                }
            };

            return $('#acctbl').DataTable(baseOptions);
        };

        var createAccountTypeSelect = function () {
            if ($("#accounttypes").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "accounttypes",
                    sort: 'accName',
                    change: function (e, a, v) {
                        console.log("Selected account type:", e.added); // Debug log
                        $("#acc-id").val(e.added.accId);
                        createAccounts();
                    },
                    formatResult: function (a) {
                        return a.accName
                    },
                    formatSelection: function (a) {
                        return a.accName
                    },
                    initSelection: function (element, callback) {

                    },
                    id: "accId",
                    placeholder: "Select Account Type",
                });
            }
        };


        var init = function () {
            createAccountTypeSelect();
            importData();
        };

        return {
            init: init,
            confirmAccountDel: confirmAccountDel,
        };

    })(jQuery);
</script>