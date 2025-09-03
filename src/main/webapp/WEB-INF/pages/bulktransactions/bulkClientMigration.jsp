<%--
  Created by IntelliJ IDEA.
  User: orito
  Date: 22/04/2025
  Time: 15:59
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-users"></i> Bulk Client Migration</h2>
        <button type="button" class="btn btn-primary float-right"
                id="btn-client-migration-reports" data-toggle="modal" data-target="#reportsModal">
            <i class="fa fa-print"></i>
        </button>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <button class="btn btn-primary" id="btn-import-clients">Upload Clients</button>
        <a href="clientMigrationTemplate" class="btn btn-success">
            <i class="fa fa-file-excel-o"></i> Download Template
        </a>
        <div class="x_title">
            <h2>Unprocessed Clients Listing</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="card-box table-responsive">
            <table id="uploadedClientsTable" class="table table-striped" style="width: 100%">
                <thead>
                <tr>
                    <th><input type="checkbox" id="selectAll"></th>
                    <th>Client Code</th>
                    <th>Preferred Payment Mode</th>
                    <th>Client Short Desc</th>
                    <th>Surname</th>
                    <th>Other Names</th>
                    <th>Client Type</th>
                    <th>Gender</th>
                    <th>Id Number</th>
                    <th>Passport Number</th>
                    <th>Client Pin</th>
                    <th>Title</th>
                    <th>Physical Address</th>
                    <th>Credit Allowed</th>
                    <th>Limit Allowed</th>
                    <th>Phone Number</th>
                    <th>Postal Address</th>
                    <th>Country Code</th>
                    <th>Email Address</th>
                    <th>Date of Birth</th>
                    <th>Branch Code</th>
                    <th>Marital Status</th>
                    <th>Default Communication</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div style="margin-top: 20px;">
            <button class="btn btn-danger float-right" id="bulk-delete-clients">Bulk Delete</button>
            <button class="btn btn-primary float-right" id="bulk-process-clients">Bulk Process</button>
        </div>
    </div>
</div>

<!-- File Upload Modal -->
<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Upload Clients</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="uploadForm" enctype="multipart/form-data">
                    <div class="form-group">
                        <label>Select Excel File</label>
                        <input type="file" class="form-control" name="file" accept=".xlsx,.xls">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="btn-upload">Upload</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        let selectedClients = [];
        let allSelected = false;

        // Initialize DataTable
        const clientsTable = $('#uploadedClientsTable').DataTable({
            processing: true,
            serverSide: true,
            order: [[1, 'desc']],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        return '<input type="checkbox" class="client-checkbox" value="' + row.id + '"' +
                            (selectedClients.includes(row.id.toString()) ? ' checked' : '') + '>';
                    }
                },
                {
                    targets: -1, // Last column (actions)
                    orderable: false,
                    searchable: false
                }
            ],
            ajax: {
                url: 'unprocessedClients',
                type: 'GET',
                dataSrc: function (json) {
                    // Update select all checkbox state
                    if (json.recordsTotal > 0) {
                        $('#selectAll').prop('checked', allSelected);
                    }
                    return json.data;
                },
                data: function (d) {
                    if (!d.order || d.order.length === 0) {
                        d.order = [{column: 1, dir: 'desc'}];
                    }
                    return d;
                }
            },
                drawCallback: function() {
                    // Restore checkbox states after redraw
                    $('.client-checkbox').each(function() {
                        const clientId = $(this).val();
                        $(this).prop('checked', selectedClients.includes(clientId));
                    });
                    // Update select all checkbox
                    $('#selectAll').prop('checked', allSelected);
                },
            columns: [
                {
                    data: null,
                    render: function(data) {
                        return '<input type="checkbox" class="client-checkbox" value="' + data.id + '">';
                    }
                },
                { data: 'clientCode' },
                { data: 'prefferedPaymentMode' },
                { data: 'clientShortDesc' },
                { data: 'clientSurname' },
                { data: 'clientOtherNames' },
                { data: 'clientType' },
                { data: 'clientGender' },
                { data: 'idNumber' },
                { data: 'clientPassportNumber' },
                { data: 'kraPin' },
                { data: 'clientTitle' },
                { data: 'physicalAddress' },
                { data: 'clientCreditAllowed' },
                { data: 'clientLimitAllowed' },
                { data: 'phoneNumber' },
                { data: 'postalAddress' },
                { data: 'clientCountryCode' },
                { data: 'emailAddress' },
                { data: 'dateOfBirth' },
                { data: 'branchCode' },
                { data: 'clientMaritalStatus' },
                { data: 'clientDefaultCommunication' },



                {
                    data: null,
                    render: function(data) {
                        return '<button class="btn btn-sm btn-primary process-single" data-id="' + data.id +
                            '"><i class="fa fa-check"></i></button>';
                    }
                }
            ]
        });

        // Handle file upload button
        $('#btn-import-clients').click(function() {
            $('#uploadModal').modal('show');
        });

        // Handle file upload
        $('#btn-upload').click(function() {
            const formData = new FormData($('#uploadForm')[0]);

            $.ajax({
                url: 'uploadClients',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    if (response.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: 'Clients uploaded successfully'
                        });
                        $('#uploadModal').modal('hide');
                        clientsTable.ajax.reload();
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: response.message || 'Upload failed'
                        });
                    }
                },
                error: function() {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Upload failed'
                    });
                }
            });
        });

        // Handle select all checkbox
        $('#selectAll').change(function() {
            allSelected = $(this).prop('checked');

            if (allSelected) {
                // Get all client IDs via AJAX
                $.ajax({
                    url: 'getAllClientIds',
                    type: 'GET',
                    success: function(response) {
                        selectedClients = response.map(String);
                        $('.client-checkbox').prop('checked', true);
                    }
                });
            } else {
                selectedClients = [];
                $('.client-checkbox').prop('checked', false);
            }
        });

        // Handle individual checkbox selection
        $('#uploadedClientsTable').on('change', '.client-checkbox', function() {
            const clientId = $(this).val();

            if ($(this).prop('checked')) {
                if (!selectedClients.includes(clientId)) {
                    selectedClients.push(clientId);
                }
            } else {
                selectedClients = selectedClients.filter(id => id !== clientId);
                allSelected = false;
                $('#selectAll').prop('checked', false);
            }
        });

        // Handle bulk processing
        $('#bulk-process-clients').click(function() {
            if (selectedClients.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Warning',
                    text: 'Please select clients to process'
                });
                return;
            }

            $.ajax({
                url: 'processBulkClients',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(selectedClients),
                success: function(response) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Success',
                        text: response
                    });
                    clientsTable.ajax.reload();
                    selectedClients = [];
                },
                error: function(xhr, status, error) {
                    // For debugging
                    console.log('Error response:', {
                        status: xhr.status,
                        statusText: xhr.statusText,
                        responseText: xhr.responseText,
                        errorThrown: error
                    });

                    // Get the error message from the response
                    let errorMessage = 'Processing failed';

                    if (xhr.responseText) {
                        try {
                            // Try to parse as JSON
                            const errorObj = JSON.parse(xhr.responseText);

                            // Check common error response structures
                            if (errorObj.message) {
                                errorMessage = errorObj.message;
                            } else if (errorObj.error) {
                                errorMessage = errorObj.error;
                            } else if (errorObj.errorMessage) {
                                errorMessage = errorObj.errorMessage;
                            } else if (errorObj.exception) {
                                errorMessage = errorObj.exception;
                            } else if (errorObj.errors && errorObj.errors.length > 0) {
                                errorMessage = Array.isArray(errorObj.errors)
                                    ? errorObj.errors[0].message || errorObj.errors[0]
                                    : errorObj.errors;
                            } else if (typeof errorObj === 'string') {
                                errorMessage = errorObj;
                            }
                        } catch (e) {
                            errorMessage = xhr.responseText;
                        }
                    }

                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: errorMessage,
                    });
                }
            });
        });

        // Handle bulk delete
        $('#bulk-delete-clients').click(function() {
            if (selectedClients.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Warning',
                    text: 'Please select clients to delete'
                });
                return;
            }

            Swal.fire({
                icon: 'warning',
                title: 'Are you sure?',
                text:'Are you sure you want to delete the selected clients?',
                showCancelButton: true,
                confirmButtonText: 'Yes, delete them!'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: 'deleteBulkClients',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(selectedClients),
                        success: function(response) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Deleted!',
                                text: 'Clients have been deleted successfully'
                            });
                            clientsTable.ajax.reload();
                            selectedClients = [];
                        },
                        error: function() {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'Delete failed'
                            });
                        }
                    });
                }
            });
        });

        // Handle single client processing
        $('#uploadedClientsTable').on('click', '.process-single', function() {
            const clientId = $(this).data('id');

            $.ajax({
                url: 'processSingleClient/' + clientId,
                type: 'POST',
                success: function(response) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Success',
                        text: 'Client processed successfully'
                    });
                    clientsTable.ajax.reload();
                },
                error: function(xhr, status, error) {
                    // For debugging
                    console.log('Error response:', {
                        status: xhr.status,
                        statusText: xhr.statusText,
                        responseText: xhr.responseText,
                        errorThrown: error
                    });

                    // Get the error message from the response
                    let errorMessage = 'Processing failed';

                    if (xhr.responseText) {
                        try {
                            // Try to parse as JSON
                            const errorObj = JSON.parse(xhr.responseText);

                            // Check common error response structures
                            if (errorObj.message) {
                                errorMessage = errorObj.message;
                            } else if (errorObj.error) {
                                errorMessage = errorObj.error;
                            } else if (errorObj.errorMessage) {
                                errorMessage = errorObj.errorMessage;
                            } else if (errorObj.exception) {
                                errorMessage = errorObj.exception;
                            } else if (errorObj.errors && errorObj.errors.length > 0) {
                                errorMessage = Array.isArray(errorObj.errors)
                                    ? errorObj.errors[0].message || errorObj.errors[0]
                                    : errorObj.errors;
                            } else if (typeof errorObj === 'string') {
                                errorMessage = errorObj;
                            }
                        } catch (e) {
                            errorMessage = xhr.responseText;
                        }
                    }

                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: errorMessage
                    });
                }
            });
        });
    });
</script>
