// Inside HomeController.java
package com.ArushyRaina.WorkflowManagement.controllers;// <-- Use @Controller, NOT @RestController
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Use @Controller for serving or redirecting to HTML views
public class HomeController {

    /**
     * This method handles requests to the root URL ("/") and redirects
     * them to the login page.
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login.html";
    }
}
