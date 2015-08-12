module Magic.App.Round {

    export class RoundsController {
        private scope: ng.IScope;
        public draws: number = 0;
        public currentRound: number = 2;
        public tournament: any;
        public static $inject = ["$scope", "$stateParams", "$http"];
        private tournamentId : string;
        
        constructor($scope: ng.IScope, $stateParams: ng.ui.IStateParamsService, $http: ng.IHttpService) {
            this.tournament = {
                rounds: ["a", "b"],
                currentRound: 2
            };
            console.log($stateParams);
            this.tournamentId = $stateParams.id;
            this.scope = $scope;
            $http.get<any>("api/tournament/status/" + this.tournamentId).then((response) => {
                console.log(response);
                this.tournament = response.data;
            });
        }
        
        public pairNextRound() {
            this.tournament.pairRound();
        }
    }

    magic.controller("roundsController", RoundsController);
}
