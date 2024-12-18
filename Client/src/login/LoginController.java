package login;

import adapter.LoginDataAdapter;

import java.util.Map;

public class LoginController {
    private final LoginDataAdapter loginDataAdapter;

    public LoginController() {
        this.loginDataAdapter = new LoginDataAdapter();
    }

    public Map<String, Object> validateLogin(String accountName, String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        return loginDataAdapter.verifyLogin(accountName, password);
    }
}