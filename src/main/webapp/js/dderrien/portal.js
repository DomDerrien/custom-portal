define(
	[
		'./common',
		'dojo/io-query',
		'dojo/dom',
		'dojo/dom-construct',
		'dijit/registry',
		'dojo/on',
		'dojo/dom-style',
		'dojo/string',
		'dojo/store/Cache',
		'dojo/store/JsonRest',
		'dojo/store/Memory',
		'dojo/store/Observable',
		'dbootstrap', // Needs to be included before any reference to a dijit widget
		'dijit/form/DropDownButton',
		'dijit/DropDownMenu',
		'dijit/MenuItem',
		'dijit/MenuSeparator'
	],
	function(common, ioQuery, dom, construct, registry, on, style, string, Cache, JsonRest, Memory, Observable, dbootstrap, DropDownButton, DropDownMenu, MenuItem, MenuSeparator){

		//
		// Initialization
		//

		var _categoryStore,
			_categoryNb = 0,
			_linkStore;

		var _init = function(params){
			
			common.init(params);

			// Rest stores with a local cache
			_categoryStore = _getStore('category');
			_linkStore = _getStore('link');
			
			// Handle current user
			var currentUser = _detectCurrent(params.user, params.users);
			
			// Build information panes
			_buildCategoryPanes(dom.byId('insertionPoint'), currentUser);

			// Register event handlers
			_initHandlers(currentUser, params.users, params.logoutURL);
			
			// Set bookmarklet
			_setBookmarklet(currentUser);
		};
		
		var _detectCurrent = function(loggedUser, users) {
			if (users == null || users.length < 0) {
				return loggedUser;
			}
			var queryStr = window.location.search,
				queryObj = queryStr ? ioQuery.queryToObject(queryStr.substr(1)) : {};
			if (queryObj.userId) {
				for(var idx=0; idx<users.length; idx++) {
					if (users[idx].id == queryObj.userId) {
						registry.byId('userList').set('value', '' + users[idx].id);
						return users[idx];
					}
				}
			}
			return loggedUser;
		}

		var _getStore = function(entityName){
			// summary:
			//	 Returns an instance of an observable JSON REST store with a caching capability
	
			return Observable(new Cache(
				new JsonRest({ target: '/api/' + entityName + '/', idProperty: 'id', accepts: 'application/json, text/plain' }),
				new Memory({ idProperty: 'id' })
			));
		};
		
		var _buildCategoryPanes = function(insertionPoint, user){
			var fluidRow = construct.create('div', { 'class': 'row-fluid categories' }, insertionPoint),
				fluidRowSpanNb = 0;

			_categoryStore.query({ ownerId: user.id }, { sort: [{ attribute: 'order' }] }).forEach(function(category, idx){
				++ _categoryNb;
				
				var categoryWidthInSpan = category.width == null ? 2 : parseInt(category.width.substring('span'.length));
				fluidRowSpanNb += categoryWidthInSpan;
				if (12 < fluidRowSpanNb) {
					fluidRow = construct.create('div', { 'class': 'row-fluid categories' }, insertionPoint);
					fluidRowSpanNb = categoryWidthInSpan;
				}
				
				// Add the category block
				var cell = table = construct.create('table', { id: 'catBox-' + idx, 'class': (category.width == null ? 'span2' : category.width) + ' category', style: 'border-spacing: 0;' }, fluidRow),
					tr = construct.create('tr', {}, table),
					th1 = construct.create('th', { id: 'catTitle-' + idx, style: 'width: 100%;', innerHTML: category.title }, tr),
					td2 = construct.create('td', { style: 'vertical-align: top;' }, tr),
					menu = new DropDownMenu({ style: 'display: none;'});

			    menu.addChild(new MenuItem({
			    	id: 'setCatTitle-' + idx,
			        label: 'Renommer',
			        onClick: function(){
			        	registry.byId('setCatTitleId').set('value', category.id);
			        	registry.byId('setCatTitleOld').set('value', category.title);
			        	registry.byId('setCatTitleDlg').show();
			        }
			    }));
			    menu.addChild(new MenuItem({
			    	id: 'catDelete-' + idx,
			        label: 'Retirer',
			        onClick: function(){
						_categoryStore.remove(category.id).then(
							function(){ window.location.reload(); },
							function(err){ common.handleXhrErrors(err) }
						);
					}
			    }));
			    menu.addChild(new MenuSeparator());
			    menu.addChild(new MenuItem({
			    	id: 'catAddLink-' + idx,
			        label: 'Ajouter une référence',
			        onClick: function(){
			        	registry.byId('addLnkCatId').set('value', category.id);
						registry.byId('addLnkDlg').show();
			        }
			    }));
			    menu.addChild(new MenuSeparator());
			    menu.addChild(new MenuItem({
			    	id: 'setCatWidth-' + idx,
			        label: 'Changer la largeur',
			        onClick: function(){
			        	registry.byId('setCatWidthId').set('value', category.id);
			        	registry.byId('setCatWidth').set('value', category.width == null ? 'span2' : category.width);
			        	registry.byId('setCatWidthDlg').show();
			        }
			    }));
			    menu.addChild(new MenuItem({
			    	id: 'setCatOrder-' + idx,
			        label: 'Changer la position',
			        onClick: function(){
			        	var orderList = registry.byId('setCatOrder'), optionList = orderList.get('options'), idx = 1;
			        	if (optionList.length != _categoryNb + 1) { // +1 because of the default option
			        		while(idx < _categoryNb - 1) {
			        			++idx;
			        			optionList.push({ value: '' + idx, label: 'Position: ' + idx }); // Be careful: value MUST be a string
			        		}
		        			optionList.push({ value: '' + _categoryNb , label: 'Dernière position' });
			        	}
			        	registry.byId('setCatOrderId').set('value', category.id);
			        	registry.byId('setCatOrderOld').set('value', category.order == null ? ' ' : category.order);
			        	registry.byId('setCatOrder').set('value', category.order == null ? ' ' : '' + category.order); // Be careful: value MUST be a string
			        	registry.byId('setCatOrderDlg').show();
			        }
			    }));
			    td2.appendChild(new DropDownButton({
			    	id: 'catCtxtMenu-' + idx,
			        label: category.title,
			        showLabel: false,
			        dropDown: menu
			    }).domNode);

				// Add the link blocks
				_buildLinkPanes(table, category, idx);
			});
		};

		var _buildLinkPanes = function(table, category, catIdx){

			_linkStore.query({ categoryId: category.id }).forEach(function(link, linkIdx){
				
				// Add the link
				var tr = construct.create('tr', {}, table),
					td1 = construct.create('td', {}, tr),
					a = construct.create('a', { id: 'linkAnchor-' + catIdx + '-' + linkIdx, href: link.href, target: '_blank', innerHTML: link.title }, td1),
					td2 = construct.create('td', { style: 'text-align: center; vertical-align: top;' }, tr),
					span = construct.create('span', { id: 'linkDelete-' + catIdx + '-' + linkIdx, 'class': 'icon-remove' }, td2);

				dojo.connect(span, 'onclick', function(evt){
					_linkStore.remove(link.id).then(
						function(){ window.location.reload(); },
						function(err){ common.handleXhrErrors(err) }
					);
				});
			});
		};		

		var _initHandlers = function(currentUser, users, logoutURL){
	
			// Control the menu items
			dojo.connect(dom.byId('addCat'), 'onclick', function(evt){
				registry.byId('addCatDlg').show();
				evt.preventDefault();
				return false;
			});
			dojo.connect(dom.byId('logoutBtn'), 'onclick', function(evt){
				window.location = logoutURL;
				evt.preventDefault();
				return false;
			});
			
			// To handle the user profile switch
			if (users) {
				registry.byId('userList').on('change', function(value){
					if (currentUser.id != parseInt(value)) {
						var href = window.location.href,
							questionMarkPos = href.indexOf('?');
							newLocation = (questionMarkPos == -1 ? href : href.substring(0, questionMarkPos)) + '?userId=' + value;
						window.location = newLocation;
					}
				});
			}

			// To manage a category
			dojo.connect(dom.byId('addCatForm'), 'onsubmit', function(evt){
				evt.preventDefault();

				var form = registry.byId('addCatForm');
				if (!form.validate()) {
					return false;
				}
				var candidate = form.get('value');
				candidate.ownerId = currentUser.id;
				candidate.width = 'span2';
				candidate.order = _categoryNb + 1;

				_categoryStore.add(candidate).then(
					function(){ window.location.reload(); },
					function(err){ common.handleXhrErrors(err) }
				);

				return true;
			});

			dojo.connect(dom.byId('addCatCancel'), 'onclick', function(evt){
				registry.byId('addCatDlg').hide();
			});

			// To manage a link
			dojo.connect(dom.byId('addLnkForm'), 'onsubmit', function(evt){
				evt.preventDefault();
				
				var form = registry.byId('addLnkForm');
				if (!form.validate()) {
					return false;
				}
				var candidate = form.get('value');
				candidate.ownerId = currentUser.id;

				_linkStore.add(candidate).then(
					function(){ window.location.reload(); },
					function(err){ common.handleXhrErrors(err) }
				);

				return true;
			});

			dojo.connect(dom.byId('addLnkCancel'), 'onclick', function(evt){
				registry.byId('addLnkDlg').hide();
			});

			// To update a category title
			dojo.connect(dom.byId('setCatTitleForm'), 'onsubmit', function(evt){
				evt.preventDefault();
				
				var form = registry.byId('setCatTitleForm');
				if (!form.validate()) {
					return false;
				}
				var candidate = form.get('value');
				candidate.ownerId = currentUser.id;

				_categoryStore.put(candidate).then(
					function(){ window.location.reload(); },
					function(err){ common.handleXhrErrors(err) }
				);

				return true;
			});

			dojo.connect(dom.byId('setCatTitleCancel'), 'onclick', function(evt){
				registry.byId('setCatTitleDlg').hide();
			});

			// To update a category width
			dojo.connect(dom.byId('setCatWidthForm'), 'onsubmit', function(evt){
				evt.preventDefault();
				
				var form = registry.byId('setCatWidthForm');
				if (!form.validate()) {
					return false;
				}
				var candidate = form.get('value');
				candidate.ownerId = currentUser.id;

				_categoryStore.put(candidate).then(
					function(){ window.location.reload(); },
					function(err){ common.handleXhrErrors(err) }
				);

				return true;
			});

			dojo.connect(dom.byId('setCatWidthCancel'), 'onclick', function(evt){
				registry.byId('setCatWidthDlg').hide();
			});

			// To update a category order
			dojo.connect(dom.byId('setCatOrderForm'), 'onsubmit', function(evt){
				evt.preventDefault();
				
				var form = registry.byId('setCatOrderForm');
				if (!form.validate()) {
					return false;
				}
				var candidate = form.get('value');
				if (candidate.oldOrder == candidate.order) {
					registry.byId('setCatOrderDlg').hide();
					return false;
				}
				delete candidate.oldOrder;
				candidate.ownerId = currentUser.id;

				_categoryStore.put(candidate).then(
					function(){ window.location.reload(); },
					function(err){ common.handleXhrErrors(err) }
				);

				return true;
			});

			dojo.connect(dom.byId('setCatOrderCancel'), 'onclick', function(evt){
				registry.byId('setCatOrderDlg').hide();
			});

		};

		var _setBookmarklet = function(currentUser) {
			var anchor = dom.byId('bookmarklet'),
				href = 'javascript:(function(){' +
					'var dc=document,' +
	                	'sc=dc.createElement("script"),' +
	                	'hd=dc.getElementsByTagName("head")[0];' +
					// 'sc.src="http://custom-portal.appspot.com/js/dderrien/bookmarklet.js";' +
					'sc.src="http://localhost:9090/js/dderrien/bookmarklet.js";' +
		            'hd.appendChild(sc);' +
	            '})()';
            if (anchor) {
				anchor.href = href;
            }
		}

		// Publish the methods at the AMD level (to be accessible to AMD module consumers)
		return {
			init: _init
		}
	}
);
