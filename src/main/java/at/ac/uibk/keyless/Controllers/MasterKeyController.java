package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class MasterKeyController {

  @Autowired
  SessionService sessionService;

  /**
   * This byte-array represents the master-key, which is used to communicate with the MIFARE DESFire Tags.
   */
  private static final byte[] DEFAULT_KEY_AES128 = {
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
  };

  @RequestMapping(value = "/master-key/aes", method = RequestMethod.POST)
  public byte[] getAESMasterKey(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return DEFAULT_KEY_AES128;
    }
    return null;
  }
}
