package it.chalmers.gamma.utils;

public enum CharacterTypes {
    UPPERCASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    LOWERCASE("abcdefghijklmnopqrstuvwxyz"),
    NUMBERS("123456789"),
    SPECIALS("!@#$%&()+=[]|/?><");

    private String characters;

    CharacterTypes(String characters) {
        this.characters = characters;
    }

    public String getCharacters() {
        return this.characters;
    }

    public static CharacterTypes[] allValues() {
        return new CharacterTypes[]{
                UPPERCASE, LOWERCASE, NUMBERS, SPECIALS
        };
    }
}
