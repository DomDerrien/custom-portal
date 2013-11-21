define(
	[
		'dojo',
		'dojo/text!./templates/PickerDialog.html',
		'dijit/_Widget',
		'dijit/_TemplatedMixin',
		'dijit/_WidgetsInTemplateMixin',
		'dojo/on',
		'dojo/store/Cache',
		'dojo/store/JsonRest',
		'dojo/store/Memory',
		'dojo/store/Observable',
		'dgrid/List',
		'dgrid/OnDemandGrid',
		'dgrid/Tree',
		'dgrid/Selection',
		'dgrid/Keyboard',
		'dojo/string',
		'dijit/Dialog',
		'dijit/form/Button',
		'dijit/form/Form',
		'dijit/form/TextBox',
		'dijit/form/ValidationTextBox',
		'dijit/layout/BorderContainer',
		'dijit/layout/ContentPane'
	],
	function(dojo, template, _Widget, _TemplatedMixin, _WidgetsInTemplateMixin, on, Cache, JsonRest, Memory, Observable, List, Grid, Tree, Selection, Keyboard) {
		// module:
		//	 ea/scrabble/picker
		// summary:
		//	 The module defines a dialog box displaying a search field set and a grid to allow
		//	 the selection of a Entity record. The selected {record display name; record identifier}
		//	 are conveyed back to the caller via a callback method.

		var pickerBox = dojo.declare(
			[_Widget, _TemplatedMixin, _WidgetsInTemplateMixin], {
				// Default attributes
				templateString: template,
				widgetsInTemplate: true,
				baseClass: 'pickerDialog', //	root CSS class of the widget

				// Custom attributes
				dialogTitle: 'Entity Selector',
				searchAreaTitle: 'Search Criteria',
				width: '80%',
				gridHeight: '200px',
				store: null,

				dialogNode: null,
				searchFormNode: null,
				searchButtonNode: null,
				listHeaderNode: null,
				listGridNode: null,
				cancelButtonNode: null,
				okButtonNode: null,

				parent: null,
				selectedEntity: null,

				postCreate: function() {
					this.inherited(arguments);

					this.searchButtonNode.set('onClick', dojo.hitch(this, '_search'));
					this.cancelButtonNode.set('onClick', dojo.hitch(this.dialogNode, 'hide'));
					this.okButtonNode.set('onClick', dojo.hitch(this, '_pickIt'));

					this.listHeaderNode.innerHTML = dojo.string.substitute('List size: ${0}', [0]);
				},

				_getStoreAttr: function() { return this.store; },
				_setStoreAttr: function(value) { this._set('store', value); },

				show: function() {
					this.dialogNode.show();
					if(!this.store) {
						alert('Unusable picker as it connection to the back-end store has not been intialized! Ask the dummy developer to fix it...');
					}
				},

				_search: function() {
					var params = this.searchFormNode.get('value');
					for (var p in params)
						if (params[p] == '')
							delete params[p];

					if (!this.listGridNode) {
						this.listGridNode = new dojo.declare([Grid, Selection, Keyboard])(
							{
								columns: { key: 'Key', name: 'Name', description: 'Description' },
								query: params,
								selectionMode: 'single',
								store: this.store
							},
							this.id + '_list'
						);
						this.listGridNode.on('.dgrid-row:click', dojo.hitch(this, '_handleEntityHighlighted'));
						this.listGridNode.on('.dgrid-row:dblclick', dojo.hitch(this, '_handleEntitySelected'));
					}
					else {
						this.listGridNode.set('query', params);
					}
				},

				_handleEntitySelected: function(event) {
					this._handleEntityHighlighted(event);
					this._pickIt();
				},

				_handleEntityHighlighted: function(event) {
					this.okButtonNode.set('disabled', false);
					this.selectedEntity = this.listGridNode.row(event).data;
				},

				_pickIt: function() {
					this.parent.reportValue(this.selectedEntity.key, this.selectedEntity.name);
					this.dialogNode.hide();
				}
			}
		);

		return pickerBox;
	}
);
