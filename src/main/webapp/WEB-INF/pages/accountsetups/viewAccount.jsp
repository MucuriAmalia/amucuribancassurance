<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript">
    var accIdView = ${accIdView};

	function unlockForm() {
		if (accIdView !== -2000 && accIdView !== 'undefined') {
			navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
					new URLSearchParams({"formName": "INSURER/AGENT", "formId": accIdView})
			);
			console.log("Form unlock request sent.");
		}
	}

	window.addEventListener("beforeunload", unlockForm);

</script>
<div class="x_title">
	<h2><i class="fa fa-bars"></i> View Insurance Company/Sub-Agent</h2>
	<div class="clearfix"></div>

</div>
<div id="editISPButton">
    </div>
<div class="x_panel" id="acct_model">

    <div class="col-md-9 col-sm-9  offset-md-3">
            <h4 class="float-left blue" style="font-weight: bolder" id="h4pol">Name: </h4>
    </div>

	<div class="" role="tabpanel" data-example-id="togglable-tabs">
		<ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
			<li role="presentation" class="active"><a href="#tab_content1"
													  id="home-tab" role="tab" data-toggle="tab"
													  aria-expanded="true">Details</a>
			</li>
			<li role="presentation" id="show-docs" style="display:none"><a href="#tab_content2"
																role="tab" id="profile-tab" data-toggle="tab"
																aria-expanded="false">Documents</a>
			</li>
			</ul>

			<div id="myTabContent" class="tab-content">
				<div role="tabpanel" class="tab-pane active"
					 id="tab_content1" aria-labelledby="home-tab">
	 <form id="account-form" class="form-horizontal" enctype="multipart/form-data">
		<div class="x_panel">
			<div class="item form-group">

			</div>
		</div>
		    <input type="hidden"  id="acctId-pk">
			<div class="item form-group form-required">
				<div class="col-md-6 col-xs-12">
					<label for="accounttypes" class="col-md-5 label-align">Select
						Intermediary Type<span class="required">*</span></label>
					<div class="col-md-7 col-xs-12">
						<p id="accounttypes" </p>
					</div>
				</div>
				<div class="col-md-6 col-xs-12">

				</div>

			</div>
		<div class="item form-group">
			<div class="col-md-6 col-xs-12">
				<label for="other-names" class="label-align col-md-5">
					Sht Desc</label>
				<div class="col-md-7 col-xs-12">
					<p type="text"id="other-names"></p>
				</div>
			</div>
			<div class="col-md-6 col-xs-12">
				<label for="fname" class="label-align col-md-5">
					Name<span class="required">*</span></label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="fname"></p>
				</div>
			</div>


		</div>
		 <div class="item form-group sub-agents">
			 <div class="col-md-6 col-xs-12">
				 <label for="id_No" class="label-align col-md-5">
					 ID No<span class="required">*</span></label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="id_No">
				 </div>
			 </div>
			 <div class="col-md-6 col-xs-12">
				 <label for="absa_No" class="label-align col-md-5">
					 AB No<span class="required">*</span></label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="absa_No">
				 </div>
			 </div>
		 </div>
		<div class="item form-group form-required inhouse-agents">
			<div class="col-md-6 col-xs-12">
				<label for="bank-dtls" class="label-align col-md-5">Bank</label>
				<div class="col-md-7 col-xs-12">
					<p type="text"id="bank-dtls"></p>
				</div>

			</div>
			<div class="col-md-6 col-xs-12">
				<label for="bank-branch-lov" class="label-align col-md-5">Bank Branch</label>
				<div class="col-md-7 col-xs-12">
					<p type="text"id="bank-branch-lov"></p>
				</div>

			</div>


		</div>
			<div class="item form-group form-required">
				<div class="col-md-6 col-xs-12">
					<label for="acct-acct-no" class="label-align col-md-5">Account Number</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="acct-acct-no"></p>
					</div>
				</div>

				<div class="col-md-6 col-xs-12">
					<label for="pinNo" class="label-align col-md-5">Pin No</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="pinNo"></p>
					</div>
				</div>

			</div>
			<div class="item form-group form-required">
				<div class="col-md-6 col-xs-12">
					<label for="email" class="label-align col-md-5">Email</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="email"></p>
					</div>
				</div>

				<div class="col-md-6 col-xs-12">
					<label for="phone-no" class="label-align col-md-5">Phone No<span class="required">*</span></label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="phone-no"></p>
					</div>
				</div>
			</div>

		 <div class="item form-group">
			 <div class="col-md-6 col-xs-12">
				 <label for="insurance-type" class="label-align col-md-5">Insurance Type</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="insurance-type"></p>
				 </div>
			 </div>
		 </div>

			<div class="item form-group inhouse-agents">
			    <div class="col-md-6 col-xs-12 form-required">
					<label for="address" class="label-align col-md-5">Postal Address</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="address"></p>
					</div>

				</div>
				<div class="col-md-6 col-xs-12">
				    <label for="phy-address" class="label-align col-md-5">Physical Address</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="phy-address"></p>
					</div>
				</div>
			</div>

			<div class="item form-group inhouse-agents">
			    <div class="col-md-6 col-xs-12">
					<label for="cont-title" class="label-align col-md-5">Contact Title</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="cont-title"></p>
					</div>
				</div>
				<div class="col-md-6 col-xs-12">
					<label for="contact-person" class="label-align col-md-5">Contact Person</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="contact-person"></p>

					</div>
				</div>
			</div>
			<div class="item form-group form-required">
				<div class="col-md-6 col-xs-12">
					<label for="sel2" class="label-align col-md-5">Status</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="sel2"></p>
					</div>
				</div>
				<div class="col-md-6 col-xs-12">
						<label for="acct-branch" class="label-align col-md-5">Branch</label>
					<div class="col-md-7 col-xs-12">
					    <p type="text"id="acct-branch"></p>
					</div>
				</div>
			</div>
		<div class="item form-group form-required inhouse-agents">
			<div class="col-md-6 col-xs-12">
				<label for="acct-paybill" class="label-align col-md-5">Pay Bill Number</label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="acct-paybill"></p>
				</div>

			</div>
			<div class="col-md-6 col-xs-12">
				<label for="acct-mob-tel" class="label-align col-md-5">Payment Tel Number</label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="acct-mob-tel"></p>
				</div>
			</div>
		</div>
		<div class="item form-group">
			<div class="col-md-6 col-xs-12">
				<label for="reg-wet" class="col-md-5 label-align" id="acct-date-reg">Date of Reg.</label>

				<div class="col-md-7 col-xs-12">
					<div class='input-group date datepicker-input'>
					    <p type="text"id="dob"></p>
						<input type='hidden' />
					</div>
				</div>
			</div>
			<div class="col-md-6 col-xs-12 inhouse-agents">
				<label for="pm-mode-frm" class="label-align col-md-5">
					Payment Mode</label>
				<div class="col-md-7 col-xs-12">
					<div id="edit-payment-mode">
					    <p type="text"id="pm-mode-frm"></p>

					</div>
				</div>

			</div>
		</div>
		<div class="item form-group form-required inhouse-agents">
			<div class="col-md-6 col-xs-12">
				<label for="integration-type" class="label-align col-md-5">Integration Type</label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="integration-type"></p>

				</div>

			</div>
			<div class="col-md-6 col-xs-12">
				<label for="integration-url" class="label-align col-md-5">Integration URL</label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="integration-url"></p>

				</div>
			</div>
		</div>
		<div class="item form-group form-required inhouse-agents">
			<div class="col-md-6 col-xs-12">
				<label for="integration-username" class="label-align col-md-5">Integration Username</label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="integration-username"></p>

				</div>

			</div>
			<div class="col-md-6 col-xs-12">
				<label for="integration-password" class="label-align col-md-5">Integration Password</label>
				<div class="col-md-7 col-xs-12">
				    <p type="text"id="integration-password"></p>

				</div>
			</div>
		</div>
		<div class="item form-group">
			<div class="col-md-6 col-xs-12  form-required">
				<label for="brn-id" class="col-md-5 label-align">Date
					Activated</label>

				<div class="col-md-7 col-xs-12">
					<div class='input-group date datepicker-input' id="wef-date">
					    <p type="text"id="from-date"></p>
						<input type='hidden'/>

					</div>
				</div>
			</div>
			<div class="col-md-6 col-xs-12">
				<label for="noOfUnits" class="label-align col-md-5">Date
					Deactivated</label>
				<div class="col-md-7 col-xs-12">
					<div class='input-group date datepicker-input' id="cover-to-date">
					    <p type="text"id="wet-date"></p>
                        <input type='hidden'/>

					</div>
				</div>

			</div>


		</div>
		 <div class="item form-group inhouse-agents">
			 <div class="col-md-6 col-xs-12  form-required">
				 <label for="edit-receivable-premium" class="col-md-5 label-align">Premium Receivable
					 Account</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="edit-receivable-premium"></p>
				 </div>
			 </div>
			 <div class="col-md-6 col-xs-12">
				 <label for="edit-payable-premium" class="label-align col-md-5">Premium Payable
					 Account</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="edit-payable-premium"></p>
				 </div>
			 </div>
		 </div>
		 <div class="item form-group inhouse-agents">
			 <div class="col-md-6 col-xs-12  form-required">
				 <label for="edit-whtx" class="col-md-5 label-align">Whtx
					 Account</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="edit-whtx"></p>
				 </div>
			 </div>
			 <div class="col-md-6 col-xs-12">
				 <label for="edit-comm" class="label-align col-md-5">Commission Receivable
					 Account</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="edit-comm"></p>
				 </div>
			 </div>
		 </div>
		 <div class="item form-group">
			 <div class="col-md-6 col-xs-12  form-required inhouse-agents">
				 <label for="edit-admin" class="col-md-5 label-align">Admin Fees
					 Account</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="edit-admin"></p>
				 </div>
			 </div>
			 <div class="col-md-6 col-xs-12 sub-agents">
				 <label for="commCheckbox" class="label-align col-md-5">
					 Commission Earning</label>
				 <div class="col-md-7 col-xs-12">
					 <p type="text" id="commCheckbox">
				 </div>
			 </div>
		 </div>
		<div class="item form-group">
			<div class="col-md-6 col-xs-12">
                <label for="file" class="label-align col-md-5" id="acct-photo">
                    Logo
                </label>
                <div class="col-md-7 col-xs-12">
                    <div class="kv-avatar center-block" id="acct-photo" style="width: 200px">

                    </div>
                </div>
            </div>

			<div class="col-md-6 col-xs-12">

			</div>
		</div>


	</form>
	</div>
				<div role="tabpanel" class="tab-pane fade"
					 id="tab_content2" aria-labelledby="profile-tab" style="display:none">
					<button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
					<div class="card-box table-responsive">
						<table id="accDocsList" class="table table-striped" style="width:100%">
							<thead>
							<tr>
								<th>Document ID</th>
								<th>Document Desc</th>
								<th>File Name</th>
								<th>Uploaded By</th>
								<th>Uploaded Date</th>
								<th>Verified By</th>
								<th>Verified Date</th>
								<th width="5%"></th>
							</tr>
							</thead>
						</table>
					</div>
				</div>
				</div>
			</div>
				</div>


<div class="modal fade" id="acctReqDocsModal" tabindex="-1" role="dialog"
	 aria-labelledby="acctReqDocsModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="acctReqDocsModalLabel">Select Required Docs</h4>
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">
				<form class="form-horizontal">
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">Document Name</label>

						<div class="col-md-6">
							<input type="text" class="form-control" id="doc-name-search"
							>
						</div>
						<div class="col-md-1">
							<button  id="searchDocuments"
									 type="button" class="btn btn-primary">
								Search
							</button>
						</div>
					</div>
				</form>
				<div style="height: 300px !important; overflow: scroll;">
					<table class="table table-striped table-hover table-bordered table-fixed" id="acctDocsTbl">
						<thead>
						<tr>
							<th width="1%"></th>
							<th width="4%">Document Id</th>
							<th width="12%">Document Name</th>
						</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
				<form id="acct-doc-form">
					<input type="hidden" id="req-acct-code" name="subCode"/>
				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saveAcctDocsBtn"
						type="button" class="btn btn-success">Save</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					Cancel</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="acctdocModal" tabindex="-1" role="dialog"
	 aria-labelledby="acctdocModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<form id="accts-doc-form" class="form-horizontal" enctype="multipart/form-data">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="acctdocModalLabel">Upload Account Document</h4>
					<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body">

					<input type="hidden" id="acct-doc-id" name="docId"/>
					<input type="hidden" id="reqd-doc-id" name="requiredDoc"/>
					<div class="item form-group">
						<label for="acct-doc-name" class="col-md-3 label-align">Document Type</label>

						<div class="col-md-8">
							<p class="form-control-static" id="acct-doc-name"></p>
						</div>
					</div>
					<div class="item form-group">
						<label for="acct-upload-name" class="col-md-3 label-align">Uploaded File Name</label>

						<div class="col-md-8">
							<p class="form-control-static" id="acct-upload-name"></p>
						</div>
					</div>
					<div class="item form-group">
						<label for="brn-id" class="col-md-4 label-align">Document</label>

						<div class="col-md-8">
							<div class="input-group col-xs-12">
								<input name="file" type="file" id="acct-avatar" required>
							</div>
						</div>
					</div>



				</div>
				<div class="modal-footer">
					<input  value="Upload"
							type="submit" class="btn btn-success" id="upload-doc-btn">

					</input>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						Close</button>
				</div>
			</div>
		</form>
	</div>
</div>

<script>

	$(function(){

		$(document).ready(function() {
			AccountDef.init();
		});

	});

	var AccountDef = (function($){
		'use strict';

		var pinNoElement = $('#pinNo');
		var model = {
			accounts: {
				accType:{
					accId:"",
				},
				branch:{
					brnCode:"",
				},
			}
		};

		var accountImage = function(id){
			$("#avatar").fileinput('refresh',{
				overwriteInitial: true,
				maxFileSize: 1500,
				showClose: false,
				showCaption: false,
				browseLabel: '',
				removeLabel: '',
				browseIcon: '<i class="fa fa-folder-open"></i>',
				removeIcon: '<i class="fa fa-times"></i>',
				removeTitle: 'Cancel or reset changes',
				elErrorContainer: '#kv-avatar-errors',
				msgErrorClass: 'alert alert-block alert-danger',
				defaultPreviewContent: '<img src="'+SERVLET_CONTEXT+'/protected/setups/accountImage/'+id+'"  style="width:180px">',
				layoutTemplates: {main2: '{preview} ' + ' {remove} {browse}'},
				allowedFileExtensions: ["jpg", "png", "gif"]
			});
		};

		var populateAccountDetails = function(data){
			$("#acctId-pk").val(data.acctId);
			$("#accounttypes").text(data.accountTypeName);
			$("#fname").text(data.name);
			$("#other-names").text(data.shtDesc);
			$("#bank-dtls").text(data.bankName);
			$("#bank-branch-lov").text(data.bankBranchName);
			$("#acct-acct-no").text(data.bankAccount);
			$("#idno").val(data.idPassportNo);
			$("#pinNo").text(data.pinNo);
			$("#email").text(data.email);
			$("#phone-no").text(data.phoneNo);
			$("#insurance-type").text(data.insuranceType || 'N/A');
			$("#address").text(data.address);
			$("#phy-address").text(data.physaddress);
			$("#cont-title").text(data.contactTitle);
			$("#contact-person").text(data.contactPerson);
			$("#absa_No").text(data.absaNo);
			$("#id_No").text(data.indNumber);
			$("#commCheckbox").text(data.commissionEarning);
			if(data.status && data.status==='A'){
				$("#sel2").text("Active");
			}
			else if(data.status && data.status==='D'){
				$("#sel2").text("Draft");
			}
			else if(data.status && data.status==='I'){
                $("#sel2").text("Inactive");
            }
			else if(data.status && data.status==='DA'){
                $("#sel2").text("Deactivated");
			}
			else if(data.status && data.status==='NA'){
				$("#sel2").text("Not Approved");
			}
			$("#h4pol").text("Name: "+data.name);
			$("#acct-branch").text(data.branchName);
			$("#acct-paybill").text(data.paybillNumber);
			$("#acct-mob-tel").text(data.payTelNo);
			if(data.dob)
				$("#dob").text(moment(data.dob).format('DD/MM/YYYY'));
			if(data.wef)
				$("#from-date").text(moment(data.wef).format('DD/MM/YYYY'));
			if(data.wet)
				$("#wet-date").text(moment(data.wet).format('DD/MM/YYYY'));
			$("#pm-mode-frm").text(data.paymentMode);
			if(data.integrationType && data.integrationType==='N'){
                $("#integration-type").text("No ISP Integration");
            }
            else if(data.integrationType && data.integrationType==='D'){
                $("#integration-type").text("JWT");
            }
            else if(data.integrationType && data.integrationType==='A'){
                $("#integration-type").text("OAUTH2");
            }
            else if(data.integrationType && data.integrationType==='I'){
                $("#integration-type").text("File Upload");
            }
            else if(!data.integrationType || data.integrationType===''){
                $("#integration-type").text("Integration details not captured");
            }

            if(!data.underwriterApiUrl || data.underwriterApiUrl===''){
                $("#integration-url").text("No Url");
            }
            else if(data.underwriterApiUrl){
                $("#integration-url").text(data.underwriterApiUrl);
            }
			if (data.receivableAccount != null) {
				$("#edit-receivable-premium").text(data.receivableAccount + "-" + data.receivableAccountName);
			}
			if (data.payableAccount != null) {
				$("#edit-payable-premium").text(data.payableAccount + "-" + data.payableAccountName);
			}
			if (data.whtxAccount != null) {
				$("#edit-whtx").text(data.whtxAccount + "-" + data.whtxAccountName);
			}
			if (data.commAccount != null) {
				$("#edit-comm").text(data.commAccount + "-" + data.commAccountName);
			}
			if (data.adminAccount != null) {
				$("#edit-admin").text(data.adminAccount + "-" + data.adminAccountName);
			}
            $("#integration-username").text(data.underwriterApiUsername);
            $("#integration-password").text(data.underwriterApiPassword);
            $("#acct-photo").html('<img src="' + data.logoUrl + '" alt="ISP Logo" style="width: 100%; height: auto;">');



			if(data.accountTypeId){
				if(data.accountTypeId==="IA" || data.accountTypeId==="SUB" || data.accountTypeId==="MRK" || data.accountTypeId==="INT"){
					$(".inhouse-agents").hide();
					$("#acct-photo").text("Photo");
					// $("#acct-date-reg").text("Date of Birth");
					$(".insurance-co").show();
				}
				else{
					$(".inhouse-agents").show();
					$(".sub-agents").hide();
					$("#acct-photo").text("Logo");
					$("#acct-date-reg").text("Date of Reg");
					$(".insurance-co").hide();
				}
			}


			accountImage(data.acctId);
			getAccountDocs(data.acctId);
			renderEditButton(data.acctId);
			console.log("from populate data",data.acctId)
			console.log("from populate data",accIdView)
		};
		var renderEditButton = function(id) {
		    console.log("from render btn",id)
            var editButtonHtml = '<form action="editAcctForm" method="post">' +
                                 '<input type="hidden" name="id" value="' + id + '">' +
                                 '<input type="submit" class="btn btn-success btn-info btn-sm" value="Edit">' +
                                 '</form>';
            $("#editISPButton").html(editButtonHtml);
        };

		var getAccountDetails = function(){
			if(typeof accIdView!== 'undefined'){
				if(accIdView!==-2000){
					$.ajax( {
						url: 'accounts/'+accIdView,
						type: 'GET',
						processData: false,
						contentType: false,
						success: function (s ) {
							$("#h4pol").show();
							populateAccountDetails(s);
							$("#show-docs").css("display","block");
							$("#tab_content2").css("display","block");
						},
						error: function(xhr, error){
							bootbox.alert(xhr.responseText);
						}
					});
				}
				else{
					$("#h4pol").hide();
					accountImage(-2000);
					$("#show-docs").css("display","none");
					$("#tab_content2").css("display","none");
					$("#btn-deactivate").css('display','none');
					$("#btn-reject").css('display','none');
					$("#btn-approve").css('display','none');
				}

			}
		};


		var getAccountDocs = function(acctId){
			var ajaxUrl = "accountDocs/"+acctId;
			var currTable = $('#accDocsList').DataTable(UTILITIES.extendsOpts({
				"ajaxUrl":ajaxUrl,
				"columns": [
					{ "data": "requiredDoc",
						"render": function ( data, type, full, meta ) {

							return full.requiredDoc.reqShtDesc;
						}
					},
					{ "data": "requiredDoc",
						"render": function ( data, type, full, meta ) {

							return full.requiredDoc.reqDesc;
						}
					},
					{ "data": "uploadedFileName" },
					// { "data": "checkSum" },
					{"data": "initiator"},
					{
						"data": "creationDate",
						"render" : function(data, type, full, meta) {
							return full.creationDate ? moment(full.creationDate).format('YYYY-MM-DD HH:mm:ss') : "";
						}
					},
					{"data": "approver"},
					{
						"data": "approvalDate",
						"render" : function(data, type, full, meta) {
							return full.approvalDate ? moment(full.approvalDate).format('YYYY-MM-DD HH:mm:ss') : "";
						}
					},
					{
						"data": "adId",
						"render": function ( data, type, full, meta ) {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="AccountDef.downloadAcctDoc(this);"><i class="fa fa-file-archive-o"></button>';

						}

					},

				]
			}));
			return currTable;
		};


		var downloadAcctDoc = function(button){
			var docs = JSON.parse(decodeURI($(button).data("docs")));
			window.open(SERVLET_CONTEXT+"/protected/setups/acctDocument/"+docs['adId'],
					'_blank' // <- This is what makes it open in a new window.
			);
		};


		var searchReqDocs = function(search){
			if($("#acctId-pk").val() != ''){
				$.ajax({
					type: 'GET',
					url:  'acctreqdocs',
					dataType: 'json',
					data: {"acctCode": $("#acctId-pk").val(),"docName":search},
					async: true,
					success: function(result) {
						$("#acctDocsTbl tbody").each(function(){
							$(this).remove();
						});
						for(var res in result){
							var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].reqId+"'></td><td>" + result[res].reqShtDesc + "</td><td>" + result[res].reqDesc + "</td></tr>";
							$("#acctDocsTbl").append(markup);
						}
						$("#req-acct-code").val($("#acctId-pk").val());
						$('#acctReqDocsModal').modal({
							backdrop: 'static',
							keyboard: true
						})
					},
					error: function(jqXHR, textStatus, errorThrown) {
						Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
					}
				});
			}
			else{
				bootbox.alert("No Account to attach Documents")
			}
		};


		var init = function(){

			$(document).ajaxStart(function () {
				$("#upload-doc-btn").prop("disabled", true);
			});
			$(document).ajaxComplete(function () {
				$("#upload-doc-btn").prop("disabled", false);
			});
			$("#sel2").val("D");
			$("#sel2").prop("disabled", true);
			$(".datepicker-input").each(function() {
				$(this).datetimepicker({
					format: 'DD/MM/YYYY'
				});

			});



			$("#h4pol").hide();

			getAccountDetails();
			$("#btn-add-docs").click(function(){
				searchReqDocs("");
			});
			$("#searchDocuments").click(function(){
				searchReqDocs($("#doc-name-search").val());
			});
			var genShtDesc = UTILITIES.getParamValue("AUTO_ACCOUNT_ID_GEN");
			if(genShtDesc && genShtDesc==="Y"){
				$("#other-names").prop("readonly", true);
			}
			else{
				$("#other-names").removeAttr('readonly');
			}

			if($("#acc-type-desc").val()==='SUB'){
				$("#parent-account-lov").select2("readonly", false);
			}
			else{
				$("#parent-account-lov").select2("val", "").select2("readonly", true);
			}
		};

		return {
			init: init,
			downloadAcctDoc:downloadAcctDoc,
			getAccountDetails: getAccountDetails
		};

	})(jQuery);
</script>