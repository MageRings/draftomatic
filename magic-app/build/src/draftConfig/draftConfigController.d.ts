declare module Magic.App.DraftConfig {
    interface ITournamentModel {
        numRounds: number;
        players: IPlayer[];
        format: string;
        set: string;
    }
    interface IPlayer {
        name: string;
    }
    interface ISet {
        name: string;
        value: string;
    }
    interface IDraftConfigController {
    }
    interface IDraftConfigScope extends ng.IScope {
        draftConfigController: IDraftConfigController;
        tournamentModel: ITournamentModel;
    }
    class DraftConfigController implements IDraftConfigController {
        private $scope;
        formats: string[];
        sets: ISet[];
        pendingPlayerName: string;
        private currentId;
        static $inject: string[];
        constructor($scope: IDraftConfigScope, $http: ng.IHttpService);
        addPendingPlayer(): void;
        removePlayer(player: IPlayer): void;
    }
}
