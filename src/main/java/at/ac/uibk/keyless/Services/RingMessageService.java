package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.RingMessage;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.RingMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class RingMessageService {

  @Autowired
  RingMessageRepository messageRepository;

  @Autowired
  UserService userService;


  /**
   * Method to save a new ring message.
   */
  public void saveRingMessage(String sender, String message, Long recipient) {
    RingMessage toSave = new RingMessage();
    toSave.setRecipientId(recipient);
    toSave.setSenderName(sender);
    toSave.setMessage(message);
    toSave.setTimestamp(new Date());
    messageRepository.save(toSave);
  }

  /**
   * @return list of messages, where the recipient is the given user
   */
  public List<RingMessage> getRingMessagesForUser(String username) {
    User user = userService.getUserByEmail(username);
    if (user != null) {
      return messageRepository.findAll().stream()
        .filter(m -> m.getRecipientId() == user.getUserId())
        .collect(Collectors.toList());
    }
    return null;
  }
}
