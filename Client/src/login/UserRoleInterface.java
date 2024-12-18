package login;

import cashier.src.View.CashierView;
import customer.src.View.CustomerView;
import manager.src.Controller.ManagerController;

public interface UserRoleInterface {
    void performAction(int accountID);
}

class CustomerRole implements UserRoleInterface {
    @Override
    public void performAction(int accountID) {
        CustomerView.run(accountID);
    }
}

class ManagerRole implements UserRoleInterface {
    @Override
    public void performAction(int accountID) {
        ManagerController.run();
    }
}

class CashierRole implements UserRoleInterface {
    @Override
    public void performAction(int accountID) {
        CashierView.run();
    }
}
