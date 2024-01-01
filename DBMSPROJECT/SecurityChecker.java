public class SecurityChecker {
    private static final String SECURITY_CODE = "cs430@SIUC";

    public static boolean checkSecurityCode(String inputCode) {
        return SECURITY_CODE.equals(inputCode);
    }
}
