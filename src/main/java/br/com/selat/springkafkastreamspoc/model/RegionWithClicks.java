package br.com.selat.springkafkastreamspoc.model;

public class RegionWithClicks {

    private String region;
    private Long clicks;

    public RegionWithClicks() {
    }

    public RegionWithClicks(String region, Long clicks) {
        this.region = region;
        this.clicks = clicks;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }
}
