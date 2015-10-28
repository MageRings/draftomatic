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
                        tournamentModel: "="
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

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImRyYWZ0Q29uZmlnL2RyYWZ0Q29uZmlnRGlyZWN0aXZlLnRzIl0sIm5hbWVzIjpbIk1hZ2ljIiwiTWFnaWMuQXBwIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnLkRyYWZ0Q29uZmlnRGlyZWN0aXZlIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnLkRyYWZ0Q29uZmlnRGlyZWN0aXZlLmNvbnN0cnVjdG9yIl0sIm1hcHBpbmdzIjoiQUFBQSxJQUFPLEtBQUssQ0FhWDtBQWJELFdBQU8sS0FBSztJQUFDQSxJQUFBQSxHQUFHQSxDQWFmQTtJQWJZQSxXQUFBQSxHQUFHQTtRQUFDQyxJQUFBQSxXQUFXQSxDQWEzQkE7UUFiZ0JBLFdBQUFBLFdBQVdBLEVBQUNBLENBQUNBO1lBRTFCQztnQkFBQUM7b0JBQ1dDLGVBQVVBLEdBQUdBLHVCQUF1QkEsQ0FBQ0E7b0JBQ3JDQSxpQkFBWUEsR0FBR0EsTUFBTUEsQ0FBQ0E7b0JBQ3RCQSxhQUFRQSxHQUFHQSxHQUFHQSxDQUFDQTtvQkFDZkEsVUFBS0EsR0FBR0E7d0JBQ1hBLGVBQWVBLEVBQUVBLEdBQUdBO3FCQUN2QkEsQ0FBQ0E7b0JBQ0tBLGdCQUFXQSxHQUFHQSxzQ0FBc0NBLENBQUNBO2dCQUNoRUEsQ0FBQ0E7Z0JBQURELDJCQUFDQTtZQUFEQSxDQVJBRCxBQVFDQyxJQUFBRDtZQVJZQSxnQ0FBb0JBLHVCQVFoQ0EsQ0FBQUE7WUFFREEsU0FBS0EsQ0FBQ0EsU0FBU0EsQ0FBQ0EsYUFBYUEsRUFBRUEsY0FBTUEsT0FBQUEsSUFBSUEsb0JBQW9CQSxFQUFFQSxFQUExQkEsQ0FBMEJBLENBQUNBLENBQUNBO1FBQ3JFQSxDQUFDQSxFQWJnQkQsV0FBV0EsR0FBWEEsZUFBV0EsS0FBWEEsZUFBV0EsUUFhM0JBO0lBQURBLENBQUNBLEVBYllELEdBQUdBLEdBQUhBLFNBQUdBLEtBQUhBLFNBQUdBLFFBYWZBO0FBQURBLENBQUNBLEVBYk0sS0FBSyxLQUFMLEtBQUssUUFhWCIsImZpbGUiOiJkcmFmdENvbmZpZy9kcmFmdENvbmZpZ0RpcmVjdGl2ZS5qcyIsInNvdXJjZXNDb250ZW50IjpbIm1vZHVsZSBNYWdpYy5BcHAuRHJhZnRDb25maWcge1xuXG4gICAgZXhwb3J0IGNsYXNzIERyYWZ0Q29uZmlnRGlyZWN0aXZlIGltcGxlbWVudHMgbmcuSURpcmVjdGl2ZSB7XG4gICAgICAgIHB1YmxpYyBjb250cm9sbGVyID0gXCJkcmFmdENvbmZpZ0NvbnRyb2xsZXJcIjtcbiAgICAgICAgcHVibGljIGNvbnRyb2xsZXJBcyA9IFwiY3RybFwiO1xuICAgICAgICBwdWJsaWMgcmVzdHJpY3QgPSBcIkVcIjtcbiAgICAgICAgcHVibGljIHNjb3BlID0ge1xuICAgICAgICAgICAgdG91cm5hbWVudE1vZGVsOiBcIj1cIlxuICAgICAgICB9O1xuICAgICAgICBwdWJsaWMgdGVtcGxhdGVVcmwgPSBcImRyYWZ0Q29uZmlnL2RyYWZ0Q29uZmlnVGVtcGxhdGUuaHRtbFwiO1xuICAgIH1cblxuICAgIG1hZ2ljLmRpcmVjdGl2ZShcImRyYWZ0Q29uZmlnXCIsICgpID0+IG5ldyBEcmFmdENvbmZpZ0RpcmVjdGl2ZSgpKTtcbn1cblxuXG4iXSwic291cmNlUm9vdCI6Ii9zb3VyY2UvIn0=