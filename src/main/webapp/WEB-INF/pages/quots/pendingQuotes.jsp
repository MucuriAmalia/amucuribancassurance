<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h4>Pending Quotes Enquiry</h4>
    </div>
    <div class="table-responsive">
        <table id="quot_enquiry" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">
                <th>Quot No</th>
                <th>Quoted Premium</th>
                <th>Product</th>
                <th>Quote Date</th>
                <th>Quote Status</th>
                <th>Quote Expiry Date</th>
                <th></th>
            </tr>
            </thead>
        </table>
        </div>
</div>

<script>
    $(function() {

        $(document).ready(function () {
            getPendingQuotes();
        });

        function getPendingQuotes(){

                var url = SERVLET_CONTEXT+"/protected/home/pendingQuotes";
            return $('#quot_enquiry').DataTable({
                    "processing": true,
                    "serverSide": true,
                    autoWidth: true,
                    dom: "Bfrtip",
                    "deferRender": true,
                    "ajax": {
                        'url': url
                    },
                    buttons: [
                        'excel', 'pdf', 'print'
                    ],
                    lengthMenu: [[10, 15, 20], [10, 15, 20]],
                    pageLength: 10,
                    destroy: true,
                    "columns": [
                        {"data": "quotNo"},
                        {
                            "data": "premium",
                            "render": function (data, type, full, meta) {
                                return UTILITIES.currencyFormat(full.premium);
                            }
                        },
                        {"data": "product"},
                        {
                            "data": "quotDate",
                            "render": function (data, type, full, meta) {
                                return moment(full.quotDate).format('DD/MM/YYYY');
                            }
                        },
                        {
                            "data": "status",
                            "render": function (data, type, full, meta) {
                                if (full.status) {
                                    if (full.status === 'C') {
                                        return "Confirmed";
                                    } else if (full.status === 'R') {
                                        return "Ready";
                                    } else if (full.status === 'A') {
                                        return "Authorized";
                                    } else if (full.status === 'CL') {
                                        return "Cancelled";
                                    } else if (full.status === 'D') {
                                        return "Draft";
                                    } else return full.status;
                                } else {
                                    return full.status;
                                }
                            }
                        },
                        {
                            "data": "expiryDate",
                            "render": function (data, type, full, meta) {
                                return moment(full.expiryDate).format('DD/MM/YYYY');
                            }
                        },
                        {
                            "data": "quoteId",
                            "render": function ( data, type, full, meta ) {
                                return '<form action="editquottrans" method="post"><input type="hidden" name="id" value='+full.quoteId+'><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                            }

                        },
                    ]
                });
        }

    });
</script>