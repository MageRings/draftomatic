var Magic;
(function (Magic) {
    var App;
    (function (App) {
        var DraftConfig;
        (function (DraftConfig) {
            var DraftConfigDirective = (function () {
                function DraftConfigDirective() {
                    this.controller = "draftConfigController";
                    this.controllerAs = "ctrl";
                    this.restrict = "E";
                    this.scope = {
                        tournamentModel: "=",
                        allPlayers: "="
                    };
                    this.templateUrl = "draftConfig/draftConfigTemplate.html";
                }
                return DraftConfigDirective;
            })();
            DraftConfig.DraftConfigDirective = DraftConfigDirective;
            App.magic.directive("draftConfig", function () { return new DraftConfigDirective(); });
        })(DraftConfig = App.DraftConfig || (App.DraftConfig = {}));
    })(App = Magic.App || (Magic.App = {}));
})(Magic || (Magic = {}));

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImRyYWZ0Q29uZmlnL2RyYWZ0Q29uZmlnRGlyZWN0aXZlLnRzIl0sIm5hbWVzIjpbIk1hZ2ljIiwiTWFnaWMuQXBwIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnLkRyYWZ0Q29uZmlnRGlyZWN0aXZlIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnLkRyYWZ0Q29uZmlnRGlyZWN0aXZlLmNvbnN0cnVjdG9yIl0sIm1hcHBpbmdzIjoiQUFBQSxJQUFPLEtBQUssQ0FjWDtBQWRELFdBQU8sS0FBSztJQUFDQSxJQUFBQSxHQUFHQSxDQWNmQTtJQWRZQSxXQUFBQSxHQUFHQTtRQUFDQyxJQUFBQSxXQUFXQSxDQWMzQkE7UUFkZ0JBLFdBQUFBLFdBQVdBLEVBQUNBLENBQUNBO1lBRTFCQztnQkFBQUM7b0JBQ1dDLGVBQVVBLEdBQUdBLHVCQUF1QkEsQ0FBQ0E7b0JBQ3JDQSxpQkFBWUEsR0FBR0EsTUFBTUEsQ0FBQ0E7b0JBQ3RCQSxhQUFRQSxHQUFHQSxHQUFHQSxDQUFDQTtvQkFDZkEsVUFBS0EsR0FBR0E7d0JBQ1hBLGVBQWVBLEVBQUVBLEdBQUdBO3dCQUNwQkEsVUFBVUEsRUFBRUEsR0FBR0E7cUJBQ2xCQSxDQUFDQTtvQkFDS0EsZ0JBQVdBLEdBQUdBLHNDQUFzQ0EsQ0FBQ0E7Z0JBQ2hFQSxDQUFDQTtnQkFBREQsMkJBQUNBO1lBQURBLENBVEFELEFBU0NDLElBQUFEO1lBVFlBLGdDQUFvQkEsdUJBU2hDQSxDQUFBQTtZQUVEQSxTQUFLQSxDQUFDQSxTQUFTQSxDQUFDQSxhQUFhQSxFQUFFQSxjQUFNQSxPQUFBQSxJQUFJQSxvQkFBb0JBLEVBQUVBLEVBQTFCQSxDQUEwQkEsQ0FBQ0EsQ0FBQ0E7UUFDckVBLENBQUNBLEVBZGdCRCxXQUFXQSxHQUFYQSxlQUFXQSxLQUFYQSxlQUFXQSxRQWMzQkE7SUFBREEsQ0FBQ0EsRUFkWUQsR0FBR0EsR0FBSEEsU0FBR0EsS0FBSEEsU0FBR0EsUUFjZkE7QUFBREEsQ0FBQ0EsRUFkTSxLQUFLLEtBQUwsS0FBSyxRQWNYIiwiZmlsZSI6ImRyYWZ0Q29uZmlnL2RyYWZ0Q29uZmlnRGlyZWN0aXZlLmpzIiwic291cmNlc0NvbnRlbnQiOlsibW9kdWxlIE1hZ2ljLkFwcC5EcmFmdENvbmZpZyB7XG5cbiAgICBleHBvcnQgY2xhc3MgRHJhZnRDb25maWdEaXJlY3RpdmUgaW1wbGVtZW50cyBuZy5JRGlyZWN0aXZlIHtcbiAgICAgICAgcHVibGljIGNvbnRyb2xsZXIgPSBcImRyYWZ0Q29uZmlnQ29udHJvbGxlclwiO1xuICAgICAgICBwdWJsaWMgY29udHJvbGxlckFzID0gXCJjdHJsXCI7XG4gICAgICAgIHB1YmxpYyByZXN0cmljdCA9IFwiRVwiO1xuICAgICAgICBwdWJsaWMgc2NvcGUgPSB7XG4gICAgICAgICAgICB0b3VybmFtZW50TW9kZWw6IFwiPVwiLFxuICAgICAgICAgICAgYWxsUGxheWVyczogXCI9XCJcbiAgICAgICAgfTtcbiAgICAgICAgcHVibGljIHRlbXBsYXRlVXJsID0gXCJkcmFmdENvbmZpZy9kcmFmdENvbmZpZ1RlbXBsYXRlLmh0bWxcIjtcbiAgICB9XG5cbiAgICBtYWdpYy5kaXJlY3RpdmUoXCJkcmFmdENvbmZpZ1wiLCAoKSA9PiBuZXcgRHJhZnRDb25maWdEaXJlY3RpdmUoKSk7XG59XG5cblxuIl0sInNvdXJjZVJvb3QiOiIvc291cmNlLyJ9