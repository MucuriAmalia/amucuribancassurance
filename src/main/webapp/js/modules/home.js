/**
 * Created by peter on 2/22/2017.
 */

var HomeScreen = (function ($) {
    'use strict';
    var getUserTasks = function () {
        var url = SERVLET_CONTEXT + '/protected/home/pendingTickets';
        var currTable = $('#tasks').DataTable(UTILITIES.extendsOpts({
            "ajax": {
                "url": url,
                "type": "GET",
                "dataSrc": function (json) {
                    console.log('Tasks response:', json);
                    return json.data;
                },
                "error": function (xhr, error, thrown) {
                    console.error('Tasks AJAX error:', error, thrown);
                }
            },
            "columns": [
                {"data": "taskId"},
                {"data": "taskName"},
                {"data": "transType"},
                {"data": "clientName"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        if (full.madeOnDate) {
                            return timeago().format(full.madeOnDate);
                        } else {
                            return "";
                        }
                    }
                },
                {
                    data: null,
                    render: function (data, type, row) {
                        return row.initiatorName ? row.initiatorName : row.madeBy;
                    }
                },
                {"data": "resubmissionComment"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        console.log(full)
                        if (full.taskType === 'QT')
                            return '<form action="checkerGetQuoteForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'CL')
                            return '<form action="' + CONTEXT + '/protected/claims/checkerViewClaim" method="post">' +
                                '<input type="hidden" name="id" value="' + full.taskId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="View">' +
                                '</form>';
                        else if (full.taskType === 'RF')
                            return '<form action="checkerGetRefundForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';

                        else if (full.taskType === 'RC')
                            return '<form action="checkerGetReceiptForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'LP')
                            return '<form action="checkerViewPolicy" method="post"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'ANP' || full.taskType === 'ALP' || full.taskType === 'CN')
                            return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'IM')
                            return '<form action="editAcctForm" method="post"><input type="hidden" name="id" value=' + full.acctId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                    }

                },


            ],

        }));
        return currTable;
    };
    var getUserReceipts = function () {
        var url = SERVLET_CONTEXT + '/protected/home/pendingReceipts';
        var currTable = $('#receipt_tbl').DataTable(UTILITIES.extendsOpts({
            "ajax": {
                "url": url,
                "type": "GET",
                "dataSrc": function (json) {
                    console.log('Tasks response:', json);
                    return json.data;
                },
                "error": function (xhr, error, thrown) {
                    console.error('Tasks AJAX error:', error, thrown);
                }
            },
            "columns": [
                {"data": "taskId"},
                {"data": "taskName"},
                {"data": "policyNumber"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {

                        if (full.madeOnDate) {
                            return timeago().format(full.madeOnDate);
                        } else {
                            return "";
                        }
                    }
                },
                {"data": "madeBy"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        console.log(full)
                        if (full.taskType === 'QT')
                            return '<form action="checkerGetQuoteForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'CL')
                            return '<form action="checkerGetClientForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'RC')
                            return '<form action="checkerGetReceiptForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'LP')
                            return '<form action="checkerViewPolicy" method="post"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'ANP' || full.taskType === 'ALP')
                            return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                        else if (full.taskType === 'IM')
                            return '<form action="editAcctForm" method="post"><input type="hidden" name="id" value=' + full.acctId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                    }

                },


            ],
        }));
        return currTable;
    };
    var getUserClaims = function () {
        var url = SERVLET_CONTEXT + '/protected/home/pendingClaims';
        var currTable = $('#claims_tbl').DataTable(UTILITIES.extendsOpts({
            "ajax": {
                "url": url,
                "type": "GET",
                "dataSrc": function (json) {
                    return json.data;
                },
                "error": function (xhr, error, thrown) {
                    console.error('Claims AJAX error:', error, thrown);
                }
            },
            "columns": [
                {
                    "data": "taskId",
                    "render": function(data, type, row) {

                        return data || '';
                    }
                },
                {
                    "data": "taskName",
                    "render": function(data, type, row) {
                        return data || '';
                    }
                },

                {
                    "data": "status",
                    "render": function(data, type, row) {
                        return data || 'N/A';
                    }
                },
                {
                    "data": "nextReviewDate",
                    "title": "Next Review Date",
                    "render": function(data, type, row) {
                        if (data) {
                            var date = new Date(data);
                            return date.toLocaleDateString('en-GB');
                        }
                        return '';
                    }
                },
                {
                    "data": "madeOnDate",
                    "render": function(data, type, row) {
                        return data ? timeago().format(new Date(data)) : '';
                    }
                },
                {
                    "data": "initiatorName",
                    "render": function(data, type, row) {
                        return data || row.madeBy || '';  // Fallback to maker if no initiator
                    }
                },
                {
                    "data": "taskId",
                    "render": function(data, type, row) {
                        return '<form action="' + CONTEXT + '/protected/claims/checkerViewClaim" method="post">' +
                            '<input type="hidden" name="id" value="' + data + '">' +
                            '<input type="submit" class="btn btn-success btn-info btn-sm" value="View">' +
                            '</form>';
                    }
                }

            ],
            "columnDefs": [
                {
                    "targets": [6],  // Action column
                    "orderable": false,
                    "searchable": false
                }
            ]
        }));

        // Refresh table every 30 seconds
        setInterval(function() {
            currTable.ajax.reload(null, false);
        }, 30000);

        return currTable;
    };



    var getMakerTasks = function () {
        var url = SERVLET_CONTEXT + '/protected/home/makerTickets';
        var currTable = $('#maker-tasks').DataTable(UTILITIES.extendsOpts({

            "ajax": {  // Changed from "ajaxUrl" to "ajax"
                "url": url,
                "type": "GET",
                "dataSrc": function (json) {
                    console.log('Tasks response:', json);
                    return json.data;
                },
                "error": function (xhr, error, thrown) {
                    console.error('Tasks AJAX error:', error, thrown);
                }
            },
            "columns": [
                {"data": "taskId"},
                {"data": "taskName"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        console.log("full data: ", full);
                        if (full.madeOnDate) {
                            return timeago().format(full.madeOnDate);
                        } else {
                            return "";
                        }
                    }
                },
                {"data": "status"},
                {"data": "checkedBy"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        if (full.checkedOnDate) {
                            return timeago().format(full.checkedOnDate);
                        } else {
                            return "";
                        }
                    }
                },
                {"data": "rejectionReasonDesc"},
                {"data": "rejectedReason"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        let disabled = (full.status === "Pending Approval") ? "disabled" : "";

                        if (full.taskType === 'RC') {
                            return '<form action="makerGetReceiptForm" method="get">' +
                                '<input type="hidden" name="id" value="' + full.taskId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="View" ' + disabled + '>' +
                                '</form>';
                        } else if (full.taskType === 'ANP' || full.taskType === 'ALP') {
                            return '<form action="edituwtrans" method="post">' +
                                '<input type="hidden" name="id" value="' + full.policyId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="View" ' + disabled + '>' +
                                '</form>';
                        } else if (full.taskType === 'IM') {
                            return '<form action="editAcctForm" method="post">' +
                                '<input type="hidden" name="id" value="' + full.acctId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="View" ' + disabled + '>' +
                                '</form>';
                        }
                        else if (full.taskType === 'CL') {


                            if (full.status === "Rejected" || full.status === "R") {

                                if (!full.taskCode || full.taskCode === null || full.taskCode === 'null') {

                                    return '<span class="text-danger">No claim ID available</span>';
                                }

                                return '<form action="' + CONTEXT + '/protected/claims/editRejectedClaim" method="get">' +
                                    '<input type="hidden" name="claimId" value="' + full.taskCode + '">' +
                                    '<input type="hidden" name="mode" value="edit">' +
                                    '<input type="submit" class="btn btn-success btn-info btn-sm" value="Edit Claim">' +
                                    '</form>';
                            } else {

                                return '<form action="' + CONTEXT + '/protected/claims/checkerViewClaim" method="post">' +
                                    '<input type="hidden" name="id" value="' + full.taskId + '">' +
                                    '<input type="submit" class="btn btn-success btn-info btn-sm" value="View" ' + disabled + '>' +
                                    '</form>';
                            }
                        }




                    }
                }
            ]
        }));
        return currTable;
    };

    window.rejectTask = function (button) {
        console.log("Reject button clicked");
        var task = JSON.parse(decodeURI($(button).attr('data-certlots')));
        console.log("Task data: ", task);

        // Display the modal
        $('#rejectionModal').modal('show');

        $('#rejectConfirmButton').off('click').on('click', function () {
            var reason = $('#rejectionReason').val();
            console.log("Rejection reason: ", reason);

            if (reason) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        taskId: task.taskId,
                        reason: reason
                    }),
                    success: function (response) {
                        console.log("Task rejected successfully");
                        // Remove the row from the table
                        var table = $('#tasks').DataTable();
                        table.row($(button).closest('tr')).remove().draw();
                    },
                    error: function (xhr, status, error) {
                        console.error("Error rejecting task: ", error);
                    }
                });
            } else {
                console.error("No reason provided for rejection");
            }
            // Hide the modal after submission
            $('#rejectionModal').modal('hide');
        });
    };

    window.approveTask = function (button) {
        var task = JSON.parse(decodeURI($(button).attr('data-certlots')));
        console.log("Task data: ", task);
        // Display the modal
        $('#approvalModal').modal('show');
        $('#approveConfirmButton').off('click').on('click', function () {

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/approveTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(task.taskId),
                success: function (response) {
                    console.log("Task approved successfully");
                    // Remove the row from the table
                    var table = $('#tasks').DataTable();
                    table.row($(button).closest('tr')).remove().draw();
                },
                error: function (xhr, status, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });

            // Hide the modal after submission
            $('#approvalModal').modal('hide');
        });
    };

    var getUserPortfolio = function () {
        var url = SERVLET_CONTEXT + '/protected/home/userPortfolio';

        $('#pol_enquiry_tbl').DataTable({
            "processing": true,
            "serverSide": true,
            "searching": true,
            "destroy": true,
            "order": [[1, 'asc']], // Default sorting by policy number
            "ajax": {
                "url": url,
                "type": "GET",
                "data": function (d) {
                    console.log('AJAX Data:', d);
                    return {
                        draw: d.draw,
                        start: d.start,
                        length: d.length,
                        search: {
                            value: d.search.value,
                            regex: d.search.regex
                        },
                        order: d.order,
                        columns: d.columns,
                        status: $('#statusFilter').val() // Include the selected status
                    };
                },
                "dataSrc": function (data) {
                    // console.log('Data Received:', data);
                    return data.data;  // Ensure the array is returned from the response object
                }
            },
            "columns": [
                {
                    "data": "polId",
                    "render": function (data, type, full, meta) {
                        let disabled = (full.status === "N" || full.status === "R") ? "disabled" : "";

                        return '<form action="edituwtrans" method="post">' +
                            '<input type="hidden" name="id" value="' + full.polId + '">' +
                            '<input type="submit" class="btn btn-success btn-info btn-sm" value="View" ' + disabled + '>' +
                            '</form>';
                    }
                },
                {"data": "polNo"},
                {"data": "polRevNo"},
                {"data": "product"},
                {"data": "clientName"},
                {"data": "agentName"},
                {"data": "username"},
                {"data": "currentStatus"},
                {"data": "authComments"},
            ]
        });

        // Trigger table reload when status filter changes
        $('#statusFilter').on('change', function () {
            $('#pol_enquiry_tbl').DataTable().ajax.reload();
        });
    };


    var getUserTickets = function () {
        var url = SERVLET_CONTEXT + '/protected/home/userTickets';
        var currTable = $('#pol_tbl').DataTable(UTILITIES.extendsOpts({
            "ajax": {  // Changed from "ajaxUrl" to "ajax"
                "url": url,
                "type": "GET",
                "dataSrc": function (json) {
                    return json.data;
                },
                "error": function (xhr, error, thrown) {
                    console.error('Tickets AJAX error:', error, thrown);
                }
            },
            "order": [[5, "desc"]],
            "columns": [
                {"data": "taskId"},
                {"data": "activeProcess"},
                {"data": "refNo"},
                {"data": "clientName"},
                {"data": "username"},
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        if (full.createdDate) {
                            return timeago().format(full.createdDate);
                        } else {
                            return "";
                        }
                    }
                },
                {
                    "data": "taskId",
                    "render": function (data, type, full, meta) {
                        console.log(full.status);
                        let disabled = (full.status === "N" || full.status === "R") ? "disabled" : "";
                        if (full.transType === 'P') {
                            return '<form action="edituwtrans" method="post">' +
                                '<input type="hidden" name="id" value="' + full.transactionId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="Edit" ' + disabled + '>' +
                                '</form>';
                        } else if (full.transType === 'Q') {
                            return '<form action="editquottrans" method="post">' +
                                '<input type="hidden" name="id" value="' + full.transactionId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="Edit" ' + disabled + '>' +
                                '</form>';
                        } else if (full.transType === 'C') {
                            return '<form action="editClients" method="post">' +
                                '<input type="hidden" name="id" value="' + full.transactionId + '">' +
                                '<input type="submit" class="btn btn-success btn-info btn-sm" value="Edit" ' + disabled + '>' +
                                '</form>';
                        }
                    }
                }
            ]
        }));
        return currTable;
    };

    var updateBranchesDoughnut = function () {
        if ($('#branchesDoughnut').length) {

            $.ajax({
                url: 'home/branchPremium',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    var branches = [];
                    var production = [];
                    for (var i = 0; i < s.length; i++) {
                        branches.push(s[i].branch);
                        production.push(s[i].premium);
                    }
                    var ctx = document.getElementById("branchesDoughnut");
                    var data = {
                        labels: branches,
                        datasets: [{
                            data: production,
                            backgroundColor: [
                                "#AF144B",
                                "#9B59B6",
                                "#BDC3C7",
                                "#26B99A",
                                "#3498DB"
                            ],
                            hoverBackgroundColor: [
                                "#AF144B",
                                "#B370CF",
                                "#CFD4D8",
                                "#36CAAB",
                                "#49A9EA"
                            ]

                        }]
                    };

                    var canvasDoughnut = new Chart(ctx, {
                        type: 'doughnut',
                        tooltipFillColor: "rgba(51, 51, 51, 0.55)",
                        data: data
                    });

                },
                error: function (xhr, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });
        }
    };

    var updateProductsDoughnut = function () {
        if ($('#productsDoughnut').length) {

            $.ajax({
                url: 'home/productPremium',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    var products = [];
                    var production = [];
                    for (var i = 0; i < s.length; i++) {
                        products.push(s[i].product);
                        production.push(s[i].premium);
                    }
                    var ctx = document.getElementById("productsDoughnut");
                    var data = {
                        labels: products,
                        datasets: [{
                            data: production,
                            backgroundColor: [
                                "#AF144B",
                                "#9B59B6",
                                "#BDC3C7",
                                "#26B99A",
                                "#3498DB"
                            ],
                            hoverBackgroundColor: [
                                "#AF144B",
                                "#B370CF",
                                "#CFD4D8",
                                "#36CAAB",
                                "#49A9EA"
                            ]

                        }]
                    };

                    var canvasDoughnut = new Chart(ctx, {
                        type: 'doughnut',
                        tooltipFillColor: "rgba(51, 51, 51, 0.55)",
                        data: data
                    });

                },
                error: function (xhr, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });


        }
    };

    var updateProductionChart = function () {
        if ($('#lineChart').length) {

            $.ajax({
                url: 'home/premiumProduction',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    var months = [];
                    var production = [];
                    for (var i = 0; i < s.length; i++) {
                        months.push(s[i].month + "/" + s[i].year);
                        production.push(s[i].premium);
                    }
                    var ctx = document.getElementById("lineChart");
                    var lineChart = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: months,
                            datasets: [{
                                label: "Premium Production for the Year",
                                backgroundColor: "rgba(0,51,102)",
                                data: production
                            }]
                        },
                        options: {
                            scales: {
                                yAxes: [{
                                    ticks: {
                                        callback: function (value, index, values) {
                                            if (parseInt(value) > 999) {
                                                return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                                            } else if (parseInt(value) < -999) {
                                                return Math.abs(value).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                                            } else {
                                                return value;
                                            }
                                        }
                                    }
                                }]
                            }
                        }
                    });

                },
                error: function (xhr, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });


        }
    };

    var getDashboardDetails = function () {
        $.ajax({
            url: 'home/dashboardDetails',
            type: 'GET',
            async: true,
            processData: false,
            contentType: false,
            success: function (s) {
                $("div.pending-quote").html(UTILITIES.currencyFormat(s.sumAssuredYTD));
                $("div.expired-pols").html(s.policiesSold);
                $("div.pend-endorse").html(UTILITIES.currencyFormat(s.premiumYTD));

            },
            error: function (xhr, error) {
                Swal.fire({
                    title: 'Error',
                    text: xhr.responseText,
                    icon: 'error'
                });
            }
        });
    };

    var init_calendar = function () {
        if (typeof ($.fn.fullCalendar) === 'undefined') {
            return;
        }

        var date = new Date(),
            d = date.getDate(),
            m = date.getMonth(),
            y = date.getFullYear(),
            started,
            categoryClass;

        var calendar = $('#calendar').fullCalendar({
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,agendaWeek,agendaDay,listMonth'
            },
            selectable: true,
            selectHelper: true,
            select: function (start, end, allDay) {
                $('#fc_create').click();

                started = start;
                var ended = end;

                $(".antosubmit").on("click", function () {
                    var title = $("#title").val();
                    if (end) {
                        ended = end;
                    }

                    categoryClass = $("#event_type").val();

                    if (title) {
                        calendar.fullCalendar('renderEvent', {
                                title: title,
                                start: started,
                                end: end,
                                allDay: allDay
                            },
                            true // make the event "stick"
                        );
                    }

                    $('#title').val('');

                    calendar.fullCalendar('unselect');

                    $('.antoclose').click();

                    return false;
                });
            },
            eventClick: function (calEvent, jsEvent, view) {
                $('#fc_edit').click();
                $('#title2').val(calEvent.title);

                categoryClass = $("#event_type").val();

                $(".antosubmit2").on("click", function () {
                    calEvent.title = $("#title2").val();

                    calendar.fullCalendar('updateEvent', calEvent);
                    $('.antoclose2').click();
                });

                calendar.fullCalendar('unselect');
            },
            editable: true,
            events: [{
                title: 'All Day Event',
                start: new Date(y, m, 1)
            }, {
                title: 'Long Event',
                start: new Date(y, m, d - 5),
                end: new Date(y, m, d - 2)
            }, {
                title: 'Meeting',
                start: new Date(y, m, d, 10, 30),
                allDay: false
            }, {
                title: 'Lunch',
                start: new Date(y, m, d + 14, 12, 0),
                end: new Date(y, m, d, 14, 0),
                allDay: false
            }, {
                title: 'Birthday Party',
                start: new Date(y, m, d + 1, 19, 0),
                end: new Date(y, m, d + 1, 22, 30),
                allDay: false
            }, {
                title: 'Click for Google',
                start: new Date(y, m, 28),
                end: new Date(y, m, 29),
                url: 'http://google.com/'
            }]
        });
    };
    const initTabPersistence = function() {
        let tables = {};


        function initTableRefs() {
            tables = {
                '#tab_content1': getUserTasks(),
                '#tab_content2': getUserTickets(),
                '#tab_content3': getUserPortfolio(),
                '#tab_content4': getUserReceipts(),
                '#tab_content6': function () {
                    if (!$.fn.DataTable.isDataTable('#maker-tasks')) {
                        return getMakerTasks();
                    }
                    return $('#maker-tasks').DataTable();
                },
                '#tab_content8': getUserClaims()
            };
        }

        function getDefaultActiveTab() {
            if ($('#tab_content4').length && $('#tab_content4').hasClass('active')) {
                return '#tab_content4'; // Checker users
            } else if ($('#tab_content6').length && $('#tab_content6').hasClass('active')) {

                return '#tab_content6'; // Maker users
            }

            return '#tab_content1'; // Fallback
        }


        function loadActiveTab() {
            const serverActiveTab = $('.tab-content .tab-pane.active').attr('id');


            let activeTab = localStorage.getItem('activeTab');
            const isCheckerTabAvailable = $('#tab_content4').length > 0;
            const isMakerTabAvailable = $('#tab_content6').length > 0;

            if (activeTab) {
                if ((activeTab === '#tab_content4' && !isCheckerTabAvailable) ||
                    (activeTab === '#tab_content6' && !isMakerTabAvailable) ||
                    !$(`a[href="${activeTab}"]`).length) {

                    localStorage.removeItem('activeTab');
                    activeTab = null;
                }
            }

            if (!activeTab && serverActiveTab) {
                activeTab = '#' + serverActiveTab;

            } else if (!activeTab) {
                activeTab = getDefaultActiveTab();

            } else {

            }

            // Ensure the selected tab exists
            if (!$(`a[href="${activeTab}"]`).length) {

                activeTab = getDefaultActiveTab();
            }

            localStorage.setItem('activeTab', activeTab);

            const currentActive = $('.tab-content .tab-pane.active').attr('id');
            if ('#' + currentActive !== activeTab) {

                $('.nav-tabs .active').removeClass('active');
                $('.tab-content .active').removeClass('active');
                const $tabLink = $(`a[href="${activeTab}"]`);
                $tabLink.parent().addClass('active');
                $(activeTab).addClass('active').addClass('in');
                try {
                    $tabLink.tab('show');

                } catch (e) {

                }
            } else {

            }

            if (tables[activeTab] && typeof tables[activeTab] === 'function') {

                tables[activeTab]();
            }
        }

        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            const activeTab = $(e.target).attr('href');

            $('.nav-tabs .active').removeClass('active');
            $('.tab-content .active').removeClass('active in');
            $(e.target).parent().addClass('active');
            $(activeTab).addClass('active in');
            localStorage.setItem('activeTab', activeTab);
            if (tables[activeTab] && typeof tables[activeTab] === 'function') {
                tables[activeTab]();
            }
        });


        initTableRefs();
        loadActiveTab();
    };

    var init = function () {
        // Initialize charts
        getDashboardDetails();
        updateProductionChart();
        updateProductsDoughnut();
        updateBranchesDoughnut();

        // Setup tab persistence and initialize tables
        initTabPersistence();

        // Refresh active table every 5 minutes
        // setInterval(function() {
        //     const activeTab = localStorage.getItem('activeTab') || '#tab_content1';
        //     const tableId = $(activeTab).find('table').attr('id');
        //     if (tableId) {
        //         $('#' + tableId).DataTable().ajax.reload(null, false);
        //     }
        // }, 300000);
    };

    return {
        init: init
    }

})(jQuery);

jQuery(HomeScreen.init);