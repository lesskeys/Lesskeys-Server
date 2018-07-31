package at.ac.uibk.keyless.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Lukas Dötlinger.
 */
@Controller
public class RingController {

  @RequestMapping(value = "/ring", method = RequestMethod.GET)
  public String ringList() {
    return "/ring.xhtml";
  }
}
