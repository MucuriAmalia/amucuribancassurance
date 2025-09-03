<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/benefits.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-benefits">New</button>
    <div class="x_title">
        <h4>Benefits</h4>
    </div>
    <div class="cutom-container">
    <table id="benefits-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Benefits ID</th>
            <th>Benefits Desc</th>
            <th>Pre Authored Benefits</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="benefitsModal" tabindex="-1" role="dialog"
     aria-labelledby="benefitsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="bedTypesModalLabel">
                    Edit/Add Benefits
                </h4>
            </div>
            <div class="modal-body">
                <form id="benefits-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="benefit-pk" name="benId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Benefit ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="benefit-id"
                                   name="benShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Benefit Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="benefit-desc"
                                   name="benDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Pre Authored?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="preauthored" id="chk-pre-authored">
                            </label>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBenefits"
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

