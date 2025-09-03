<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
<form:form class="form-horizontal form-label-left input_mask"  modelAttribute="organization"
                       method="post" action="createOrganization" id="orgForm"
                       enctype="multipart/form-data">
				        <input type="submit"  class="btn btn-success" value="Save" >
				        <hr>
				      <spring:hasBindErrors name="organization">
				      <div class="alert alert-error alert-dismissible">
						  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
						  <form:errors path="*" cssClass="help-inline"/>
						</div>
						</spring:hasBindErrors>
				          
				        <div class="item form-group form-required">
				            <div class="col-md-6 col-xs-12">
		                    <label for="orgName" class="label-align col-md-5">Company Name</label>
		                     <div class="col-md-7 col-xs-12">
		                     <form:input path="orgName" class="form-control" placeholder="Org. Sht Desc"/>   
		                     </div>               
		                    </div>
		                    <div class="col-md-6 col-xs-12">
		                    <label for="orgShtDesc" class="label-align col-md-5">Alias</label>
		                    <div class="col-md-7 col-xs-12">
		                    <form:input path="orgShtDesc" class="form-control" placeholder="Description"/>
		                    </div>    
		                    </div>   
				        </div>
				     
				        <div class="item form-group">
				          <div class="col-md-6 col-xs-12">
		                    <label for="orgMobile" class="label-align col-md-5">Mobile</label>
		                     <div class="col-md-7 col-xs-12">
		                     <form:input path="orgMobile" class="form-control" placeholder="Mobile Number"/>                  
		                 </div>
		                 </div>
		                 <div class="col-md-6 col-xs-12">
		                  <label for="orgPhone" class="label-align col-md-5">Phone</label>
		                    <div class="col-md-7 col-xs-12">
		                     <form:input path="orgPhone" class="form-control" placeholder="Tel. Number"/>                  
		                 </div>
		                 </div>
				        </div>
				       
				        
				          <div class="item form-group">
				           <div class="col-md-6 col-xs-12">
		                    <label for="orgFax" class="label-align col-md-5">Fax Number</label>
		                    <div class="col-md-7 col-xs-12">
		                     <form:input path="orgFax" class="form-control" placeholder="Fax Number"/>                  
		                 </div>
		                 </div>
		                  <div class="col-md-6 col-xs-12">
		                 <label for="orgWebsite" class="label-align col-md-5">WebSite</label>
		                    <div class="col-md-7 col-xs-12">
		                     <form:input path="orgWebsite" class="form-control" placeholder="Website"/>                  
		                 </div>
		                 </div>
				        </div>
				        
				        <div class="item form-group">
				           <div class="col-md-6 col-xs-12">
		                    <label for="orgDesc" class="label-align col-md-5">Date of Incorporation</label>
		                    <div class="col-md-7 col-xs-12">
		                     <div class='input-group date datepicker-input'>
		                     <form:input path="dateIncorp"  class="form-control"/>    
		                     <span class="input-group-addon">
				                        <span class="fa fa-calendar"></span>
				                    </span>    
				                </div>          
		                 </div>
		                 </div>
		                 <div class="col-md-6 col-xs-12">
		                   <label for="certNumber" class="label-align col-md-5">Certificate No.</label>
		                    <div class="col-md-7 col-xs-12">
		                     <form:input path="certNumber" class="form-control" placeholder="Cert Number"/>                  
		                 </div>
		                 </div>
		                 </div>
		                 <div class="item form-group form-required">
				          
				           <div class="col-md-6 col-xs-12">
		                 <label for="currencyName" class="label-align col-md-5">Currency</label>
		                    <div class="col-md-7 col-xs-12">

		                     <form:input type="text" path="curName" name="currencyName" class="form-control" id="txtCurrency" cssStyle="display:none;" disabled="true"/>
		                     <form:input type="text" path="curCode" name="curCode" class="form-control" id="txtCurrencyCode" cssStyle="display:none;" disabled="true"/>
		                     <form:hidden path="curCode" id="curCode" rv-value="organization.currency.curCode"/>
		                          <div id="currency" class="form-control"
				                                 select2-url="<c:url value="/protected/organization/currencies"/>" >

				                                 </div>
		                 </div>
		                 </div>
				            <div class="col-md-6 col-xs-12 form-required">
		                 <label for="countryName" class="label-align col-md-5">Country</label>
		                    <div class="col-md-7 col-xs-12">

		                      <form:input  path="couName" name="countryName" class="form-control" id="countryName" cssStyle="display:none;" disabled="true"/>
		                      <form:input  path="couCode" name="couCode" class="form-control" id="countryCode" cssStyle="display:none;" disabled="true"/>
		                     <form:hidden path="couCode" rv-value="organization.country.couCode"/>
				                            <div id="country" class="form-control"
				                                 select2-url="<c:url value="/protected/organization/countries"/>" >
				                                 
				                                 </div>
		                 </div>
		                 </div>
		                
				        </div>
						<div class="item form-group">
							<div class="col-md-6 col-xs-12">
								<label for="orgDesc" class="label-align col-md-5">Pin Number</label>
								<div class="col-md-7 col-xs-12">
									<form:input path="pinNo" class="form-control" placeholder="Pin Number"/>
								</div>
							</div>
							<div class="col-md-6 col-xs-12">

							</div>
						</div>
				        
				         <div class="x_title">
				        <h4>Address Information</h4>
				        </div>
				        
				        <div class="item form-group">
				           <div class="col-md-6 col-xs-12 form-required">
		                    <label for="zipCode" class="label-align col-md-5">Postal Address</label>
		                    <div class="col-md-7 col-xs-12">
		                     <form:textarea path="addAddress" rows="3" cols="20" class="form-control" placeholder="Address" />
		                 </div>
		                 </div>
		                 
				        
				        <div class="col-md-6 col-xs-12 form-required">
		                  <label for="zipCode" class="label-align col-md-5">Email Address</label>
		                    <div class="col-md-7">
		                     <form:input path="emailAddress" id="emailAddress" class="form-control" placeholder="Email Address"/>
		                 </div>
		                 </div>
				        </div>
				         <div class="item form-group">
				           <div class="col-md-6 col-xs-12 form-required">
		                    <label for="zipCode" class="label-align col-md-5">Physical Address</label>
		                    <div class="col-md-7 col-xs-12">
		                     <form:textarea path="phyAddress" rows="3" cols="20" class="form-control" placeholder="Physical Address" />
		                 </div>
		                 </div>
		                 
				        
				        <div class="col-md-6 col-xs-12 form-required">
		                  
		                 </div>
				        </div>
				        
				        
				         <div class="x_title">
				        <h4>Company Logo</h4>
				        </div>
				         
				        
				        <div class="item form-group">
				           
				             <div class="col-md-6 col-xs-12">
		                    <label for="logo" class="label-align col-md-4">Logo</label>
		                    <div class="col-md-8 col-xs-12">
		                     <div class="kv-avatar center-block" style="width:200px">
						       <form:input path="file" type="file" id="avatar" cssClass="file-loading"/>
						       
						    </div>
						    </div>
		                 </div>
				          
				          </div>
				      <form:hidden path="formAction" />
				      <form:hidden path="orgCode" id="orgCodepk"/>  
                      </form:form>
                      </div>
                      