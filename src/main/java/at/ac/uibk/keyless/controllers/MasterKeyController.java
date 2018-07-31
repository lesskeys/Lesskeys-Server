package at.ac.uibk.keyless.controllers;

import at.ac.uibk.keyless.services.SessionService;
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
    //TODO: Check if requesting user is allowed to have master-key
    if (sessionService.isValidSession(data.get("session"))) {
      return DEFAULT_KEY_AES128;
    }
    return null;
  }
}
