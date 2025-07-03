document.addEventListener('DOMContentLoaded', () => {
    // This script is primarily for handling any front-end logic on the login page.
    // Since Spring Security is handling the form submission and redirect,
    // we don't need to add an AJAX call here for the login itself.
    
    // You could add logic here to display error messages that Spring might add to the URL,
    // for example: if (window.location.search.includes('error')) { ... }
    console.log("Login page loaded.");
});