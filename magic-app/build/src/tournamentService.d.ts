declare module Magic.App.Tournament {
    interface IPlayer {
        name: string;
    }
    interface IPairing {
        player1: IPlayer;
        player2: IPlayer;
    }
    interface IResult {
        pairing: IPairing;
        p1wins: number;
        p2wins: number;
        draws: number;
    }
    class TournamentService {
        private $q;
        private $http;
        static $inject: string[];
        constructor($http: ng.IHttpService, $q: ng.IQService);
        get<T>(methodName: string, params?: any): ng.IHttpPromise<T>;
        post<T>(methodName: string, params?: any): ng.IHttpPromise<T>;
        setTournamentResults(): void;
    }
}
