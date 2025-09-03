var STRETCHY_REPORTS_UTILITIES = STRETCHY_REPORTS_UTILITIES || {};

STRETCHY_REPORTS_UTILITIES.createStretchyReport = function () {
    // Initialize form validation
    $('#report-form').validate({
        rules: {
            strRptName: {
                required: true
            },
            reportType: {
                required: true
            },
            reportSql: {
                required: true
            }
        },
        messages: {
            // Define error messages for each field
            strRptName: "Please enter report name",
            reportType: "Please enter report type",
            reportSql: "Please enter correct report SQL"
        }
    });


    // Prevent default form submission
    $('#report-form').submit(function (e) {
        e.preventDefault(); // Prevent the form from submitting normally
    });

    $("#add-param-btn").on('click', function () {
        STRETCHY_REPORTS_UTILITIES.addStretchyParameter();
    });
}

STRETCHY_REPORTS_UTILITIES.addStretchyParameter = function () {
    var row_index = $('#param-detail-tbl tr').length;
    var param = '<input type="hidden" id="stretchy-parameter' + row_index + '" name="parameters">' +
        '<div id="param-frm' + row_index + '" class="form-control" select2-url="' + SERVLET_CONTEXT + '/protected/stretchyreports/stretchyparams"></div>';
    var markup = "<tr><td>" + param + "</td><td><p class='form-control-static' id='stretchy-param-" + row_index + "'>" +
        "<button type='button' class='btn btn-danger btn-xs hyperlink-btn delete-param-btn'><i class='fa fa-trash-o'></i></button></p></td></tr>";
    $("#param-detail-tbl tbody").append(markup);

    Select2Builder.initAjaxSelect2({
        containerId: "param-frm" + row_index,
        sort: 'stpId',
        counter: row_index,
        change: function (e, a, v) {
            console.log("Event: ", e);
            console.log("Selected Object: ", a);
            console.log("Event: ", e.added);

            if (e && e.added) {
                console.log("Added Object: ", e.added);
                var stpId = e.added.stpId || null; // Adjust based on actual property name
                console.log("stpId: ", stpId); // Debugging: Log stpId to confirm

                if (stpId) {
                    var stpIdSelector = "#stretchy-parameter" + row_index;
                    $(stpIdSelector).val(stpId); // Set the hidden input's value
                } else {
                    console.error("stpId not found!");
                }
            } else {
                console.error("No item added");
            }
        },
        formatResult: function (a) {
            return a.paramName;
        },
        formatSelection: function (a) {
            return a.paramName;
        },
        initSelection: function (element, callback) {
            // Optional: Add initial selection logic if needed
        },
        id: "stpId",
        width: "300px",
        placeholder: "Select Parameter"
    });
    // Attach click event to the delete button
    $("#param-detail-tbl").on('click', '.delete-param-btn', function() {
        $(this).closest('tr').remove();
    })
}

STRETCHY_REPORTS_UTILITIES.createStretchyReportsListing = function () {
    var url = SERVLET_CONTEXT + "/protected/stretchyreports/stretchyrpts";
    return $('#streportbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "searching": true,
        "ajax": {
            'url': url,
            'dataSrc': function (json) {
                // console.log("Server response:", json);
                return json.data;
            }
        },
        lengthMenu: [[10, 15], [10, 15]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "strId"},
            {"data": "strRptName"},
            {"data": "reportCategory"},
            {"data": "description"},
            {
                "data": "strId",
                "render": function (data, type, full, meta) {
                    return '<form action="viewstretchyreport" method="post">' +
                        '<input type="hidden" name="id" value="' + full.strId + '">' +
                        '<button type="submit" class="btn btn-success btn btn-info btn-xs">' +
                        '<i class="fa fa-eye"></i>' +
                        '</button>' +
                        '</form>';
                }
            },

            {
                "data": "strId",
                "render": function (data, type, full, meta) {
                    return '<form action="editstretchyreport" method="post">' +
                        '<input type="hidden" name="id" value="' + full.strId + '">' +
                        '<button type="submit" class="btn btn-info btn-xs">' +
                        '<i class="fa fa-pencil-square-o"></i>' +
                        '</button>' +
                        '</form>';
                }
            },

            {
                "data": "strId",
                "render": function (data, type, full, meta) {
                    // Encode the full object to be used as a data attribute
                    var jsonString = encodeURI(JSON.stringify(full));
                    return '<button type="button" class="btn btn-danger btn-xs" data-stretchyReports="' + jsonString + '" onclick="deleteStretchyReport(this);"><i class="fa fa-trash-o"></i></button>';
                }
            }
        ]
    });
}

function deleteStretchyReport(button) {
    // Retrieve the data-stretchyReports attribute from the button
    var dataAttr = $(button).attr("data-stretchyReports");
    if (dataAttr) {
        // Decode and parse the JSON string
        var stretchyReports = JSON.parse(decodeURI(dataAttr));
        // Confirm deletion using bootbox
        bootbox.confirm("Are you sure you want to delete the report?", function(result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'deleteStretchyReport/' + stretchyReports['strId'],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Report Deleted Successfully',
                            icon: 'success'
                        });
                        // Reload the DataTable to reflect changes
                        $('#streportbl').DataTable().ajax.reload();
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                    }
                });
            }
        });
    } else {
        console.error("Data attribute is undefined or null.");
    }
}


STRETCHY_REPORTS_UTILITIES.editStretchyReport = function (button){
    var dataAttr = $(button).attr("data-stretchyReports");
    if (dataAttr) {
        // Decode and parse the JSON string
        var stretchyrReports = JSON.parse(decodeURI(dataAttr));

        console.log(stretchyrReports)
    // var stretchyrReports = JSON.parse(decodeURI($(button).data("stretchyrReports")));
    $("#stretchy-report-form").val(stretchyrReports["strId"]);
    $("#report-name").val(stretchyrReports["strRptName"]);
    $("#report-type").val(stretchyrReports["reportType"]);
    $("#report-category").val(stretchyrReports["reportCategory"]);
    $("#report-sql").val(stretchyrReports["reportSql"]);
    $("#description").val(stretchyrReports["description"]);
    $('#editStretchyReportModal').modal({
        backdrop: 'static',
        keyboard: true
    })

    } else {
        console.error("Data attribute is undefined or null.");
    }
}

STRETCHY_REPORTS_UTILITIES.saveReportDefs = function() {
    var $classForm = $('#report-form');
    var validator = $classForm.validate();
    $('#saveStretchyParameterBtn').click(function() {
        var $btn = $(this).button('Saving');
        var data = {};
        $('#report-form').serializeArray().map(function(x) {
            data[x.name] = x.value;
        });

        var params = [];
        $('#param-detail-tbl input[type="hidden"]').each(function() {
            var val = $(this).val().trim();
            console.log('Hidden Input Value:', val); // Debugging: Log the value

            // Check if the value is not empty and is a valid number
            if (val !== "" && !isNaN(val)) {
                var parsedVal = parseInt(val, 10);
                if (!isNaN(parsedVal)) {
                    params.push(parsedVal); // Convert to integer and add to array
                } else {
                    console.error("Invalid number found: ", val); // Log invalid number error
                }
            } else {
                console.error("Skipping invalid or empty value:", val); // Log skipped value
            }
        });

        data.parameters = params;

        console.log("Final Data Object: ", data); // Log the data object to verify

        var url = data.strId ?
            SERVLET_CONTEXT + "/protected/stretchyreports/updateStretchyReport" :
            SERVLET_CONTEXT + "/protected/stretchyreports/createStretchyReport";

        var methodType = data.strId ?
            "PUT" :
            "POST";

        var request = $.ajax({
            type: methodType,
            url: url,
            data: JSON.stringify(data),
            contentType: "application/json",
            dataType: "json"
        });

        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success',
                showCancelButton: false,
                confirmButtonText: 'OK'
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = redirectUrl;
                }
            });
            $('#report-form').trigger("reset");
            $('#param-detail-tbl tbody').empty();
        });

        request.fail(function(jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });

        request.always(function() {
            $btn.button('reset');
        });
    });
}


STRETCHY_REPORTS_UTILITIES.getStretchyReportDetails = function(){

    if(typeof SreportId!== 'undefined'){
        if(SreportId!==-2000){
            $.ajax( {
                url: 'getStretchyReportDetails',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s ) {
                    console.log(s);
                    $("#renbtn").show();
                    // reportId = s.strId;
                    $("#report-id").val(SreportId);
                    $("#report-name").text(s.strRptName);
                    if(s.reportType==="T"){
                        $("#report-type").text("Table");
                    }
                    else{
                        $("#report-type").text("Chart");
                    }

                    if(s.reportCategory==="A"){
                        $("#report-category").text("Accounts");
                    }
                    else if(s.reportCategory==="U"){
                        $("#report-category").text("Underwriting");
                    }
                    else if(s.reportCategory==="C"){
                        $("#report-category").text("Claims");
                    }
                    else if(s.reportCategory==="M"){
                        $("#report-category").text("Medical");
                    }

                    $("#report-sql").text(s.reportSql);
                    $("#description").text(s.description);
                    // console.log(reportId);
                    getParametersTable();
                },
                error: function(xhr, error){
                    bootbox.alert(xhr.responseText);
                }
            });
        }
    }
}
var deleteParameter = function(reportId, stpId) {
    $.ajax({
        type: 'POST',
        url: 'deleteStretchyReportParameter',
        data: JSON.stringify({ reportId: reportId, stpId: stpId }),
        contentType: 'application/json',
        success: function (response) {
            Swal.fire({
                title: 'Success',
                text: 'Parameter Deleted Successfully',
                icon: 'success'
            });
            getParametersTable(reportId);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
};

$(document).ready(function() {
    $("#stretchy-parameters-tbl").on('click', '.btn-danger', function () {

        var row = $(this).closest('tr');
        var reportId = $("#report-id").val();
        var stpId = row.find('input[type="hidden"]').val();
        bootbox.confirm("Are you sure you want to delete the parameter?", function(result) {
            if (result) {
                deleteParameter(reportId, stpId);

            }
        });
        $('#stretchy-parameters-tbl').DataTable().ajax.reload();
    });
});

STRETCHY_REPORTS_UTILITIES.getEditStretchyReportDetails = function(){

    if(typeof SreportId!== 'undefined'){
        if(SreportId!==-2000){
            $.ajax( {
                url: 'getStretchyReportDetails',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s ) {
                    console.log(s);
                    $("#renbtn").show();
                    // reportId = s.strId;
                    $("#report-id").val(SreportId);
                    $("#report-name").val(s.strRptName);
                    $("#report-type").val(s.reportType);
                    $("#report-category").val(s.reportCategory);
                    $("#report-sql").text(s.reportSql);
                    $("#description").text(s.description);
                    // console.log(reportId);
                    getParametersTable();
                },
                error: function(xhr, error){
                    bootbox.alert(xhr.responseText);
                }
            });
        }
    }
}
var getParametersTable = function(SreportId) {
    $.ajax({
        type: 'GET',
        url: 'getStretchyReportParameters',
        dataType: 'json',
        data: { "reportId": $("#report-id").val() },
        async: true,
        success: function(result) {
            $("#stretchy-parameters-tbl tbody").empty();

            // Define a mapping for paramType values
            var paramTypeMapping = {
                "D": "Date",
                "L": "List of Values",
                "O": "Options",
                "N": "Number",
                "T": "Text"
            };

            for (var res in result) {
                var paramTypeDisplay = paramTypeMapping[result[res].paramType] || result[res].paramType;

                var markup = "<tr>" +
                    "<td><input type='hidden' class='section form-control' value='" + result[res].stpId + "'>" + result[res].stpId + "</td>" +
                    "<td>" + result[res].paramName + "</td>" +
                    "<td>" + result[res].paramActualName + "</td>" +
                    "<td>" + paramTypeDisplay + "</td>" +
                    "<td><button type='button' class='btn btn-success btn-xs edit-param-btn'><i class='fa fa-pencil-square-o'></i></button></td>" +
                    "<td><button type='button' class='btn btn-danger btn-xs delete-param-btn'><i class='fa fa-trash-o'></i></button></td>" +
                    "</tr>";

                $("#stretchy-parameters-tbl").append(markup);
            }

            $("#stretchy-parameters-tbl tr").find("input[type=text]").number(true, 3);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: 'Failed to load parameters',
                icon: 'error'
            });
        }
    });
};


var editParameter = function (reportId, stpId) {
    $('#edit-parameter-modal').modal('show'); // Show modal with Select2
    Select2Builder.initAjaxSelect2({
        containerId: "edit-param-select",
        sort: 'stpId',
        change: function (e, a, v) {
            console.log("Event: ", e);
            console.log("Selected Object: ", e);
            console.log("Event: ", e.added);

            if (e && e.added) {
                console.log("Added Object: ", e.added);
                var newStpId = e.added.stpId || null;
                var newStpId = e.added.stpId || null;
                $("#edit-parameter-modal").data('new-stpId', newStpId);
                console.log(" new stpId: ", newStpId);
            } else {
                console.error("No item added");
            }
        },
        formatResult: function (a) {
            return a.paramName;
        },
        formatSelection: function (a) {
            return a.paramName;
        },
        initSelection: function (element, callback) {
        },
        id: "stpId",
        width: "300px",
        placeholder: "Select Parameter"
    });

    $('#save-edited-parameter').on('click', function() {
        var newStpId = $("#edit-parameter-modal").data('new-stpId');
        if(newStpId) {
            console.log("Payload being sent:", JSON.stringify({ reportId: reportId, oldStpId: stpId, newStpId: newStpId }));
            $.ajax({
                type: 'PUT',
                url: 'updateStretchyReportParameter',
                data: JSON.stringify({ reportId: reportId, oldStpId: stpId, newStpId: newStpId }),

                contentType: 'application/json',
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Parameter Updated Successfully',
                        icon: 'success'
                    });
                    getParametersTable(reportId); // Refresh table
                    $('#edit-parameter-modal').modal('hide'); // Close modal
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: 'Failed to update parameter',
                        icon: 'error'
                    });
                }
            });
        } else {
            Swal.fire({
                title: 'Error',
                text: 'Please select a parameter',
                icon: 'error'
            });
        }
    });
};

$(document).ready(function() {
    $("#stretchy-parameters-tbl").on('click', '.delete-param-btn', function () {
        var row = $(this).closest('tr');
        var reportId = $("#report-id").val();
        var stpId = row.find('input[type="hidden"]').val();
        bootbox.confirm("Are you sure you want to delete the parameter?", function(result) {
            if (result) {
                deleteParameter(reportId, stpId);
            }
        });
    });

    $("#stretchy-parameters-tbl").on('click', '.edit-param-btn', function () {
        var row = $(this).closest('tr');
        var reportId = $("#report-id").val();
        var stpId = row.find('input[type="hidden"]').val();
        editParameter(reportId, stpId);
    });
});



