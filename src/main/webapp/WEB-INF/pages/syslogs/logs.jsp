<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="x_panel">
    <div class="x_title">
        <h4>System Logs</h4>
    </div>
    <form id="search-form" class="form-horizontal">
        <div class="item form-group">

            <div class="col-md-4 col-xs-12">
                <label for="from-date" class="col-md-5 label-align">Date
                    From</label>

                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="wef-date">
                        <input type='text' class="form-control float-right"
                               id="from-date" required />
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-xs-12">
                <label for="to-date" class="col-md-5 label-align">Date
                    To</label>

                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="wet-date">
                        <input type='text' class="form-control float-right"
                               id="to-date" required />
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-2 col-xs-12">
                <input type="button" class="btn btn-info float-right"
                       style="margin-right: 10px;" value="Search"
                       id="btn-search-logs">
            </div>
        </div>



    </form>
    <hr>
        <table id="users_tbl" class="table" style="width:100%">

            <thead>
            <tr>
                <th class="th-sm">User</th>
                <th  class="th-sm">Action</th>
                <th class="th-sm">Time</th>
                <th class="th-sm">Address</th>
                <th class="th-sm">Machine Name</th>
            </tr>
            </thead>
        </table>
</div>

<script type="application/javascript">
    $(document).ready(function() {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD-MM-YYYY'
            });

        });
        $("#btn-search-logs").on('click', function () {
            if (!$("#from-date").val() || !$("#to-date").val()) {
                Swal.fire({
                    title: 'Error',
                    text: 'Select Date Range to view Logs..',
                    icon: 'error'
                });
                return;
            }
            $("#users_tbl tbody").each(function () {
                $(this).remove();
            });
            $.ajax({
                type: 'GET',
                url: 'allsyslogs/' + $("#from-date").val()+'/'+ $("#to-date").val(),
                dataType: 'json',
                async: true,
                success: function (result) {
                    for (var res in result) {
                        var markup = "<tr><td>" + result[res].username + "</td><td>" + result[res].action + "</td><td>"
                                    + moment(result[res].timetamp).format('DD/MM/YYYY HH:mm:ss') + "</td><td>"
                                    + result[res].address + "</td><td>" + result[res].machinename +  "</td></tr>";
                        $("#users_tbl").append(markup);
                    }
                    $('#users_tbl').DataTable({
                        dom: 'Bfrtip',
                        "paging": true,
                        buttons: [
                            'excel', 'pdf', 'print'
                        ]
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR);
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                }
            });

        });


    });
</script>
