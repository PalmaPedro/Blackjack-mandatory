package com.blackjack.project.Blackjack;

import com.blackjack.project.User.Security.UserService;
import com.blackjack.project.User.User;
import com.blackjack.project.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@SessionAttributes("game")
@RequestMapping("/play")
public class GameController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome() {
        return "game/mainMenu";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String startNewGame(HttpSession session, @RequestParam("amount") int amount, Principal principal) {
        BlackJackGame game = new BlackJackGame();
        game.setUser(userService.findByUsername(principal.getName()));
        game.setStartCoins(game.getStartCoins());
        game.setBetAmount(amount);
        //evaluate if bet amount is not higher than start coins
        if (game.getBetAmount() > game.getStartCoins()) {
            //System.out.println("You can´t bet higher than what you have. Insert a new value!");
            return "game/mainMenu";
        }else {
            session.setAttribute("game", game);
            game.updateAmount();
            return "game/inProgress";
        }
    }


    @RequestMapping(method = RequestMethod.POST, params = "hit")
    public String hit(HttpSession session) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("game");
        game.playerHit();
        if (game.playerBusted()) {
            game.resolveDealerHand();
            return "game/endGame";
        } else {
            return "game/inProgress";
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = "stand")
    public String stand(HttpSession session) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("game");
        game.resolveDealerHand();
        return "game/endGame";
    }

    @RequestMapping(method = RequestMethod.POST, params = "finish")
    public String finish(HttpSession session, SessionStatus status) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("game");
        User user = game.getUser();
        userRepository.save(user);
        status.setComplete();
        return "game/mainMenu";
    }
}