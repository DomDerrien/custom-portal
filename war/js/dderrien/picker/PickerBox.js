define(
	[
		'dojo',
		'dojo/text!./templates/PickerBox.html',
		'dijit/_Widget',
		'dijit/_TemplatedMixin',
		'dijit/_WidgetsInTemplateMixin',
		'./PickerDialog',
		'dijit/form/Button',
		'dijit/form/TextBox'
	],
	function(dojo, template, _Widget, _TemplatedMixin, _WidgetsInTemplateMixin, pickerDialog) {
		// module:
		//	 ea/scrabble/picker
		// summary:
		//	 The module defines an visible <input/> box associated with a hidden one
		//	 (storing the visible value identifier) and a button launching a dialog box
		//	 (displaying a search field set and a grid to allow a resource selection)

		var pickerBox = dojo.declare(
			[_Widget, _TemplatedMixin, _WidgetsInTemplateMixin], {
				// Default attributes
				templateString: template,
				widgetsInTemplate: true,
				baseClass: 'pickerBox', //	root CSS class of the widget

				// Custom attributes
				name: null, // name of the input field which will contain the identifier of the picked entity
				value: null, // identifier of the picked entity
				hiddenAttribute: 'key', // identifier of the attribute to be displayed in the hidden field
				visibleAttribute: 'name', // identifier of the attribute to be displayed in the visible field
				required: false, // if the values are required
				store: null,

				category: null,
				buttonTitle: 'Pick It!',
				buttonIconClass: 'lookupIcon',
				buttonShowLabel: false,
				dialogProps: { width: '50%' },
				_pickerDialog: null,

				hValueNode: null,
				vValueNode: null,
				pickItButtonNode: null,

				postCreate: function() {
					this.inherited(arguments);

					var title = this.category.charAt(0).toUpperCase() + this.category.substring(1) + ' Selector';
					
					this._pickerDialog = new pickerDialog(dojo.mixin({ parent: this, id: this.id + '_dialog', title: title, store: this.store }, this.dialogProps));
					this.pickItButtonNode.set('onClick', dojo.hitch(this._pickerDialog, 'show'));
					if (this.required)
						this.vValueNode.set('required', true);
				},

				_getNameAttr: function() { return this.name; },
				_setNameAttr: function(value) { this._set('name', value); },

				_getValueAttr: function() { return this.value; },
				_setValueAttr: function(value) { this._set('value', value); },

				_getHiddenNameAttr: function() { return this.hiddenName; },
				_setHiddenNameAttr: function(value) { this._set('hiddenName', value); },

				_getVisibleNameAttr: function() { return this.visibleName; },
				_setVisibleNameAttr: function(value) { this._set('visibleName', value); },

				_getStoreAttr: function() { return this._pickerDialog.get('store'); },
				_setStoreAttr: function(value) { this._pickerDialog.set('store', value); },

				reportValue: function(hValue, vValue) {
					this._setValueAttr(hValue);
					this.hValueNode.set('value', hValue);
					this.vValueNode.set('value', vValue || hValue);
				}
			}
		);

		return pickerBox;
	}
);
