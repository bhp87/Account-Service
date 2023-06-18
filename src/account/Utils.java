package account;

public class Utils {
    public final static String[] BREACHED_PASSWORDS = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

    public final static String DIFFERENT_PASSWORD_MESSAGE = "The passwords must be different!";
    public final static String CHANGE_PASSWORD_PATH = "/api/auth/changepass";
    public final static String BAD_REQUEST = "Bad Request";
    public final static String BREACHED_PASSWORD_MESSAGE = "The password is in the hacker's database!";
    public final static String REGISTER_PATH = "/api/auth/signup";
    public final static String USER_EXISTS = "User exist!";
    public final static String USER_NOT_EXISTS = "User doesn't exist!";
    public final static String PAYMENTS = "/api/acct/payments";
    public final static String SALARY_MUST_BE_POSITIVE = "Salary cant be 0 or negative!";
    public final static String WRONG_DATE_MESSAGE = "Wrong date!";
    public final static String WRONG_DATE_FORMAT = "Wrong date format!";
    public final static String GET_PAYMENT_PATH = "/api/empl/payment";
}