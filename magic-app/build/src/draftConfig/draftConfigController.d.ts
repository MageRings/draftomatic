declare module Magic.App.DraftConfig {
    interface ITournamentModel {
        numRounds: number;
        players: IPlayer[];
        format: string;
        set: string;
    }
    interface IPlayer {
        name: string;
        id: number;
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
        allPlayers: IPlayer[];
    }
    class DraftConfigController implements IDraftConfigController {
        private $scope;
        formats: string[];
        sets: ISet[];
        pendingPlayer: any;
        static $inject: string[];
        constructor($scope: IDraftConfigScope, $http: ng.IHttpService);
        addPendingPlayer(): void;
        removePlayer(player: IPlayer): void;
    }
}
