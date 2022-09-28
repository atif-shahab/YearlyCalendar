public enum HijriMonth {
    Muharram(1), Safar(2), RabiAlAwal(3), RabeeAlAkhir(4), JumadaAlUla(5),
    JumadaAlAkhirah(6), Rajab(7), Shaaban(8), Ramadhan(9), Shawwal(10),
    DhuAlQada(11), DhuAlHijja(12);

    private final int value;
    HijriMonth(int value) {
        this.value = value;
    }

    public static HijriMonth fromInt(int x) {
        for(HijriMonth e : values()) {
            if(e.value == x)
                return e;
        }
        throw new IllegalStateException("unknown month " + x);
    }
}
