$(function() {


    $(document).ready(function () {

        showCheckerQuoteData();

        $("#btn-approve-quote").on('click', function (){
            approveTask();
        });

        $("#btn-reject-quot").on('click', function (){
            rejectTask();
        });
    });


    function rejectTask(){
        // Display the modal
        $('#rejectionModal').modal('show');

        $('#rejectConfirmButton').off('click').on('click', function() {
            var reason = $('#rejectionReason').val();

            if (reason) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        taskId: mck_id_receipt,
                        reason: reason
                    }),
                    success: function(response) {
                        window.location.href = SERVLET_CONTEXT + "/protected/home";

                    },
                    error: function(xhr, status, error) {
                        Swal.fire({
                            title: 'Error',
                            text: xhr.responseText,
                            icon: 'error'
                        });
                    }
                });
            } else {
                Swal.fire({
                    title: 'Error',
                    text: 'No reason provided for rejection',
                    icon: 'error'
                });
            }
            // Hide the modal after submission
            $('#rejectionModal').modal('hide');
        });
    }

    function approveTask(){
        // Display the modal
        $('#approvalModal').modal('show');
        $('#approveConfirmButton').off('click').on('click', function() {

            $("#btn-approve-quote").prop("disabled", true);
            $("#btn-reject-quot").prop("disabled", true);

            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while the task is being approved.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/approveTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(mck_id_receipt),
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Task Approved Successfully',
                        icon: 'success'
                    });
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                },
                error: function(xhr, status, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                },
                complete: function () {
                    $('#btn-approve-quote').prop('disabled', false);
                    $('#btn-reject-quot').prop('disabled', false);
                }
            });

            // Hide the modal after submission
            $('#approvalModal').modal('hide');
        });
    }

    function showCheckerQuoteData(){
        var mckIdNo = mck_id_receipt;  // Make sure mck_id_no is set
        var url = SERVLET_CONTEXT + "/protected/getCheckerData";
        var taskType = "receipt";

        $.ajax({
            url: url,
            method: 'GET',
            data: {
                mckIdNo: mckIdNo,
                taskType: taskType
            },
            success: function(data) {
                var clientData = (typeof data === "string") ? JSON.parse(data) : data;
                displayUnverifiedReceiptDetails(clientData);

            },
            error: function(xhr, status, error) {
                Swal.fire({
                    title: 'Error',
                    text: xhr.responseText,
                    icon: 'error'
                });
            }
        });
    }
    function displayUnverifiedReceiptDetails(s) {
        console.log(s)
        if ($.fn.DataTable.isDataTable('#rct-detail-tbl')) {
                $('#rct-detail-tbl').DataTable().clear().destroy();
        }
        console.log(s)
        if (s.receiptType === "N" || s.receiptType === "COM") {
            if(s.receiptType === "N")
              $("#selreceiptType").text("General Insurance");
            else if(s.receiptType === "COM"){
              $("#selreceiptType").text("Commissions");
            }

            if (s.transactions.length !== 0) {
                $('#rct-detail-tbl').DataTable({
                    data: s.transactions, // Use the transactions array as the data source
                    columns: [
                        {data: 'transno', title: 'Trans No'},
                        {
                            data: null, // Custom rendering logic for Policy Number
                            title: 'Policy Number',
                            render: function (data, type, row) {
                                let valueToRender = null;

                                // Use switch to check available fields
                                switch (true) {
                                    case !!row.policy && !!row.policy.polNo:
                                        valueToRender = row.policy.polNo;
                                        break;
                                    case !!row.narrations:
                                        valueToRender = row.narrations;
                                        break;
                                    // Add more cases as needed in the future
                                    default:
                                        valueToRender = ''; // No valid value available
                                }

                                return valueToRender; // Return the selected value or an empty string
                            }
                        },
                        {
                            data: null, // Custom rendering logic for dates
                            title: 'Date',
                            render: function (data, type, row) {
                                let dateToRender = null;

                                // Use switch to check available date fields
                                switch (true) {
                                    case !!row.authDate:
                                        dateToRender = row.authDate;
                                        break;
                                    case !!row.transDate:
                                        dateToRender = row.transDate;
                                        break;
                                    // Add more cases as needed in the future
                                    default:
                                        dateToRender = null; // No date available
                                }

                                // If a date is available, format it
                                if (dateToRender) {
                                    let date = new Date(dateToRender); // Convert the timestamp to a Date object
                                    const day = String(date.getDate()).padStart(2, '0');
                                    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based
                                    const year = date.getFullYear();
                                    return `${day}/${month}/${year}`; // Return formatted date
                                }

                                return ''; // Return empty if no date is available
                            }
                        },
                        {
                            data: null, // Custom rendering for client full name
                            title: 'Client',
                            render: function (data, type, row) {
                                if (row.client && row.client.fname && row.client.otherNames) {
                                    return row.client.fname + ' ' + row.client.otherNames;
                                } else if (row.client && row.client.fname) {
                                    return row.client.fname; // return fname if otherNames is missing
                                } else if (row.payeeName) {
                                    return row.payeeName; // Return payeeName for commission receipts
                                }
                                return '';
                            }
                        },
                        {
                            data: 'netAmount',
                            title: 'Balance',
                            render: function (data) {
                                if (data != null) {
                                    return parseFloat(data).toLocaleString('en-US', {
                                        minimumFractionDigits: 2,
                                        maximumFractionDigits: 2
                                    });
                                }
                                return '0.00';
                            }
                        },

                        {
                            data: null, // Custom rendering for the Allocated Amount
                            title: 'Allocated Amount',
                            render: function (data, type, row, meta) {
                                // Use the index to get the corresponding details for the transaction
                                const index = meta.row; // Get the index of the current row
                                if (s.details && s.details.length > index) {
                                    const amt = s.details[index].rctAmount;
                                    return amt != null
                                        ? parseFloat(amt).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
                                        : '0.00';
                                    //details array aligns with transactions
                                }
                                return '';
                            }
                        }
                    ]
                });
            } else if (s.transactionsTemps.length !== 0) {
                $('#rct-detail-tbl').DataTable({
                    data: s.transactionsTemps, // Use the transactions array as the data source
                    columns: [
                        {data: 'tempTransno', title: 'Trans No'},
                        {
                            data: null, // Custom rendering logic for Policy Number
                            title: 'Policy Number',
                            render: function (data, type, row) {
                                let valueToRender = null;

                                // Use switch to check available fields
                                switch (true) {
                                    case !!row.policy && !!row.policy.polNo:
                                        valueToRender = row.policy.polNo;
                                        break;
                                    case !!row.narrations:
                                        valueToRender = row.narrations;
                                        break;
                                    // Add more cases as needed in the future
                                    default:
                                        valueToRender = ''; // No valid value available
                                }

                                return valueToRender; // Return the selected value or an empty string
                            }
                        },
                        {
                            data: null, // Custom rendering logic for dates
                            title: 'Date',
                            render: function (data, type, row) {
                                let dateToRender = null;

                                // Use switch to check available date fields
                                switch (true) {
                                    case !!row.authDate:
                                        dateToRender = row.authDate;
                                        break;
                                    case !!row.transDate:
                                        dateToRender = row.transDate;
                                        break;
                                    // Add more cases as needed in the future
                                    default:
                                        dateToRender = null; // No date available
                                }

                                // If a date is available, format it
                                if (dateToRender) {
                                    let date = new Date(dateToRender); // Convert the timestamp to a Date object
                                    const day = String(date.getDate()).padStart(2, '0');
                                    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based
                                    const year = date.getFullYear();
                                    return `${day}/${month}/${year}`; // Return formatted date
                                }

                                return ''; // Return empty if no date is available
                            }
                        },
                        {
                            data: null, // Custom rendering for client full name
                            title: 'Client',
                            render: function (data, type, row) {
                                if (row.client && row.client.fname && row.client.otherNames) {
                                    return row.client.fname + ' ' + row.client.otherNames;
                                } else if (row.client && row.client.fname) {
                                    return row.client.fname; // return fname if otherNames is missing
                                } else if (row.payeeName) {
                                    return row.payeeName; // Return payeeName for commission receipts
                                }
                                return '';
                            }
                        },
                        {
                            data: 'netAmount',
                            title: 'Balance',
                            render: function (data) {
                                if (data != null) {
                                    return parseFloat(data).toLocaleString('en-US', {
                                        minimumFractionDigits: 2,
                                        maximumFractionDigits: 2
                                    });
                                }
                                return '0.00';
                            }
                        },

                        {
                            data: null, // Custom rendering for the Allocated Amount
                            title: 'Allocated Amount',
                            render: function (data, type, row, meta) {
                                // Use the index to get the corresponding details for the transaction
                                const index = meta.row; // Get the index of the current row
                                if (s.details && s.details.length > index) {
                                    const amt = s.details[index].rctAmount;
                                    return amt != null
                                        ? parseFloat(amt).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
                                        : '0.00';

                                }
                                return '';
                            }
                        }
                    ]
                });

            }
        }
        else if (s.receiptType === "L"){
            $("#selreceiptType").text("Life Insurance");
            $('#rct-detail-tbl').DataTable({
                data: s.policyTrans, // Use the transactions array as the data source
                columns: [
                    {
                        data: null, // Custom rendering for the Allocated Amount
                        title: 'Trans No',
                        render: function (data, type, row, meta) {
                            // Use the index to get the corresponding details for the transaction
                            const index = meta.row; // Get the index of the current row
                            if (s.details && s.details.length > index) {
                                return s.details[index].transNo; // Assuming details array aligns with transactions
                            }
                           return ''; // Return empty if no details
                        }
                    },
                    {
                        data: 'polNo',
                        title: 'Policy Number',
                        render: function(data, type, row) {
                            if (data === "" || data === null || data === undefined) {
                                return row.proposalNo; // Return proposalNo if polNo is empty
                            }
                            return data; // Return the actual policy number if it's not empty
                        }
                    },
                    {
                        data: 'wefDate',
                        title: 'Date',
                        render: function (data, type, row) {
                            // Convert the timestamp to a readable date format
                            if (data) {
                                let date = new Date(data); // Convert the timestamp to a Date object
                                const day = String(date.getDate()).padStart(2, '0');
                                const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based
                                const year = date.getFullYear();
                                return `${day}/${month}/${year}`;
                            }
                            return ''; // Return empty if no date available
                        }
                    },
                    {
                        data: null, // Custom rendering for client full name
                        title: 'Client',
                        render: function (data, type, row) {
                            if (row.client && row.client.fname && row.client.otherNames) {
                                return row.client.fname + ' ' + row.client.otherNames; // Concatenate fname and otherNames
                            } else if (row.client && row.client.fname) {
                                return row.client.fname; // Just return fname if otherNames is missing
                            }
                            return ''; // Return empty if client or fname is missing
                        }
                    },
                    {
                        data: 'premium',
                        title: 'Balance',
                        render: function (data) {
                            if (data != null) {
                                return parseFloat(data).toLocaleString('en-US', {
                                    minimumFractionDigits: 2,
                                    maximumFractionDigits: 2
                                });
                            }
                            return '0.00';
                        }
                    },

                    {
                        data: null, // Custom rendering for the Allocated Amount
                        title: 'Allocated Amount',
                        render: function (data, type, row, meta) {
                            // Use the index to get the corresponding details for the transaction
                            const index = meta.row; // Get the index of the current row
                            if (s.details && s.details.length > index) {
                                const amt = s.details[index].rctAmount;
                                return amt != null
                                    ? parseFloat(amt).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
                                    : '0.00';


                            }
                            return ''; // Return empty if no details
                        }
                    }
                ]
            });
        }

        if(s && s.collectionAccount){
            $(".collect-acct").css('display','block');
            $("#coll-div").text(s.collectionAccount.name);
            $("#pymt-mode").text(s.collectionAccount.paymentModes.pmDesc);
            $("#currency-desc").text(s.collectionAccount.currencies.curName);
            $("#bank-desc").text(s.collectionAccount.bankBranches.branchName+" - "+s.collectionAccount.bankBranches.bank.bankName);
        }
        else{
            $(".collect-acct").css('display','none');
        }
        $("#brn-frm").text(s.branch.obName);
        $("#rct-amount").text(s.receiptAmount.toLocaleString('en-US'));
        $("#narration").text(s.receiptDesc);
        $("#receipt-date").text(s.receiptDate);
        $("#payment-ref").text(s.paymentRef);
        $("#doc-date").text(s.documentDate);
        $("#manual-ref").text(s.manualRef);
        $("#paid-by").text(s.paidBy);
        let insurerName = "Not available";

        if (s.receiptType === "N" || s.receiptType === "COM") {
            // General insurance - get from transactionsTemps[0].policy.agent.name
            if (s.transactionsTemps?.[0]?.policy?.agent?.name) {
                insurerName = s.transactionsTemps[0].policy.agent.name;
            }
        } else if (s.receiptType === "L") {
            // Life insurance - get from policyTrans[0].agent.name
            if (s.policyTrans?.[0]?.agent?.name) {
                insurerName = s.policyTrans[0].agent.name;
            }
        }

        // Display the insurer name
        $("#insurer").text(insurerName);

    }


});