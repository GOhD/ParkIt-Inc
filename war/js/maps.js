var map;
var infowindow;
var contextMenu;
var pinImages = {};
var pinShadow;
var allEntries = [];
var vancouver = new google.maps.LatLng(49.2505, -123.1119);
var currentLocation;
var directionsService;
var directionsDisplay;
var dialogShowing = false;
var newResourceLocation; // latlong of selected location

function JSMapEntry(type, key, opts)
{	
	this.entryType = type;
	this.key = key;
	this.userName = opts.userName;
	
	var latLng = opts.latLng;
	if (latLng != null) this.latLng = latLng;
	else {
		var lat = opts.lat;
		var lng = opts.lng;
		this.latLng = new google.maps.LatLng(lat, lng);
	}
	
	this.desc = opts.desc;
	this.trustLevel = (opts.trustLevel || 0);
}

function getCurrentLocation()
{
	return {
		latLng: currentLocation,
		hasLocation: (currentLocation != null)
	};
}

function removeAllEntries() {
	for (var i = 0; i < allEntries.length; i++) {
		allEntries[i].marker.setMap(null);
	}
	allEntries = [];
}

function resizeMapsForDirectionPanel(showing)
{
	var defaultMargin = 8;
	var leftMargin = (showing ? 20 : defaultMargin);
	var width = 100 - leftMargin - defaultMargin; 
	$("#map_container").animate({
		'width': width.toString() + '%',
		"margin-left": leftMargin.toString() + '%'
	}, 'normal');
}

function getDirections(entry)
{
	var start = currentLocation;
	var end = entry.latLng;
	
	var request = {
		origin: start,
		destination: end,
		travelMode: google.maps.TravelMode.WALKING,
		unitSystem: google.maps.UnitSystem.METRIC
	};
	directionsService.route(request, function(response, status){
		if (status == google.maps.DirectionsStatus.OK) {
			directionsDisplay.setDirections(response);
			directionsDisplay.setMap(map);
			infowindow.close();
			resizeMapsForDirectionPanel(true);
			$("#direction_container").show('normal');
			
			// dirty hack to make pin icons in directions show up correctly
			setTimeout(function(){
				$("#direction_panel .adp-placemark td:has(img)").each(function(){
					$(this).css({
						display: "inline-block",
						width: "30px"
					});
				});
			}, 10);
		}
	});
}

function tweetLocation(latLng, formatTweetCallback)
{
	$.ajax({
		type: 'get',
		dataType: 'json',
		async: false,
		url: "https://api.foursquare.com/v2/venues/search",
		data: {
			client_id: "WPZ3KO3L0ST522PFQB5MU03BFZCFOISOVWJKFIN1QN0AXGLQ",
			client_secret: "2KJZWRNZC3SHWKUSF2OOGQ02T2YYYMZSN5YXLEBG30CLQ1BX",
			ll: latLng.lat() + "," + latLng.lng(),
			v: "20130331",
			limit: 1
		},
		success: function(response) {
			var venue = response.response.venues[0];
			var shareText = formatTweetCallback(venue);
			if (!shareText) return;
			
			var twitterUrl = "https://twitter.com/intent/tweet?text=" + encodeURIComponent(shareText);
			
			var width = 575,
				height = 400,
				left   = ($(window).width()  - width)  / 2,
				top    = ($(window).height() - height) / 2,
				opts   = 'status=1' + ',location=false' + ',width=' + width + 
						 ',height=' + height + ',top=' + top + ',left=' + left;
	
			window.open(twitterUrl, 'twitter', opts);
		}
	});
}

function tweetEntry(entry)
{
	function typeDescription(type) {
		switch (type.toLowerCase()) {
			case "parkingMeter":
				return "A parking meter";
			default:
				return "meep?";
		}
	}
	
	tweetLocation(entry.latLng, function(venue){
		return "There's " + typeDescription(entry.entryType) + " near " + venue.name + " at " + venue.location.address + " in " + venue.location.city + "!";
	});
}

function tweetMyLocation() 
{
	if (currentLocation == null) {
		alert("We could not retrieve your location, so we can't tweet this!");
		return;
	}
	
	tweetLocation(currentLocation, function(venue){
		return "I am parked at " + venue.name + " at " + venue.location.address + " in " + venue.location.city + "!";
	});
}

$.fn.setTrustLevel = function(level)
{
	function colorForTrustLevel(trust) {
		if (trust > 0) return "#318920";
		else if (trust == 0) return "black";
		else return "#89001a";
	}
	
	return $(this).css({
		"background-color": colorForTrustLevel(level)
	}).text(level);
}

function updateTrustLevel(entry, amount)
{
	updateTrustForEntry(amount, entry.key);
}

function setTrustLevel(key, level)
{
	for (var i = 0; i < allEntries.length; i++) {
		var entry = allEntries[i];
		if (!entry) continue;
		if (entry.key === key) {
			entry.trustLevel = level;
			break;
		}
	}
	$(".trust_rating").setTrustLevel(level);
}

function buildInfoWindowContent(entry)
{
	var content = document.createElement("div");
	var header = $(content).append($("<div></div>"));
	$(header).append(
		$("<h3></h3>")
		.addClass("markerHeader")
		.text(entry.entryType)
		.css({
			display: "inline",
			"padding-right": "10px"
		}));
	$(header).append(
		$("<b></b>")
		.addClass("trust_rating")
		.setTrustLevel(entry.trustLevel));
	var userName = entry.userName;
	var createdBy = "Created by: " + (userName ? userName : "Admin");
	$(content).append(
		$("<p></p>")
		.text(createdBy));
	if (entry.desc) {
		$(content).append(
			$("<p></p>")
			.css("word-wrap", "break-word")
			.text("Description: " + entry.desc));
	}
	
	// dictionary of options to display in the info window,
	// with the associated function being the click handler
	var options = {
		"Directions" : getDirections,
		"Tweet" : tweetEntry,
		"Trust Level" : {
			action: updateTrustLevel,
			dropdown: {
				"Trustworthy" : 1,
				"Compromised!" : -1
			}
		}
	};
	
	$('.dropdown-toggle').dropdown();
	
	var actionList = $("<ul></ul>");
	function appendAction(key) {
		var action = options[key];
		var dropdown = null;
		if (typeof(action) !== "function") {
			dropdown = action.dropdown;
			action = action.action;
		}
		var link = $("<a></a>").text(key);
		if (!dropdown) {
			link.click(function(){
				action(entry);
			});
		}
		var item = $("<li></li>").append(link);
		if (dropdown) {
			item.addClass("dropdown");
			link.addClass("dropdown-toggle")
				.attr("data-toggle", "dropdown")
				.append($("<span></span>")
					.addClass("caret")
					.css("border-top-color", "#0066cc"))
				.dropdown();
			var menu = $("<ul></ul>")
				.addClass("dropdown-menu")
				.attr({
					role: "menu",
					"aria-labelledby": "dLabel"
				});
			
			function addMenuItem(opt) {
				var ctx = dropdown[opt];
				menu.append(
					$("<li></li>").append(
						$("<a></a>")
						.text(opt)
						.attr("href", "#")
						.click(function(){
							action(entry, ctx);
						})));
			}
			
			for (var dropdownOpt in dropdown) {
				addMenuItem(dropdownOpt);
			}
			item.append(menu);
		}
		actionList.append(item);
	}
	
	for (var key in options) {
		appendAction(key);
	}
	
	$(content).append(
				$("<div></div>")
				.addClass("marker_actions")
				.append(actionList));
	
	return content;
}

function addUserMapEntry(latLng, type, description)
{
	createUserMapEntry(Number(latLng.lat()), Number(latLng.lng()), type, description);
	var userName = getCurrentUserName();
	var mapEntry = new JSMapEntry(type, '', {
		userName: userName,
		latLng: latLng,
		desc: description
	});
	addMapEntry(mapEntry);
}

function addMapEntry(mapEntry)
{
	function pinColorForType(type)
	{
		switch (type.toLowerCase())
		{
			case "meter":
				return "1e75ef";
			default:
				return "21db0e";
		}
	}
	
	// create pin image and shadow based on map entry type
	var pinColor = pinColorForType(mapEntry.entryType);
	var isUserEntry = mapEntry.userName !== null;
	console.log("userName: " + mapEntry.userName + " isUserEntry: " + isUserEntry);
	
	var pinImageKey = pinColor + ",userEntry:" + isUserEntry;
	var pinImage = pinImages[pinImageKey];
	
	if (!pinImage) {
	    pinImage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + (isUserEntry ? "U" : "%E2%80%A2") + "|" + pinColor,
	        new google.maps.Size(21, 34),
	        new google.maps.Point(0,0),
	        new google.maps.Point(10, 34));
		pinImages[pinImageKey] = pinImage;
	}
	if (!pinShadow) {
	    pinShadow = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_shadow",
	        new google.maps.Size(40, 37),
	        new google.maps.Point(0, 0),
	        new google.maps.Point(12, 35));
	}
		
	// create marker
	var marker = new google.maps.Marker({
		position: mapEntry.latLng,
		map: map,
		title: mapEntry.entryType,
		icon: pinImage,
		shadow: pinShadow,
		animation: google.maps.Animation.DROP
	});
	mapEntry.marker = marker;
	allEntries.push(mapEntry);
	
	// add click handler to display info window (popup)
	google.maps.event.addListener(marker, 'click', function() {
		if (!dialogShowing) {
			infowindow.setContent(buildInfoWindowContent(mapEntry));
			infowindow.open(map, marker);
		}
	});
}

function displayMapEntry(key, lat, lng, type, userName, description, trust)
{
	var mapEntry = new JSMapEntry(type, key, {
		userName: userName,
		lat: lat,
		lng: lng,
		desc: description,
		trustLevel: trust
	});
	addMapEntry(mapEntry);
}

function initMapMenu()
{
	var contextMenuOptions={};
	contextMenuOptions.classNames = {
		menu: 'context_menu', 
		menuSeparator: 'context_menu_separator'
	};
	var menuItems = [
		{
			className: 'context_menu_item', 
			eventName: 'add_location_click', 
			id: 'addResourceLocation', 
			label: 'Add resource location'
		},
		{}, // separator
		{
			className: 'context_menu_item', 
			eventName: 'zoom_in_click', 
			label: 'Zoom in'
		},
		{
			className: 'context_menu_item', 
			eventName: 'zoom_out_click', 
			label: 'Zoom out'
		},
		{}, // separator
		{
			className:'context_menu_item', 
			eventName:'center_map_click', 
			label:'Center map here'
		}
	];
	contextMenuOptions.menuItems = menuItems;
	
	contextMenu = new ContextMenu(map, contextMenuOptions);
	
	/*
	google.maps.event.addListener(map, 'rightclick', function(mouseEvent){
		if (!dialogShowing)
			contextMenu.show(mouseEvent.latLng);
	});
	
	google.maps.event.addListener(contextMenu, 'menu_item_selected', function(latLng, eventName){
		switch (eventName) {
			case 'add_location_click':
				newResourceLocation = latLng;
				$("#resource-form").modal();
				break;
			case 'zoom_in_click':
				map.setZoom(map.getZoom()+1);
				break;
			case 'zoom_out_click':
				map.setZoom(map.getZoom()-1);
				break;
			case 'center_map_click':
				map.panTo(latLng);
				break;
			default:
				break;
		}
	});*/
}

function plotCurrentLocation(lat, lng)
{
	currentLocation = new google.maps.LatLng(lat, lng);
	map.setCenter(currentLocation);
	map.setZoom(map.getZoom()+1);
	
	var marker = new google.maps.Marker({
		position: currentLocation,
		map: map,
		title: "Current location",
		icon: "../bluecircle.png"
	});
	
	// update location on GWT client instance
	setCurrentLocation(lat, lng);
}

function initMap() {
	function handleNoGeolocation() {
		map.setCenter(vancouver);
		plotCurrentLocation(vancouver.lat(), vancouver.lng());
		noteLocationUnavailable();
		alert("We couldn't access your location, so we will assume you're in Vancouver. Try refreshing.");
		
		// Hides map, but disables login :/
		map.hide();
	}
	
	// Try W3C Geolocation (Preferred)
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var lat = position.coords.latitude;
			var lng = position.coords.longitude;
			plotCurrentLocation(lat, lng);
		}, function() {
			handleNoGeolocation();
		}, {timeout:5000});
	}
	// Browser doesn't support Geolocation
	else {
		handleNoGeolocation();
	}
	
	// init maps
	var mapOptions = {
		zoom: 11,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	
	
	initMapMenu();

	
	infowindow = new google.maps.InfoWindow();
	directionsService = new google.maps.DirectionsService();
	
	google.maps.event.addListener(map, 'click', function(){
		infowindow.close();
		contextMenu.hide();
	});
	
	var rendererOptions = {
		panel: document.getElementById("direction_panel")
	}
	directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
}

var mapVisible = true;
function toggleMapVisible(visible) {
	if (mapVisible == visible) return;
	mapVisible = visible;
	
	var tableView = $("#table_container");
	
	var mapsView = $("#map_container");
	
	var viewToShow = (mapVisible ? mapsView : tableView);
	var viewToHide = (mapVisible ? tableView : mapsView);
	
	viewToHide.hide();
	viewToShow.show();
	
	function toggleButtonForView(view) {
		switch (view.attr('id'))
		{
			case "map_container":
				return $("#map_button");
			case "table_container":
				return $("#table_button");
			default:
				return null;
		}
	}
	
	toggleButtonForView(viewToShow).addClass("active");
	toggleButtonForView(viewToHide).removeClass("active");
}

var tickerSetup = false;
function setupHintsTicker() {
	tickerSetup = true;
	$('#hints').carouFredSel({
		width: '100%',
		height: 30,
		padding: [3, 0],
		items: {
			width: 'variable',
			height: 'auto',
			visible: 1
		},
		scroll: {
			delay: 1000,
			easing: 'linear',
			items: 1,
			duration: 0.03,
			timeoutDuration: 0,
			pauseOnHover: 'immediate'
		}
	});
}

function setHints(hints) {
	$.each(hints, function(index, hint){
		$('#hints').prepend("<label class=\"hint_label\">" + hint + "</label>");
	});
	
	if (!tickerSetup) setupHintsTicker();
}

function showWelcomeAlert(loginUrl) {
	var goToLoginPage = function(){
		window.location.assign(loginUrl);
		// loginIfNecessary();
	}
	$("#welcome_alert").on('hidden', goToLoginPage).modal({
		// remote: loginUrl
	});
	$("#signin_button").click(goToLoginPage);
}

$(document).ready(function(){
	initMap();
	
	$("#table_button").click(function(){
		toggleMapVisible(false);
	}).button();
	$("#map_button").click(function(){
		toggleMapVisible(true);
	}).button().addClass("active");
	
	$("#logout_button").click(function(e){
		e.preventDefault();
		logout();
	});
	
	$("#table_container").hide();
	
	$("#resource-form").on('show', function(){
		$("#resource-description").val("");
		dialogShowing = true;
		infowindow.close();
		contextMenu.hide();
	}).on('hidden', function(){
		dialogShowing = false;
		newResourceLocation = null;
	});
	
	$("#close_directions").click(function(){
		$("#direction_container").hide('normal');
		resizeMapsForDirectionPanel(false);
		directionsDisplay.setMap(null);
	});
	
	$("#resource-form #create_resource").click(function(){
		var selectedType = $("#resource-type").find(":selected").val();
		var description = $("#resource-description").val();
		addUserMapEntry(newResourceLocation, selectedType, description);
		$("#resource-form").modal("hide");
	});
});
