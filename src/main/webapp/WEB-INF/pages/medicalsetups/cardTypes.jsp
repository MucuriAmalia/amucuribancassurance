<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/bedtypes.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-card-types">New</button>
    <div class="x_title">
        <h4>Card Types</h4>
    </div>
    <div class="cutom-container">
        <table id="card-type-tbl" class="table" style="width:100%">
            <thead>
            <tr>

                <th>Card Type ID</th>
                <th>Card Type Desc</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<div class="modal fade" id="cardTypesModal" tabindex="-1" role="dialog"
     aria-labelledby="cardTypesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="cardTypesModalLabel">
                    Edit/Add Card Type
                </h4>
            </div>
            <div class="modal-body" id="branch_model">
                <form id="card-type-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="card-type-pk" name="cardId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Card Type ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="card-type-id"
                                   name="cardShtDesc"  required style="text-transform:uppercase">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Card Type</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="card-type-desc"
                                   name="cardDesc"  required style="text-transform:uppercase">
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCardType"
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
