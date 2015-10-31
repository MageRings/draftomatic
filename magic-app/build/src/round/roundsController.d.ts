declare module Magic.App.Round {
    class RoundsController {
        private scope;
        tournament: any;
        seatings: any;
        static $inject: string[];
        private tournamentId;
        private http;
        constructor($scope: ng.IScope, $stateParams: ng.ui.IStateParamsService, $http: ng.IHttpService);
        private pingHeroku();
        pairNextRound(): void;
        getFinalStandings(): void;
        downloadTournamentData(): void;
    }
}
