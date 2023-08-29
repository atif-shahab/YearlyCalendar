public class Settings {
    final String calEndPoint;
    final String hijriEndPoint;
    final String country;
    final String zipCode;
    final String latitude;
    final String longitude;
    final String method;
    final String juristic;
   final  String timeFormat;
    final String timezone;
    final String initFajrIqama;
    final String initDhuhrIqama;
    final String initAsrIqama;
    final String initIshaIqama;
    final  int fajrIqamaOffsetOutsideRamadhan;
    final int fajrIqamaOffsetInRamdhan;

    private Settings(Builder build)  {
        this.calEndPoint = build.calEndPoint;
        this.hijriEndPoint = build.hijriEndPoint;
        this.country = build.country;
        this.zipCode = build.zipCode;
        this.latitude = build.latitude;
        this.longitude = build.longitude;
        this.method = build.method;
        this.juristic = build.juristic;
        this.timeFormat = build.timeFormat;
        this.timezone = build.timezone;
        this.initFajrIqama = build.initFajrIqama;
        this.initDhuhrIqama = build.initDhuhrIqama;
        this.initAsrIqama = build.initAsrIqama;
        this.initIshaIqama = build.initIshaIqama;
        this.fajrIqamaOffsetOutsideRamadhan = build.fajrIqamaOffsetOutsideRamadhan;
        this.fajrIqamaOffsetInRamdhan = build.fajrIqamaOffsetInRamdhan;
    }

    public static class Builder{
        private String calEndPoint;
        private String hijriEndPoint;
        private String country;
        private String zipCode;
        private String latitude;
        private String longitude;
        private String method;
        private String juristic;
        private String timeFormat;
        private String timezone;
        private String initFajrIqama;
        private String initDhuhrIqama;
        private String initAsrIqama;
        private String initIshaIqama;
        private int fajrIqamaOffsetOutsideRamadhan = -1;
        private int fajrIqamaOffsetInRamdhan = -1;

        public Builder calEndPoint(String calEndPoint) {
            this.calEndPoint = calEndPoint;
            return this;
        }

        public Builder hijriEndPoint(String hijriEndPoint) {
            this.hijriEndPoint = hijriEndPoint;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }


        public Builder zipCode(String zipCode){
            this.zipCode = zipCode;
            return this;
        }


        public Builder latitude(String latitude){
            this.latitude = latitude;
            return this;
        }


        public Builder longitude(String longitude){
            this.longitude = longitude;
            return this;
        }


        public Builder method(String method){
            this.method = method;
            return this;
        }


        public Builder juristic(String juristic){
            this.juristic = juristic;
            return this;
        }


        public Builder timeFormat(String timeFormat){
            this.timeFormat = timeFormat;
            return this;
        }


        public Builder timezone(String timezone){
            this.timezone = timezone;
            return this;
        }


        public Builder initFajrIqama(String initFajrIqama){
            this.initFajrIqama = initFajrIqama;
            return this;
        }


        public Builder initDhuhrIqama(String initDhuhrIqama){
            this.initDhuhrIqama = initDhuhrIqama;
            return this;
        }


        public Builder initAsrIqama(String initAsrIqama){
            this.initAsrIqama = initAsrIqama;
            return this;
        }


        public Builder initIshaIqama(String initIshaIqama){
            this.initIshaIqama = initIshaIqama;
            return this;
        }


        public Builder fajrIqamaOffsetOutsideRamadhan(int fajrIqamaOffsetOutsideRamadhan){
            this.fajrIqamaOffsetOutsideRamadhan = fajrIqamaOffsetOutsideRamadhan;
            return this;
        }


        public Builder fajrIqamaOffsetInRamadhan(int fajrIqamaOffsetInRamdhan){
            this.fajrIqamaOffsetInRamdhan = fajrIqamaOffsetInRamdhan;
            return this;
        }

        public Settings build() {
            if(this.calEndPoint == null )
                throw new IllegalArgumentException(" calEndPoint not set");
            else if(this.hijriEndPoint == null)
                throw new IllegalArgumentException(" hijriEndPoint not set");
            else if(this.country == null)
                throw new IllegalArgumentException(" country not set");
            else if(this.zipCode == null)
                throw new IllegalArgumentException(" zipCode not set");
            else if(this.latitude == null)
                throw new IllegalArgumentException(" latitude not set");
            else if(this.longitude == null)
                throw new IllegalArgumentException(" longitude not set");
            else if(this.method == null)
                throw new IllegalArgumentException(" method not set");
            else if(this.juristic == null)
                throw new IllegalArgumentException(" juristic not set");
            else if(this.timeFormat == null)
                throw new IllegalArgumentException(" timeFormat not set");
            else if(this.timezone == null)
                throw new IllegalArgumentException(" timezone not set");
            else if(this.initFajrIqama == null)
                throw new IllegalArgumentException(" initFajrIqama not set");
            else if(this.initDhuhrIqama == null)
                throw new IllegalArgumentException(" initDhuhrIqama not set");
            else if(this.initAsrIqama == null)
                throw new IllegalArgumentException(" initAsrIqama not set");
            else if(this.initIshaIqama == null)
                throw new IllegalArgumentException(" initIshaIqama not set");
            else if(this.fajrIqamaOffsetOutsideRamadhan == -1)
                throw new IllegalArgumentException(" fajrIqamaOffsetOutsideRamadhan not set");
            else if(this.fajrIqamaOffsetInRamdhan == -1)
                throw new IllegalArgumentException(" fajrIqamaOffsetInRamdhan not set");

            return new Settings(this);
        }
    }
}
