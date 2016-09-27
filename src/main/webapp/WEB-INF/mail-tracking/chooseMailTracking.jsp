<%@page import="javax.swing.text.StyledEditorKit.BoldAction"%>
<%@page import="java.util.Collection"%>
<%@page import="module.mailtracking.domain.CorrespondenceType" %>
<%@page import="module.mailtracking.domain.MailTracking"%>
<%@page import="module.mailtracking.domain.Year"%>
<%@page import="module.organization.domain.Unit"%>

<%@page import="java.util.Set"%>
<%@page import="java.util.Collection"%>
<%@page import="module.mailtracking.presentationTier.YearBean"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet"
	href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	
<!-- DataTables CSS -->
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.7/css/jquery.dataTables.css">
  
 
<!-- DataTables -->
<script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.7/js/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf8" src="//cdn.datatables.net/plug-ins/1.10.9/filtering/type-based/accent-neutralise.js"></script>


<%
    final String contextPath = request.getContextPath();
	final Collection<MailTracking> mailTrackings = (Collection<MailTracking>) request.getAttribute("mailTrackings");
	final Year year = (Year) request.getAttribute("year");
	final Boolean check = Boolean.valueOf(request.getAttribute("check").toString());
	final String options = (String)request.getAttribute("options");
	final MailTracking mailTracking = (MailTracking) request.getAttribute("mailTracking");
%>
<script src='<%=contextPath + "/webjars/jquery-ui/1.11.1/jquery-ui.js"%>'></script>
<style type="text/css" title="currentStyle">
	@import "<%= request.getContextPath() + "/javaScript/dataTables/media/css/demo_table.css" %>";
	.saviourDiv {
		height: 30px;
	}
</style>

<style type="text/css" title="currentStyle">
	
	.hidden-link {
		display: none;
	}
	
	.spinner {
		display: none;
	}
	.entry_deleted {
		opacity: 0.5;
		filter: alpha(opacity = 20);
		zoom: 1;
	}

</style>




<spring:url var="searchUrl" value="/mail-tracking/management/" />
<div class="page-header">
	<h1>
		<spring:message code="title.mail.tracking.application"
			text="Gestão de Correspondência" />
	</h1>
</div>


<%
    if (mailTrackings.isEmpty()) {
%>
<p>
	<em><spring:message
			code="message.not.operator.in.any.mail.tracking"
			text="message.not.operator.in.any.mail.tracking" /></em>
</p>
<%
    } else {
           %>


<form class="form-horizontal">
${csrf.field()}
	<div class="form-group" >
		<label class="control-label col-sm-2" for="mailTracking"> <spring:message
				code="label.mailTracking.unit" text="label.mailTracking.unit" />
		</label>
		<div class="col-sm-10">
			<%if(mailTrackings.size() == 1 && mailTrackings.iterator().next().hasCurrentUserOnlyViewOrEditionOperations()){%>
			<select class="form-control" id="mailTracking" name="mailTracking" >
			   <option value="<%= mailTrackings.iterator().next().getExternalId() %>">
			   <%= mailTrackings.iterator().next().getName().getContent() %>
			   </option>
			</select>
			<%}else {%>
		    <c:set var="tracking" value='<%= mailTracking==null? "":mailTracking.getExternalId() %>'/>
			<select class="form-control" id="mailTracking" 
				name="mailTracking">
				
				<option value="" ><spring:message
						code="option.mailTracking.select.unit" text="Select Unit"></spring:message></option>
				<c:forEach var="mt" items="<%=mailTrackings%>">
				    <c:if test="${mt.getExternalId() == tracking}">
					<option value='${mt.getExternalId()}' selected="selected">
						<c:out value='${mt.getName().getContent()}' />
					</option>
					</c:if>
					<c:if test="${mt.getExternalId() != tracking}">
					<option value='${mt.getExternalId()}'>
						<c:out value='${mt.getName().getContent()}' />
					</option>
					</c:if>
				</c:forEach>
			</select>
			<%} %>

		</div>
	</div>

	<div id="yearVisibility" class="form-group" hidden="true">
		<label class="control-label col-sm-2" for="year"> <spring:message
				code="label.mailTracking.year" text="label.mailTracking.year" />
		</label>
		<div class="col-sm-10">  
		<c:set var="ano" value='<%= year==null? "":year.getExternalId() %>'/>
		<c:if test='${ano=="" }'>
			<select class="form-control" id="year" name="year" 
				disabled="disabled">
				<option value=""></option>
			</select>
		</c:if>	
		<c:if test='${ano!="" }'>
			<select class="form-control" id="year" name="year" >
				<option value="<%=year.getExternalId()%>"><%=year.getName() %></option>
			</select>
		</c:if>	
		</div>
	</div>

<div id="demo" hidden='true' >

	<div id="criar" hidden="true">
			<a id="newEntry" href="#">
				<spring:message code="label.mail.tracking.create.new.entry" text="Create New Entry"/>
			</a>
	</div>
	<div id="set" hidden="true">	
			<a id="setCounter" href="#">
				<spring:message code="label.mail.tracking.set.counters" text="Set Counters" /> 
			</a>
	</div>
<p/><p/><p/>
   <div id="checkDelete" >
   <input style='vertical-align: text-bottom;' type='checkbox'  id='viewDeleted' name='viewDeleted' value="<%=check %>" onclick='viewCheck()' />
				<span><spring:message code="check.mailTracking.allCorrespondence" text="check.mailTracking.allCorrespondence"/></span>
   </div>
   <p/><p/><p/>
   <div class='options' id="checkOptions" >
   <label><spring:message code="label.type.receive" text="label.type.receive" /></label>
   
	<input id="rec" type="radio" name="entryType" value="Recebido" >

	<label><spring:message code="label.type.sent" text="label.type.sent" /></label>
	<input id = "env" type="radio" name=entryType value="Expedido" >
	
	<label><spring:message code="label.both" text="label.both" /></label>  
	<input id="both" type="radio" name="entryType" value="">
   </div>
    <p></p>
    <p></p>
<table style="width:100%;table-layout:fixed;word-wrap: break-word;" class="tstyle3 mtop05 mbottom05 ajax-table table">
    <thead>
      <tr>
      <th scope="col" class="" width="40px">
          <label for="Type" title="Tipo de correspondência" >
            <spring:message code="label.mailTracking.table.mail" text="Correio"/>
          </label>
        </th>
        <th scope="col" class=""  width="60px">
          <label for="Process" title="Reference" >
            <spring:message code="label.mailTracking.table.reference" text="Nº"/>
          </label>
        </th>
          <th scope="col" class=""  width="52px">
        <label for="Data" title="Data">
             <spring:message code="label.mailTracking.table.data" text="Data"/>
          </label>
        </th>
        <th scope="col" class="" >
          <label for="Recipient" title="Destinatário">
            <spring:message code="label.mailTracking.table.receiver" text="Destinatário"/>
          </label>
        </th>
         <th scope="col" class=""  >
          <label for="Sender" title="Remetente">
            <spring:message code="label.mailTracking.table.sender" text="Remetente"/>
          </label>
        </th>
         <th scope="col" class="" width="80px">
          <label for="SenderLetterNumber" title="SenderLetterNumber">
            <spring:message code="label.mailTracking.table.senderLetterNumber" text="Nº Corresp."/>
          </label>
        </th>
        
        <th scope="col" class="" >
          <label for="Subject" title="Assunto">
             <spring:message code="label.mailTracking.table.subject" text="Assunto"/>
          </label>
        </th>
        <th scope="col" class=""  width="0%">
          <label for="State" title="Estado">

          </label>
        </th>
        <th scope="col" class="" width="0%">      
        </th>
         <th scope="col" class="" width="0%">        
        </th>
         <th scope="col" class="" width="0%">        
        </th>
         <th scope="col" class="" width="0%">        
        </th>
         <th scope="col" class="" width="0%"> 
        </th>
        <th scope="col" class="" width="150px">
        <label for="links" title="links">
        
          </label> 
        </th>
       
      </tr>
    </thead>
<tbody></tbody>
  </table>
 </div>

</form >



<% } %>

<div class="spinner">
</div>


<html:link  styleClass="hidden-link fast-entry-copy-submission-link" page='<%= String.format("/mail-tracking/management/prepareCopyEntry") %>'></html:link> 


<script type="text/javascript">

var contextPath = '<%=contextPath%>';
var unit = $("#mailTracking").val();
var yearaux = $("#year").val();
var c = '<%=check%>';
var opt='<%=options%>';

var type="";
var msg ="";



function is_undefined(value) {
	   if(value === undefined || value ===""){
		   return true;
	   } else{
	       return false;
	   }
	};
	
	jQuery.fn.DataTable.ext.type.search.string = function ( data ) {
		return ! data ?
		'' :
		typeof data === 'string' ?
		data
		.replace( /έ/g, 'ε' )
		.replace( /[ύϋΰ]/g, 'υ' )
		.replace( /ό/g, 'ο' )
		.replace( /ώ/g, 'ω' )
		.replace( /ά/g, 'α' )
		.replace( /[ίϊΐ]/g, 'ι' )
		.replace( /ή/g, 'η' )
		.replace( /\n/g, ' ' )
		.replace(new RegExp('[ÁÀÂÃ]','gi'), 'A')
		.replace(new RegExp('[ÉÈÊ]','gi'), 'E')
		.replace(new RegExp('[ÍÌÎ]','gi'), 'I')
		.replace(new RegExp('[ÓÒÔÕ]','gi'), 'O')
		.replace(new RegExp('[ÚÙÛ]','gi'), 'U')
		.replace(new RegExp('[Ç]','gi'), 'C').toLowerCase() :
		data;
		};

		$.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource, fnCallback, bStandingRedraw )
		{
		    if ( typeof sNewSource != 'undefined' && sNewSource != null )
		    {
		        oSettings.sAjaxSource = sNewSource;
		    }
		    this.oApi._fnProcessingDisplay( oSettings, true );
		    var that = this;
		    var iStart = oSettings._iDisplayStart;
		      
		    oSettings.fnServerData( oSettings.sAjaxSource, [], function(json) {
		        /* Clear the old information from the table */
		        that.oApi._fnClearTable( oSettings );
		          
		        /* Got the data - add it to the table */
		        var aData =  (oSettings.sAjaxDataProp !== "") ?
		            that.oApi._fnGetObjectDataFn( oSettings.sAjaxDataProp )( json ) : json;
		          
		        for ( var i=0 ; i<json.aaData.length ; i++ )
		        {
		            that.oApi._fnAddData( oSettings, json.aaData[i] );
		        }
		          
		        oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
		        that.fnDraw();
		          
		        if ( typeof bStandingRedraw != 'undefined' && bStandingRedraw === true )
		        {
		            oSettings._iDisplayStart = iStart;
		            that.fnDraw( false );
		        }
		          
		        that.oApi._fnProcessingDisplay( oSettings, true );
		          
		        /* Callback user function - for event handlers etc */
		        if ( typeof fnCallback == 'function' && fnCallback != null )
		        {
		            fnCallback( oSettings );
		        }
		    }, oSettings );
		};		

jQuery.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings )
{
		return {
		"iStart":         oSettings._iDisplayStart,
		"iEnd":           oSettings.fnDisplayEnd(),
		"iLength":        oSettings._iDisplayLength,
		"iTotal":         oSettings.fnRecordsTotal(),
		"iFilteredTotal": oSettings.fnRecordsDisplay(),
		"iPage":          oSettings._iDisplayLength === -1 ?
		 0 : Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
		"iTotalPages":    oSettings._iDisplayLength === -1 ?
		 0 : Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
		};
	};
	
	
	function viewOpt(t,term,year,check,opt){
		t.fnFilter( opt, 0, true, false );		
		
		createEntry(term,year);
		createSetCounter(term,year);
	};

	function viewCheck() {
		c=$("#viewDeleted").prop('checked');
		$("#year").trigger('change');
	};
	
	function getListYears(value){
		$.get(
				contextPath
						+ "/mail-tracking/management/getYearByUnit/json",
				{
					term : value
					
				},
				function(j) {
					if (j.length > 0) {
						
						$("#year")
								.removeAttr(
										"disabled");
						var options = '';
						
						for (var i = 0; i < j.length; i++) {
							options += '<option value="' + j[i].id + '">'+ j[i].name+ '</option>';
						};
						
						$("#year").html(
								options);
						if(!is_undefined(yearaux)){
							$("#year option[value="+yearaux+"]").attr('selected','selected');
						};
						$("#yearVisibility").removeAttr('hidden');
						$("#year").trigger('change');
					} else {
						$("#year").attr(
								'disabled',
								'disabled');
						$("#year")
								.html(
										'<option value="">Year</option>');
						$("#yearVisibility").attr('hidden','true');
						$("#demo").attr('hidden','true');
						return false;
					}
				});
		
	};
	
	function createEntry(value,ano){
		
		$.ajax({
		    url: contextPath + "/mail-tracking/management/isAbleToCreate?data="+value,
		    type: 'GET',
		    success: function(data){     	
				if(data === "true" && ano!=""){
			 
				     var newEntry = contextPath +"/mail-tracking/management/prepareCreateNewEntry?mailTrackingId="+value+"&check="+c+"&yearId="+ano+"&type="+type+"&message="+msg+"&options="+opt;
		        	  $("#newEntry").attr('href',newEntry);
		        	  $("#criar").removeAttr("hidden");
		       	  
				}else{
	        		  $("#criar").attr("hidden","true");
	        		  
	        	 };
		    },
		    error: function(data) {
		    	 return false;
		    }
		});

	};
	
	function createSetCounter(value,ano){
		$.ajax({
		    url: contextPath + "/mail-tracking/management/isAbleToSetCount?data="+value,
		    type: 'GET',
		    success: function(data){ 
		    	if(data === "true" && ano!=""){
		        	  var counter = contextPath +'/mail-tracking/management/prepareSetReferenceCounters?mailTrackingId='+value+"&check="+c+"&yearId="+ano+"&options="+opt;
						 $("#setCounter").attr('href',counter);
						 $("#set").removeAttr("hidden");
			        	 
					  }else{
						  $("#set").attr("hidden","true");
		        		 
					  };
		    	
		    },
		    error: function(data) {
		    	 return false;
		    }
		});
		

	};
	
	
	
	
	$.extend(jQuery.fn.dataTableExt.oSort, {
		"num-html-pre": function ( a ) {
		    var x = String(a).replace(/(?!^-)[^0-9.]/g, "");
		  
		    return  parseFloat( x );
		},

		"num-html-asc": function ( a, b ) {
		    return ((a < b) ? -1 : ((a > b) ? 1 : 0));
		},

		"num-html-desc": function ( a, b ) {
		    return ((a < b) ? 1 : ((a > b) ? -1 : 0));
		}
	});
	
	
	$(document).ready(function () {
		
		
		var table;
		
		if(c=='true'){
			$("#viewDeleted").attr('checked','checked');
		}else{
			$("#viewDeleted").removeAttr('checked');
		};
		
		
		if(opt==''){
       		$("input#both").focus();
			$("input#both").attr('checked','true');
		};
			if(opt=='Expedido'){			
				$("input#env").attr('checked','true');
				$("input#env").focus();
				
			};
			if(opt=='Recebido'){			
				$("input#rec").attr('checked','true');
				$("input#rec").focus();
			};
			
		
		
		
		

		if(!is_undefined(unit)){
			
			$("#mailTracking option[value="+unit+"]").attr('selected','selected');
			getListYears(unit);

			
		};
		
		$("#checkOptions input:radio").on('click',function(){
			 opt=this.value;
			 table.fnReloadAjax(contextPath + "/mail-tracking/management/getCurrMailTracking/json?term="+term+"&year="+year+"&check="+check+"&options="+opt);
			 viewOpt(table,$('#mailTracking').val(),$('#year').val(),$("#viewDeleted").prop('checked'),opt);
			 return true;
			 
		});
		
		
		$("#mailTracking").change(function(e) {
				e.preventDefault();
				$("#demo").attr('hidden','true');
				if (this.selectedIndex == 0) {
					$("#yearVisibility").attr('hidden','true');
					$("#year").attr('disabled', 'disabled');
					$("#year").html(
							'<option value="">Year</option>');
					return false;
				};
				yearaux="";
			
				getListYears($(this).val());
				table.fnFilter('');
			
		});
		
		var check;
		var term;
		var year;
		
		$("#year").on("change",function(){							
						
						check =$("#viewDeleted").prop('checked');
						term =$('#mailTracking').val();
						year=$('#year').val();
						
						createEntry(term,year);
						createSetCounter(term,year);
						
						table=$(".ajax-table").dataTable({
							'bDestroy': true,
							"dom": '<lf<t>ip>',
							"bStateSave": false,
							
							'bPaginate': true,
							'bProcessing': true,
							'bServerSide': false,
							'iDisplayLength': 25,
							"iDisplayStart": 0,
							'bAutoWidth':false,
							'bDeferRender':true,

							'oLanguage': {
								'sProcessing': 'A processar...',
								"sEmptyTable": "loading...",
								'sLengthMenu': 'Mostrar _MENU_ registos',
								'sInfo': '_START_ - _END_ de _TOTAL_',
								"sInfoThousands": "",
								'sInfoEmpty': '0 - 0 de 0',
								'sInfoFiltered': '(filtrado de _MAX_ total de registos)',
								"sSearch": "Procura",
								'oPaginate': {
									'sFirst': 'Primeiro',
									'sPrevious': 'Anterior',
									'sNext': 'Seguinte',
									'sLast': 'Último'
								}
							},
							
							
							'aaSorting': [[1,'desc']],
							'fnFormatNumber': function ( iIn ) {
							      if ( iIn < 1000 ) {
							        return iIn;
							      } else {
							        var
							          s=(iIn+""),
							          a=s.split(""), out="",
							          iLen=s.length;     
							          for ( var i=0 ; i<iLen ; i++ ) {
								          if ( i%3 === 0 && i !== 0 ) {
								            out = ""+out;
								          }
								          out = a[iLen-i-1]+out;
							          }
							      }
							      return out;
							    },
							    

							'sAjaxSource': contextPath + "/mail-tracking/management/getCurrMailTracking/json?term="+term+"&year="+year+"&check="+check+"&options="+opt,
							"fnServerData": function ( sSource, aoData, fnCallback ) {
					            $.ajax( {
					                dataType: 'json',
					                type: "GET",
					                url: sSource,
					                data: aoData,
					                success: fnCallback,
					                error : function (e) {
				                        //alert (e);
				                    }
					            } );
					        },
					       
						    "fnRowCallback": function( nRow, aData, iDisplayIndex) {
											       
						    	if(aData.State == "DELETED") {
									$(nRow).addClass("entry_deleted");
								}
								 
								 return nRow;
						         },

					      
					        "aoColumns":[
									{ "mData":'Type',
									  "sClass":'dt-body-left'
									},
					  	            { "mData":'Process',
									  "sClass":'dt-body-left',
									  "sType":'num-html'
									  
									},
					  	            {"mData": 'Data',
					  	             "sClass":'dt-body-left'
									},
					  	            { "mData": 'Recipient',
					  	              "sClass":'dt-body-left',
					  	              "mRender": function( data, type, row ){
					  	            	  if(row.AvatUrlRec !== null){ 					  	            		  
					  	            		  return '<img class="img-circle" width="30" height="30" src="'+row.AvatUrlRec+'"/>&nbsp;&nbsp;'+ row.Recipient;					  	            		   
					  	            	  }else{
					  	            		  return data;
					  	            	  }
					  	              }
					  	            },
					  	            { "mData": 'Sender',
					  	              "sClass":'dt-body-left',
					  	            	"mRender": function( data, type, row ){
					  	            	  if(row.AvatUrlSend !== null){
					  	            			return'<img class="img-circle" width="30" height="30" src="'+row.AvatUrlSend+'"/>&nbsp;&nbsp;'+ row.Sender;					  	           		 
					  	            	  }else{
					  	            		  return data;
					  	            	  }
					  	              }	
					  	            },
					  	            { "mData": 'SenderLetterNumber',
					  	            	"sClass": 'dt-body-left'
					  	            },
					  	            { "mData": 'Subject',
					  	              "sClass":'dt-body-left'
					  	            
					  	            },
					  	            { "mData": 'State',
						  	          "sClass":'dt-body-left'
					  	            },
					  	          	{ "mData": 'View',
						  	          "sClass": 'dt-body-left'
						  	         },
					  	        	{ "mData": 'Edit',
					  	              "sClass":'dt-body-left'
							  	    },
					  	      		{ "mData": 'Delete',
					  	      		  "sClass":'dt-body-left'
							  	     },
					  	    		{ "mData": 'Document',
					  	    		  "sClass":'dt-body-left'
			   		                 },
					  	  			{ "mData": 'CopyEntry',
					  	  			  "sClass":'dt-body-left'
					   		         },
					   		        {"mData":null,
					   		         "sClass":'dt-body-left'
					   		        }					   		         
					  	            ],

					   		"aoColumnDefs": [
			   		                 {
			   		                  "aTargets" :[7],
			   		               	  "visible": false,
					   		          'mRender':  function ( data, type, row )
					   		          	{		
											if(row.State=='ACTIVE')
												return "A";
											if(row.State=='DELETED')
												return "D";
						   		         }
			   		                 },
					  	          	{					  	          		
					  	          		"aTargets": [8],
					   		          	"visible": false,
					   		         	'bSortable': false,
					   		          	"searchable": false
					  	          	},
					  	        	{ "aTargets": [9],
			   		                "visible": false,
			   		             	'bSortable': false,
			   		                "searchable": false
			   		                },
					  	        	
					  	      		{ "aTargets": [10],
				   		              "visible": false,
				   		              'bSortable': false,
				   		               "searchable": false
				   		                },
					  	      		
					  	    		{ "aTargets": [11],
					   		           "visible": false,
					   		           'bSortable': false,
					   		           "searchable": false
					   		         },
					  	  			{ "aTargets": [12],
			   		                   "visible": false,
			   		             	   'bSortable': false,
			   		             	    "searchable": false
					   		        },

  	            		 /*Links */ { 
									'bSortable': false,
									"sClass" : "nowrap dt-body-left",
									'mRender':  function ( data, type, row ) {
												
													var links='';
													if(row.View!= 'permission_not_granted')
						 							links += "<" + "a href='"+row.View+"'><img src='" + contextPath + "/images/view.gif' alt='Visualizar' /></a> "
						 							if(row.Edit!= 'permission_not_granted')
 						 							links += "<" + "a href='"+row.Edit+"'><img src='" + contextPath + "/images/edit.gif' alt='Editar' /></a> "
													if(row.Delete!= 'permission_not_granted')
 						 							links += "<" + "a href='"+row.Delete+"'><img src='" + contextPath + "/images/delete.gif' alt='Remover' /></a> "
													if(row.Document!= 'permission_not_granted')
 						 							links += "<" + "a href='"+row.Document+"'><img src='" + contextPath + "/images/document.gif' alt='Visualizar Documento' /></a> "
													if(row.CopyEntry!= 'permission_not_granted')
 						 							links += "<" + "a href='"+row.CopyEntry+"'><img src='" + contextPath + "/images/copyEntry.gif' alt='Copiar' /></a>"
													return links;
													},
										"aTargets": [13]					  	  															
									}
 					  			   ]				      
				
						});	
						
						$('div.dataTables_filter input').keyup( function (e) {  
					        table.fnFilter(
					            jQuery.fn.DataTable.ext.type.search.string(this.value)
					        );
					    } );					
						viewOpt(table,term,year,check,opt);						
						$("#demo").removeAttr(
						"hidden");

			});
			jQuery.fn.DataTable.ext.type.search.string('');
	});


</script>


