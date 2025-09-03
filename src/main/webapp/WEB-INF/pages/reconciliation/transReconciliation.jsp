<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<div class="x_panel">
  <div class="x_title">
    <h2><i class="fa fa-bars"></i> Reconciliation</h2>
    <ul class="nav navbar-right panel_toolbox">
      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
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
              <input type='text' class="form-control float-right" name="wefDate"
                     id="from-date" required/>
              <div class="input-group-addon">
                <span class="fa fa-calendar"></span>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-xs-12">
          <label for="wet-date" class="col-md-5 label-align">Date To
          </label>

          <div class="col-md-7 col-xs-12">
            <div class='input-group date datepicker-input' id="cover-to-date">
              <input type='text' class="form-control float-right" name="wetDate"
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
                 select2-url="<c:url value="/protected/setups/binders/selAgentsAccounts"/>" >
            </div>
          </div>
        </div>
        <div class="col-md-6 col-xs-12">
          <label for="unified-search" class="col-md-5 label-align">Risk Note No/ Recon Status</label>
          <div class="col-md-7 col-xs-12">
            <input type='text' class="form-control pull-right" id="unified-search"
                   placeholder="Enter Risk Note No/ Recon Status" />
          </div>
        </div>
      </div>
      <div class="item form-group">
        <input type="button" class="btn btn-primary float-right"
               style="margin-right: 10px;" value="Search"
               id="btn-search-trans">
      </div>
    </form>
    <div class="clearfix"></div>
  </div>
  <div class="x_panel">
    <div class="x_title">
      <h2>Reconciliation Data</h2>
      <ul class="nav navbar-right panel_toolbox">
        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
      </ul>
      <div class="clearfix"></div>
    </div>
    <div class="x_content">
      <div class="card-box table-responsive">
        <button class="btn btn-primary btn btn-primary" id="btn-import-data">Import Data</button>
        <a href="bulktransreconciletemplate" class="btn btn-success">
          <i class="fa fa-file-excel-o"></i> Download Template
        </a>
        <table id="reconciliationTable" class="table table-striped" style="width: 100%">
          <thead>
          <tr>
            <th><input type="checkbox" id="selectAll"></th> <!-- Checkbox column -->
            <th>Policy</th>
            <th>Client</th>
            <th>Risk Note Number</th>
            <th>Underwriter Policy Number</th>
            <th>Underwriter Trans Code</th>
            <th>Premium</th>
            <th>Settlement</th>
            <th>Balance</th>
          </tr>
          </thead>
          <tbody>
          </tbody>
        </table>
      </div>
      <button id="reconcileButton" class="btn btn-primary float-right">Reconcile</button>
    </div>
  </div>

  <div class="x_panel">
    <div class="x_title d-flex justify-content-between align-items-center">
      <h2>Reconciled Data</h2>
      <ul class="nav navbar-right panel_toolbox">
        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
      </ul>
    </div>

    <!-- Flex container for right-aligned controls -->
    <div class="d-flex justify-content-end align-items-center mb-3" style="gap: 10px;">
      <select id="reportFormat" class="form-control" style="width: auto;">
        <option value="pdf">PDF</option>
        <option value="csv">CSV</option>
        <option value="xlsx">Excel</option>
      </select>

      <button class="btn btn-primary" id="btn-print-report">Print Report</button>

      <button class="btn btn-danger" id="btn-delete-reconciled" disabled>
        <i class="fa fa-trash"></i> Delete Reconciled Data
      </button>
    </div>
  </div>

  <div class="x_content">
    <div class="card-box table-responsive">
      <table id="reconciliation_child" class="table table-striped" style="width: 100%">
        <thead>
        <tr>
          <th>Policy</th>
          <th>Client</th>
          <th>Risk Note Number</th>
          <th>Underwriter Policy Number</th>
          <th>Underwriter Trans Code</th>
          <th>Premium</th>
          <th>Settlement</th>
          <th>Balance</th>
          <th>Status</th>
          <th>Unmatched Entries</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
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


<script type="text/javascript">
  var uploadUrl = '<c:url value="/protected/reconciliation/uploadExcelFile"/>';
  var dataUrl = '<c:url value="/protected/reconciliation/transReconciliationData"/>';


  var accountCodeStored, dateFromStored, dateToStored, currentUnifiedSearch = '';

  $(document).ready(function () {
    $(".datepicker-input").each(function () {
      $(this).datetimepicker({
        format: 'DD/MM/YYYY'
      });
    });
    createAccountsForSel();
    importData();

    $('#reconcileButton').prop('disabled', true);
    $('#btn-print-report').prop('disabled', true);

    $('#selectAll').on('click', function () {
      var isChecked = $(this).prop('checked');
      $('.row-checkbox').prop('checked', isChecked);
    });

    $('#btn-search-trans').on('click', function () {
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

      // STORE THE UNIFIED SEARCH VALUE GLOBALLY
      currentUnifiedSearch = $("#unified-search").val() || '';

      console.log("Search initiated with unified search:", currentUnifiedSearch); // Debug log

      var reconDataTable = getTransReconciliationData(
              $("#agent-search-number").val(),
              $("#from-date").val(),
              $("#wet-date").val(),
              currentUnifiedSearch
      );

      var reconTable = getTransReconciledData(
              $("#agent-search-number").val(),
              $("#from-date").val(),
              $("#wet-date").val(),
              currentUnifiedSearch
      );

      reconDataTable.on('draw', function () {
        var data = reconDataTable.rows().data().length;
        $('#reconcileButton').prop('disabled', data === 0);
      });

      reconTable.on('draw', function () {
        var data = reconTable.rows().data().length;
        $('#btn-print-report').prop('disabled', data === 0);
        // Update delete button state after search
        toggleDeleteButton();
      })
    });
  });

  function getTransReconciliationData(accountCode, dateFrom, dateTo, unifiedSearch) {
    if ($.fn.DataTable.isDataTable('#reconciliationTable')) {
      $('#reconciliationTable').DataTable().clear().destroy();
    }

    var table = $('#reconciliationTable').DataTable({
      processing: true,
      serverSide: true,
      searching: false,
      lengthMenu: [5, 10, 25, 50],
      pageLength: 10,
      "ajax": {
        "url": dataUrl,
        "data": {
          accountCode: accountCode,
          dateFrom: dateFrom,
          dateTo: dateTo,
          unifiedSearch: unifiedSearch
        }
      },
      "columns": [
        {
          "data": null,
          "className": 'dt-body-center',
          "orderable": false,
          "render": function (data, type, row) {
            return '<input type="checkbox" class="row-checkbox" />';
          }
        },
        {"data": "policyNumber"},
        {"data": "clientName"},
        {"data": "riskNoteNumber"},
        {"data": "underwriterPolicyNumber"},
        {"data": "underwriterTransCode"},
        {"data": "premium"},
        {"data": "settlement"},
        {"data": "balance"},
      ],
      "order": [[1, 'asc']]
    });

    transReconcileData(table);
    return table;
  }

  function importData() {
    // Remove existing handlers before binding new ones
    $("#btn-import-data").off('click').on('click', function () {
      $('#importDataModal').modal({
        backdrop: 'static',
        keyboard: true
      });
    });

    var $form = $("#data-upload-form");
    var validator = $form.validate();
    $('form#data-upload-form').off('submit').on('submit', function (e) {
      e.preventDefault();

      if (!$form.valid()) {
        return;
      }

      // Disable button during upload
      $("#btn-import-data").prop('disabled', true);

      var data = new FormData(this);
      data.append('file', $('#file-avatar')[0].files[0]);

      $.ajax({
        url: 'uploadExcelFile',
        type: 'POST',
        data: data,
        processData: false,
        contentType: false,
        success: function (s) {
          $('#importDataModal').modal('hide');
          Swal.fire({
            title: 'Success',
            text: 'Data Uploaded Successfully',
            icon: 'success'
          });
          $('#reconciliationTable').DataTable().ajax.reload();

          // Re-enable button after success
          $("#btn-import-data").prop('disabled', false);
        },
        error: function (jqXHR, textStatus, errorThrown) {
          Swal.fire({
            title: 'Error',
            text: jqXHR.responseText,
            icon: 'error'
          });

          // Re-enable button after error
          $("#btn-import-data").prop('disabled', false);
        }
      });
    });
  }
  var reconcileTable;

  $(document).ready(function() {
    $('#reconcileButton').click(function () {
      if (!reconcileTable) {
        console.error('Table not initialized');
        return;
      }

      var selectedRows = [];

      $('#reconciliationTable tbody input.row-checkbox:checked').each(function () {
        var rowData = reconcileTable.row($(this).closest('tr')).data();
        selectedRows.push({
          policyNumber: rowData.policyNumber,
          clientName: rowData.clientName,
          riskNoteNumber: rowData.riskNoteNumber,
          underwriterPolicyNumber: rowData.underwriterPolicyNumber,
          underwriterTransCode: rowData.underwriterTransCode,
          premium: rowData.premium,
          settlement: rowData.settlement,
          balance: rowData.balance
        });
      });

      if (selectedRows.length === 0) {
        bootbox.alert('No Record Selected to Reconcile.');
        return;
      }

      $('#reconcileButton').prop('disabled', true).text('Reconciling...');

      $.ajax({
        url: 'transReconcile',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(selectedRows),
        success: function (response) {
          Swal.fire({
            title: 'Success',
            text: 'Data Reconciled Successfully',
            icon: 'success'
          });
          $('#reconcileButton').prop('disabled', false).text('Reconcile');
          $('#reconciliationTable').DataTable().ajax.reload();
          $('#reconciliation_child').DataTable().ajax.reload();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          Swal.fire({
            title: 'Error',
            text: jqXHR.responseText,
            icon: 'error'
          });
          $('#reconcileButton').prop('disabled', false).text('Reconcile');
        }
      });
    });
  });

  function transReconcileData(table) {
    reconcileTable = table;
  }

  function getTransReconciledData(accountCode, dateFrom, dateTo, unifiedSearch) {
    // Store the parameters globally for use in the print function
    accountCodeStored = accountCode;
    dateFromStored = dateFrom;
    dateToStored = dateTo;

    if ($.fn.DataTable.isDataTable('#reconciliation_child')) {
      $('#reconciliation_child').DataTable().clear().destroy();
    }

    var table = $('#reconciliation_child').DataTable({
      processing: true,
      serverSide: true,
      searching: false,
      lengthMenu: [5, 10, 25, 50],
      pageLength: 10,
      "ajax": {
        "url": "transReconciledData",
        "data": {
          accountCode: accountCode,
          dateFrom: dateFrom,
          dateTo: dateTo,
          unifiedSearch: unifiedSearch
        }
      },
      "columns": [
        {"data": "policyNumber"},
        {"data": "clientName"},
        {"data": "riskNoteNumber"},
        {"data": "underwriterPolicyNumber"},
        {"data": "underwriterTransCode"},
        {"data": "premium"},
        {"data": "settlement"},
        {"data": "balance"},
        {"data": "status"},
        {
          "data": "unmatchedEntries",
          "render": function(data, type, row) {
            return (data === 'None') ? '' : data;
          }
        }
      ],
      "order": [[1, 'asc']]
    });

    return table;
  }

  $(document).ready(function () {
    $('#btn-print-report').click(function () {
      if (!accountCodeStored || !dateFromStored || !dateToStored) {
        Swal.fire({
          icon: 'warning',
          title: 'Missing Parameters',
          text: 'Please ensure account, date from, and date to are selected.',
        });
        return;
      }

      // Get the selected format from the dropdown
      const selectedFormat = $('#reportFormat').val();
      console.log('Selected format:', selectedFormat);

      // Create and submit a form (POST)
      const form = document.createElement('form');
      form.method = 'post';
      form.action = 'rpt_trans_reconciliation.pdf';
      form.target = '_blank';

      const accountInput = document.createElement('input');
      accountInput.type = 'hidden';
      accountInput.name = 'accountCode';
      accountInput.value = accountCodeStored;
      form.appendChild(accountInput);

      const fromDateInput = document.createElement('input');
      fromDateInput.type = 'hidden';
      fromDateInput.name = 'dateFrom';
      fromDateInput.value = dateFromStored;
      form.appendChild(fromDateInput);

      const toDateInput = document.createElement('input');
      toDateInput.type = 'hidden';
      toDateInput.name = 'dateTo';
      toDateInput.value = dateToStored;
      form.appendChild(toDateInput);

      // Add the format parameter - THIS IS THE KEY ADDITION
      const formatInput = document.createElement('input');
      formatInput.type = 'hidden';
      formatInput.name = 'format';
      formatInput.value = selectedFormat;
      form.appendChild(formatInput);

      // Risk Note Number parameter
      const riskNoteInput = document.createElement('input');
      riskNoteInput.type = 'hidden';
      riskNoteInput.name = 'RiskNoteNumber';
      riskNoteInput.value = currentUnifiedSearch;
      form.appendChild(riskNoteInput);

      // Reconciliation Status parameter
      const reconStatusInput = document.createElement('input');
      reconStatusInput.type = 'hidden';
      reconStatusInput.name = 'ReconStatus';
      reconStatusInput.value = currentUnifiedSearch;
      form.appendChild(reconStatusInput);

      document.body.appendChild(form);
      form.submit();
      document.body.removeChild(form);
    });
  });

  // Add function to toggle delete button state
  function toggleDeleteButton() {
    var accountCode = $("#agent-search-number").val();
    var dateFrom = $("#from-date").val();
    var dateTo = $("#wet-date").val();

    console.log("Toggle delete button - Account:", accountCode, "DateFrom:", dateFrom, "DateTo:", dateTo); // Debug log

    // Enable delete button only if account code and date range are available
    if (accountCode && accountCode.trim() !== '' && dateFrom && dateTo) {
      $('#btn-delete-reconciled').prop('disabled', false);
      console.log("Delete button enabled"); // Debug log
    } else {
      $('#btn-delete-reconciled').prop('disabled', true);
      console.log("Delete button disabled"); // Debug log
    }
  }

  function createAccountsForSel() {
    if ($("#acc-frm").filter("div").html() != undefined) {
      Select2Builder.initAjaxSelect2({
        containerId: "acc-frm",
        sort: 'name',
        change: function (e, a, v) {
          $("#agent-search-number").val(e.added.acctId);
          toggleDeleteButton();
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
      toggleDeleteButton();
    })
  }

  // DELETE BUTTON CLICK HANDLER - Simplified version
  $('#btn-delete-reconciled').click(function() {
    console.log("Delete button clicked"); // Debug log

    var accountCode = $("#agent-search-number").val();
    var dateFrom = $("#from-date").val();
    var dateTo = $("#wet-date").val();
    var unifiedSearch = currentUnifiedSearch || '';

    console.log("Delete parameters:", {
      accountCode: accountCode,
      dateFrom: dateFrom,
      dateTo: dateTo,
      unifiedSearch: unifiedSearch
    }); // Debug log

    var reconciledTable = $('#reconciliation_child').DataTable();
    var rowCount = reconciledTable ? reconciledTable.rows().data().length : 0;

    if (rowCount === 0) {
      Swal.fire({
        title: 'No Data',
        text: 'There is no data to delete. Please perform a search first.',
        icon: 'info',
        confirmButtonText: 'OK'
      });
      return;
    }

    if (!accountCode || accountCode.trim() === '') {
      Swal.fire({
        title: 'No Selection',
        text: 'Please select an intermediary first.',
        icon: 'warning',
        confirmButtonText: 'OK'
      });
      return;
    }

    if (!dateFrom || !dateTo) {
      Swal.fire({
        title: 'Missing Date Range',
        text: 'Please perform a search with date range first.',
        icon: 'warning',
        confirmButtonText: 'OK'
      });
      return;
    }

    var confirmMessage = 'Are you sure you want to delete this reconciliation data?';

    Swal.fire({
      title: 'Are you sure?',
      text: confirmMessage,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      preConfirm: () => {
        return new Promise((resolve) => {
          Swal.showLoading();
          resolve();
        });
      }
    }).then((result) => {
      if (result.isConfirmed) {
        deleteReconciledData(accountCode, dateFrom, dateTo, unifiedSearch);
      }
    });
  });

  function deleteReconciledData(accountCode, dateFrom, dateTo, unifiedSearch) {
    console.log("Starting delete operation with params:", {
      accountCode: accountCode,
      dateFrom: dateFrom,
      dateTo: dateTo,
      unifiedSearch: unifiedSearch
    });

    $('#btn-delete-reconciled').prop('disabled', true);

    $.ajax({
      url: 'deleteTransReconciledData',
      type: 'POST',
      data: {
        accountCode: accountCode,
        dateFrom: dateFrom,
        dateTo: dateTo,
        unifiedSearch: unifiedSearch
      },
      success: function(response) {
        console.log("Delete successful:", response);

        Swal.fire({
          title: 'Deleted!',
          text: response || 'Reconciliation data deleted successfully.',
          icon: 'success',
          timer: 3000,
          timerProgressBar: true
        }).then(() => {
          // Refresh both tables with current search criteria
          $('#reconciliationTable').DataTable().ajax.reload();
          $('#reconciliation_child').DataTable().ajax.reload();
          toggleDeleteButton();
        });
      },
      error: function(jqXHR, textStatus, errorThrown) {
        console.error("Delete error:", jqXHR.responseText); // Debug log
        var errorMessage = jqXHR.responseText || 'Failed to delete reconciliation data';
        Swal.fire({
          title: 'Error',
          text: errorMessage,
          icon: 'error',
          confirmButtonText: 'OK'
        });
        toggleDeleteButton();
      }
    });
  }

</script>