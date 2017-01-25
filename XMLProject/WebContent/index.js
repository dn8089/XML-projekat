(function(angular) {
	var app = angular.module("app", ['act','ui.router']);
	
	app.config(config);
	
	function config ($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise('/main');
		$stateProvider
		.state('main', {
			url: '/main',
			templateUrl: 'welcome.html'
		})
		.state('proposedActs', {
			url: '/predlozena-akta',
			templateUrl: 'acts/actsList.html',
			controller: 'proposedActsCtrl'
		})
		.state('acceptedActs', {
			url: '/usvojena-akta',
			templateUrl: 'acts/actsList.html',
			controller: 'accepteddActsCtrl'
		})
		.state('readAct', {
			url: '/akt/:docId',
			templateUrl: 'acts/act.html',
			controller: 'readActCtrl'
		})
		.state('createAct', {
			url: '/kreiraj-akt',
			templateUrl: 'acts/createAct.html',
			controller: 'createActCtrl'
		})
		.state('search', {
			url: '/pretraga',
			templateUrl: 'acts/search.html',
			controller: 'searchCtrl'
		});
	};

})(angular);
