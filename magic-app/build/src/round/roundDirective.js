var Magic;
(function (Magic) {
    var App;
    (function (App) {
        var Round;
        (function (Round) {
            var RoundDirective = (function () {
                function RoundDirective() {
                    this.restrict = "E";
                    this.controller = "roundController";
                    this.controllerAs = "roundController";
                    this.scope = {
                        round: "=",
                    };
                    this.templateUrl = "round/roundTemplate.html";
                }
                return RoundDirective;
            })();
            Round.RoundDirective = RoundDirective;
            App.magic.directive("round", function () { return new RoundDirective(); });
        })(Round = App.Round || (App.Round = {}));
    })(App = Magic.App || (Magic.App = {}));
})(Magic || (Magic = {}));

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInJvdW5kL3JvdW5kRGlyZWN0aXZlLnRzIl0sIm5hbWVzIjpbIk1hZ2ljIiwiTWFnaWMuQXBwIiwiTWFnaWMuQXBwLlJvdW5kIiwiTWFnaWMuQXBwLlJvdW5kLlJvdW5kRGlyZWN0aXZlIiwiTWFnaWMuQXBwLlJvdW5kLlJvdW5kRGlyZWN0aXZlLmNvbnN0cnVjdG9yIl0sIm1hcHBpbmdzIjoiQUFBQSxJQUFPLEtBQUssQ0FhWDtBQWJELFdBQU8sS0FBSztJQUFDQSxJQUFBQSxHQUFHQSxDQWFmQTtJQWJZQSxXQUFBQSxHQUFHQTtRQUFDQyxJQUFBQSxLQUFLQSxDQWFyQkE7UUFiZ0JBLFdBQUFBLEtBQUtBLEVBQUNBLENBQUNBO1lBRXBCQztnQkFBQUM7b0JBQ1dDLGFBQVFBLEdBQUdBLEdBQUdBLENBQUNBO29CQUNmQSxlQUFVQSxHQUFHQSxpQkFBaUJBLENBQUNBO29CQUMvQkEsaUJBQVlBLEdBQUdBLGlCQUFpQkEsQ0FBQ0E7b0JBQ2pDQSxVQUFLQSxHQUFHQTt3QkFDWEEsS0FBS0EsRUFBRUEsR0FBR0E7cUJBQ2JBLENBQUNBO29CQUNLQSxnQkFBV0EsR0FBR0EsMEJBQTBCQSxDQUFDQTtnQkFDcERBLENBQUNBO2dCQUFERCxxQkFBQ0E7WUFBREEsQ0FSQUQsQUFRQ0MsSUFBQUQ7WUFSWUEsb0JBQWNBLGlCQVExQkEsQ0FBQUE7WUFFREEsU0FBS0EsQ0FBQ0EsU0FBU0EsQ0FBQ0EsT0FBT0EsRUFBRUEsY0FBTUEsT0FBQUEsSUFBSUEsY0FBY0EsRUFBRUEsRUFBcEJBLENBQW9CQSxDQUFDQSxDQUFDQTtRQUN6REEsQ0FBQ0EsRUFiZ0JELEtBQUtBLEdBQUxBLFNBQUtBLEtBQUxBLFNBQUtBLFFBYXJCQTtJQUFEQSxDQUFDQSxFQWJZRCxHQUFHQSxHQUFIQSxTQUFHQSxLQUFIQSxTQUFHQSxRQWFmQTtBQUFEQSxDQUFDQSxFQWJNLEtBQUssS0FBTCxLQUFLLFFBYVgiLCJmaWxlIjoicm91bmQvcm91bmREaXJlY3RpdmUuanMiLCJzb3VyY2VzQ29udGVudCI6WyJtb2R1bGUgTWFnaWMuQXBwLlJvdW5kIHtcblxuICAgIGV4cG9ydCBjbGFzcyBSb3VuZERpcmVjdGl2ZSBpbXBsZW1lbnRzIG5nLklEaXJlY3RpdmUge1xuICAgICAgICBwdWJsaWMgcmVzdHJpY3QgPSBcIkVcIjtcbiAgICAgICAgcHVibGljIGNvbnRyb2xsZXIgPSBcInJvdW5kQ29udHJvbGxlclwiO1xuICAgICAgICBwdWJsaWMgY29udHJvbGxlckFzID0gXCJyb3VuZENvbnRyb2xsZXJcIjtcbiAgICAgICAgcHVibGljIHNjb3BlID0ge1xuICAgICAgICAgICAgcm91bmQ6IFwiPVwiLFxuICAgICAgICB9O1xuICAgICAgICBwdWJsaWMgdGVtcGxhdGVVcmwgPSBcInJvdW5kL3JvdW5kVGVtcGxhdGUuaHRtbFwiO1xuICAgIH1cblxuICAgIG1hZ2ljLmRpcmVjdGl2ZShcInJvdW5kXCIsICgpID0+IG5ldyBSb3VuZERpcmVjdGl2ZSgpKTtcbn1cbiJdLCJzb3VyY2VSb290IjoiL3NvdXJjZS8ifQ==