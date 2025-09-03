<%--
  Created by IntelliJ IDEA.
  User: orito
  Date: 25/02/2025
  Time: 09:08
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Audit Trail Logs</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">

        <!-- Add date range filters -->
        <div class="row mb-3">
            <div class="col-md-3">
                <label>From Date:</label>
                <input type="datetime-local" id="dateFrom" class="form-control">
            </div>
            <div class="col-md-3">
                <label>To Date:</label>
                <input type="datetime-local" id="dateTo" class="form-control">
            </div>
            <div class="col-md-2">
                <label>&nbsp;</label>
                <button id="filterDates" class="btn btn-primary form-control">Filter</button>
            </div>
            <div class="col-md-4" style="margin-top: 28px;">
                <div class="input-group">
                    <select id="reportFormat" class="form-control">
                        <option value="pdf">PDF</option>
                        <option value="csv">CSV</option>
                        <option value="xlsx">Excel</option>
                    </select>
                    <div class="input-group-append">
                        <button type="submit" id="generateReport" class="btn btn-primary">
                            <i class="fa fa-print"></i> Generate Report
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Audit table -->
        <div class="table-responsive">
            <table id="audittbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>User Name</th>
                    <th>Resource</th>
                    <th>TimeStamp</th>
                    <th>Ip Address/Host</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        function createSegmentsTable() {
            var table = $('#audittbl').DataTable({
                "processing": true,
                "serverSide": true,
                "ajax": {
                    "url": "auditLogsList",
                    "type": "GET",
                    "data": function (d) {
                        var dateFrom = $('#dateFrom').val();
                        var dateTo = $('#dateTo').val();

                        if (dateFrom) {
                            d.dateFrom = new Date(dateFrom).getTime();
                        }
                        if (dateTo) {
                            // Set time to end of day for 'to' date
                            var toDate = new Date(dateTo);
                            d.dateTo = toDate.getTime();
                        }
                        return d;
                    }
                },
                lengthMenu: [[100, 200, 300], [100, 200, 300]],
                pageLength: 100,
                destroy: true,
                "columns": [
                    { "data": "username" },
                    { "data": "resource" },
                    {
                        "data": "timestamp",
                        "render": function (data) {
                            return new Date(parseInt(data)).toLocaleString();
                        }
                    },
                    { "data": "details" }
                ],
                "order": [[2, "desc"]]
            });
            return table;
        }

        var table = createSegmentsTable();

        $('#generateReport').on('click', function () {
            var dateFrom = $('#dateFrom').val();
            var dateTo = $('#dateTo').val();
            var format = $('#reportFormat').val();

            if (!dateFrom || !dateTo) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Date Required',
                    text: 'Please select at least one date',
                });
                return;
            }

            // Create form and submit
            const form = document.createElement('form');
            form.method = 'post';
            form.action = 'rpt_auditlogs';
            form.target = '_blank';

            // Format dates as timestamps for Java Date
            const fromInput = document.createElement('input');
            fromInput.type = 'hidden';
            fromInput.name = 'dateFrom';
            fromInput.value = new Date(dateFrom).toISOString();

            const toInput = document.createElement('input');
            toInput.type = 'hidden';
            toInput.name = 'dateTo';
            toInput.value = new Date(dateTo).toISOString();

            const formatInput = document.createElement('input');
            formatInput.type = 'hidden';
            formatInput.name = 'format';
            formatInput.value = format;

            form.appendChild(fromInput);
            form.appendChild(toInput);
            form.appendChild(formatInput);
            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        });

        $('#filterDates').on('click', function () {
            var dateFrom = $('#dateFrom').val();
            var dateTo = $('#dateTo').val();

            if (!dateFrom && !dateTo) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Date Required',
                    text: 'Please select at least one date',

                });
                return;
            }

            table.draw(); // This will trigger a new server request with the date parameters
        });

    });
</script>

