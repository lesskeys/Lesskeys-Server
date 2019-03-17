package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.RingMessage;
import at.ac.uibk.keyless.Services.RingMessageService;
import at.ac.uibk.keyless.Services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
///CLOVER:OFF
@RestController
public class RingMessageController {

  @Autowired
  RingMessageService messageService;

  @Autowired
  SessionService sessionService;


  @RequestMapping(value = "/messages", method = RequestMethod.POST)
  public List<RingMessage> getRingMessageForUser(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return messageService.getRingMessagesForUser(data.get("username"));
    }
    return null;
  }
}
///CLOVER:ON