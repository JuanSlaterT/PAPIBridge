package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.fonts;

public enum DefaultFontInfo
{
    A("A", 0, 'A', 5), 
    a("a", 1, 'a', 5), 
    B("B", 2, 'B', 5), 
    b("b", 3, 'b', 5), 
    C("C", 4, 'C', 5), 
    c("c", 5, 'c', 5), 
    D("D", 6, 'D', 5), 
    d("d", 7, 'd', 5), 
    E("E", 8, 'E', 5), 
    e("e", 9, 'e', 5), 
    F("F", 10, 'F', 5), 
    f("f", 11, 'f', 4), 
    G("G", 12, 'G', 5), 
    g("g", 13, 'g', 5), 
    H("H", 14, 'H', 5), 
    h("h", 15, 'h', 5), 
    I("I", 16, 'I', 3), 
    i("i", 17, 'i', 1), 
    J("J", 18, 'J', 5), 
    j("j", 19, 'j', 5), 
    K("K", 20, 'K', 5), 
    k("k", 21, 'k', 4), 
    L("L", 22, 'L', 5), 
    l("l", 23, 'l', 1), 
    M("M", 24, 'M', 5), 
    m("m", 25, 'm', 5), 
    N("N", 26, 'N', 5), 
    n("n", 27, 'n', 5), 
    O("O", 28, 'O', 5), 
    o("o", 29, 'o', 5), 
    P("P", 30, 'P', 5), 
    p("p", 31, 'p', 5), 
    Q("Q", 32, 'Q', 5), 
    q("q", 33, 'q', 5), 
    R("R", 34, 'R', 5), 
    r("r", 35, 'r', 5), 
    S("S", 36, 'S', 5), 
    s("s", 37, 's', 5), 
    T("T", 38, 'T', 5), 
    t("t", 39, 't', 4), 
    U("U", 40, 'U', 5), 
    u("u", 41, 'u', 5), 
    V("V", 42, 'V', 5), 
    v("v", 43, 'v', 5), 
    W("W", 44, 'W', 5), 
    w("w", 45, 'w', 5), 
    X("X", 46, 'X', 5), 
    x("x", 47, 'x', 5), 
    Y("Y", 48, 'Y', 5), 
    y("y", 49, 'y', 5), 
    Z("Z", 50, 'Z', 5), 
    z("z", 51, 'z', 5), 
    NUM_1("NUM_1", 52, '1', 5), 
    NUM_2("NUM_2", 53, '2', 5), 
    NUM_3("NUM_3", 54, '3', 5), 
    NUM_4("NUM_4", 55, '4', 5), 
    NUM_5("NUM_5", 56, '5', 5), 
    NUM_6("NUM_6", 57, '6', 5), 
    NUM_7("NUM_7", 58, '7', 5), 
    NUM_8("NUM_8", 59, '8', 5), 
    NUM_9("NUM_9", 60, '9', 5), 
    NUM_0("NUM_0", 61, '0', 5), 
    EXCLAMATION_POINT("EXCLAMATION_POINT", 62, '!', 1), 
    AT_SYMBOL("AT_SYMBOL", 63, '@', 6), 
    NUM_SIGN("NUM_SIGN", 64, '#', 5), 
    DOLLAR_SIGN("DOLLAR_SIGN", 65, '$', 5), 
    PERCENT("PERCENT", 66, '%', 5), 
    UP_ARROW("UP_ARROW", 67, '^', 5), 
    AMPERSAND("AMPERSAND", 68, '&', 5), 
    ASTERISK("ASTERISK", 69, '*', 5), 
    LEFT_PARENTHESIS("LEFT_PARENTHESIS", 70, '(', 4), 
    RIGHT_PERENTHESIS("RIGHT_PERENTHESIS", 71, ')', 4), 
    MINUS("MINUS", 72, '-', 5), 
    UNDERSCORE("UNDERSCORE", 73, '_', 5), 
    PLUS_SIGN("PLUS_SIGN", 74, '+', 5), 
    EQUALS_SIGN("EQUALS_SIGN", 75, '=', 5), 
    LEFT_CURL_BRACE("LEFT_CURL_BRACE", 76, '{', 4), 
    RIGHT_CURL_BRACE("RIGHT_CURL_BRACE", 77, '}', 4), 
    LEFT_BRACKET("LEFT_BRACKET", 78, '[', 3), 
    RIGHT_BRACKET("RIGHT_BRACKET", 79, ']', 3), 
    COLON("COLON", 80, ':', 1), 
    SEMI_COLON("SEMI_COLON", 81, ';', 1), 
    DOUBLE_QUOTE("DOUBLE_QUOTE", 82, '\"', 3), 
    SINGLE_QUOTE("SINGLE_QUOTE", 83, '\'', 1), 
    LEFT_ARROW("LEFT_ARROW", 84, '<', 4), 
    RIGHT_ARROW("RIGHT_ARROW", 85, '>', 4), 
    QUESTION_MARK("QUESTION_MARK", 86, '?', 5), 
    SLASH("SLASH", 87, '/', 5), 
    BACK_SLASH("BACK_SLASH", 88, '\\', 5), 
    LINE("LINE", 89, '|', 1), 
    TILDE("TILDE", 90, '~', 5), 
    TICK("TICK", 91, '`', 2), 
    PERIOD("PERIOD", 92, '.', 1), 
    COMMA("COMMA", 93, ',', 1), 
    SPACE("SPACE", 94, ' ', 3), 
    DEFAULT("DEFAULT", 95, 'a', 4);
    
    private char character;
    private int length;
    
    private DefaultFontInfo(final String s, final int n, final char character, final int length) {
        this.character = character;
        this.length = length;
    }
    
    public char getCharacter() {
        return this.character;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getBoldLength() {
        if (this == DefaultFontInfo.SPACE) {
            return this.getLength();
        }
        return this.length + 1;
    }
    
    public static DefaultFontInfo getDefaultFontInfo(final char c) {
        DefaultFontInfo[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final DefaultFontInfo dFI = values[i];
            if (dFI.getCharacter() == c) {
                return dFI;
            }
        }
        return DefaultFontInfo.DEFAULT;
    }
}

