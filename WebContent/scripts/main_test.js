/* step1: ()() */
(function() {									// 匿名自执行函数(闭包体)
  
	/* step2: variables */
	var user_id = '1111';						// 因为不涉及登录，所以模拟用户名和地址
	var user_fullname = 'John Smith';
	var lng = -122.08;
	var lat = 37.38;

	/* step3: main function(entrance) */
	init();										// 入口函数/主函数：让代码更加模块化

	// 需要实现：1. welcome  2. 点击nearby/My Favorites/Recommendation，出现item list信息   3. 网页刷新后，item list信息也会出现

	/* step4: define init function */
	function init() {
		// Register event listeners
		/* id='...'的标签绑定							做什么样操作(函数) */
		$('nearby-btn').addEventListener('click', loadNearbyItems);
//		$('fav-btn').addEventListener('click', loadFavoriteItems);
//		$('recommend-btn').addEventListener('click', loadRecommendedItems);
		
		var welcomeMsg = $('welcome-msg');
		/*			修改内容									*/
        welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;
		
        
        // step 7
        initGeoLocation();		// 进入网页后，不点击直接实现item list
	}
	
	/* step5: create $ function */
	/**
	 * A helper function that creates a DOM element <tag options...>
	 */
	// 模拟选择标签的功能
	function $(tag, options) {
		if (!options) {
			return document.getElementById(tag);
		}
		var element = document.createElement(tag);

		for ( var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}
		return element;
	}
	
	/* step6: create AJAX helper function */
	/**
	 * @param method - GET|POST|PUT|DELETE
	 * @param url - API end point
	 * @param callback - This the successful callback
	 * @param errorHandler - This is the failed callback
	 */
	// 实现AJAX core
	function ajax(method, url, data, callback, errorHandler) {
		var xhr = new XMLHttpRequest();					// 创建AJAX core的对象

		xhr.open(method, url, true);					// 告知使用方法(GET, POST), URL地址

		// 判断1：xhr是否有问题
		xhr.onload = function() {						// 若xhr正常，处理从服务器来的response
			if (xhr.status === 200) {
				callback(xhr.responseText);						// 回调函数：将xhr.responseText函数传给ajax函数
			} else if (xhr.status === 403) {
				onSessionInvalid();
			} else {
				errorHandler();
			}
		};

		xhr.onerror = function() {						// 若xhr有问题，进行错误处理
			console.error("The request couldn't be completed.");
			errorHandler();
		};

		// 判断2：是否有data
		if (data === null) {							// 若request不包含data，直接传递
			xhr.send();
		} else {										// 若request包含data = 要向服务器传送数据，request加header后传递
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
	}
	
	/** step 7: initGeoLocation function **/
	function initGeoLocation() {
		if (navigator.geolocation) {			// 若位置存在，则更新
			// step 8
			navigator.geolocation.getCurrentPosition(onPositionUpdated,	// getCurrentPosition是web browser自带的函数
					onLoadPositionFailed, {
						maximumAge : 60000
					});
			showLoadingMessage('Retrieving your location...');
		} else {
			// step 9
			onLoadPositionFailed();
		}
	}

	/** step 8: onPositionUpdated function **/
	function onPositionUpdated(position) {
		lat = position.coords.latitude;
		lng = position.coords.longitude;

		// step 11
		loadNearbyItems();
	}

	/** step 9: onPositionUpdated function **/
	function onLoadPositionFailed() {			// 若出错，先显示无法从navigator获得geo信息，再从IP获得geo信息
		console.warn('navigator.geolocation is not available');
		
		//step 10
		getLocationFromIP();
	}
	
	/** step 10: getLocationFromIP function **/
	function getLocationFromIP() {
		// Get location from http://ipinfo.io/json
		var url = 'http://ipinfo.io/json'
		var req = null;
		ajax('GET', url, req, function(res) {
			var result = JSON.parse(res);
			if ('loc' in result) {
				var loc = result.loc.split(',');
				lat = loc[0];
				lng = loc[1];
			} else {
				console.warn('Getting location by IP failed.');
			}
			// step 11
			loadNearbyItems();
		});
	}

	/** step 11: loadNearbyItems function **/
	/**
	 * API #1 Load the nearby items API end point: [GET]
	 * /Dashi/search?user_id=1111&lat=37.38&lon=-122.08
	 */
	function loadNearbyItems() {
		console.log('loadNearbyItems');
		// step 12
		activeBtn('nearby-btn');

		// The request parameters
		var url = './search';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
		var req = JSON.stringify({});

		// step 13
		// display loading message
		showLoadingMessage('Loading nearby items...');

		// make AJAX call
		ajax('GET', url + '?' + params, req,
		// successful callback
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				// step 14
				showWarningMessage('No nearby item.');
			} else {
				// step 16
				listItems(items);
			}
		},
		// failed callback
		function() {
			// step 15
			showErrorMessage('Cannot load nearby items.');
		});
	}
	/** step 12: activeBtn function **/
	
	/**
	 * A helper function that makes a navigation button active
	 * 
	 * @param btnId - The id of the navigation button
	 */
	function activeBtn(btnId) {
		var btns = document.getElementsByClassName('main-nav-btn');

		// deactivate all navigation buttons
		for (var i = 0; i < btns.length; i++) {
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}

		// active the one that has id = btnId
		var btn = $(btnId);
		btn.className += ' active';
	}

	/** step 13: showLoadingMessage function **/
	function showLoadingMessage(msg) {
		var itemList = $('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
				+ msg + '</p>';
	}
	
	/** step 14: showWarningMessage function **/
	function showWarningMessage(msg) {
		var itemList = $('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> '
				+ msg + '</p>';
	}
	
	/** step15: showErrorMessage function **/
	function showErrorMessage(msg) {
		var itemList = $('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> '
				+ msg + '</p>';
	}

	/** step16: listItems function **/
	/**
	 * @param items - An array of item JSON objects
	 */
	function listItems(items) {
		// Clear the current results
		var itemList = $('item-list');
		itemList.innerHTML = '';

		for (var i = 0; i < items.length; i++) {
			// step 17
			addItem(itemList, items[i]);
		}
	}

	/** step17: addItem function **/
	/**
	 * Add item to the list
	 * @param itemList - The <ul id="item-list"> tag
	 * @param item - The item data (JSON object)
	 */
	function addItem(itemList, item) {
		var item_id = item.item_id;

		// create the <li> tag and specify the id and class attributes
		var li = $('li', {
			id : 'item-' + item_id,
			className : 'item'
		});

		// set the data attribute
		li.dataset.item_id = item_id;
		li.dataset.favorite = item.favorite;

		// item image
		if (item.image_url) {
			li.appendChild($('img', {
				src : item.image_url
			}));
		} else {
			li.appendChild($('img', {
				src : 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
			}))
		}
		// section
		var section = $('div', {});

		// title
		var title = $('a', {
			href : item.url,
			target : '_blank',
			className : 'item-name'
		});
		title.innerHTML = item.name;
		section.appendChild(title);

		// category
		var category = $('p', {
			className : 'item-category'
		});
		category.innerHTML = 'Category: ' + item.categories.join(', ');
		section.appendChild(category);

		var stars = $('div', {
			className : 'stars'
		});
		
		for (var i = 0; i < item.rating; i++) {
			var star = $('i', {
				className : 'fa fa-star'
			});
			stars.appendChild(star);
		}

		if (('' + item.rating).match(/\.5$/)) {
			stars.appendChild($('i', {
				className : 'fa fa-star-half-o'
			}));
		}

		section.appendChild(stars);

		li.appendChild(section);

		// address
		var address = $('p', {
			className : 'item-address'
		});

		address.innerHTML = item.address.replace(/,/g, '<br/>').replace(/\"/g,
				'');
		li.appendChild(address);

		// favorite link
		var favLink = $('p', {
			className : 'fav-link'
		});

		favLink.onclick = function() {
			changeFavoriteItem(item_id);
		};

		favLink.appendChild($('i', {
			id : 'fav-icon-' + item_id,
			className : item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
		}));

		li.appendChild(favLink);

		itemList.appendChild(li);
	}


})()
