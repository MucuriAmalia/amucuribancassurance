<%--
  Created by IntelliJ IDEA.
  User: orito
  Date: 26/02/2025
  Time: 10:10
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>

<div class="x_panel" id="acct_model">

    <!-- Date and Action Buttons -->
    <div class="row mb-3">
        <div class="col-md-3">
            <label>As At Date:</label>
            <input type="date" id="asAtDate" class="form-control">
        </div>
        <div class="col-md-2" style="margin-top: 24px;">
            <button id="applyFilter" class="btn btn-primary">Apply Filter</button>
        </div>
        <div class="col-md-2" style="margin-top: 24px;">
            <select id="reportFormat" class="form-control">
                <option value="pdf">PDF</option>
                <option value="csv">CSV</option>
                <option value="xlsx">Excel</option>
            </select>
        </div>
        <div class="col-md-2" style="margin-top: 24px;">
            <button type="submit" id="generateReportGlListings" class="btn btn-primary">
                <i class="fa fa-print"></i> Report
            </button>
        </div>
    </div>

    <div>
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Account Balances</h2>
            <div class="clearfix"></div>
        </div>
        <hr>
        <table id="openingBalanceTbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Account No</th>
                <th>Account Name</th>
                <th>Balance</th>
            </tr>
            </thead>
        </table>
    </div>

    <hr>
    <div>
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> GL Transactions</h2>
            <div class="clearfix"></div>
        </div>
        <hr>
        <table id="glTransactionsTbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>GL Acc</th>
                <th>GL Year</th>
                <th>GL Month</th>
                <th>Auth Date</th>
                <th>GL Trans Type</th>
                <th>GL D/C</th>
                <th>GL Amount</th>
            </tr>
            </thead>
            <tfoot>
            <tr>
                <th colspan="6" style="text-align:right">Total:</th>
                <th></th>
            </tr>
            </tfoot>
        </table>
    </div>
</div>

<script>
    $(document).ready(function() {
        var ACCOUNTS = ACCOUNTS || {};
        var glTable;

        ACCOUNTS.createOpeningBalances = function() {
            return $('#openingBalanceTbl').DataTable({
                processing: true,
                serverSide: true,
                scrollCollapse: true,
                scrollY: '400px',
                ajax: {
                    url: "openingbalances"
                },
                lengthMenu: [[20, 30, 40], [20, 30, 40]],
                pageLength: 20,
                destroy: true,
                select: true,
                columns: [
                    {
                        data: "accountNo",
                        render: function(data) {
                            return '<a href="#" class="account-link">' + data + '</a>';
                        }
                    },
                    { data: "accountName" },
                    // {
                    //     "data": "balance",
                    //     "render": function (data, type, full, meta) {
                    //         return UTILITIES.currencyFormat(full.balance);
                    //     }
                    // },
                    {
                        data: "currBalance",
                        render: function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.currBalance);
                        }
                    }
                ]
            });
        };

        function glTransactionsTable(accountNo) {
            if (glTable) {
                glTable.destroy();
            }

            glTable = $('#glTransactionsTbl').DataTable({
                processing: true,
                serverSide: true,
                scrollCollapse: true,
                scrollY: '400px',
                ajax: {
                    url: 'gltransactions',
                    data: function(d) {
                        d.accountNo = accountNo;
                        d.asAtDate = $('#asAtDate').val();
                    }
                },
                lengthMenu: [[20, 30, 40], [20, 30, 40]],
                pageLength: 20,
                destroy: true,
                columns: [
                    {
                        data: "glAcc",
                        render: function(data, type, full) {
                            return full.glAcc ? full.glAcc.code : '';
                        }
                    },
                    { data: "glYear" },
                    { data: "glMonth" },
                    {
                        data: "authDate",
                        render: function(data) {
                            return data ? new Date(data).toLocaleDateString() : '';
                        }
                    },
                    { data: "transType" },
                    { data: "gldc" },
                    {
                        data: "amount",
                        render: function(data, type, full) {
                            return UTILITIES.currencyFormat(full.amount);
                        }
                    }
                ],
                footerCallback: function(row, data, start, end, display) {
                    var total = this.api()
                        .column(6, { page: 'current' })
                        .data()
                        .reduce(function(acc, val) {
                            return acc + (typeof val === 'string' ? parseFloat(val.replace(/[^\d.-]/g, '')) : val);
                        }, 0);
                    $(this.api().column(6).footer()).html(UTILITIES.currencyFormat(total));
                }
            });
            return glTable;
        }

        // Initialize Opening Balances
        ACCOUNTS.createOpeningBalances();

        // Hide GL Transactions by default
        $('#glTransactionsTbl').closest('div').hide();

        // Handle account click
        $('#openingBalanceTbl').on('click', '.account-link', function(e) {
            e.preventDefault();
            var rowData = $('#openingBalanceTbl').DataTable().row($(this).closest('tr')).data();
            $('#glTransactionsTbl').closest('div').show();
            glTable = glTransactionsTable(rowData.accountNo);
        });

        // Handle date filter
        $('#applyFilter').on('click', function() {
            if (glTable) {
                glTable.ajax.reload();
            }
        });

        // Handle report generation
        $('#generateReportGlListings').on('click', function() {
            var asAtDate = $('#asAtDate').val();
            var selectedAccount = $('#openingBalanceTbl').DataTable().row('.selected').data();
            var format = $('#reportFormat').val();

            if (!asAtDate) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Date Required',
                    text: 'Please select a date',
                });
                return;
            }

            // if (!selectedAccount) {
            //     Swal.fire({
            //         icon: 'warning',
            //         title: 'Account Required',
            //         text: 'Please select an account from the table',
            //     });
            //     return;
            // }


            // Format date as dd/MM/yyyy to match SimpleDateFormat in controller
            var dateParts = asAtDate.split('-');
            var formattedDate = dateParts[2] + '/' + dateParts[1] + '/' + dateParts[0];

            // Create form and submit
            const form = document.createElement('form');
            form.method = 'post';
            form.action = 'rpt_gltransactions';
            form.target = '_blank';

            const dateInput = document.createElement('input');
            dateInput.type = 'hidden';
            dateInput.name = 'asAtDate';
            dateInput.value = formattedDate;
            form.appendChild(dateInput);

            const accountInput = document.createElement('input');
            accountInput.type = 'hidden';
            accountInput.name = 'accountNo';
            accountInput.value = selectedAccount ? selectedAccount.accountNo : '';
            form.appendChild(accountInput);

            const formatInput = document.createElement('input');
            formatInput.type = 'hidden';
            formatInput.name = 'format';
            formatInput.value = format;
            form.appendChild(formatInput);

            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        });
    });
</script>
