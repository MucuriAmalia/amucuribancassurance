<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Approved Bulk Debit Report</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="panel panel-default">
            <div class="panel-body">
                <form id="bulkReportForm">
                    <div class="row">
                        <div class="col-md-2" style="margin-top: 24px;">
                            <label for="dateFrom">Date From <span style="color:red">*</span></label>
                            <input type="date" id="dateFrom" name="dateFrom" class="form-control" required>
                        </div>
                        <div class="col-md-2" style="margin-top: 24px;">
                            <label for="dateTo">Date To <span style="color:red">*</span></label>
                            <input type="date" id="dateTo" name="dateTo" class="form-control" required>
                        </div>
                        <div class="col-md-2" style="margin-top: 24px;">
                            <label for="agent">Agent</label>
                            <select id="agent" name="agent" class="form-control">
                                <option value="">All Agents</option>
                            </select>
                        </div>
                        <div class="col-md-2" style="margin-top: 24px;">
                            <label for="reportFormat">Format</label>
                            <select id="reportFormat" name="format" class="form-control">
                                <option value="pdf">PDF</option>
                                <option value="csv">CSV</option>
                                <option value="xlsx">Excel</option>
                            </select>
                        </div>
                        <div class="col-md-2" style="margin-top: 48px;">
                            <button type="submit" id="generateReportBulkUpload" class="btn btn-primary">
                                <i class="fa fa-print"></i> Report
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function formatDate(inputDate) {
        const parts = inputDate.split('-');
        return parts[2] + '/' + parts[1] + '/' + parts[0]; // dd/MM/yyyy
    }

    $(document).ready(function () {
        var  url = SERVLET_CONTEXT + '/protected/setups/selParentAccts';
        // Load agents
        $.ajax({
            url:  url,
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                const agents = data.content || [];
                agents.forEach(agent => {
                    $('#agent').append(
                        $('<option>', {
                            value: agent.acctId,
                            text: agent.name
                        })
                    );
                });
            },
            error: function (xhr, status, error) {
                console.error('Failed to fetch agents:', error);
            }
        });

        // Handle report generation
        $('#generateReportBulkUpload').on('click', function (e) {
            e.preventDefault(); // Prevent default form submission
            var  urlsubmit = SERVLET_CONTEXT + '/protected/accounts/rpt_bulk_uploads';
            const dateFrom = $('#dateFrom').val();
            const dateTo = $('#dateTo').val();
            const selectedAgent = $('#agent').val();
            const format = $('#reportFormat').val();

            if (!dateFrom || !dateTo) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Date Required',
                    text: 'Please select both "Date From" and "Date To"',
                });
                return;
            }

            const form = document.createElement('form');
            form.method = 'post';
            form.action = urlsubmit;
            form.target = '_blank';

            const dateFromInput = document.createElement('input');
            dateFromInput.type = 'hidden';
            dateFromInput.name = 'dateFrom';
            dateFromInput.value = formatDate(dateFrom);
            form.appendChild(dateFromInput);

            const dateToInput = document.createElement('input');
            dateToInput.type = 'hidden';
            dateToInput.name = 'dateTo';
            dateToInput.value = formatDate(dateTo);
            form.appendChild(dateToInput);

            const agentInput = document.createElement('input');
            agentInput.type = 'hidden';
            agentInput.name = 'agent';
            agentInput.value = selectedAgent;
            form.appendChild(agentInput);

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
