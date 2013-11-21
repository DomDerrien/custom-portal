(function() {
	try {
		
	var dc = document,
		selectId = 'customPortalCategories',
		select = dc.getElementById(selectId),
		// serverURL = 'http://custom-portal.appspot.com/',
		serverURL = 'http://localhost:9090/',
		xhr;
		
	var checkIfSelectOK = function() {
		var previous = dc.getElementById(selectId);
		if (previous) {
			previous.style.display = 'block';
			previous.selectedIndex = 0;
			return true;
		}
	};

	var getCategories = function(onSuccess, onFailure) {
		// Requires JsonP
		var categories = [{id:1,title:'test'},{id:2,title:'testttttttttttt'}];
		onSuccess(categories);
	}

	var getCreateSelect = function(createLink) {
		return function(categories) {
			var body = dc.getElementsByTagName('body')[0],
				select = dc.createElement('select'),
				option = dc.createElement('option'),
				image = dc.createElement('img'),
				idx;
			option.value = 0;
			option.innerHTML = 'Choisir une cat&eacute;gorie';
			select.appendChild(option);
			for (idx = 0; idx < categories.length; idx++) {
				option = dc.createElement('option');
				option.value = categories[idx].id;
				option.innerHTML = categories[idx].title;
				select.appendChild(option);
			}
			select.id = selectId;
			select.style.position = 'absolute';
			select.style.top = '0';
			select.style.right = '0';
			select.style.borderWidth = '5px';
			select.style.borderColor = 'orange';
			select.onchange = function(value) {
				console.log('onchange', select.value, value)
				if (value == ' ') {
					return;
				}
				image.src = '' +
					serverURL + 'api/link' +
					'?title=' + window.title +
					'&href=' + escape(window.location) +
					'&categoryId=' + select.value +
					'&_method=PUT';
				image.onerror = function() {
					alert('La référence n\'a pu être créée. Assurez-vous d\'être authentifié(e) sur la page principale ' + serverURL);
				}
				select.style.display = 'none';
			}
			console.log(select)
			body.appendChild(select);
		};
	};
	
	var getCreateLink = function(onError) {
		return function(evt) {
			if (select.value == ' ') {
				return;
			}
			image.src = '' +
				serverURL + 'api/link' +
				'?title=' + window.title +
				'&href=' + escape(window.location) +
				'&categoryId=' + select.value +
				'&_method=PUT';
			image.onerror = onError;
			select.style.display = 'none';
		}
	};
	
	var onError = function() {
		alert('La référence n\'a pu être créée. Assurez-vous d\'être authentifié(e) sur la page principale disponible à l\'adresse:\n' + serverURL);
	};
	
	var main = function() {
		if (!checkIfSelectOK()) {
			getCategories(getCreateSelect(getCreateLink(onError)), null);
		}
	};
	
	main();
	}
	catch (ex) {
		console.log(ex)
		alert("Pas possible de transmettre la référence :(");
	}
})()