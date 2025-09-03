<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/bedtypes.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-bed-types">New</button>
    <div class="x_title">
        <h4>Bed Types</h4>
    </div>
    <div class="cutom-container">
    <table id="bed-type-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Bed Type ID</th>
            <th>Bed Type Desc</th>
            <th>Bed Cost</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="bedTypesModal" tabindex="-1" role="dialog"
     aria-labelledby="bedTypesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="bedTypesModalLabel">
                    Edit/Add Bed Type
                </h4>
            </div>
            <div class="modal-body" id="branch_model">
                <form id="bed-type-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="bed-type-pk" name="bedId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bed Type ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="bed-type-id"
                                   name="bedShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bed Type</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="bed-type-desc"
                                   name="bedDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bed Cost</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="bed-cost"
                                   name="bedCost"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBedType"
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
