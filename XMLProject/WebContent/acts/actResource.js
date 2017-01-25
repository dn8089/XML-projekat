(function (angular) {
	angular.module('act.resource', ['ngResource'])
	.factory('Act', function($resource) {
		var Act = $resource('/XMLProject/act', 
			{docId:'@docId'},
			{
				'search': {method:'GET', isArray:true, params: {criteria:'@criteria'}, url: '/XMLProject/act/search/:criteria'},
				'findByCollection': {method:'GET', isArray:true, params: {collId:'@collId'}, url: '/XMLProject/act/collection?collId=:collId'},
				'acceptAct' : {method:'PUT', params: {docId:'@docId'}, url: '/XMLProject/act/acceptAct?docId=:docId'}
			}
		);
		return Act;
	});
}(angular));