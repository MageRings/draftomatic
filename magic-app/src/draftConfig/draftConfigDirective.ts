module Magic.App.DraftConfig {

    export class DraftConfigDirective implements ng.IDirective {
        public controller = "draftConfigController";
        public controllerAs = "ctrl";
        public restrict = "E";
        public scope = {
            tournamentModel: "="
        };
        public templateUrl = "draftConfig/draftConfigTemplate.html";
    }

    magic.directive("draftConfig", () => new DraftConfigDirective());
}


