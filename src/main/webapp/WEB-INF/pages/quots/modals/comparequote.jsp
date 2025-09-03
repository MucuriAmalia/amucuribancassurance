<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="compareQuotModal" tabindex="-1" role="dialog"
     aria-labelledby="compareQuotModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="compareQuotModalLabel">
                    Select Comparison Contract(s)
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">

                <form id="compare-quot-form" class="form-horizontal">
                    <div class="form-group">
                        <input type="hidden" class="form-control" id="quot-id" value="quotId">
                        <label for="comp-binder-frm" class="control-label col-md-3">
                            Insurance Provider<span class="required">*</span></label>
                        <div class="col-md-9 col-xs-12">

                            <div id="comp-binder-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwCompBinders"/>">

                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveComparisonBtn"
                        type="button" class="btn btn-success">Save</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="compQuoteSelectModal" tabindex="-1" role="dialog"
     aria-labelledby="compQuoteSelectModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="compQuoteSelectModalLabel">
                    Select Quote to Submit
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div class="table-responsive" style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="compQuotSelectTable">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th>Insurance Contract</th>
                            <th>Premium</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="comp-quote-form">
                    <input type="hidden" id="quote-id" name="quotId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSelectedContract"
                        type="button" class="btn btn-primary">
                    Submit
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
