define(
	[
		'dojo/_base/kernel',
		'dojo/dom',
		'dojo/_base/window',
		'dojo/dom-construct',
		'dojo/dom-style',
		'dojo/json'
	],
	function(dojo, dom, win, construct, style, JSON) {
		// module:
		//	 dderrien/common
		// summary:
		//	 The module defines a set of common resources

		// _initParams: Object
		//	 Keeps an handle on the parameters passed during the initialization
		var _initParams = {};

		var _init = function(params) {
			// summary:
			//	 Keeps tracks of the given parameters (later available with getSettings(name))
			//	 Prepares a default floating <div/> to display {info, warning, error} messages to the end-user

			dojo.mixin(_initParams, params);

			if (!dom.byId('notificationArea')) {
				var area = construct.create('div', {
					id: 'notificationArea'
				}, win.body());
				construct.create('span', { id: 'notificationAreaClose', innerHTML: 'X' }, area);
				construct.create('img', { id: 'notificationLevelIcon', }, area);
				construct.create('span', { id: 'notificationMessage' }, area);
			}
			dojo.connect(dom.byId('notificationAreaClose'), 'onclick', _hideNotificationArea);
		};

		var _handleXhrErrors = function(error) {
			// summary:
			//	 Displays a message related with the error reported by a failing XHR call

			if (console) {
				console.log('XHR error', error);
			}

			var message, level = 'error';
			switch (error.status) {
				case 201:
					message = 'The resource has been successfully created.';
					level = 'success';
					break;
				case 400:
					var payload = error.responseText, violations, violationNb, idx, violation;
					message = 'The requested process failed.';
					if (payload) {
						violations = JSON.parse(payload);
						violationNb = violations == null ? 0 : violations.length;
						for(idx = 0; idx < violationNb; idx++) {
							violation = violations[idx];
							message += '<br>Error: ' + violation.message + ' in ' + violation.entity;
							if (violation.property) {
								message += ', ' + violation.property;
							}
							message += '.';
						}
					}
					break;
				case 404: message = 'The requested information were not found.'; break;
				case 405: message = 'The triggered action is not supported.'; break;
				case 500: message = 'The server reported an error.\n\n' + error.responseText; break;
				default: message = 'Unknown error.';
			}

			_showNotification(message, level);
		};

		var _showNotification = function(message, level, autoHide) {
			// summary:
			//	 Tries to display the message in <div/> with the id='notificationArea'
			//	 Falls back on an alert() box

			var imageSource = '/images/warning.png';
			switch(level) {
				case 'info': imageSource = 'images/info.png'; autoHide = true; break;
				case 'success': imageSource = 'images/success.png'; autoHide = true; break;
				case 'error': imageSource = 'images/error.png'; break;
			}
			try {
				dom.byId('notificationLevelIcon').src = imageSource;
				dom.byId('notificationMessage').innerHTML = message;

				dojo.fadeIn({ onBegin: function() { style.set('notificationArea', 'display', 'block'); }, node: 'notificationArea' }).play();

				if (autoHide) {
					setTimeout(_hideNotificationArea, 5000);
				}
			}
			catch(ex) {
				alert(message.replace(/<br>/g, '\n'));
			}
		};

		var _hideNotificationArea = function() {
			// summary:
			//	 Hides gently the notification area

			dojo.fadeOut({ node: 'notificationArea', onEnd: function() { style.set('notificationArea', 'display', 'none'); } }).play();
		};

		// Publish these methods plus others at the AMD level (to be accessible to AMD module consumers)
		return {
			init: _init,
			handleXhrErrors: _handleXhrErrors,
			showNotification: _showNotification,
			hideNotificationArea: _hideNotificationArea
		}
	}
);
