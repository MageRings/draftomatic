declare module Magic.App.Round {
    class RoundController {
        private scope;
        static $inject: string[];
        constructor($scope: ng.IScope);
        completeRoundSuffix(match: any): string;
    }
}
