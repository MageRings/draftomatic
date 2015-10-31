declare module Magic.App.Tournament {
    class TournamentController {
        static $inject: string[];
        tournamentModel: Magic.App.DraftConfig.ITournamentModel;
        state: ng.ui.IStateService;
        private http;
        constructor($scope: ng.IScope, $http: ng.IHttpService, $state: ng.ui.IStateService);
        startTournament(): void;
    }
}
