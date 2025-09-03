<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/eventtype.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-event-types">New</button>
    <div class="x_title">
        <h4>Event Types</h4>
    </div>
    <div class="cutom-container">
    <table id="event-type-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Event Type ID</th>
            <th>Event Type Desc</th>
            <th>Type</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="eventTypesModal" tabindex="-1" role="dialog"
     aria-labelledby="eventTypesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="eventTypesModalLabel">
                    Edit/Add Event Type
                </h4>
            </div>
            <div class="modal-body">
                <form id="event-type-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="event-type-pk" name="eventId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Event Type ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="event-type-id"
                                   name="eventShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Event Type</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="event-type-desc"
                                   name="eventDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">
                             Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="event-type" name="type"
                                    required>
                                <option value="">Select  Type</option>
                                <option value="A">Accident</option>
                                <option value="I">Illness</option>
                                <option value="D">Death</option>
                            </select>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveEventType"
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
