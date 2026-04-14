package inventory.util;

/**
 * Validation utility for input fields.
 */
public class ValidationUtil {

    /** Returns true if the string is null or blank. */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Basic email format validation (optional field — blank is OK). */
    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return true;
        return email.matches("^[\\w.%-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    /** Phone validation: digits, spaces, +, -, () allowed; 7-20 chars. */
    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) return true;
        return phone.matches("^[0-9+\\-() ]{7,20}$");
    }

    /** Returns true if parsed integer is > 0. */
    public static boolean isPositiveInt(String s) {
        try {
            return Integer.parseInt(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns true if parsed double is >= 0. */
    public static boolean isNonNegativeDouble(String s) {
        try {
            return Double.parseDouble(s.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns true if the string is a valid date in yyyy-MM-dd format. */
    public static boolean isValidDate(String s) {
        if (isBlank(s)) return false;
        try {
            java.sql.Date.valueOf(s.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
