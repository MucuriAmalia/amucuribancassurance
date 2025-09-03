const taskTypeLabels = {
    CL: 'Client',
    RC: 'Receipt',
    EN: 'Endorsement',
    IM: 'Accounts',
    BN: 'Binder',
    COA: 'Chart of Accounts',
    RV: 'Reversals',
    RN: 'Renewals',
    CA: 'Claims',
    REC: 'Reconciliation',
    LP: 'Life Policy Administration',
    GI: 'General Policy Administration'
};
$(document).ready(function () {
    $('#config-tab').tab('show');
    const table = $('#taskConfigTable').DataTable({
        ajax: {
            url: SERVLET_CONTEXT + '/protected/setups/getEscalationList',
            dataSrc: ''
        },
        columns: [
            {
                data: 'taskType',
                render: function (data) {
                    return taskTypeLabels[data] || data; // Use label if available, fallback to raw value
                }
            },
            { data: 'timeLimitPerUser' },
            { data: 'timeLimitPerTransaction' },
            {
                data: null,
                render: function (data) {
                    return `<button class="btn btn-sm btn-success edit-config" data-id="${data.escConfigId}">Edit</button>`;
                }
            }
        ]
    });

    // Open modal for new configuration
    $('#btnNewConfig').click(function () {
        $('#configModalLabel').text('New Task Configuration');
        $('#configForm')[0].reset();
        $('#configForm').removeData('id'); // Clear stored ID for new config
    });

    // Open modal for editing
    $('#taskConfigTable').on('click', '.edit-config', function () {
        const id = $(this).data('id');
        $.get(`/absa-bancassurance/protected/setups/getEscalationList`, function (configs) {
            const config = configs.find(cfg => cfg.escConfigId === id);
            if (config) {
                $('#configModalLabel').text('Edit Task Configuration');
                $('#taskType').val(config.taskType);
                $('#timeLimitPerUser').val(config.timeLimitPerUser);
                $('#timeLimitPerTransaction').val(config.timeLimitPerTransaction);
                $('#configForm').data('id', id);
                $('#configModal').modal('show');
            }
        });
    });

    // Save configuration
    $('#configForm').submit(function (e) {
        e.preventDefault();
        const id = $(this).data('id');
        const data = {
            escConfigId: id || null, // Include ID for update, null for new
            taskType: $('#taskType').val(),
            timeLimitPerUser: $('#timeLimitPerUser').val(),
            timeLimitPerTransaction: $('#timeLimitPerTransaction').val()
        };
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/setups/saveEscalationList',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                $('#configModal').modal('hide');
                table.ajax.reload();
            },
            error: function (xhr) {
                $('#configModal').modal('hide');
                if (xhr.status === 400) {
                    bootbox.alert(xhr.responseText); // Display the server-provided message
                } else {
                    bootbox.alert('Failed to save configuration. Please try again or contact system administrator.');
                }
            }
        });
    });
    function loadHierarchyRoles() {
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/setups/getEscalationLevels',
            type: 'GET',
            success: function (roles) {
                const tableBody = $('#hierarchyRolesTable tbody');
                tableBody.empty(); // Clear existing rows

                // Populate table with fetched roles
                roles.forEach(role => {
                    const row = `
                        <tr>
                            <td>${role.hierarchyLevel}</td>
                            <td>${role.roleName}</td>
                        </tr>
                    `;
                    tableBody.append(row);
                });
            },
            error: function () {
                bootbox.alert('Failed to load hierarchy roles. Please try again.');
            }
        });
    }
    var model = {
        accounts: {
            accType:{
                accId:"",
            },
            branch:{
                brnCode:"",
            },
        }
    };
    function createBranchSelect(){
        if($("#acct-branch").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "acct-branch",
                sort : 'obName',
                change: function(e, a, v){
                    $("#obId").val(e.added.obId);
                    fetchCreatedTasksPerBranch(e.added.obId)
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
                    var code = $("#obId").val();
                    var name = $("#ob-name").val();
                    model.accounts.branch.brnCode = code;
                    var data = {obName:name,obId:code};
                    callback(data);

                },
                id: "obId",
                placeholder:"Select Branch",
            });
        }
    };


    function fetchCreatedTasksPerBranch(branchId, startDate, endDate) {
        console.log("Fetching records for Branch ID:", branchId, "Start Date:", startDate, "End Date:", endDate);

        $.ajax({
            url: SERVLET_CONTEXT + "/protected/setups/getEscalationRecordsByBranch",
            type: "GET",
            data: {
                branchId: branchId,
                startDate: startDate,
                endDate: endDate,
                page: 1,  // Start from page 1
                size: 5   // Number of entries per page
            },
            success: function (data) {
//                console.log("Fetched Data:", data);

                if ($.fn.dataTable.isDataTable('#escalationRecordsTable')) {
                    $('#escalationRecordsTable').DataTable().clear().destroy();
                }
                // Clear the existing table
                const tableBody = $("#escalationRecordsTable tbody");
                tableBody.empty();

                // Populate the table with fetched data
                data.forEach(record => {
                    const row = `
                        <tr data-task-id="${record.task?.id}">
                            <td>${record.task?.id || 'Not Found'}</td>
                            <td>${record.taskInitiationTime ? moment(record.taskInitiationTime).format('DD-MM-YYYY HH:mm:ss') : moment(record.madeTime).format('DD-MM-YYYY HH:mm:ss')}</td>
                            <td>${record.checkTime ? moment(record.checkTime).format('DD-MM-YYYY HH:mm:ss') : 'Not Verified'}</td>
                            <td>${record.task?.taskOverdue != null ? record.task.taskOverdue : 'NO'}</td>
                            <td>${record.escalationLevel?.roleName || 'N/A'}</td>
                            <td>${record.owner?.username || 'N/A'}</td>
                            <td>${record.task?.taskName || 'N/A'}</td>
                            <td>${record.verifier?.username || 'Not Verified'}</td>
                        </tr>
                    `;
                    tableBody.append(row);
                });

                $('#escalationRecordsTable').DataTable({
                    pageLength: 5,
                    processing: true,
                    serverSide: false,
                    searching: true,
                    lengthChange: false,
                    info: true,
                });
            },
            error: function (xhr, status, error) {
                console.error("Error fetching data:", error);
            }
        });
    }

    // Attach event listener to the filter button
    $("#filterTasksButton").on("click", function () {
        const branchId = $("#obId").val();
        const startDate = moment($("#startDate").val()).format("YYYY-MM-DDTHH:mm:ss");
        const endDate = moment($("#endDate").val()).format("YYYY-MM-DDTHH:mm:ss");

        // Validate inputs
        if (!branchId) {
            bootbox.alert("Please select a branch.");
            return;
        }
        if (!startDate || !endDate) {
            bootbox.alert("Please specify both start and end dates.");
            return;
        }

        fetchCreatedTasksPerBranch(branchId, startDate, endDate);
    });


     $('#escalationRecordsTable').on('click', 'tbody tr', function () {
        // Remove 'selected-row' class from all rows
        $('#escalationRecordsTable tbody tr').removeClass('selected-row');

        // Add 'selected-row' class to the clicked row
        $(this).addClass('selected-row');

        // Get the task ID
        const taskId = $(this).data('task-id');
        if (taskId) {
//            console.log('Task ID:', taskId);
            fetchActivityRecords(taskId);
        }
    });

    function fetchActivityRecords(taskId) {
//        console.log("Fetching activity records for Task ID:", taskId);

        $.ajax({
            url: SERVLET_CONTEXT + "/protected/setups/getActivityRecordsByTask",
            type: "GET",
            data: {
                taskId: taskId
            },
            success: function (data) {
//                console.log("Fetched Activity Data:", data);

                // Clear the activity table before adding new data
                if ($.fn.dataTable.isDataTable('#activityRecordsTable')) {
                    // Clear the DataTable rows while keeping its structure
                    const dataTable = $('#activityRecordsTable').DataTable();
                    dataTable.clear();
                    dataTable.destroy();
                }

                const activityTableBody = $("#activityRecordsTable tbody");
                activityTableBody.empty();

                // Populate the table with fetched data
                data.forEach(activity => {
                    const row = `
                        <tr>
                            <td>${activity.activityTime ? moment(activity.activityTime).format('DD-MM-YYYY HH:mm:ss') : 'N/A'}</td>
                            <td>${activity.escalationLevel?.roleName || 'N/A'}</td>
                            <td>${activity.user?.username || 'N/A'}</td>
                            <td>${activity.comments || 'No Comments'}</td>
                            <td>${activity.systemAction || 'N/A'}</td>
                        </tr>
                    `;
                    activityTableBody.append(row);
                });

                // Reinitialize the DataTable
                $('#activityRecordsTable').DataTable({
                    pageLength: 5,  // Number of entries per page
                    processing: true,
                    serverSide: false,
                    searching: true,
                    lengthChange: false,
                    info: true,
                    destroy: true, // Allow reinitialization
                });
            },
            error: function (xhr, status, error) {
                console.error("Error fetching activity data:", error);
            }
        });
    }

        // Load hierarchy roles on tab activation
    $('a[href="#tab_content2"]').on('shown.bs.tab', function () {
        loadHierarchyRoles();
    });
    $('a[href="#tab_content3"]').on('shown.bs.tab', function () {
            createBranchSelect();
        });
});
