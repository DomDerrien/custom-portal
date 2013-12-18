<!DOCTYPE html>
<%@page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List"
    import="dderrien.common.model.User"
%><html lang="en">
<head>
	<meta charset="utf-8">
	<title>Liste de favoris</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<link rel="stylesheet" href="/js/3rdParty/dbootstrap/theme/dbootstrap/dbootstrap.css" />
	<link rel="stylesheet" href="/js/dderrien/common.css" />
	<link rel="stylesheet" href="/styles/portal.css" />
</head>
<body class="dbootstrap">

	<div data-dojo-type="dijit/layout/BorderContainer" style="height: 100%;">
		<div data-dojo-type="dijit/MenuBar" data-dojo-props="region: 'top', 'class': 'primary inverse'">
	        <span class="brand">Liste de favoris</span>
	        <div data-dojo-type="dijit/PopupMenuBarItem" id="admin">
	            <span>Administration</span>
	            <div data-dojo-type="dijit/Menu" id="adminMenu">
	                <div data-dojo-type="dijit/MenuItem" id="addCat">Ajouter une catégorie</div>
					<div data-dojo-type="dijit/MenuSeparator"></div>
	                <div data-dojo-type="dijit/MenuItem" id="logoutBtn" data-dojo-props="iconClass:'dijitIconDelete'">Se déconnecter</div>
	            </div>
	        </div>
	        <div data-dojo-type="dijit/PopupMenuBarItem" id="help">
	            <span>Aide</span>
	            <div data-dojo-type="dijit/Menu" id="helpMenu">
	                <div data-dojo-type="dijit/MenuItem">À propos</div>
	            </div>
	        </div><%
	        List<User> users = (List<User>) request.getAttribute("users");
	        if (users != null && 1 < users.size()) { %>
	        <div style="float: right; margin: 6px 0;">
		        <label class="brand" for="userList" style="font-size: 14px;">Usagers : </label>
				<select data-dojo-type="dijit/form/Select" data-dojo-props="required: true" id="userList"><%
	            	for (User user: users) { %>
			        <option value="<%= user.getId() %>"<% if (user.getId().equals(((User) request.getAttribute("user")).getId())) {%> selected=selected<% } %>><%= user.getName() %></option><%
	            	} %>
				</select>
			</div><%
	        } else { %>
	        <span class="brand" style="float: right; font-size: 14px;"><%= ((User) request.getAttribute("user")).getName() %></span><%
	        } %>
		</div>
		<div data-dojo-type="dijit/layout/ContentPane" data-dojo-props="region: 'center'" id="insertionPoint"></div>
		<div data-dojo-type="dijit/layout/ContentPane" data-dojo-props="region: 'bottom', style: 'text-align: right'">
			<a href="http://creativecommons.org/licenses/by-nc-sa/2.5/ca/deed.fr_CA" target="_blank"><img src="http://i.creativecommons.org/l/by-nc-sa/2.5/ca/88x31.png" alt="Certains droits réservés" title="Certains droits réservés" /></a>
		</div>
	</div>
	
	<script
		data-dojo-config="
			async: true,
			// cacheBust: 1,
			tlmSiblingOfDojo: false,
			isDebug: true,
			locale: 'en',
			parseOnLoad: false,
			packages: [
				{ name: 'dojo', location: '/js/3rdParty/dojo' },
				{ name: 'dijit', location: '/js/3rdParty/dijit' },
				{ name: 'dderrien', location: '/js/dderrien' },
				{ name: 'dbootstrap', location: '/js/3rdParty/dbootstrap' },
				{ name: 'xstyle', location: '/js/3rdParty/xstyle' }
			],
			useXDomain: false
		"
		src="/js/3rdParty/dojo/dojo.js"
		type="text/javascript"
	></script>
	<script>
		require(
			[
				'dojo/ready',
				'dojo/parser',
				'dojo/query',
				'dojo/dom-style',
				'dderrien/portal',
				'dbootstrap',
				'dijit/layout/BorderContainer',
				'dijit/layout/ContentPane',
			    'dijit/MenuBar',
			    'dijit/PopupMenuBarItem',
			    'dijit/Menu',
			    'dijit/MenuItem',
			    'dijit/MenuSeparator',
			    'dijit/PopupMenuItem',
			    'dijit/CheckedMenuItem',
			    'dijit/Dialog',
			    'dijit/form/Form',
			    'dijit/form/Button',
			    'dijit/form/ValidationTextBox',
			    'dijit/form/Select'
			],
			function(ready, parser, query, style, portal) {
				ready(function() {
					parser.parse().then(function() {
						portal.init({
							user: <%= ((User) request.getAttribute("user")).toString() %>,<%
							if (users != null && 0 < users.size()) { %>
							users: [ <%
							for(User user: users) { %><%= user.toString() %><%= ", " %><% }
							%> ],<%
							} %>
							logoutURL: '<%= (String) request.getAttribute("logoutURL") %>'
						});
						style.set(query('body')[0], 'visibility', 'visible');
					});
				});
			}
		);
	</script>

	<div id="notificationArea">
		<div id="notificationAreaClose">X</div>
		<img id="notificationLevelIcon" />
		<span id="notificationMessage"><!-- content dynamically filled up later --></span>
	</div>

    <div id="addCatDlg" data-dojo-type="dijit/Dialog" data-dojo-props="title:'Ajouter une catégorie'" style="display:none;min-width:350px;">
    	<form id="addCatForm" data-dojo-type="dijit/form/Form">
	        <div class="dijitDialogPaneContentArea">
	        	<div style="text-align:right;">
		        	<label for="addCatTitle">Titre :</label>
		            <input data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props="required: true" type="text" id="addCatTitle" name="title" placeholder="Titre de la catégorie">
		        </div>
	        </div>
	
	        <div class="dijitDialogPaneActionBar">
	            <button data-dojo-type="dijit/form/Button" data-dojo-props="'class':'primary'" type="submit" id="addCatOK">OK</button>
	            <button data-dojo-type="dijit/form/Button" type="button" id="addCatCancel">Cancel</button>
	        </div>
       </form>
    </div>

    <div id="addLnkDlg" data-dojo-type="dijit/Dialog" data-dojo-props="title:'Ajouter une référence'" style="display:none;min-width:350px;">
    	<form id="addLnkForm" data-dojo-type="dijit/form/Form">
	        <div class="dijitDialogPaneContentArea">
	            <input data-dojo-type="dijit/form/TextBox" data-dojo-props="required: true" type="hidden" name="categoryId" id="addLnkCatId">
	        	<div style="text-align:right; margin-bottom:10px;">
		        	<label for="addLnkTitle">Titre :</label>
		            <input data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props="required: true" type="text" id="addLnkTitle" name="title" placeholder="Titre de la référence">
		        </div>
	        	<div style="text-align:right; margin-bottom:10px;">
		        	<label for="addLnkHRef">Adresse :</label>
		            <input data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props="required: true, pattern:'http(?:s)?://\\S{8,}'" type="text" id="addLnkHRef" name="href" placeholder="Adresse de la référence">
		        </div>
	        </div>
	
	        <div class="dijitDialogPaneActionBar">
	            <button data-dojo-type="dijit/form/Button" data-dojo-props="'class':'primary'" type="submit" id="addLnkOK">OK</button>
	            <button data-dojo-type="dijit/form/Button" type="button" id="addLnkCancel">Cancel</button>
	        </div>
       </form>
    </div>

    <div id="setCatTitleDlg" data-dojo-type="dijit/Dialog" data-dojo-props="title:'Renommer la catégorie'" style="display:none;min-width:350px;">
    	<form id="setCatTitleForm" data-dojo-type="dijit/form/Form">
	        <div class="dijitDialogPaneContentArea">
	            <input data-dojo-type="dijit/form/TextBox" data-dojo-props="required: true" type="hidden" name="id" id="setCatTitleId">
		        <div style="text-align: right; margin-bottom: 10px;">
		        	<label>Ancien titre :</label>
		        	<input data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props="required: true" readonly="readonly" id="setCatTitleOld"/>
		        </div>
		        <div style="text-align: right;">
		        	<label>Nouveau titre :</label>
		        	<input data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props="required: true" name="title" />
		        </div>
	        </div>
	
	        <div class="dijitDialogPaneActionBar">
	            <button data-dojo-type="dijit/form/Button" data-dojo-props="'class':'primary'" type="submit">OK</button>
	            <button data-dojo-type="dijit/form/Button" type="button" id="setCatTitleCancel">Cancel</button>
	        </div>
       </form>
    </div>

    <div id="setCatWidthDlg" data-dojo-type="dijit/Dialog" data-dojo-props="title:'Changer la largeur'" style="display:none;min-width:350px;">
    	<form id="setCatWidthForm" data-dojo-type="dijit/form/Form">
	        <div class="dijitDialogPaneContentArea">
	            <input data-dojo-type="dijit/form/TextBox" data-dojo-props="required: true" type="hidden" name="id" id="setCatWidthId">
		        <div style="text-align: right;">
		        	<label for="setCatWidth">Choix de largeur :</label>
		        	<select data-dojo-type="dijit/form/Select" data-dojo-props="required: true" style="width: 15.1em" id="setCatWidth" name="width">
		        		<option value="span2">La plus étroite</option>
		        		<option value="span3">Un quart</option>
		        		<option value="span4">Un tiers</option>
		        		<option value="span6">La moitié</option>
		        		<option value="span8">Deux tiers</option>
		        		<option value="span9">Trois quarts</option>
		        		<option value="span12">Toute la largeur</option>
		        	</select>
		        </div>
	        </div>
	
	        <div class="dijitDialogPaneActionBar">
	            <button data-dojo-type="dijit/form/Button" data-dojo-props="'class':'primary'" type="submit" id="setCatWidthOK">OK</button>
	            <button data-dojo-type="dijit/form/Button" type="button" id="setCatWidthCancel">Cancel</button>
	        </div>
       </form>
    </div>

    <div id="setCatOrderDlg" data-dojo-type="dijit/Dialog" data-dojo-props="title:'Changer la position'" style="display:none;min-width:350px;">
    	<form id="setCatOrderForm" data-dojo-type="dijit/form/Form">
	        <div class="dijitDialogPaneContentArea">
	            <input data-dojo-type="dijit/form/TextBox" data-dojo-props="required: true" type="hidden" name="id" id="setCatOrderId">
	            <input data-dojo-type="dijit/form/TextBox" data-dojo-props="required: true" type="hidden" name="oldOrder" id="setCatOrderOld">
		        <div style="text-align: right;">
		        	<label for="setCatOrder">Choix de position :</label>
		        	<select data-dojo-type="dijit/form/Select" data-dojo-props="required: true" style="width: 15.1em" id="setCatOrder" name="order">
		        		<option value=" ">Choisir une position</option>
		        		<option value="1">Première position</option>
		        	</select>
		        </div>
	        </div>
	
	        <div class="dijitDialogPaneActionBar">
	            <button data-dojo-type="dijit/form/Button" data-dojo-props="'class':'primary'" type="submit" id="setCatOrderOK">OK</button>
	            <button data-dojo-type="dijit/form/Button" type="button" id="setCatOrderCancel">Cancel</button>
	        </div>
       </form>
    </div>
	
</body>
</html>
