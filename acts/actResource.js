(function (angular) {
	angular.module('act.resource', ['ngResource'])
	.factory('Act', function($resource) {
		var Act = $resource('http://localhost:8080/XMLProject/skupstina', 
			{docId:'@docId'},
			{
				'search': {method:'GET', isArray:true, params: {criteria:'@criteria'}, url: 'http://localhost:8080/XMLProject/skupstina/pretraga?criteria=:criteria'}
			}
		);
		return Act;
	});
}(angular));