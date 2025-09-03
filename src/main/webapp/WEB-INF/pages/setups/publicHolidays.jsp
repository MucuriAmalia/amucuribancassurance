<%--
  Created by IntelliJ IDEA.
  User: joan
  Date: 15/04/2025
  Time: 10:59
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-holiday">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Public Holidays</h2>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
        <table id="holidays-tbl" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">
                <th>Holiday Name</th>
                <th>Date</th>
                <th>Delete</th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="holidayModal" tabindex="-1" role="dialog"
     aria-labelledby="holidayModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <h4 class="modal-title" id="holidayModalLabel">
                Edit/Add Public Holiday
            </h4>
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="holiday_model">
                <form id="holiday-form" class="form-horizontal">
                    <input type="hidden" name="holidayId" id="holiday-id">
                    <div class="item form-group">
                        <label for="holiday-name" class="col-md-3 label-align">Holiday Name</label>
                        <div class="col-md-8">
                            <input type="text" name="holidayName" id="holiday-name"
                                   class="form-control" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="holiday-date" class="col-md-3 label-align">Date</label>
                        <div class="col-md-8">
                            <input type="date" name="holidayDate" id="holiday-date"
                                   class="form-control" required>
                        </div>

                    </div>
                    <div class="item form-group">
                        <label for="is-recurring" class="col-md-3 label-align">Recurring</label>
                        <div class="col-md-8">
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" id="is-recurring" name="isRecurring" value="Y"> Yes (every year)
                                </label>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveHoliday"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script>
    $(function() {
        $(document).ready(function () {
            loadHolidays();
            bindHolidayModal();
            saveHoliday();
        });
    });

    function bindHolidayModal() {
        $("#btn-add-holiday").on("click", function(){
            $('#holiday-form').find("input[type=text],input[type=date],input[type=hidden]").val("");
            $('#holiday-id').val("");
            $('#holidayModal').modal('show');
        });

        $('#holidayModal').on('hidden.bs.modal', function () {
            $('#holiday-form').find("input[type=text],input[type=date],input[type=hidden]").val("");
        });
    }



    function saveHoliday() {
        var $form = $('#holiday-form');
        var validator = $form.validate();

        $('#saveHoliday').click(function(){
            if (!$form.valid()) {
                return;
            }

            var $btn = $(this).button('Saving');
            var data = {};
            $form.serializeArray().map(function(x){data[x.name] = x.value;});

            var url = "createPublicHolidays";
            var request = $.post(url, data);

            request.done(function() {
                Swal.fire({
                    title: 'Success',
                    text: 'Public holiday saved successfully',
                    icon: 'success'
                });
                $('#holidays-tbl').DataTable().ajax.reload();
                validator.resetForm();
                $('#holidayModal').modal('hide');
            });

            request.error(function(jqXHR,textStatus, errorThrown) {
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


    function editHoliday(button) {
        var holiday = JSON.parse(decodeURI($(button).data("holiday")));
        $("#holiday-id").val(holiday["holidayId"]);
        $("#holiday-name").val(holiday["holidayName"]);
        $("#holiday-date").val(holiday["holidayDate"]);
        $('#holidayModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }


    function deleteHoliday(button) {
        var holiday = JSON.parse(decodeURI($(button).data("holiday")));
        bootbox.confirm("Are you sure you want to delete this holiday?", function (result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'deletePublicHoliday/' + holiday.holidayId,
                    dataType: 'json',
                    async: true,
                    success: function () {
                        Swal.fire({
                            title: 'Success',
                            text: 'Holiday deleted successfully',
                            icon: 'success'
                        });
                        $('#holidays-tbl').DataTable().ajax.reload();
                    },
                    error: function (jqXHR,textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                    }
                });
            }
        });
    }

    function loadHolidays() {
        $('#holidays-tbl').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: 'loadPublicHolidays',
                type: 'GET',
                data: function (d) {
                    return d;
                }
            },
            destroy: true,
            lengthMenu: [[10, 15], [10, 15]],
            pageLength: 10,
            columns: [
                { data: "holidayName" },
                { data: "holidayDate" },
                {
                    data: "holidayId",
                    render: function (data, type, full) {
                        // Create a clean holiday object
                        var holidayData = {
                            holidayId: full.holidayId,
                            holidayName: full.holidayName,
                            holidayDate: full.holidayDate
                        };

                        // Stringify and escape single quotes to prevent HTML issues
                        var holidayJson = encodeURI(JSON.stringify(holidayData));
                        return '<button type="button" class="btn btn-danger btn-sm" data-holiday="' + holidayJson + '" onclick="deleteHoliday(this);"><i class="fa fa-trash-o"></i></button>';
                    }
                }
            ]
        });
    }
</script>
